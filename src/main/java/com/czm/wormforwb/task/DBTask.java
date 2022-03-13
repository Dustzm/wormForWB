package com.czm.wormforwb.task;

import com.czm.wormforwb.utils.DBUtils;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 数据库定时任务
 * @author Slience
 * @date 2022/3/13 15:53
 **/
public class DBTask {

    /**
     * 每月最后一天2点，生成次月日志表
     **/
    @Scheduled(cron = "0 0 2 L * ? ")
    public void createDynamicLogTableEveryMonth(){
        DBUtils.executeSQL(DBUtils.getLogTableSQLForNextMonth());
    }


}
