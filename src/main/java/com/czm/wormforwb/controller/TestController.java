package com.czm.wormforwb.controller;

import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.EmailSendService;
import com.czm.wormforwb.service.WBQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试服务用控制层
 * @author Slience
 * @date 2022/3/10 13:33
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private EmailSendService emailSendService;

    @Resource
    private WBQueryService wbQueryService;

    @GetMapping("sendemail")
    public String sendemail(){
        if(emailSendService.sendEmail("hello email","测试邮件")){
            return "ok";
        }
        return "no";
    }

    @GetMapping("wb")
    public List<DynamicResVO> wb(){
        return wbQueryService.monitorDynamic();
    }
}
