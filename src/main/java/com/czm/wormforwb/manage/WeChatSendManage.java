package com.czm.wormforwb.manage;

import com.czm.wormforwb.pojo.dto.SendInfoDTO;
import com.czm.wormforwb.service.EmailSendService;
import com.czm.wormforwb.service.WeChatSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author comdotwww
 * @package com.czm.wormforwb.manage
 * @date 2022/12/4 2:12
 * @description:
 */
@Component
@Slf4j
public class WeChatSendManage implements ApplicationListener<ContextRefreshedEvent> {

    private static final LinkedBlockingQueue<SendInfoDTO> sendInfoQueue = new LinkedBlockingQueue<>(5000);
    /**
     * 是否已初始化 true:是 ；false:否
     */
    private static Boolean isInitialized = false;

    private final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    @Resource
    private EmailSendService emailSendService;

    @Resource
    private WeChatSendService weChatSendService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isInitialized) {
            sendMsg();
            isInitialized = true;
        }
    }

    public void addQueue(SendInfoDTO sendInfoDTO) {
        boolean offer = sendInfoQueue.offer(sendInfoDTO);
        if (!offer) {
            log.info("队列容量不足, 加入队列失败");
        }
    }

    private void sendMsg() {
        THREAD_EXECUTOR.execute(() -> {
            try {
                SendInfoDTO sendInfoDTO = sendInfoQueue.take();
                emailSendService.sendEmail(sendInfoDTO.getTitle(), sendInfoDTO.getContent());
                weChatSendService.sendPushplus(sendInfoDTO.getTitle(), sendInfoDTO.getContent());
                weChatSendService.sendIgot(sendInfoDTO.getTitle(), sendInfoDTO.getContent());
            } catch (InterruptedException e) {
                log.info(e.getMessage(), e);
                try {
                    // 每 10 秒执行一次
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ignored) {

                }
            }
        });
    }
}
