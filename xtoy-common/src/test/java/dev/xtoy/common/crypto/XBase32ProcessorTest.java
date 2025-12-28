package dev.xtoy.common.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XBase32ProcessorTest {
    private static final XBase32Processor PROCESSOR = XBase32Processor.INSTANCE;

    @Test
    void encodeAndDecode() {
        String original = "Hello, World!";
        byte[] originalBytes = original.getBytes();
        String encoded = PROCESSOR.encodeToString(originalBytes);
        byte[] decoded = PROCESSOR.decode(encoded);
        assertArrayEquals(originalBytes, decoded);
    }
}