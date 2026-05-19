package dev.xtoy.common.concurrent.lock;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 分布式锁
 */
public interface XDistributedLock {
    /**
     * 尝试获取锁并执行任务，未获取锁时不会执行任务
     *
     * @param key 锁 key
     * @param ttl 锁自动过期时间
     * @param action 获取锁后执行的任务
     * @return 锁执行结果，业务返回 null 时仍表示已获取锁
     */
    <T> XLockResult<T> tryExecute(String key, Duration ttl, Supplier<T> action);

    /**
     * 在等待时间内反复尝试获取锁并执行任务，超时未获取锁时不会执行任务
     *
     * @param key 锁 key
     * @param ttl 锁自动过期时间
     * @param waitTimeout 等待获取锁的最长时间
     * @param retryInterval 重试间隔
     * @param action 获取锁后执行的任务
     * @return 锁执行结果，业务返回 null 时仍表示已获取锁
     */
    <T> XLockResult<T> tryExecuteWithWait(String key, Duration ttl, Duration waitTimeout, Duration retryInterval, Supplier<T> action);

    /**
     * 尝试获取锁并执行无返回值任务
     *
     * @param key 锁 key
     * @param ttl 锁自动过期时间
     * @param action 获取锁后执行的任务
     * @return 锁执行结果
     */
    XLockResult<Void> tryExecute(String key, Duration ttl, Runnable action);

    /**
     * 在等待时间内反复尝试获取锁并执行无返回值任务，超时未获取锁时不会执行任务
     *
     * @param key 锁 key
     * @param ttl 锁自动过期时间
     * @param waitTimeout 等待获取锁的最长时间
     * @param retryInterval 重试间隔
     * @param action 获取锁后执行的任务
     * @return 锁执行结果
     */
    XLockResult<Void> tryExecuteWithWait(String key, Duration ttl, Duration waitTimeout, Duration retryInterval, Runnable action);
}
