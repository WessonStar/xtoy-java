package dev.xtoy.common.enums;

/**
 * 支持编码成员
 */
public interface XCodeMember {
    /**
     * 获取编码值
     */
    String getCode();

    default boolean equalsByCode(String code) {
        return code != null && getCode().equalsIgnoreCase(code);
    }
}
