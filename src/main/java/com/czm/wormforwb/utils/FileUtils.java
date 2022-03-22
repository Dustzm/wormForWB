package com.czm.wormforwb.utils;

import com.czm.wormforwb.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;


/**
 * 文件与文件夹工具类
 * @author Slience
 * @date 2022/3/11 17:09
 **/
@Component
@Slf4j
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
     * 创建文件
     * @param user 用户
     * @return Boolean 是否创建成功
     **/
    public static String createFileForUser(User user, Boolean isToday) {
        String path;
        if(isToday){
            path = getLogDirPathForUser(user) + "/" + DateUtils.getNowDateForFile() + ".pdf";
        }else {
            path = getLogDirPathForUser(user) + "/" + DateUtils.getYesterDayForFile() + ".pdf";
        }
        try {
            File file=new File(path);
            boolean res = false;
            if(!file.exists()){//如果文件夹不存在
                file.createNewFile();//创建文件夹
            }
            return path;
        } catch (IOException e){
            log.error("文件创建出现异常，请检查路径：" + path);
            return null;
        }

    }

    /**
     * 为指定用户创建文件夹
     * @param user 用户
     * @return String 用户文件夹名
     **/
    public static String getLogDirPathForUser(User user){
        StringBuilder res = new StringBuilder(unifiedLogPath);
        if(!unifiedLogPath.endsWith("/")){
            res.append("/");
        }
        return res.append(user.getUid()).toString();
    }

    public static String getPicsDirPathForMonth(){
        StringBuilder res = new StringBuilder(unifiedLogPath);
        if(!unifiedLogPath.endsWith("/")){
            res.append("/");
        }
        return res.append("pics/").append(DateUtils.getNowDateForLogDB()).toString();
    }

    public static String getPicsDirPath(){
        StringBuilder res = new StringBuilder(unifiedLogPath);
        if(!unifiedLogPath.endsWith("/")){
            res.append("/");
        }
        return res.append("pics").toString();
    }

    @Value("${unified.log.path}")
    private void setUnifiedLogPath(String unifiedLogPath){
        FileUtils.unifiedLogPath = unifiedLogPath;
    }

}
