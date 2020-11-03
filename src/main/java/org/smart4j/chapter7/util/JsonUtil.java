package org.smart4j.chapter7.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON工具类
 */
public final class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private  static  final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将POJO转为JSON
     */
    public static <T>String toJson(T obj){
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        }catch (Exception e){
            LOGGER.error("convert pojo to json failure ",e);
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * 将JSON转为POJO
     */
    public  static <T>T fromJson(String json,Class<T> objClass){
        T obj;
        try{
            obj = OBJECT_MAPPER.readValue(json,objClass);
        }catch (Exception e){
            LOGGER.error(" convert json to pojo failure ",e);
            throw new RuntimeException(e);
        }
        return obj;
    }
}
