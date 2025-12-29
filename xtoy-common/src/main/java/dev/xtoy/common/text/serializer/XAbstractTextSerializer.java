package dev.xtoy.common.text.serializer;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
abstract class XAbstractTextSerializer implements XTextSerializer {
    private final ObjectMapper mapper;

    public XAbstractTextSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> List<T> deserializeList(String text, Class<T> tClass) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return mapper.readValue(text,
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, tClass));
        } catch (Exception e) {
            log.error("Error deserializing {} to List<{}>", getTextType(), tClass.getSimpleName(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, String> deserializeStringMap(InputStream textStream) {
        return deserialize(textStream, new TypeReference<Map<String, String>>() {});
    }

    @Override
    public <T> String serialize(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error serializing object to {}", getTextType(), e);
            return null;
        }
    }

    @Override
    public <T> T deserialize(String text, Class<T> tClass) {
        try {
            return mapper.readValue(text, tClass);
        } catch (Exception e) {
            log.error("Error deserializing {} to {}", getTextType(), tClass.getSimpleName(), e);
            return null;
        }
    }

    @Override
    public <T> T deserialize(String text, TypeReference<T> tRefer) {
        try {
            return mapper.readValue(text, tRefer);
        } catch (Exception e) {
            log.error("Error deserializing {} to {}", getTextType(), tRefer.getType(), e);
            return null;
        }
    }

    @Override
    public <T> T deserialize(InputStream textStream, Class<T> tClass) {
        if (textStream == null) {
            return null;
        }

        try {
            return mapper.readValue(textStream, tClass);
        } catch (Exception e) {
            log.error("Error deserializing {} Stream to {}", getTextType(), tClass.getSimpleName(), e);;
            return null;
        }
    }

    @Override
    public <T> T deserialize(InputStream textStream, TypeReference<T> tRefer) {
        if (textStream == null) {
            return null;
        }

        try {
            return mapper.readValue(textStream, tRefer);
        } catch (Exception e) {
            log.error("Error deserializing {} Stream to {}", getTextType(), tRefer.getType(), e);;
            return null;
        }
    }

    @Override
    public XTextTypeEnum getTextType() {
        return XTextTypeEnum.UNDEFINED;
    }
}
