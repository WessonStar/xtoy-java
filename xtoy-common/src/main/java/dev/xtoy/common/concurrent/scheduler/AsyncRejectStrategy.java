package dev.xtoy.common.concurrent.scheduler;

/**
 * 异步任务提交被拒绝时的处理策略。
 */
public enum AsyncRejectStrategy {
    /**
     * 直接丢弃任务，不阻塞当前线程。
     */
    DISCARD,

    /**
     * 回退到调用线程执行任务。
     */
    CALLER_RUNS,

    /**
     * 将拒绝异常继续抛给调用方处理。
     */
    THROW
}
