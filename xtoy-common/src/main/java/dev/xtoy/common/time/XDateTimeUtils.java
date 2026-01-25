package dev.xtoy.common.time;

import java.time.LocalDateTime;

/**
 * 日期时间工具类
 */
public final class XDateTimeUtils {
    private static final LocalDateTime DEFAULT_MIN_VALID_TIME =
            LocalDateTime.of(2000, 1, 1, 0, 0);

    /**
     * 如果 source 早于 DEFAULT_MIN_VALID_TIME，则返回 null，否则返回 source 本身
     * @param source 目标时间
     * @return source 或者 null
     */
    public static LocalDateTime validOrNull(LocalDateTime source) {
        return validOrNull(source, DEFAULT_MIN_VALID_TIME);
    }

    /**
     * 如果 source 早于 minValidTime，则返回 null，否则返回 source 本身
     * @param source 目标时间
     * @param minValidTime 最小有效时间
     * @return source 或者 null
     */
    public static LocalDateTime validOrNull(LocalDateTime source, LocalDateTime minValidTime) {
        if (source != null && source.isBefore(minValidTime)) {
            return null;
        }
        return source;
    }
}
