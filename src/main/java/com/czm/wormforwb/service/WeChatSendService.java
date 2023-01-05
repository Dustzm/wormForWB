package com.czm.wormforwb.service;

/**
 * @author comdotwww
 * @package com.czm.wormforwb.service
 * @date 2022/12/4 0:29
 * @description: 微信渠道 通知 服务
 */
public interface WeChatSendService {

    /**
     * pushplus 微信推送
     *
     * @param title 标题
     * @param content 内容
     * @return 是否发送成功
     */
    Boolean sendPushplus(String title, String content);

    /**
     * igot 微信推送
     *
     * @param title 标题
     * @param content 内容
     * @return 是否发送成功
     */
    Boolean sendIgot(String title, String content);


}
