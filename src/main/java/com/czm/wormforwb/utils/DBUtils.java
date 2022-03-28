package com.czm.wormforwb.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库操作工具类
 * @author Slience
 * @date 2022/3/13 14:47
 **/
public class DBUtils {

    /**
     * 执行指定sql
     * @param sql sql语句
     **/
    public static void executeSQL(String sql){
        try {
            // 获取数据库相关配置信息
            Properties props = Resources.getResourceAsProperties("application.properties");
            // jdbc 连接信息: 注: 现在版本的JDBC不需要配置driver，因为不需要Class.forName手动加载驱动
            String url = props.getProperty("spring.datasource.url");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");
            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//int
            // 关闭连接
            conn.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成次月动态日志表表名
     * @return 次月日志表名
     **/
    public static String getLogTableSQLForNextMonth(){
        return "CREATE IF NOT EXIST TABLE `user_dynamic_log_" + DateUtils.getNextMonthForLogDB() +
                "` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',\n" +
                "  `uid` char(30) DEFAULT NULL COMMENT '用户id',\n" +
                "  `m_name` varchar(50) DEFAULT NULL COMMENT '博主名称',\n" +
                "  `mid` char(30) DEFAULT NULL COMMENT '动态mid',\n" +
                "  `bid` char(10) DEFAULT NULL COMMENT '动态bid',\n" +
                "  `page_url` varchar(255) DEFAULT NULL COMMENT '原页面url',\n" +
                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
                "  `del` tinyint(1) DEFAULT NULL COMMENT '删除标记',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    }

    /**
     * 生成次月动态内容表名
     * @return 次月动态内容表名
     **/
    public static String getLogInfoTableForNextMonth(){
        return "CREATE IF NOT EXIST TABLE `dynamic_info_" + DateUtils.getNextMonthForLogDB() +
                "` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',\n" +
                "  `mid` char(30) NOT NULL COMMENT '动态mid',\n" +
                "  `m_content` text COMMENT '文字内容',\n" +
                "  `pics` text COMMENT '图片绝对路径',\n" +
                "  `del` tinyint(1) DEFAULT NULL COMMENT '删除标记',\n" +
                "  PRIMARY KEY (`id`) USING BTREE,\n" +
                "  UNIQUE KEY `mid` (`mid`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }

    /**
     * 获取当前使用的日志表
     * @return String 日志表名
     **/
    public static String getLogTableName(){
        return "user_dynamic_log_" + DateUtils.getNowMonthForLogDB();
    }

    /**
     * 获取上个月使用的日志表
     * @return String 日志表名
     **/
    public static String getLastMonthLogTableName(){
        return "user_dynamic_log_" + DateUtils.getLastMonthForDB();
    }

    /**
     * 获取当前的日志详细信息表名
     * @return String 日志详细信息表名
     **/
    public static String getLogInfoTableName(){
        return "dynamic_info_" + DateUtils.getNowMonthForLogDB();
    }

    /**
     * 获取当前的日志详细信息表名
     * @return String 日志详细信息表名
     **/
    public static String getLastMonthLogInfoTableName(){
        return "dynamic_info_" + DateUtils.getLastMonthForDB();
    }

    /**
     * 执行sql脚本
     * @param sqlFileName sql脚本文件
     **/
    public static void executeSQLSource(String sqlFileName){
        try {
            // 获取数据库相关配置信息
            Properties props = Resources.getResourceAsProperties("application.properties");

            // jdbc 连接信息: 注: 现在版本的JDBC不需要配置driver，因为不需要Class.forName手动加载驱动
            String url = props.getProperty("spring.datasource.url");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");
            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            // 创建ScriptRunner，用于执行SQL脚本
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);
            // 执行SQL脚本
            runner.runScript(Resources.getResourceAsReader("sql/" + sqlFileName + ".sql"));
            // 关闭连接
            conn.close();
            // 若成功，打印提示信息
            System.out.println("====== SUCCESS ======");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
