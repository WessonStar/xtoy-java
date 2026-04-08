package dev.xtoy.common.process;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 进程命令执行器
 */
@Slf4j
public class ProcessCommandExecutor {
    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    private static final boolean WINDOWS = OS_NAME.contains("win");

    /**
     * 默认执行器，适合大多数轻量命令执行场景。
     */
    public static final ProcessCommandExecutor DEFAULT = new ProcessCommandExecutor(4);

    private final int processLogTailSize;
    private final int summaryMaxLength;
    private final long logReaderWaitTimeout;
    private final TimeUnit logReaderWaitUnit;
    private final long stopProcessWaitTimeout;
    private final TimeUnit stopProcessWaitUnit;
    private final String logReaderThreadPrefix;
    private final ExecutorService logReaderExecutor;

    public ProcessCommandExecutor(int logReaderThreadCount) {
        this(10, 240, logReaderThreadCount, "process-log-reader-",
                2, TimeUnit.SECONDS, 3, TimeUnit.SECONDS);
    }

    public ProcessCommandExecutor(
            int processLogTailSize, int summaryMaxLength, int logReaderThreadCount,
            String logReaderThreadPrefix, long logReaderWaitTimeout, TimeUnit logReaderWaitUnit,
            long stopProcessWaitTimeout, TimeUnit stopProcessWaitUnit) {
        this.processLogTailSize = processLogTailSize;
        this.summaryMaxLength = summaryMaxLength;
        this.logReaderWaitTimeout = logReaderWaitTimeout;
        this.logReaderWaitUnit = logReaderWaitUnit;
        this.stopProcessWaitTimeout = stopProcessWaitTimeout;
        this.stopProcessWaitUnit = stopProcessWaitUnit;
        this.logReaderThreadPrefix = logReaderThreadPrefix;

        // 进程输出读取任务需要尽快启动，使用 SynchronousQueue 避免在队列中排队。
        this.logReaderExecutor = new ThreadPoolExecutor(
                0,
                logReaderThreadCount,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ProcessLogReaderThreadFactory(logReaderThreadPrefix));
    }

    /**
     * 通过系统默认 shell 执行命令，并返回统一的执行结果。
     */
    public ProcessExecResult executeByShell(String command, long timeout, TimeUnit unit) {
        return executeInternal(() -> {
            ProcessBuilder processBuilder = buildShellProcess(command);
            processBuilder.redirectErrorStream(true);
            return processBuilder;
        }, summarizeCommand(command), timeout, unit);
    }

    /**
     * 直接执行命令（非 shell），适合需要精确保留参数边界的场景。
     */
    public ProcessExecResult execute(List<String> command, long timeout, TimeUnit unit) {
        return executeInternal(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            return processBuilder;
        }, summarizeCommand(String.join(" ", command)), timeout, unit);
    }

    private ProcessExecResult executeInternal(
            ProcessBuilderProvider processBuilderProvider, String commandSummary, long timeout, TimeUnit unit) {
        long startTime = System.currentTimeMillis();
        String execId = UUID.randomUUID().toString().substring(0, 8);
        log.info("Start exec command. execId: {}, timeout: {} {}, command: {}", execId, timeout,
                unit.name().toLowerCase(Locale.ROOT), commandSummary);

        Process process = null;
        BoundedLogBuffer outputTail = new BoundedLogBuffer(processLogTailSize);
        Future<?> outputReader = null;
        try {
            ProcessBuilder processBuilder = processBuilderProvider.get();
            process = processBuilder.start();
            outputReader = startProcessLogReader(process.getInputStream(), outputTail);

            boolean finished = process.waitFor(timeout, unit);
            if (!finished) {
                long costMs = System.currentTimeMillis() - startTime;
                log.error("Exec command timeout. execId: {}, costMs: {}, command: {}, tailLogs: {}",
                        execId, costMs, commandSummary, outputTail.snapshot());
                stopProcess(process);
                waitLogReader(outputReader, execId);
                return new ProcessExecResult(false, true, -1, costMs);
            }

            int exitCode = process.exitValue();
            waitLogReader(outputReader, execId);
            long costMs = System.currentTimeMillis() - startTime;
            if (exitCode != 0) {
                log.warn("Exec command failed. execId: {}, costMs: {}, exitCode: {}, command: {}, tailLogs: {}",
                        execId, costMs, exitCode, commandSummary, outputTail.snapshot());
                return new ProcessExecResult(false, false, exitCode, costMs);
            }

            log.info("Exec command success. execId: {}, costMs: {}, exitCode: {}, command: {}",
                    execId, costMs, exitCode, commandSummary);
            return new ProcessExecResult(true, false, exitCode, costMs);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            long costMs = System.currentTimeMillis() - startTime;
            log.warn("Exec command error. execId: {}, command: {}, msg: {}, tailLogs: {}",
                    execId, commandSummary, e.getMessage(), outputTail.snapshot(), e);
            return new ProcessExecResult(false, false, -1, costMs);
        } finally {
            if (process != null) {
                try {
                    process.getOutputStream().close();
                } catch (IOException e) {
                    log.warn("Close process output stream error. execId: {}", execId, e);
                }
            }
        }
    }

    private ProcessBuilder buildShellProcess(String command) {
        if (WINDOWS) {
            return new ProcessBuilder("powershell", "-Command", command);
        }
        return new ProcessBuilder("/bin/bash", "-c", command);
    }

    /**
     * 异步读取进程输出，只保留尾部若干行日志。
     */
    private Future<?> startProcessLogReader(InputStream inputStream, BoundedLogBuffer outputTail) {
        try {
            return logReaderExecutor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputTail.add(line);
                    }
                } catch (IOException e) {
                    outputTail.add("log-reader-error: " + e.getMessage());
                }
            });
        } catch (RejectedExecutionException e) {
            outputTail.add("log-reader-rejected: executor is busy");
            log.error("Reject process log reader task. threadPrefix: {}, tailSize: {}",
                    logReaderThreadPrefix, processLogTailSize, e);
            throw e;
        }
    }

    /**
     * 等待日志读取任务结束，避免进程已退出但尾部日志尚未消费完成。
     */
    private void waitLogReader(Future<?> outputReader, String execId) {
        if (outputReader == null) {
            return;
        }
        try {
            outputReader.get(logReaderWaitTimeout, logReaderWaitUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting process log reader. execId: {}", execId, e);
        } catch (Exception e) {
            log.warn("Process log reader finished abnormally. execId: {}", execId, e);
        }
    }

    /**
     * 优雅终止进程；如果未在限定时间内退出，则强制销毁。
     */
    private void stopProcess(Process process) {
        process.destroy();
        try {
            if (!process.waitFor(stopProcessWaitTimeout, stopProcessWaitUnit)) {
                process.destroyForcibly();
                process.waitFor(stopProcessWaitTimeout, stopProcessWaitUnit);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
        }
    }

    /**
     * 将命令压缩为适合日志记录的摘要文本，避免输出过长。
     */
    private String summarizeCommand(String command) {
        String normalizedCommand = command.replaceAll("\\s+", " ").trim();
        return normalizedCommand.length() <= summaryMaxLength
                ? normalizedCommand
                : normalizedCommand.substring(0, summaryMaxLength) + "...";
    }

    @FunctionalInterface
    private interface ProcessBuilderProvider {
        ProcessBuilder get();
    }

    /**
     * 为日志读取线程生成稳定、可辨识的线程名。
     */
    private static final class ProcessLogReaderThreadFactory implements ThreadFactory {
        private final String threadPrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private ProcessLogReaderThreadFactory(String threadPrefix) {
            this.threadPrefix = threadPrefix;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, threadPrefix + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
