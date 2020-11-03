package org.smart4j.chapter7.util;

/**
 * 转型操作工具类
 */
public class CastUtil {

    /**
     * 转为String型
     */
    public static String castString(Object obj){
        return CastUtil.castString(obj,"");
    }

    /**
     * 转为String型(提供默认值)
     */
    public static String castString(Object obj,String defaultValue){
        return obj != null?String.valueOf(obj):defaultValue;
    }

    /**
     * 转为double型
     */
    public static double castDouble(Object obj){
        return CastUtil.castDouble(obj,0);
    }

    /**
     * 转为double型(提供默认值)
     */
    public static double castDouble(Object obj,double defaultValue){
        double value = defaultValue;
        if(obj!=null){
            String doubleStr = castString(obj);
            if(StringUtil.isNotEmpty(doubleStr)){
                try {
                    value = Double.parseDouble(doubleStr);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    /**
     * 转为long型
     */
    public static long castLong(Object obj){
        return CastUtil.castLong(obj,0);
    }

    /**
     * 转为long型(可提供默认值)
     */
    public static long castLong(Object obj,long defaultValue){
        long value =  defaultValue;
        if(obj != null){
            String longStr =  castString(obj);
            if(StringUtil.isNotEmpty(longStr)){
                try {
                    value = Long.parseLong(longStr);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    /**
     * 转为int型
     */
    public static int castInt(Object obj){
        return castInt(obj,0);
    }

    /**
     * 转为Int型(可提供默认值)
     */
    public static int castInt(Object obj,int defaultValue){
        int value = defaultValue;
        if(obj != null){
            String intStr = castString(obj);
            if(StringUtil.isNotEmpty(intStr)){
                try {
                    value = Integer.parseInt(intStr);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    /**
     * 转为boolean型
     */
    public static boolean castBoolean(Object obj){
        return CastUtil.castBoolean(obj,false);
    }

    /**
     * 转为boolean型(可提供默认值)
     */
    public static boolean castBoolean(Object obj,boolean defaultValue){
        boolean value = defaultValue;
        if(obj != null){
            value = Boolean.parseBoolean(castString(obj));
        }
        return value;
    }
}
