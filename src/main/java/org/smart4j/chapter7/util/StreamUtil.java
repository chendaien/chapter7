package org.smart4j.chapter7.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 流操作工具类
 */
public final class StreamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

    /**
     * 从输入流中获取字符串
     */
    public static String getString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedInputStream.readLine()) != null){
                stringBuilder.append(line);
            }
        }catch (IOException e){
            LOGGER.error("read InputStream failure",e);
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }


}
