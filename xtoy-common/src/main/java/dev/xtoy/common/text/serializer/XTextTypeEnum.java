package dev.xtoy.common.text.serializer;

import dev.xtoy.common.enums.XCodeMember;
import dev.xtoy.common.enums.XIdMember;

/**
 * 文本类型枚举
 */
public enum XTextTypeEnum implements XIdMember, XCodeMember {
    UNDEFINED(0, "undefined", "未定义"),
    JSON(1, "json", "JSON"),
    YAML(2, "yaml", "YAML"),
    XML(3, "xml", "XML"),
    ;

    private final int id;
    private final String code;
    private final String showName;

    XTextTypeEnum(int id, String code, String showName) {
        this.id = id;
        this.code = code;
        this.showName = showName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getShowName() {
        return showName;
    }
}
