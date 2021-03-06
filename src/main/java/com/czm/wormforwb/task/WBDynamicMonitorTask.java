package com.czm.wormforwb.task;

import com.alibaba.fastjson.JSONObject;
import com.czm.wormforwb.mapper.UserDynamicLogMapper;
import com.czm.wormforwb.mapper.UserMapper;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.EmailSendService;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.DBUtils;
import com.czm.wormforwb.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微博指定博主动态监视任务，检测到更新后发送到指定邮箱
 * @author Slience
 * @date 2022/3/10 16:01
 **/
@Component
@Slf4j
public class WBDynamicMonitorTask {

    @Resource
    private EmailSendService emailSendService;

    @Resource
    private WBQueryService wbQueryService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDynamicLogMapper userDynamicLogMapper;

    @Value("${service.domain}")
    private String domain;

    //默认2分钟执行一次
    @Scheduled(cron = "0 */5 * * * ?")
    public void monitor(){
        log.debug("------微博动态监视任务开始------");
        Long startTime = System.currentTimeMillis();
        List<User> users = userMapper.queryAllUserInfo();
        log.debug("------查询当前活跃用户：" + JSONObject.toJSONString(users));
        for(User user : users){
            new Thread(()->{
                execute(user);
            }).start();
        }
        Long endTime = System.currentTimeMillis();
        log.debug("------微博动态监视任务主线程结束，耗时：" + (endTime - startTime)/1000 + "s------");
    }

    private void execute(User user){
        Long startTime = System.currentTimeMillis();
        log.debug("------为当前用户: " + user.getUid() + "&" + user.getName() + "执行动态监视任务");
        StringBuilder emailContent = new StringBuilder();
        List<DynamicResVO> dynamicResVOS = wbQueryService.monitorDynamic(user.getUid(),user.getSubIds());
        if(dynamicResVOS.size() != 0){
            for(DynamicResVO dynamicRes : dynamicResVOS){
                emailContent.append(getEmailContent(dynamicRes));
            }
            emailContent.append(getFormattedLogUrl(user));
            //log.debug("------邮件内容:" + emailContent);
            emailSendService.sendEmail(user.getName() + ",你的心头好有更新哦～",emailContent.toString(), user.getEmail());
            //log.debug("------将动态内容记录至数据库------");
            userDynamicLogMapper.insertDynamicLogBatch(dynamicResVOS, DBUtils.getLogTableName(),user.getUid());
            List<String> mids = userDynamicLogMapper.queryMids(DBUtils.getLogInfoTableName());
            //mid不重复
            dynamicResVOS = dynamicResVOS.stream().filter(a->!mids.contains(a.getMid())).collect(Collectors.toList());
            if(dynamicResVOS.size() != 0){
                userDynamicLogMapper.insertDynamicInfo(dynamicResVOS, DBUtils.getLogInfoTableName());
            }
        }else{
            log.debug("未检测到更新，休眠中。。。");
        }
        Long endTime = System.currentTimeMillis();
        log.debug("------用户" + user.getUid() + "&" + user.getName() + "任务执行结束，耗时：" + (endTime - startTime)/1000 + "s");
    }

    private StringBuilder getEmailContent(DynamicResVO dynamicRes){
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<br>").append("你关注的博主【").append(dynamicRes.getName()).append("】在")
                .append(dynamicRes.getCreateTime())
                .append("更新了动态：").append("<br>")
                .append(dynamicRes.getText()).append("<br>")
                .append("&nbsp&nbsp&nbsp&nbsp")
                .append("<a href=\"").append(dynamicRes.getPageUrl()).append("\">").append("点此去康康").append("</a>")
                .append("<br>")
                .append("&nbsp&nbsp&nbsp&nbsp").append("点赞数:").append(dynamicRes.getAttitudesCount())
                .append("&nbsp&nbsp&nbsp&nbsp").append("评论数:").append(dynamicRes.getCommentsCount())
                .append("&nbsp&nbsp&nbsp&nbsp").append("转发数：").append(dynamicRes.getRepostsCount()).append("<br>");
        return emailContent;

    }

    private StringBuilder getFormattedLogUrl(User user){
        StringBuilder logUrl = new StringBuilder();
        logUrl.append("<br>")
                .append("<a href=\"").append(getLogUrl(user)).append("\">").append("这里是昨天的动态记录QWQ").append("</a>")
                .append("<br>");
        return logUrl;
    }

    private StringBuilder getLogUrl(User user){
        StringBuilder res = new StringBuilder();
        StringBuilder domainUrl = new StringBuilder(domain);
        if(!domain.endsWith("/")){
            domainUrl.append("/");
        }
        res.append(domain).append("log/").append(user.getUid()).append("/").append(DateUtils.getYesterDayForFile()).append(".pdf");
        return res;
    }

}
