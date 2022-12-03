package com.czm.wormforwb.task;

import com.czm.wormforwb.manage.WeChatSendManage;
import com.czm.wormforwb.pojo.dto.SendInfoDTO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.WBQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 微博指定博主动态监视任务，检测到更新后发送到指定邮箱
 *
 * @author Slience
 * @date 2022/3/10 16:01
 **/
@Component
@Slf4j
public class WBDynamicMonitorTask {

    @Resource
    private WBQueryService wbQueryService;

    @Resource
    private WeChatSendManage weChatSendManage;

    //默认2分钟执行一次
    @Scheduled(cron = "14 */2 * * * ?")
    public void monitor() {
        log.debug("------微博动态监视任务开始------");
        StringBuilder emailContent = new StringBuilder();
        List<DynamicResVO> dynamicResVOS = wbQueryService.monitorDynamic();
        if (dynamicResVOS.size() != 0) {
            for (DynamicResVO dynamicRes : dynamicResVOS) {
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
            }
            log.debug("------邮件内容:" + emailContent);
            SendInfoDTO sendInfoDTO = new SendInfoDTO();
            sendInfoDTO.setTitle("微博有更新啦");
            sendInfoDTO.setContent(emailContent.toString());
            weChatSendManage.addQueue(sendInfoDTO);
        } else {
            log.debug("未检测到更新，休眠中。。。");
        }
    }

}
