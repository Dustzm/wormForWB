package com.czm.wormforwb.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.czm.wormforwb.config.WeChatSenderConfig;
import com.czm.wormforwb.service.WeChatSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author comdotwww
 * @package com.czm.wormforwb.service.impl
 * @date 2022/12/4 0:32
 * @description:
 */
@Slf4j
@Service
public class WeChatSendServiceImpl implements WeChatSendService {

    @Resource
    private WeChatSenderConfig weChatSenderConfig;

    /**
     * 账号被禁止发送消息
     * 这儿的缓存应该使用类似 redis 的进行管理
     */
    private static volatile Boolean isPushplusLimit = false;

    /**
     * 每天 0 点刷新
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshTokenLimit() {
        isPushplusLimit = false;
    }

    /**
     * pushplus 微信推送
     *
     * @param title   标题
     * @param content 内容
     * @return 是否发送成功
     */
    @Override
    public Boolean sendPushplus(String title, String content) {
        if (!isPushplusLimit) {
            Boolean pushplusSender = weChatSenderConfig.getPushplusSender();
            // 未配置 或者配置是不发送
            if (ObjectUtils.isEmpty(pushplusSender) || !pushplusSender) {
                return false;
            }
            String pushplusToken = weChatSenderConfig.getPushplusToken();
            if (!StringUtils.hasText(pushplusToken)) {
                log.info("pushplus token 为空");
                return false;
            }
            String pushplusUser = weChatSenderConfig.getPushplusUser();
            String url = "https://www.pushplus.plus/send/";
            Map<String, Object> map = new HashMap<>();
            map.put("token", pushplusToken);
            map.put("title", title);
            map.put("content", content);
            if (StringUtils.hasText(pushplusUser)) {
                map.put("topic", pushplusUser);
            }
            //服务器发送POST请求，接收响应内容
            String response = HttpUtil.post(url, map);
            // 把返回的字符串结果变成 JSONObject
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (!ObjectUtils.isEmpty(jsonObject.get("code")) && (Integer) jsonObject.get("code") == 200) {
                return true;
            }
            // 账号被禁止发送消息，单日请求超过200次，账号被封2天、7天状态接口都会返回900代码
            //判断返回码是否为900（用户账号使用受限），如果是就修改redis对象，下次请求不在发送
            if (!ObjectUtils.isEmpty(jsonObject.get("code"))) {
                log.error((String) jsonObject.get("msg"));
            }
            return false;
        }
        return false;
    }

    /**
     * igot 微信推送
     *
     * @param title   标题
     * @param content 内容
     * @return 是否发送成功
     */
    @Override
    public Boolean sendIgot(String title, String content) {
        Boolean igotSender = weChatSenderConfig.getIgotSender();
        // 未配置 或者配置是不发送
        if (ObjectUtils.isEmpty(igotSender) || !igotSender) {
            return false;
        }
        String igotPushKey = weChatSenderConfig.getIgotPushKey();
        if (!StringUtils.hasText(igotPushKey)) {
            log.info("igot token 为空");
            return false;
        }
        String url = "https://push.hellyw.com/" + igotPushKey;
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        //服务器发送POST请求，接收响应内容
        String response = HttpUtil.post(url, map);
        // 把返回的字符串结果变成 JSONObject
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (!ObjectUtils.isEmpty(jsonObject.get("ret")) && (Integer) jsonObject.get("ret") == 0) {
            return true;
        }
        if (!ObjectUtils.isEmpty(jsonObject.get("errMsg"))) {
            log.error((String) jsonObject.get("errMsg"));
        }
        return false;
    }
}
