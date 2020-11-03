package org.smart4j.chapter7.helper;

import org.smart4j.chapter7.constant.ConfigConstant;
import org.smart4j.chapter7.util.PropsUtil;

import java.util.Properties;

public final class ConfigHelper {

    private final static Properties properties = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE.getStatusMsg());

    /**
     * 获取JDBC驱动
     */
    public static String getJdbcDriver(){
        return PropsUtil.getString(properties,ConfigConstant.JDBC_DRIVER.getStatusMsg());
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl(){
        return PropsUtil.getString(properties,ConfigConstant.JDBC_URL.getStatusMsg());
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername(){
        return PropsUtil.getString(properties,ConfigConstant.JDBC_USERNAME.getStatusMsg());
    }

    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword(){
        return PropsUtil.getString(properties,ConfigConstant.JDBC_PASSWORD.getStatusMsg());
    }

    /**
     * 获取 应用基础包名
     */
    public static String getAppBasePackage(){
        return PropsUtil.getString(properties,ConfigConstant.APP_BASE_PACKAGE.getStatusMsg());
    }

    /**
     * 获取 JSP路径
     */
    public static String getAppJspPath(){
        return PropsUtil.getString(properties,ConfigConstant.APP_JSP_PATH.getStatusMsg(),"/WEB-INF/view/");
    }

    /**
     * 获取 应用静态资源路径
     */
    public static String getAppAssetPath(){
        return PropsUtil.getString(properties,ConfigConstant.APP_ASSET_PATH.getStatusMsg(),"/asset/");
    }
}
