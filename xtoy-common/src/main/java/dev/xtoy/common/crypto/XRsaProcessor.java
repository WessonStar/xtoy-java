package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;

/**
 * RSA 处理器
 */
@Slf4j
public class XRsaProcessor {
    public static XRsaProcessor RSA_ECB_PKCS1 = new XRsaProcessor("RSA/ECB/PKCS1Padding");

    private static final String ALGORITHM_RSA = "RSA";
    private static final XPemProcessor PEM_PROCESSOR = XPemProcessor.INSTANCE;

    private final String transformation;

    public XRsaProcessor(String transformation) {
        this.transformation = transformation;
    }

    /**
     * 使用 PEM 格式的公钥加密数据
     */
    public byte[] encryptWithPem(byte[] data, String publicKeyPem) {
        PublicKey publicKey = PEM_PROCESSOR.decodePublicKey(publicKeyPem);
        if (publicKey == null) {
            throw new IllegalArgumentException("Invalid public key PEM.");
        }

        return encrypt(data, publicKey);
    }

    /**
     * 使用 PEM 格式的私钥解密数据
     */
    public byte[] decryptWithPem(byte[] encryptedData, String privateKeyPem) {
        PrivateKey privateKey = PEM_PROCESSOR.decodePrivateKey(privateKeyPem);
        if (privateKey == null) {
            throw new IllegalArgumentException("Invalid private key PEM.");
        }

        return decrypt(encryptedData, privateKey);
    }

    /**
     * 加密数据
     */
    public byte[] encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            log.error("RSA encrypt error.", ex);
            return new byte[0];
        }
    }

    /**
     * 解密数据
     */
    public byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception ex) {
            log.error("RSA decrypt error.", ex);
            return new byte[0];
        }
    }

    /**
     * 生成 RSA 密钥对
     *
     * @param keySize 常见 2048 或 4096
     * @return KeyPair
     */
    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
            kpg.initialize(keySize);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error generating RSA KeyPair.", ex);
            return null;
        }
    }
}
