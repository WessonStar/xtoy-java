package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 签名处理器
 */
@Slf4j
public class XRsaProcessor {
    public static final XRsaProcessor RSA_SHA256 = new XRsaProcessor("SHA256withRSA");

    private static final String ALGORITHM_RSA = "RSA";
    private static final XBase64Processor BASE_64_PROCESSOR = XBase64Processor.INSTANCE;
    private static final XUrlBase64Processor URL_BASE_64_PROCESSOR = XUrlBase64Processor.INSTANCE;

    private final String algorithm;

    public XRsaProcessor(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 使用 RSA 签名，返回 Base64 编码的签名字符串。
     * @param data 原文（字符串）
     * @param privateKeyPem 私钥
     * @return Base64 签名
     */
    public String signToUrlBase64(String data, String privateKeyPem) {
        try {
            PrivateKey privateKey = loadPrivateKeyFromPem(privateKeyPem);
            byte[] signature = sign(data.getBytes(StandardCharsets.UTF_8), privateKey);
            return URL_BASE_64_PROCESSOR.encodeToString(signature, true);
        } catch (GeneralSecurityException ex) {
            log.error("RSA sign error.", ex);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 使用 RSA 签名，返回原始签名字节。
     *
     * @param dataBytes 原文字节
     * @param privateKey 私钥
     * @return 签名字节
     */
    public byte[] sign(byte[] dataBytes, PrivateKey privateKey) throws GeneralSecurityException {
        Signature sig = Signature.getInstance(algorithm);
        sig.initSign(privateKey);
        sig.update(dataBytes);
        return sig.sign();
    }

    /**
     * 验证 Base64 签名
     *
     * @param data 原文（字符串）
     * @param base64Signature Base64 签名
     * @param publicKeyPem 公钥
     * @return true if valid
     */
    public boolean verifyUrlBase64(String data, String base64Signature, String publicKeyPem) {
        try {
            byte[] sigBytes = URL_BASE_64_PROCESSOR.decode(base64Signature);
            PublicKey publicKey = loadPublicKeyFromPem(publicKeyPem);
            return verify(data.getBytes(StandardCharsets.UTF_8), sigBytes, publicKey);
        } catch (Exception ex) {
            log.error("RSA verify error.", ex);
            return false;
        }
    }

    /**
     * 验证签名字节
     * @param dataBytes 原文字节
     * @param signatureBytes 签名字节
     * @param publicKey 公钥
     * @return true if valid
     */
    public boolean verify(byte[] dataBytes, byte[] signatureBytes, PublicKey publicKey) throws GeneralSecurityException {
        Signature sig = Signature.getInstance(algorithm);
        sig.initVerify(publicKey);
        sig.update(dataBytes);
        return sig.verify(signatureBytes);
    }

    /**
     * 生成 RSA KeyPair
     *
     * @param keySize 常见 2048 或 4096
     * @return KeyPair
     */
    public static KeyPair generateKeyPair(int keySize) throws GeneralSecurityException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        kpg.initialize(keySize);
        return kpg.generateKeyPair();
    }

    /**
     * 从 PEM（PKCS#8 私钥文本）加载 PrivateKey
     * <p>
     * 支持形如：
     * -----BEGIN PRIVATE KEY-----
     * base64...
     * -----END PRIVATE KEY-----
     * </p>
     * @param pem PKCS#8 PEM 字符串
     * @return PrivateKey
     */
    public static PrivateKey loadPrivateKeyFromPem(String pem) throws GeneralSecurityException {
        String base64 = pem
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = URL_BASE_64_PROCESSOR.decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
        return kf.generatePrivate(spec);
    }

    /**
     * 验证 PEM 公钥格式是否有效
     */
    public static boolean isValidPublicKeyPem(String pem) {
        try {
            loadPublicKeyFromPem(pem);
            return true;
        } catch (GeneralSecurityException ex) {
            return false;
        }
    }

    /**
     * 从 PEM（X.509 公钥文本）加载 PublicKey
     * <p>
     * 支持形如：
     * -----BEGIN PUBLIC KEY-----
     * base64...
     * -----END PUBLIC KEY-----
     * </p>
     * @param pem X.509 PEM 字符串
     * @return PublicKey
     */
    public static PublicKey loadPublicKeyFromPem(String pem) throws GeneralSecurityException {
        String base64 = pem
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = URL_BASE_64_PROCESSOR.decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
        return kf.generatePublic(spec);
    }

    /**
     * 将 PrivateKey 导出为 PKCS#8 PEM 字符串
     */
    public static String privateKeyToPem(PrivateKey privateKey) {
        String base64 = BASE_64_PROCESSOR.encodeToString(privateKey.getEncoded());
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PRIVATE KEY-----\n");
        chunkAppend(base64, sb);
        sb.append("-----END PRIVATE KEY-----\n");
        return sb.toString();
    }

    /**
     * 将 PublicKey 导出为 X.509 PEM 字符串
     */
    public static String publicKeyToPem(PublicKey publicKey) {
        String base64 = BASE_64_PROCESSOR.encodeToString(publicKey.getEncoded());
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        chunkAppend(base64, sb);
        sb.append("-----END PUBLIC KEY-----\n");
        return sb.toString();
    }

    /**
     * 将长 base64 字符串按 64 字符换行，符合 PEM 风格
     */
    private static void chunkAppend(String base64, StringBuilder sb) {
        final int WIDTH = 64;
        int index = 0;
        while (index < base64.length()) {
            int end = Math.min(index + WIDTH, base64.length());
            sb.append(base64, index, end).append('\n');
            index = end;
        }
    }
}
