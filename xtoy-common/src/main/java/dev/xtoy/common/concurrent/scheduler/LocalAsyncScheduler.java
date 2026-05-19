package dev.xtoy.common.concurrent.scheduler;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * 本地异步调度器
 */
@Slf4j
public class LocalAsyncScheduler implements AsyncScheduler, AutoCloseable {
    private static final int DEFAULT_CORE_POOL_SIZE = 20;
    private static final int DEFAULT_MAX_POOL_SIZE = 40;
    private static final int DEFAULT_QUEUE_CAPACITY = 40;

    private final ExecutorService defaultExecutor;
    private final ScheduledExecutorService scheduledExecutor;

    public LocalAsyncScheduler() {
        this.defaultExecutor = new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SIZE,
                DEFAULT_MAX_POOL_SIZE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY),
                new DiscardWithLogPolicy("local-async-default"));
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        return runAsync(runnable, AsyncRejectStrategy.THROW);
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable, AsyncRejectStrategy rejectStrategy) {
        return runAsync(runnable, defaultExecutor, rejectStrategy);
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable, ExecutorService executor) {
        return runAsync(runnable, executor, AsyncRejectStrategy.THROW);
    }

    @Override
    public CompletableFuture<Void> runAsync(
            Runnable runnable, ExecutorService executor, AsyncRejectStrategy rejectStrategy) {
        return runAsyncSafely(MdcRunnable.wrap(runnable), executor, "runAsync", rejectStrategy);
    }

    @Override
    public CompletableFuture<Void> delayRunAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return delayRunAsync(runnable, delay, timeUnit, AsyncRejectStrategy.THROW);
    }

    @Override
    public CompletableFuture<Void> delayRunAsync(
            Runnable runnable, long delay, TimeUnit timeUnit, AsyncRejectStrategy rejectStrategy) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Runnable mdcRunnable = MdcRunnable.wrap(runnable);
        try {
            scheduledExecutor.schedule(() -> {
                try {
                    defaultExecutor.execute(() -> {
                        try {
                            mdcRunnable.run();
                            future.complete(null);
                        } catch (Throwable t) {
                            future.completeExceptionally(t);
                        }
                    });
                } catch (RejectedExecutionException e) {
                    handleRejectedExecution("delayRunAsync", rejectStrategy, runnable, future, e);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }, delay, timeUnit);
        } catch (RejectedExecutionException e) {
            handleRejectedExecution("delayRunAsync", rejectStrategy, runnable, future, e);
        }
        return future;
    }

    @Override
    public void close() throws Exception {
        Stream.of(defaultExecutor, scheduledExecutor)
                .forEach(executor -> {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                });
    }

    private CompletableFuture<Void> runAsyncSafely(
            Runnable runnable, Executor executor, String operation, AsyncRejectStrategy rejectStrategy) {
        try {
            return CompletableFuture.runAsync(runnable, executor);
        } catch (RejectedExecutionException e) {
            if (rejectStrategy == AsyncRejectStrategy.DISCARD) {
                log.debug("Async task submit rejected and discarded. operation: {}", operation, e);
                return CompletableFuture.completedFuture(null);
            }
            if (rejectStrategy == AsyncRejectStrategy.CALLER_RUNS) {
                log.warn("Run async task in caller thread because executor is exhausted. operation: {}", operation, e);
                try {
                    runnable.run();
                    return CompletableFuture.completedFuture(null);
                } catch (Throwable t) {
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    future.completeExceptionally(t);
                    return future;
                }
            }
            throw e;
        }
    }

    private void handleRejectedExecution(
            String operation,
            AsyncRejectStrategy rejectStrategy,
            Runnable runnable,
            CompletableFuture<Void> future,
            RejectedExecutionException e) {
        if (rejectStrategy == AsyncRejectStrategy.DISCARD) {
            log.debug("Async task submit rejected and discarded. operation: {}", operation, e);
            future.complete(null);
            return;
        }
        if (rejectStrategy == AsyncRejectStrategy.CALLER_RUNS) {
            log.warn("Run async task in caller thread because executor is exhausted. operation: {}", operation, e);
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
            return;
        }
        future.completeExceptionally(e);
    }

    private static final class DiscardWithLogPolicy implements RejectedExecutionHandler {
        private final String executorName;

        private DiscardWithLogPolicy(String executorName) {
            this.executorName = executorName;
        }

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            log.warn("Discard async task because executor is exhausted. executor: {}, poolSize: {}, activeCount: {}, queueSize: {}, queueRemainingCapacity: {}, completedTaskCount: {}",
                    executorName,
                    executor.getPoolSize(),
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getQueue().remainingCapacity(),
                    executor.getCompletedTaskCount());
            throw new RejectedExecutionException("Executor is exhausted: " + executorName);
        }
    }
}

