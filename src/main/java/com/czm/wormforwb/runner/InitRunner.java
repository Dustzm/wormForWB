package com.czm.wormforwb.runner;

import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.FileUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统初始化Runner
 * @author Slience
 * @date 2022/3/22 10:43
 **/
@Component
public class InitRunner implements ApplicationRunner {

    @Resource
    WBQueryService wbQueryService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //创建pics文件夹
        FileUtils.createDir(FileUtils.getPicsDirPath());
        //初始化队列
        wbQueryService.initQueue();
    }
}
