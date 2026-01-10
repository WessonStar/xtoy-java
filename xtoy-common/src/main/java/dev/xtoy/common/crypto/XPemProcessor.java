package dev.xtoy.common.crypto;

import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * PEM (Privacy-Enhanced Mail) 处理器
 */
@Slf4j
public class XPemProcessor {
    public static final XPemProcessor INSTANCE = new XPemProcessor();

    private static final String ALGORITHM_RSA = "RSA";
    private static final XBase64Processor BASE_64_PROCESSOR = XBase64Processor.INSTANCE;

    public XPemProcessor() {

    }

    /**
     * 将 PrivateKey 导出为 PKCS#8 PEM 字符串
     */
    public String encode(PrivateKey privateKey) {
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
    public String encode(PublicKey publicKey) {
        String base64 = BASE_64_PROCESSOR.encodeToString(publicKey.getEncoded());
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        chunkAppend(base64, sb);
        sb.append("-----END PUBLIC KEY-----\n");
        return sb.toString();
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
    public PrivateKey decodePrivateKey(String pem) {
        try {
            String base64 = pem
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = BASE_64_PROCESSOR.decode(base64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            return kf.generatePrivate(spec);
        } catch (Exception ex) {
            log.error("Error decoding PrivateKey from PEM.", ex);
            return null;
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
    public PublicKey decodePublicKey(String pem) {
        try {
            String base64 = pem
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = BASE_64_PROCESSOR.decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            return kf.generatePublic(spec);
        } catch (Exception ex) {
            log.error("Error decoding PublicKey from PEM.", ex);
            return null;
        }
    }

    /**
     * 将长 base64 字符串按 64 字符换行，符合 PEM 风格
     */
    private void chunkAppend(String base64, StringBuilder sb) {
        final int WIDTH = 64;
        int index = 0;
        while (index < base64.length()) {
            int end = Math.min(index + WIDTH, base64.length());
            sb.append(base64, index, end).append('\n');
            index = end;
        }
    }
}
