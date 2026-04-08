package dev.xtoy.common.process;

import lombok.Getter;

/**
 * 进程执行结果
 */
@Getter
public class ProcessExecResult {
    private final boolean success;
    private final boolean timedOut;
    private final int exitCode;
    private final long costMs;

    public ProcessExecResult(boolean success, boolean timedOut, int exitCode, long costMs) {
        this.success = success;
        this.timedOut = timedOut;
        this.exitCode = exitCode;
        this.costMs = costMs;
    }
}
