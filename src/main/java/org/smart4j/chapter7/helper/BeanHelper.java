package org.smart4j.chapter7.helper;

import org.smart4j.chapter7.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BeanHelper {

    /**
     * 定义Bean映射(用于存放bean类与bean实例的关系)
     */
    private static final Map<Class<?>,Object> BEAN_MAP= new HashMap<Class<?>,Object>();

    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> beanClass:beanClassSet) {
            BEAN_MAP.put(beanClass, ReflectionUtil.newInstance(beanClass));
        }
    }

    /**
     * 获取Bean映射
     */
    public static Map<Class<?>,Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     * 获取Bean实例
     */
    public static <T>T getBean(Class<T> cls){
        if(!BEAN_MAP.containsKey(cls)){
            throw new RuntimeException("can not get Bean by class");
        }
        return (T) BEAN_MAP.get(cls);
    }

    /**
     * 设置Bean实例
     */
    public static void setBean(Class<?> cls,Object obj){
        BEAN_MAP.put(cls,obj);
    }

}
