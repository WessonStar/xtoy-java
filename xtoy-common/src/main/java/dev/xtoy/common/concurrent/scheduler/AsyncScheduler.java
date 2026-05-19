package dev.xtoy.common.concurrent.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步调度器
 */
public interface AsyncScheduler {

    /**
     * 异步执行任务；默认在提交失败时抛出异常，由调用方感知线程池耗尽。
     */
    CompletableFuture<Void> runAsync(Runnable runnable);

    /**
     * 异步执行任务，并由调用方指定提交失败时的处理策略。
     */
    CompletableFuture<Void> runAsync(Runnable runnable, AsyncRejectStrategy rejectStrategy);

    /**
     * 使用指定线程池异步执行任务；默认在提交失败时抛出异常。
     */
    CompletableFuture<Void> runAsync(Runnable runnable, ExecutorService executor);

    /**
     * 使用指定线程池异步执行任务，并由调用方指定提交失败时的处理策略。
     */
    CompletableFuture<Void> runAsync(
            Runnable runnable, ExecutorService executor, AsyncRejectStrategy rejectStrategy);

    /**
     * 异步延迟执行任务；默认在提交失败时抛出异常。
     */
    CompletableFuture<Void> delayRunAsync(Runnable runnable, long delay, TimeUnit timeUnit);

    /**
     * 异步延迟执行任务，并由调用方指定提交失败时的处理策略。
     */
    CompletableFuture<Void> delayRunAsync(
            Runnable runnable, long delay, TimeUnit timeUnit, AsyncRejectStrategy rejectStrategy);
}
