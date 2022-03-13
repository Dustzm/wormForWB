package com.czm.wormforwb.task;

import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.service.UserService;
import com.czm.wormforwb.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 动态监控日志文件夹生成定时任务
 * @author Slience
 * @date 2022/3/12 21:44
 **/
@Component
@Slf4j
public class WormLogDirTask {

    @Value("${unified.log.path}")
    private String unifiedLogPath;

    @Resource
    UserService userService;

//    @Scheduled(cron = "0 0 1 * * ? ")
    @Scheduled(cron = "0 */1 * * * ?")
    public void createLogDir(){
        log.debug("------日志文件夹创建定时任务开始-------");
        //创建今日文件夹
        if(!FileUtils.createDir(FileUtils.getLogDirPathToday())){
            log.warn("------文件夹创建失败，检查该文件夹: " + FileUtils.getLogDirPathToday() + "是否已存在------");
            return;
        }
        //为每个用户创建文件夹
        List<User> users = userService.getAllUser();
        for(User user : users){
            if(FileUtils.createDir(FileUtils.getLogDirPathForUser(user))){
                log.debug("为用户：" + user.getName() + "&" + user.getUid() + "创建文件夹成功");
            }else{
                log.error("为用户：" + user.getName() + "&" + user.getUid() + "创建文件夹失败");
            }
        }

    }


}
