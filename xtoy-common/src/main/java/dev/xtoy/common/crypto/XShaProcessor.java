package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * SHA 签名处理器
 */
@Slf4j
public class XShaProcessor {
    public static final XShaProcessor SHA256_RSA = new XShaProcessor("SHA256withRSA");

    private static final XUrlBase64Processor URL_BASE_64_PROCESSOR = XUrlBase64Processor.INSTANCE;
    private static final XPemProcessor PEM_PROCESSOR = XPemProcessor.INSTANCE;

    private final String algorithm;

    public XShaProcessor(String algorithm) {
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
            PrivateKey privateKey = PEM_PROCESSOR.decodePrivateKey(privateKeyPem);
            byte[] signature = sign(data.getBytes(StandardCharsets.UTF_8), privateKey);
            return URL_BASE_64_PROCESSOR.encodeToString(signature, true);
        } catch (GeneralSecurityException ex) {
            log.error("RSA sign error.", ex);
            return StringUtils.EMPTY;
        }
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
            PublicKey publicKey = PEM_PROCESSOR.decodePublicKey(publicKeyPem);
            return verify(data.getBytes(StandardCharsets.UTF_8), sigBytes, publicKey);
        } catch (Exception ex) {
            log.error("RSA verify error.", ex);
            return false;
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
}
