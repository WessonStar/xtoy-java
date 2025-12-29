package dev.xtoy.common.text.serializer;

import tools.jackson.dataformat.yaml.YAMLMapper;

/**
 * YAML 序列化器
 */
final class XYamlSerializer extends XAbstractTextSerializer {
    public static XYamlSerializer DEFAULT = new XYamlSerializer();

    public XYamlSerializer() {
        super(YAMLMapper.builder()
                .build());
    }

    @Override
    public XTextTypeEnum getTextType() {
        return XTextTypeEnum.YAML;
    }
}
