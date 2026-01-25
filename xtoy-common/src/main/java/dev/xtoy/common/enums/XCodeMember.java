package dev.xtoy.common.enums;

/**
 * 支持编码成员
 */
public interface XCodeMember {
    /**
     * 根据编码获取成员
     * @param source 可选对象数组
     * @param code 编码
     * @param defaultValue 默认值
     * @return 匹配的成员对象，未找到则返回默认值
     * @param <T> 成员类型
     */
    static <T extends XCodeMember> T getByCode(T[] source, String code, T defaultValue) {
        if (code == null || code.isEmpty()) {
            return defaultValue;
        }

        for (T item : source) {
            if (item.equalsByCode(code)) {
                return item;
            }
        }
        return defaultValue;
    }

    /**
     * 获取编码值
     */
    String getCode();

    /**
     * 根据编码值比较
     * @param code 编码值
     * @return 相等返回 True
     */
    default boolean equalsByCode(String code) {
        return code != null && getCode().equalsIgnoreCase(code);
    }
}
