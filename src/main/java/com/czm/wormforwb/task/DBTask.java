package com.czm.wormforwb.task;

import com.czm.wormforwb.utils.DBUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据库定时任务
 * @author Slience
 * @date 2022/3/13 15:53
 **/
@Component
@Slf4j
public class DBTask {

    /**
     * 每月最后一天2点，生成次月日志及相关表
     **/
    @Scheduled(cron = "0 1 3 L * ? ")
    public void createDynamicLogTableEveryMonth(){
        log.debug("数据表定时任务开始");
        DBUtils.executeSQL(DBUtils.getLogTableSQLForNextMonth());
        DBUtils.executeSQL(DBUtils.getLogInfoTableForNextMonth());
        log.debug("数据表任务结束");
    }


}
