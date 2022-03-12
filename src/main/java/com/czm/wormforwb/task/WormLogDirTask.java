package com.czm.wormforwb.task;

import com.czm.wormforwb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 动态监控日志文件夹生成定时任务
 * @author Slience
 * @date 2022/3/12 21:44
 **/
@Component
@Slf4j
public class WormLogDirTask {

    @Resource
    UserService userService;

    @Scheduled(cron = "0 0 1 * * ? ")
    public void createLogDir(){

    }


}
