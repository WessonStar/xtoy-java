package dev.xtoy.common.text;

/**
 * 字符串工具类
 */
public final class XStringUtils {
    public static final String EMPTY = "";
    public static final String SLASH = "/";

    /**
     * 移除前缀
     * @param strVal 字符串值
     * @param prefix 前缀
     * @return 处理后的字符串
     */
    public static String removePrefix(String strVal, String prefix) {
        return removePrefix(strVal, prefix, 0);
    }

    /**
     * 移除前缀
     * @param strVal 字符串值
     * @param prefix 前缀
     * @param offset 偏移量
     * @return 处理后的字符串
     */
    public static String removePrefix(String strVal, String prefix, int offset) {
        if (strVal == null || prefix == null) {
            return strVal;
        }
        if (strVal.startsWith(prefix)) {
            return strVal.substring(prefix.length() + offset);
        }
        return strVal;
    }

    /**
     * 优化字符串以便打印
     */
    public static String optimizeToPrint(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        int length = source.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = source.charAt(i);
            switch (c) {
                case '\n', '\t', '\r':
                    continue;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
