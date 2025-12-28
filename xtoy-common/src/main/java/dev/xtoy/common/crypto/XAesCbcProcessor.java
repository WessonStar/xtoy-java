package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * AES-CBC 加解密处理器
 */
@Slf4j
public class XAesCbcProcessor {
    public static final XAesCbcProcessor DEFAULT = new XAesCbcProcessor(16);

    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_AES_CBC = "AES/CBC/PKCS5Padding";
    private static final XUrlBase64Processor URL_BASE_64_PROCESSOR = XUrlBase64Processor.INSTANCE;

    private final int ivSize;

    public XAesCbcProcessor(int ivSize) {
        this.ivSize = ivSize;
    }

    /**
     * 生成 AES 密钥
     * AES只允许 128, 192, 256 位的密钥。字符长度是 16, 24, 32
     */
    public byte[] genSecretKey(int keySize) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM_AES);
            keyGen.init(keySize, new SecureRandom());
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception ex) {
            log.error("Generate AES secret key error.", ex);
            return new byte[0];
        }
    }

    /**
     * AES 加密
     */
    public String encryptToUrlBase64(String content, byte[] key) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }

        byte[] bytes = encrypt(content.getBytes(StandardCharsets.UTF_8), key);
        return URL_BASE_64_PROCESSOR.encodeToString(bytes, true);
    }

    /**
     * AES 解密
     */
    public String decryptUrlBase64(String content, byte[] key) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }

        byte[] bytes = decrypt(URL_BASE_64_PROCESSOR.decode(content), key);
        return new String(bytes);
    }

    /**
     * AES 加密
     */
    public byte[] encrypt(byte[] data, byte[] key) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES_CBC);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_AES);
            byte[] iv = new byte[ivSize];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParams);
            byte[] encrypted = cipher.doFinal(data);
            byte[] result = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, result, 0, ivSize);
            System.arraycopy(encrypted, 0, result, ivSize, encrypted.length);
            return result;
        } catch (Exception ex) {
            log.error("AES-CBC encrypt error.", ex);
            return new byte[0];
        }
    }

    /**
     * AES 解密
     */
    public byte[] decrypt(byte[] data, byte[] key) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES_CBC);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_AES);
            byte[] iv = new byte[ivSize];
            System.arraycopy(data, 0, iv, 0, ivSize);
            byte[] encrypted = new byte[data.length - ivSize];
            System.arraycopy(data, ivSize, encrypted, 0, encrypted.length);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);
            return cipher.doFinal(encrypted);
        } catch (Exception ex) {
            log.error("AES-CBC decrypt error.", ex);
            return new byte[0];
        }
    }
}
