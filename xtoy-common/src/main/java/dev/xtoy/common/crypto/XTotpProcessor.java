package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * TOTP (Time-based One-Time Password) 处理器
 */
@Slf4j
public final class XTotpProcessor {
    public static final XTotpProcessor TOTP_SHA1 =
            new XTotpProcessor("HmacSHA1", "SHA1", 6, 30);

    private static final XBase32Processor BASE_32_PROCESSOR = XBase32Processor.INSTANCE;

    private final String hmacAlgorithm;
    private final String algorithmLabel;
    private final int digits;
    private final int timeStepSeconds;

    public XTotpProcessor(String hmacAlgorithm, String algorithmLabel, int digits, int timeStepSeconds) {
        if (digits != 6 && digits != 8) {
            throw new IllegalArgumentException("Digits must be 6 or 8.");
        }

        this.hmacAlgorithm = hmacAlgorithm;
        this.algorithmLabel = algorithmLabel;
        this.digits = digits;
        this.timeStepSeconds = timeStepSeconds;
    }

    /**
     * 验证 TOTP 码
     * @param secretKeyBytes 密钥字节
     * @param code TOTP 码
     * @return true if valid
     */
    public boolean verify(byte[] secretKeyBytes, String code) {
        String generatedCode = generateCode(secretKeyBytes);
        return generatedCode.equals(code);
    }

    /**
     * 生成随机密钥 160 位 (20 字节)
     */
    public byte[] generateSecretKey() {
        return generateSecretKey(20);
    }

    /**
     * 生成随机密钥
     */
    public byte[] generateSecretKey(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    /**
     * 生成 TOTP 码
     */
    public String generateCode(byte[] secretKeyBytes) {
        long currentInterval = System.currentTimeMillis() / 1000L / timeStepSeconds;
        try {
            Mac mac = Mac.getInstance(hmacAlgorithm);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, hmacAlgorithm);
            mac.init(keySpec);

            byte[] timeBytes = new byte[8];
            long value = currentInterval;
            for (int i = 8; i-- > 0; value >>>= 8) {
                timeBytes[i] = (byte) value;
            }

            byte[] hash = mac.doFinal(timeBytes);
            int offset = hash[hash.length - 1] & 0xf;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xff);
            }
            truncatedHash &= 0x7fffffff;
            truncatedHash %= (long)Math.pow(10, digits);
            return String.format("%0" + digits + "d", truncatedHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("TOTP generate error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 生成 URL
     */
    public String generateUri(byte[] secretKeyBytes, String accountName, String issuer) {
        String encodedAccount = encodeSegment(accountName);
        String encodedIssuer = encodeSegment(issuer);
        String base32Secret = BASE_32_PROCESSOR.encodeToString(secretKeyBytes);
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=%s&digits=%d&period=%d",
                encodedIssuer, encodedAccount, base32Secret, encodedIssuer, algorithmLabel, digits, timeStepSeconds);
    }

    /**
     * URL 编码段
     */
    private static String encodeSegment(String segment) {
        if (segment == null) return "";
        return URLEncoder.encode(segment, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
