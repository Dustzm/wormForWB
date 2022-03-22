package com.czm.wormforwb.task;

import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.service.UserService;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.FileUtils;
import com.czm.wormforwb.utils.PDFUtils;
import com.czm.wormforwb.utils.StringUtils;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
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

    @Resource
    UserService userService;

    @Resource
    WBQueryService wbQueryService;

    @Scheduled(cron = "0 0 1 * * ? ")
    public void createLogDir(){
        log.debug("------日志文件夹创建定时任务开始-------");
        //为每个用户创建文件夹
        List<User> users = userService.getAllUser();
        for(User user : users){
            if(FileUtils.createDir(FileUtils.getLogDirPathForUser(user))){
                log.debug("为用户：" + user.getName() + "&" + user.getUid() + "创建文件夹成功");
            }else{
                log.error("为用户：" + user.getName() + "&" + user.getUid() + "创建文件夹失败，检查该文件夹是否已存在");
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ? ")
    public void createYesterdayLogForAllUser(){
        log.debug("------日志导出任务开始------");
        Long startTime = System.currentTimeMillis();
        List<User> users = userService.getAllUser();
        try {
            for (User user : users) {
                List<DynamicLogVO> dynamicLogs = wbQueryService.getDynamicLogYesterday(user);
                if(dynamicLogs.size() == 0){
                    log.debug("用户：" + user.getName() + "&" + user.getUid() + "昨日无动态记录，不执行导出任务");
                    continue;
                }
                String logPath = FileUtils.createFileForUser(user,false);
                if (StringUtils.isBlank(logPath)) {
                    log.error("用户：" + user.getName() + "&" + user.getUid() + "创建日志pdf失败");
                    continue;
                }
                createPDFLog(PDFUtils.createNewPDF(logPath),dynamicLogs);
            }
        }catch (Exception e) {
            log.error("日志导出任务抛出异常：{}",e);
        }
        Long endTime = System.currentTimeMillis();
        log.debug("------导出任务结束，耗时：" + (endTime-startTime)/1000 + "s");
    }

    @Scheduled(cron = "0 0 1 L * ? ")
    public void createPicsDir(){
        FileUtils.createDir(FileUtils.getPicsDirPathForMonth());
    }

    private void createPDFLog(Document document, List<DynamicLogVO> dynamicLogs){
        //查找昨日该用户的记录，并将每个记录导出到pdf
        for(DynamicLogVO dynamicLog : dynamicLogs){
            PDFUtils.writeDynamicContentToPDF(document,dynamicLog);
        }
        document.close();
    }

}
