package com.czm.wormforwb.service.impl;

import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.service.ManualTaskService;
import com.czm.wormforwb.service.UserService;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.FileUtils;
import com.czm.wormforwb.utils.PDFUtils;
import com.czm.wormforwb.utils.StringUtils;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ManualTaskServiceImpl implements ManualTaskService {

    @Resource
    UserService userService;

    @Resource
    WBQueryService wbQueryService;

    @Override
    public Boolean createLogToday(String uid) {
        log.debug("------日志导出任务开始------");
        Long startTime = System.currentTimeMillis();
        User user = userService.getUserById(uid);
        try {
            List<DynamicLogVO> dynamicLogs = wbQueryService.getDynamicLogToday(user);
            if(dynamicLogs.size() == 0){
                log.debug("用户：" + user.getName() + "&" + user.getUid() + "今日无动态记录，不执行导出任务");
                return true;
            }
            String logPath = FileUtils.createFileForUser(user, true);
            if (StringUtils.isBlank(logPath)) {
                log.error("用户：" + user.getName() + "&" + user.getUid() + "创建日志pdf失败");
                return false;
            }
            Document document = PDFUtils.createNewPDF(logPath);
            for(DynamicLogVO dynamicLog : dynamicLogs){
                PDFUtils.writeDynamicContentToPDF(document,dynamicLog);
            }
            document.close();
            Long endTime = System.currentTimeMillis();
            log.debug("------导出任务结束，耗时：" + (endTime-startTime)/1000 + "s");
            return true;
        }catch (Exception e) {
            log.error("日志导出任务抛出异常：{}\n{}",e.getMessage(),e.getStackTrace());
            Long endTime = System.currentTimeMillis();
            log.debug("------导出任务结束，耗时：" + (endTime-startTime)/1000 + "s");
            return false;
        }
    }
}
