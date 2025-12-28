package dev.xtoy.common.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XUrlBase64ProcessorTest {
    private static final XUrlBase64Processor PROCESSOR = XUrlBase64Processor.INSTANCE;

    @Test
    void encodeAndDecode() {
        String original = "Hello, World!";
        byte[] originalBytes = original.getBytes();

        // 编码
        String encoded = PROCESSOR.encodeToString(originalBytes, true);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());

        // 解码
        byte[] decodedBytes = PROCESSOR.decode(encoded);
        assertArrayEquals(originalBytes, decodedBytes);
    }
}