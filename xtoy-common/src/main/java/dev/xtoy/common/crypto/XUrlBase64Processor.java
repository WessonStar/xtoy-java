package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Url Base64 处理器
 */
@Slf4j
public final class XUrlBase64Processor {
    public static final XUrlBase64Processor INSTANCE = new XUrlBase64Processor();

    /**
     * 编码为 Base64 字符串
     */
    public String encodeToString(byte[] data, boolean noPadding) {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        if (noPadding) {
            encoder = encoder.withoutPadding();
        }
        return new String(encoder.encode(data), StandardCharsets.UTF_8);
    }

    /**
     * 解码 Base64 字符串
     */
    public byte[] decode(String base64Str) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return decoder.decode(base64Str.getBytes(StandardCharsets.UTF_8));
    }
}
