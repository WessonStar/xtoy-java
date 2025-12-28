package dev.xtoy.common.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XTotpProcessorTest {
    private static final XTotpProcessor PROCESSOR = XTotpProcessor.TOTP_SHA1;

    @Test
    void verify() {
        byte[] secretKey = PROCESSOR.generateSecretKey();
        String code = PROCESSOR.generateCode(secretKey);
        assertTrue(PROCESSOR.verify(secretKey, code));
    }

    @Test
    void generateSecretKey() {
        byte[] secretKey = PROCESSOR.generateSecretKey();
        assertNotNull(secretKey);
        assertEquals(20, secretKey.length);
    }

    @Test
    void testGenerateSecretKey() {
        byte[] secretKey = PROCESSOR.generateSecretKey(32);
        assertNotNull(secretKey);
        assertEquals(32, secretKey.length);
    }

    @Test
    void generateCode() {
        byte[] secretKey = PROCESSOR.generateSecretKey();
        String code = PROCESSOR.generateCode(secretKey);
        assertNotNull(code);
        assertTrue(code.length() == 6 || code.length() == 8);
    }

    @Test
    void generateUri() {
        byte[] secretKey = PROCESSOR.generateSecretKey();
        String uri = PROCESSOR.generateUri(secretKey, "TestAccount", "TestIssuer");
        assertNotNull(uri);
        assertTrue(uri.startsWith("otpauth://totp/"));
    }
}