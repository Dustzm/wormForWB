package com.czm.wormforwb.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * http协议工具类
 * @author Slience
 * @date 2022/3/18 14:56
 **/
@Component
@Slf4j
public class HttpUtils {

    //统一日志路径
    private static String unifiedLogPath;

    /**
     * 图片url下载至本地
     * @param urlString 图片url
     * @param targetName 存储名称
     * @return String 图片本地路径
     **/
    public static String download(String urlString, String targetName){
        try{
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            StringBuilder localPath = new StringBuilder();
            String filename = localPath.append(FileUtils.getPicsDirPath())
                    .append("/").append(targetName).toString();  //下载路径及下载图片名称
            File file = new File(filename);
            FileOutputStream os = new FileOutputStream(file, true);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
            return localPath.toString();
        }catch (IOException e){
            log.error("下载抛出异常：{}\n{}",e,e.getMessage());
            return null;
        }
    }

    @Value("${unified.log.path}")
    private void setUnifiedLogPath(String unifiedLogPath){
        HttpUtils.unifiedLogPath = unifiedLogPath;
    }
}
