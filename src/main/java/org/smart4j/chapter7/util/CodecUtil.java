package org.smart4j.chapter7.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 编解码工具列
 */
public final class CodecUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodecUtil.class);

    /**
     * URL解码
     * @param source
     * @return
     */
    public static String decodeURL(String source){
        String targetSource;
        try {
            targetSource = URLDecoder.decode(source,"UTF-8");
        }catch (Exception e){
            LOGGER.error("decode URL failure",e);
            throw new RuntimeException(e);
        }
        return targetSource;
    }

    /**
     * URL编码
     */
    public static String encodeURL(String source){
        String targetSource;
        try {
            targetSource = URLEncoder.encode(source,"UTF-8");
        }catch (Exception e){
            LOGGER.error("encode URL failure",e);
            throw new RuntimeException(e);
        }
        return targetSource;
    }

}
