package org.smart4j.chapter7.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter7.util.CollectionUtil;
import org.smart4j.chapter7.util.PropsUtil;
import org.smart4j.chapter7.util.CollectionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final ThreadLocal<Connection> THREAD_LOCAL = new ThreadLocal<Connection>();

    private static final BasicDataSource DATA_SOURCE;

    private static final String  DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        Properties properties = PropsUtil.loadProps("smart.properties");
        DRIVER = properties.getProperty("smart.framework.jdbc.driver");
        URL = properties.getProperty("smart.framework.jdbc.url");
        USERNAME = properties.getProperty("smart.framework.jdbc.username");
        PASSWORD = properties.getProperty("smart.framework.jdbc.password");
        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
    }

    /**
     * 单表或多表通用查询
     */
    public static List<Map<String,Object>> executeQuery(String sql,Object...parmas){
        List<Map<String,Object>> resutl = null;
        try{
            Connection connection = getConnection();
            resutl=QUERY_RUNNER.query(connection,sql,new MapListHandler(),parmas);
        }catch (SQLException e){
            LOGGER.error("execute sql failure",e);
            throw new RuntimeException(e);
        }finally {
            closeConnectoin();
        }
        return  resutl;
    }

    /**
     * 执行更新语句(包括insert、update、delete)
     */
    public static int executeUpdate(String sql,Object...params){
        int rows = 0 ;
        try {
            Connection connection = getConnection();
            rows = QUERY_RUNNER.update(connection,sql,params);
        }catch (SQLException e){
            LOGGER.error("execute update sql failure",e);
            throw new RuntimeException(e);
        }finally {
            closeConnectoin();
        }
        return rows;
    }

    /**
     * 插入实体
     */
    public static <T>boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("not insert entity : fieldMap is empty");
            return false;
        }
        String sql = "insert into " + getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values =  new StringBuilder("(");
        for (String fieldName:fieldMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql += columns + " values " + values;
        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql,params)==1;
    }

    /**
     * 更新实体
     */
    public static <T>boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("not update entity : fieldMap is empty");
            return false;
        }
        String sql = "update "+getTableName(entityClass)+" set ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName:fieldMap.keySet()) {
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0,columns.lastIndexOf(", "))+" where id = ? ";
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        return executeUpdate(sql,params)==1;
    }

    /**
     * 删除实体
     */
    public static <T>boolean deleteEntity(Class<T> entityClass,long id){
        String sql = "delete from "+getTableName(entityClass)+" where id=? ";
        return executeUpdate(sql,id)==1;
    }

    /**
     * 获取单个实体
     */
    public static <T>T getEntity(Class<T> entityClass,long id){
        T entity = null;
        try{
            Connection connection = getConnection();
            String sql = "select * from " + getTableName(entityClass) +" where id =?";
            entity = QUERY_RUNNER.query(connection,sql,new BeanHandler<T>(entityClass),id);
        }catch (SQLException e){
            LOGGER.error("query entity failure",e);
            throw new RuntimeException(e);
        }finally {
            closeConnectoin();
        }
        return entity;
    }

    public static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }

    /**
     * 查询实体列表
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params){
        List<T> entityList = null;
        try{
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),params);
        }catch (SQLException e){
            LOGGER.error("query entity list failure",e);
            throw  new RuntimeException(e);
        }finally {
            closeConnectoin();
        }
        return entityList;
    }

    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection(){
        Connection conn = THREAD_LOCAL.get();
        if(conn == null){
            try {
                conn = DATA_SOURCE.getConnection();
            }catch (SQLException e){
                LOGGER.error("get Connection failure ",e);
                throw new RuntimeException(e);
            }finally {
                THREAD_LOCAL.set(conn);
            }
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnectoin(){
        Connection connection = THREAD_LOCAL.get();
        if(connection != null){
            try{
                connection.close();
            }catch (SQLException e){
                LOGGER.error(" close connection failure ",e);
                throw new RuntimeException(e);
            }finally {
                THREAD_LOCAL.remove();
            }
        }
    }

    /**
     * 开启事务
     */
    public static void beginTransaction(){
        Connection conn = getConnection();
        if(conn != null){
            try {
                conn.setAutoCommit(false);
            }catch (SQLException e){
                LOGGER.error(" begin transaction failure ",e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(){
        Connection conn = getConnection();
        if(conn != null){
            try {
                conn.commit();
            }catch (SQLException e){
                LOGGER.error(" commit transaction failure ",e);
                throw new RuntimeException(e);
            }finally {
                closeConnectoin();
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
        Connection connection = getConnection();
        if(connection != null){
            try {
                connection.rollback();
            }catch (SQLException e){
                LOGGER.error(" rollback transaction failure ",e);
                throw new RuntimeException(e);
            }finally {
                closeConnectoin();
            }
        }

    }

}
