package dev.xtoy.common.file;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 文件工具类
 */
@Slf4j
public final class XFileUtils {
    private static final String EMPTY = "";

    /**
     * 获取基础 url（去除 query 和 fragment）
     */
    public static String getBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return EMPTY;
        }

        try {
            URI uri = new URI(url);
            URI clean = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
            return clean.toString();
        } catch (URISyntaxException ex) {
            log.warn("Get base url failed. url: {}", url, ex);
            return EMPTY;
        }
    }
}
