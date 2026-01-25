package dev.xtoy.common.enums;

/**
 * 支持 ID 成员
 */
public interface XIdMember {
    /**
     * 根据 ID 获取成员
     * @param source 可选对象数组
     * @param id ID 值
     * @param defaultValue 默认值
     * @return 匹配的成员对象，未找到则返回默认值
     * @param <T> 成员类型
     */
    static <T extends XIdMember> T getById(T[] source, int id, T defaultValue) {
        for (T item : source) {
            if (item.equalsById(id)) {
                return item;
            }
        }
        return defaultValue;
    }

    /**
     * 获取 ID 值
     */
    int getId();

    /**
     * 根据 ID 值比较，null 视为不等
     * @param id ID 值
     * @return 相等返回 True
     */
    default boolean equalsById(Integer id) {
        return id != null && getId() == id;
    }

    /**
     * 根据 ID 值比较
     * @param id ID 值
     * @return 相等返回 True
     */
    default boolean equalsById(int id) {
        return getId() == id;
    }
}
