package dev.xtoy.common.enums;

/**
 * 支持 ID 成员
 */
public interface XIdMember {
    /**
     * 获取 ID 值
     */
    int getId();

    default boolean equalsById(Integer id) {
        return id != null && getId() == id;
    }

    default boolean equalsById(int id) {
        return getId() == id;
    }
}
