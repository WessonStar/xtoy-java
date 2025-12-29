package dev.xtoy.common.text.serializer;

import tools.jackson.dataformat.xml.XmlMapper;

/**
 * XML 序列化器
 */
final class XXmlSerializer extends XAbstractTextSerializer {
    public static final XTextSerializer DEFAULT = new XXmlSerializer();

    public XXmlSerializer() {
        super(XmlMapper.builder().build());
    }

    @Override
    public XTextTypeEnum getTextType() {
        return XTextTypeEnum.XML;
    }
}
