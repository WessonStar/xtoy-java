package dev.xtoy.common.concurrent.scheduler;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Runnable 的包装器，自动捕获并传递 MDC 上下文
 */
public class MdcRunnable implements Runnable {
    private final Runnable runnable;
    private final Map<String, String> contextMap;

    public MdcRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.contextMap = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        try {
            runnable.run();
        } finally {
            MDC.clear();
        }
    }

    public static Runnable wrap(Runnable runnable) {
        return new MdcRunnable(runnable);
    }
}
