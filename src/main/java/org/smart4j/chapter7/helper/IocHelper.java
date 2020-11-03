package org.smart4j.chapter7.helper;

import org.smart4j.chapter7.annotation.Inject;
import org.smart4j.chapter7.util.ArrayUtil;
import org.smart4j.chapter7.util.CollectionUtil;
import org.smart4j.chapter7.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入 助手类
 */
public final class IocHelper {

    static {
        //获取所有的Bean与Bean实例之间的映射关系
        Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpty(beanMap)){
            //遍历Bean Map
            for (Map.Entry<Class<?>,Object> beanEntry:beanMap.entrySet()) {
                //从beanEntry中获取bean类与bean实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                //获取Bean定义的所有成员变量
                Field[] fields = beanClass.getDeclaredFields();
                if(ArrayUtil.isNotEmpty(fields)){
                    for (Field field:fields) {
                        //判断当前成员变量是否带有 Inject注解
                        if(field.isAnnotationPresent(Inject.class)){
                            //从Bean Map中取出Bean Field对应的实例
                           Class<?> beanFieldClass = field.getType();
                           Object beanFieldInstance = beanMap.get(beanFieldClass);
                           if(beanFieldInstance!=null){
                               //通过发射初始化Bean Field值
                               ReflectionUtil.setField(beanInstance,field,beanFieldInstance);
                           }
                        }
                    }
                }
            }
        }

    }

}
