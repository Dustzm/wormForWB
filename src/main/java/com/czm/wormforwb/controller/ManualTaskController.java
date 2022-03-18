package com.czm.wormforwb.controller;

import com.czm.wormforwb.task.WormLogDirTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 手动执行定时任务控制类
 * @author Slience
 * @date 2022/3/16 09:41
 **/
@RestController
@RequestMapping("/manual/task")
@Slf4j
public class ManualTaskController {

    @Resource
    WormLogDirTask wormLogDirTask;

    @GetMapping("/log")
    public String createLog(){
        wormLogDirTask.createLogDir();
        wormLogDirTask.createYesterdayLogForAllUser();
        log.debug("手动执行日志生成任务");
        return "done";
    }
}
