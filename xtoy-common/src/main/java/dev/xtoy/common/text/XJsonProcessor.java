package dev.xtoy.common.text;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JSON 处理器
 */
@Slf4j
public final class XJsonProcessor {
    public static final XJsonProcessor DEFAULT = new XJsonProcessor();

    private final ObjectMapper mapper;

    public XJsonProcessor() {
        this.mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .changeDefaultPropertyInclusion(x ->
                        JsonInclude.Value.empty().withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
    }

    /**
     * 将 json 字符串反序列化成对象列表
     */
    public <T> List<T> deserializeList(String json, Class<T> tClass) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }

        return mapper.readValue(json,
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, tClass));
    }

    /**
     * 将 json 流反序列化成字符串键值对
     */
    public Map<String, String> deserializeStringMap(InputStream jsonStream) {
        return deserialize(jsonStream, new TypeReference<Map<String, String>>() {});
    }

    /**
     * 将对象序列化成 json 字符串
     */
    public String serialize(Object x) {
        if (x == null) {
            return null;
        }

        return mapper.writeValueAsString(x);
    }

    /**
     * 将 json 字符串反序列化成对象
     */
    public <T> T deserialize(String json, Class<T> tClass) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        return mapper.readValue(json, tClass);
    }

    /**
     * 将 json 字符串反序列化成对象 (采用 Jackson 类型)
     */
    public <T> T deserialize(String json, TypeReference<T> tRefer) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        return mapper.readValue(json, tRefer);
    }

    /**
     * 将 json 流反序列化成对象
     */
    public <T> T deserialize(InputStream jsonStream, Class<T> tClass) {
        if (jsonStream == null) {
            return null;
        }

        return mapper.readValue(jsonStream, tClass);
    }

    /**
     * 将 json 流反序列化成对象
     */
    public <T> T deserialize(InputStream jsonStream, TypeReference<T> tRefer) {
        if (jsonStream == null) {
            return null;
        }

        return mapper.readValue(jsonStream, tRefer);
    }
}
