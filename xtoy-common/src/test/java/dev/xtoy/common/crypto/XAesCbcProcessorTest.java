package dev.xtoy.common.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XAesCbcProcessorTest {
    private static final XAesCbcProcessor PROCESSOR = XAesCbcProcessor.DEFAULT;

    @Test
    void genSecretKey() {
        byte[] bytes = PROCESSOR.genSecretKey(128);
        assertEquals(16, bytes.length);
    }

    @Test
    void urlBase64Encode() {
        String originalContent = "Hello, World!";
        byte[] key = PROCESSOR.genSecretKey(128);

        String encrypted = PROCESSOR.encryptToUrlBase64(originalContent, key);
        assertNotNull(encrypted);
        assertNotEquals(originalContent, encrypted);

        String decryptedContent = PROCESSOR.decryptUrlBase64(encrypted, key);
        assertEquals(originalContent, decryptedContent);
    }

    @Test
    void byteEncode() {
        String originalContent = "Hello, World!";
        byte[] originalContentBytes = originalContent.getBytes();
        byte[] key = PROCESSOR.genSecretKey(128);

        byte[] encrypted = PROCESSOR.encrypt(originalContentBytes, key);
        assertNotNull(encrypted);
        assertNotEquals(originalContentBytes, encrypted);

        byte[] decryptedContentBytes = PROCESSOR.decrypt(encrypted, key);
        String decryptedContent = new String(decryptedContentBytes);
        assertEquals(originalContent, decryptedContent);
    }
}