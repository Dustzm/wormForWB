package com.czm.wormforwb.controller;

import com.czm.wormforwb.mapper.UserDynamicLogMapper;
import com.czm.wormforwb.pojo.DynamicLog;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.EmailSendService;
import com.czm.wormforwb.service.UserService;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.DBUtils;
import com.czm.wormforwb.utils.FileUtils;
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
@RestController
@RequestMapping("/test")
public class TestController {

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
    public List<DynamicResVO> wb(){
        //return wbQueryService.monitorDynamic();
        return null;
    }

    @GetMapping("user")
    public List<User> user(){
        return userService.getAllUser();
    }

    @GetMapping("file")
    public String file(){
        return FileUtils.createDir(FileUtils.getLogDirPathToday()).toString();
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
}
