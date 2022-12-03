package com.czm.wormforwb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author comdotwww
 * @package com.czm.wormforwb.config
 * @date 2022/12/4 0:37
 * @description:
 */
@Configuration
public class WeChatSenderConfig {

    /**
     * pushplus 是否发送
     */
    @Value("${sender.pushplus}")
    private Boolean pushplusSender;
    /**
     * pushplus token
     */
    @Value("${pushplus.token}")
    private String pushplusToken;
    /**
     * pushplus user
     */
    @Value("${pushplus.user}")
    private String pushplusUser;

    /**
     * igot 是否发送
     */
    @Value("${sender.igot}")
    private boolean igotSender;
    /**
     * igot token
     */
    @Value("${igot.pushKey}")
    private String igotPushKey;

    public Boolean getPushplusSender() {
        return pushplusSender;
    }

    public void setPushplusSender(Boolean pushplusSender) {
        this.pushplusSender = pushplusSender;
    }

    public String getPushplusToken() {
        return pushplusToken;
    }

    public void setPushplusToken(String pushplusToken) {
        this.pushplusToken = pushplusToken;
    }

    public String getPushplusUser() {
        return pushplusUser;
    }

    public void setPushplusUser(String pushplusUser) {
        this.pushplusUser = pushplusUser;
    }

    public Boolean getIgotSender() {
        return igotSender;
    }

    public void setIgotSender(Boolean igotSender) {
        this.igotSender = igotSender;
    }

    public String getIgotPushKey() {
        return igotPushKey;
    }

    public void setIgotPushKey(String igotPushKey) {
        this.igotPushKey = igotPushKey;
    }
}
