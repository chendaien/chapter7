package org.smart4j.chapter7.helper;

import org.smart4j.chapter7.annotation.Action;
import org.smart4j.chapter7.bean.Handler;
import org.smart4j.chapter7.bean.Request;
import org.smart4j.chapter7.util.ArrayUtil;
import org.smart4j.chapter7.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 */
public final class ControllerHelper {

    /**
     * 用于存放请求与处理器映射关系的Map(简称：Action Map)
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有的Controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if(CollectionUtil.isNotEmpty(controllerClassSet)){
            //遍历这些Controller类
            for(Class<?> controllerClass:controllerClassSet){
                //获取Controller类中定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if(ArrayUtil.isNotEmpty(methods)){
                    //遍历当前Controller的方法
                    for(Method method:methods){
                        //判断当前方法是否包含Action注解
                        if(method.isAnnotationPresent(Action.class)){
                            //从Action中获取URL映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            //验证URL规则
                            if(mapping.matches("\\w+:/\\w*")){
                                String[] array = mapping.split(":");
                                if(ArrayUtil.isNotEmpty(array)&&array.length==2){
                                    //获取请求方法与请求路径
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod,requestPath);
                                    Handler handler = new Handler(controllerClass,method);
                                    ACTION_MAP.put(request,handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取Handler
     */
    public static Handler getHandler(String requestMethod,String requesetPath){
        Request request = new Request(requestMethod,requesetPath);
        return ACTION_MAP.get(request);
    }

}
