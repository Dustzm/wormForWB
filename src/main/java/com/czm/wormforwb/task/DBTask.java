package com.czm.wormforwb.task;

import com.czm.wormforwb.utils.DBUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据库定时任务
 * @author Slience
 * @date 2022/3/13 15:53
 **/
@Component
public class DBTask {

    /**
     * 每月最后一天2点，生成次月日志及相关表
     **/
    @Scheduled(cron = "0 0 2 L * ? ")
    public void createDynamicLogTableEveryMonth(){
        DBUtils.executeSQL(DBUtils.getLogTableSQLForNextMonth());
        DBUtils.executeSQL(DBUtils.getLogInfoTableForNextMonth());
    }


}
