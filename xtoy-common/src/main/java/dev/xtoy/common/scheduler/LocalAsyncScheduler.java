package dev.xtoy.common.scheduler;

import java.util.concurrent.*;

/**
 * 本地异步调度器
 */
public class LocalAsyncScheduler implements AutoCloseable {
    private final Executor defaultExecutor;

    public LocalAsyncScheduler() {
        this(new ThreadPoolExecutor(
                20, 40, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(40), new ThreadPoolExecutor.CallerRunsPolicy())
        );
    }

    public LocalAsyncScheduler(Executor executor) {
        this.defaultExecutor = executor;
    }

    /**
     * 异步执行任务
     */
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(MdcRunnable.wrap(runnable), defaultExecutor);
    }

    /**
     * 异步执行任务
     */
    public CompletableFuture<Void> runAsync(Runnable runnable, ExecutorService executor) {
        return CompletableFuture.runAsync(MdcRunnable.wrap(runnable), executor);
    }

    /**
     * 异步延迟执行任务
     */
    public CompletableFuture<Void> delayRunAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return CompletableFuture.runAsync(MdcRunnable.wrap(runnable),
                CompletableFuture.delayedExecutor(delay, timeUnit, defaultExecutor));
    }

    @Override
    public void close() throws Exception {
        if (defaultExecutor instanceof ExecutorService executorService) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
