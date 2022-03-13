package com.czm.wormforwb.utils;

import com.czm.wormforwb.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;


/**
 * 文件与文件夹工具类
 * @author Slience
 * @date 2022/3/11 17:09
 **/
@Component
public class FileUtils {

    //统一日志路径
    private static String unifiedLogPath;

    /**
     * 创建文件夹
     * @param path 文件夹路径
     * @return Boolean 是否创建成功
     **/
    public static Boolean createDir(String path){
        File file=new File(path);
        boolean res = false;
        if(!file.exists()){//如果文件夹不存在
            res = file.mkdir();//创建文件夹
        }
        return res;
    }

    /**
     * 根据当日日期，生成今日的文件夹名
     * @return String 统一日志文件路径加当前日期
     **/
    public static String getLogDirPathToday(){
        StringBuilder res = new StringBuilder(unifiedLogPath);
        if(!unifiedLogPath.endsWith("/")){
            res.append("/");
        }
        return res.append(DateUtils.getNowDateForFile()).toString();
    }

    /**
     * 为指定用户创建文件夹
     * @param user 用户
     * @return String 用户文件夹名
     **/
    public static String getLogDirPathForUser(User user){
        return getLogDirPathToday() + "/" + user.getName() + "&" + user.getUid();
    }

    @Value("${unified.log.path}")
    private void setUnifiedLogPath(String unifiedLogPath){
        FileUtils.unifiedLogPath = unifiedLogPath;
    }

}
