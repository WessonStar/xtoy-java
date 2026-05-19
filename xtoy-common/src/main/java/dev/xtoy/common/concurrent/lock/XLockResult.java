package dev.xtoy.common.concurrent.lock;

/**
 * 分布式锁执行结果，用 acquired 区分未获取锁和业务返回 null 的场景
 *
 * @param acquired 是否成功获取锁并执行任务
 * @param value 业务任务返回值，允许为 null
 */
public record XLockResult<T>(boolean acquired, T value) {

    /**
     * 创建已获取锁并完成执行的结果
     *
     * @param value 业务任务返回值
     * @return 已获取锁结果
     */
    public static <T> XLockResult<T> acquired(T value) {
        return new XLockResult<>(true, value);
    }

    /**
     * 创建未获取锁的结果
     *
     * @return 未获取锁结果
     */
    public static <T> XLockResult<T> notAcquired() {
        return new XLockResult<>(false, null);
    }
}
