package dev.xtoy.common.text.serializer;

/**
 * 文本序列化器工厂
 */
public final class XTextSerializerFactory {
    /**
     * 获取文本序列化器
     */
    public static XTextSerializer getSerializer(XTextTypeEnum textType) {
        return switch (textType) {
            case JSON -> XJsonSerializer.DEFAULT;
            case YAML -> XYamlSerializer.DEFAULT;
            case XML -> XXmlSerializer.DEFAULT;
            default -> throw new IllegalArgumentException("Unsupported text type: " + textType);
        };
    }
}
