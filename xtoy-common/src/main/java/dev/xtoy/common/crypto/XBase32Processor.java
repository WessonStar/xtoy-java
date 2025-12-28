package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;

/**
 * Base32 处理器
 */
@Slf4j
public class XBase32Processor {
    public static final XBase32Processor INSTANCE = new XBase32Processor();

    private final Base32 ENCODER = new Base32();

    /**
     * 编码为 Base32 字符串
     */
    public String encodeToString(byte[] data) {
        return ENCODER.encodeToString(data);
    }

    /**
     * 解码 Base32 字符串
     */
    public byte[] decode(String base32Str) {
        return ENCODER.decode(base32Str);
    }
}
