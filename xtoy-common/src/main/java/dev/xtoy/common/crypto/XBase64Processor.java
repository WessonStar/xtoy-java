package dev.xtoy.common.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 处理器
 */
public final class XBase64Processor {
    public static final XBase64Processor INSTANCE = new XBase64Processor();

    /**
     * 编码为 Base64 字符串
     */
    public String encodeToString(byte[] data) {
        return new String(Base64.getEncoder().encode(data),
                StandardCharsets.UTF_8);
    }

    /**
     * 解码 Base64 字符串
     */
    public byte[] decode(String base64Str) {
        return Base64.getDecoder()
                .decode(base64Str.getBytes(StandardCharsets.UTF_8));
    }
}
