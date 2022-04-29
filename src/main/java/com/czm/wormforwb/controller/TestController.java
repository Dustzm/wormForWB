package com.czm.wormforwb.controller;

import com.czm.wormforwb.mapper.UserDynamicLogMapper;
import com.czm.wormforwb.mapper.UserMapper;
import com.czm.wormforwb.pojo.DynamicLog;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.dto.DynamicParamDTO;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.EmailSendService;
import com.czm.wormforwb.service.UserService;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.DBUtils;
import com.czm.wormforwb.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试服务用控制层
 * @author Slience
 * @date 2022/3/10 13:33
 **/
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailSendService emailSendService;

    @Resource
    private WBQueryService wbQueryService;

    @Resource
    private UserService userService;

    @Resource
    private UserDynamicLogMapper userDynamicLogMapper;

    @GetMapping("sendemail")
    public String sendemail(){
//        if(emailSendService.sendEmail("hello email","测试邮件")){
//            return "ok";
//        }
        return "no";
    }

    @GetMapping("wb")
    public DynamicParamDTO wb(){
        return wbQueryService.getUpdatedMid("1670659923");
    }

    @GetMapping("user")
    public List<User> user(){
        return userService.getAllUser();
    }

    @GetMapping("db")
    public Integer db(){
        DynamicResVO dynamic1 = new DynamicResVO();
        dynamic1.setMid("111");
        dynamic1.setPageUrl("http://www.baidu.com");
        dynamic1.setCreateTime("2022/03/13 15:00:00");
        DynamicResVO dynamic2 = new DynamicResVO();
        dynamic2.setMid("222");
        dynamic2.setPageUrl("http://test.com");
        dynamic2.setCreateTime("2022/03/01 12:01:11");
        List<DynamicResVO> dynamicResVOList = new ArrayList<>();
        dynamicResVOList.add(dynamic1);
        dynamicResVOList.add(dynamic2);
        return userDynamicLogMapper.insertDynamicLogBatch(dynamicResVOList, DBUtils.getLogTableName(),"test01");
    }

    @GetMapping("dbq")
    public List<String> dbq(){
        List<DynamicLogVO> dynamicLogs =  userDynamicLogMapper.queryDynamicLogYesterdayByUid(DBUtils.getLogTableName(), DBUtils.getLogInfoTableName(),"test01");
        List<String> res = new ArrayList<>();
        for(DynamicLog dynamicLog:dynamicLogs){
            res.addAll(wbQueryService.getPicsByBid(dynamicLog.getBid()));
        }
        return res;
    }

    @GetMapping("thread")
    public String test(){
        List<User> userList = userMapper.queryAllUserInfo();
        log.debug("****主线程开始");
        long startTime = System.currentTimeMillis();
        for(int i= 0 ;i<1000;i++){
            int finalI = i;
            new Thread(()->{
                executeTask(finalI);
            }).start();
        }
        log.debug("****主线程结束：" + "耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        return "done";
    }

    private void executeTask(Integer user){
        log.debug("----线程开始");
        long startTime = System.currentTimeMillis();
        System.out.println("为用户" + user + "执行任务。。。");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("----线程：" + user + " 结束,耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
    }

    @GetMapping("nothread")
    public String nothread(){
        log.debug("****主线程开始");
        long startTime = System.currentTimeMillis();
        for(int i= 0 ;i<1000;i++){
            int finalI = i;
            executeTask(finalI);
        }
        log.debug("****主线程结束：" + "耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        return "done";
    }
}
