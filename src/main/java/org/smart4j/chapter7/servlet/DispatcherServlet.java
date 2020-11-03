package org.smart4j.chapter7.servlet;

import org.apache.commons.lang3.StringUtils;
import org.smart4j.chapter7.bean.Data;
import org.smart4j.chapter7.bean.Handler;
import org.smart4j.chapter7.bean.Param;
import org.smart4j.chapter7.bean.View;
import org.smart4j.chapter7.constant.ConfigConstant;
import org.smart4j.chapter7.helper.BeanHelper;
import org.smart4j.chapter7.helper.ConfigHelper;
import org.smart4j.chapter7.helper.ControllerHelper;
import org.smart4j.chapter7.init.HelperLoader;
import org.smart4j.chapter7.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化Helper类
        HelperLoader.init();
        //获取ServletContext对象(用于注册Servlet)
        ServletContext servletContext = servletConfig.getServletContext();
        //获取注册JSP的Servlet
        ServletRegistration jspServletRegistration = servletContext.getServletRegistration("jsp");
        jspServletRegistration.addMapping(ConfigConstant.APP_JSP_PATH.getStatusMsg()+"*");
        //注册静态资源的默认servlet
        ServletRegistration defaultServletRegistration = servletContext.getServletRegistration("default");
        defaultServletRegistration.addMapping(ConfigConstant.APP_ASSET_PATH+"*");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求方法与请求路径
        String requestMethod = req.getMethod();
        String requestPath = req.getPathInfo();
        //获取Action处理器
        Handler handler = ControllerHelper.getHandler(requestMethod,requestPath);
        if(handler != null){
            //获取Controller类与Bean实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            //创建请求参数对象
            Map<String,Object> paramMap = new HashMap<String,Object>();
            Enumeration<String>  requestParams = req.getParameterNames();
            while (requestParams.hasMoreElements()){
                String paramName = requestParams.nextElement();
                String paramValue = req.getParameter(paramName);
                paramMap.put(paramName,paramValue);
            }
            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if(StringUtil.isNotEmpty(body)){
                String[] paramStrs = StringUtils.split(body,"&");
                if(ArrayUtil.isNotEmpty(paramStrs)){
                    for (String paramStr:paramStrs) {
                        String[] array = StringUtils.split(paramStr,"=");
                        if(ArrayUtil.isNotEmpty(array)&&array.length==2){
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName,paramValue);
                        }
                    }
                }
            }
            Param params = new Param(paramMap);
            Method actionMethod = handler.getActionMethod();
            Object result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,params);
            if(result instanceof View){
                //返回JSP
                View view  = (View)result;
                String path = view.getPath();
                if(StringUtil.isNotEmpty(path)){
                    if(path.startsWith("/")){
                        resp.sendRedirect(req.getContextPath()+path);
                    }else{
                        Map<String,Object> model = view.getModel();
                        for(Map.Entry<String,Object> entry:model.entrySet()){
                                req.setAttribute(entry.getKey(),entry.getValue());
                        }
                        req.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(req,resp);
                    }
                }
            }else if(result instanceof Data){
                //返回JSON数据
                Data data = (Data)result;
                Object model = data.getModel();
                if(model != null){
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter printWriter = resp.getWriter();
                    printWriter.print(JsonUtil.toJson(model));
                    printWriter.flush();
                    printWriter.close();
                }
            }
        }
    }
}
