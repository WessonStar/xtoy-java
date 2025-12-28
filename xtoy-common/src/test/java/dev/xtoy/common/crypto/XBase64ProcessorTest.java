package dev.xtoy.common.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XBase64ProcessorTest {
    private static final XBase64Processor PROCESSOR = XBase64Processor.INSTANCE;

    @Test
    void encodeAndDecode() {
        String original = "Hello, World!";
        String encoded = PROCESSOR.encodeToString(original.getBytes());
        byte[] decoded = PROCESSOR.decode(encoded);
        assertArrayEquals(original.getBytes(), decoded);
    }
}