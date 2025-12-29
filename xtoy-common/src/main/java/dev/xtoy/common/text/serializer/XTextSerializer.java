package dev.xtoy.common.text.serializer;

import tools.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 文本序列化器接口
 */
public interface XTextSerializer {
    /**
     * 将文本反序列化成对象列表
     */
    <T> List<T> deserializeList(String text, Class<T> tClass);

    /**
     * 将文本流反序列化成字符串键值对
     */
    Map<String, String> deserializeStringMap(InputStream textStream);

    /**
     * 将对象序列化成文本
     */
    <T> String serialize(T xObject);

    /**
     * 将文本反序列化成对象
     */
    <T> T deserialize(String text, Class<T> tClass);

    /**
     * 将文本反序列化成对象
     */
    <T> T deserialize(String text, TypeReference<T> tRefer);

    /**
     * 将文本流反序列化成对象
     */
    <T> T deserialize(InputStream textStream, Class<T> tClass);

    /**
     * 将文本流反序列化成对象
     */
    <T> T deserialize(InputStream textStream, TypeReference<T> tRefer);

    /**
     * 获取文本类型
     */
    XTextTypeEnum getTextType();
}
