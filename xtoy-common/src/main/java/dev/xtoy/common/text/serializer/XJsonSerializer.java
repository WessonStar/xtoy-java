package dev.xtoy.common.text.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * JSON 序列化器
 */
final class XJsonSerializer extends XAbstractTextSerializer {
    public static final XTextSerializer DEFAULT = new XJsonSerializer();

    public XJsonSerializer() {
        super(JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .changeDefaultPropertyInclusion(x ->
                        JsonInclude.Value.empty().withValueInclusion(JsonInclude.Include.NON_NULL))
                .build());
    }

    @Override
    public XTextTypeEnum getTextType() {
        return XTextTypeEnum.JSON;
    }
}
