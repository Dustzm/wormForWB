package com.czm.wormforwb.controller;

import com.czm.wormforwb.service.ManualTaskService;
import com.czm.wormforwb.task.DBTask;
import com.czm.wormforwb.task.WormLogDirTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 定时任务面板控制类
 * @author Slience
 * @date 2022/3/16 09:41
 **/
@RestController
@RequestMapping("/manual/task")
@Slf4j
public class ManualTaskController {

    @Resource
    WormLogDirTask wormLogDirTask;

    @Resource
    DBTask dbTask;

    @Resource
    ManualTaskService manualTaskService;

    @GetMapping("/log")
    public String createLog(){
        wormLogDirTask.createLogDir();
        wormLogDirTask.createYesterdayLogForAllUser();
        log.debug("手动执行日志生成任务");
        return "done";
    }

    @GetMapping("/dir/user")
    public String createUserDir(){
        wormLogDirTask.createLogDir();
        log.debug("手动执行用户文件夹生成任务");
        return "done";
    }

    @GetMapping("/dir/pics")
    public String createPicsDir(){
        wormLogDirTask.createPicsDir();
        log.debug("手动执行图片文件夹生成任务");
        return "done";
    }

    @GetMapping("/db")
    public String db(){
        dbTask.createDynamicLogTableEveryMonth();
        log.debug("数据库日志表生成任务");
        return "done";
    }

    @GetMapping("/logToday/{uid}")
    public Boolean logToday(@PathVariable("uid")String uid){
        return manualTaskService.createLogToday(uid);
    }


}
