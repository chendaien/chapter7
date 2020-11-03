package org.smart4j.chapter7.constant;

public enum ConfigConstant {

    CONFIG_FILE("smart.properties"),

    JDBC_DRIVER("smart.framework.jdbc.driver"),

    JDBC_URL("smart.framework.jdbc.url"),

    JDBC_USERNAME("smart.framework.jdbc.username"),

    JDBC_PASSWORD("smart.framework.jdbc.password"),

    APP_BASE_PACKAGE("smart.framework.app.base_package"),

    APP_JSP_PATH("smart.framework.app.jsp_path"),

    APP_ASSET_PATH("smart.framework.asset_path");

    private String statusMsg;

    ConfigConstant(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getStatusMsg(){
        return this.statusMsg;
    }
}
