package com.czm.wormforwb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.czm.wormforwb.pojo.dto.DynamicParamDTO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.DateUtils;
import com.czm.wormforwb.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 微博动态监控服务
 * @author Slience
 * @date 2022/3/10 13:36
 **/
@Service
@Slf4j
public class WBQueryServiceImpl implements WBQueryService {

    @Value("${sina.mid.port}")
    private String midUrl;

    @Value("${sina.longtext.port}")
    private String contentUrl;

    //每次获取的动态数量
    @Value("${sina.dynamic.count}")
    private String dynamicCount;

    @Resource
    RestTemplate restTemplate;

    private static final BlockingDeque<String> midQueue = new LinkedBlockingDeque<>(51);


    /**
     * 获取当前最新动态内容
     * @author Slience
     * @date 2022/3/10 15:53
     **/
    @Override
    public List<DynamicResVO> monitorDynamic(String monitorUids) {
        List<DynamicResVO> res = new ArrayList<>();
        String[] uidList = monitorUids.split(",");
        for(String uid : uidList){
            DynamicParamDTO paramDTO = getUpdatedMid(uid);
            if(paramDTO != null){
                if(checkUpdated(paramDTO.getMid())){
                    res.add(getDynamicContent(paramDTO));
                }
            }
        }
        log.debug("微博动态获取完毕：" + JSONObject.toJSONString(res));
        return res;
    }

    /**
     * 根据最新mid获取动态内容
     * @author Slience
     * @date 2022/3/10 14:56
     **/
    private DynamicResVO getDynamicContent(DynamicParamDTO paramDTO){
        StringBuilder wbUrl = new StringBuilder(contentUrl);
        log.debug("------微博动态内容接口调用开始------");
        wbUrl.append("?").append("id=").append(paramDTO.getMid());
        log.debug("------请求url拼接完毕：" + wbUrl + "，请求开始");
        String response = restTemplate.getForEntity(wbUrl.toString(),String.class).getBody();
        log.debug("------微博动态内容接口调用成功：" + response);
        JSONObject rsJson = JSONObject.parseObject(response);
        Integer isOk = rsJson.getInteger("ok");
        if(1 == isOk){
            JSONObject dataJson = rsJson.getJSONObject("data");
            DynamicResVO dynamicResVO = new DynamicResVO();
            dynamicResVO.setText(dataJson.getString("longTextContent"));
            dynamicResVO.setRepostsCount(dataJson.getInteger("reposts_count"));
            dynamicResVO.setCommentsCount(dataJson.getInteger("comments_count"));
            dynamicResVO.setAttitudesCount(dataJson.getInteger("attitudes_count"));
            dynamicResVO.setMid(paramDTO.getMid());
            dynamicResVO.setName(paramDTO.getName());
            dynamicResVO.setPageUrl(paramDTO.getPageUrl());
            dynamicResVO.setCreateTime(paramDTO.getCreateTime());
            log.debug("------微博动态内容接口封装完毕，结果：" + JSONObject.toJSONString(dynamicResVO));
            return dynamicResVO;
        }
        log.debug("------微博动态内容接口返回异常------");
        return null;
    }

    /**
     * 获取最新动态的mid
     * @author Slience
     * @date 2022/3/10 14:54
     **/
    private DynamicParamDTO getUpdatedMid(String uid){
        StringBuilder wbUrl = new StringBuilder(midUrl);
        log.debug("------微博动态监视id接口调用开始,id:" + uid +"------");
        wbUrl.append("?type=uid").append("&");
        wbUrl.append("value=").append(uid).append("&");
        wbUrl.append("containerid=107603").append(uid).append("&");
        wbUrl.append("count=").append(dynamicCount);
        log.debug("------请求url拼接完毕：" + wbUrl + "，请求开始");
        //wbUrl.append("?type=uid&value=2803301701&containerid=1076032803301701&count=25");
        String response = restTemplate.getForEntity(wbUrl.toString(),String.class).getBody();
        log.debug("------微博动态监视id接口调用成功：" + response);
        JSONObject rsJson = JSONObject.parseObject(response);
        Integer isOk = rsJson.getInteger("ok");
        if(1 == isOk){
            //接口调用成功
            JSONArray cards = rsJson.getJSONObject("data").getJSONArray("cards");
            JSONObject card;
            //判断是否为置顶动态
            if(cards.size() > 1){
                card = JSONObject.parseObject(JSONObject.toJSONString(cards.get(1)));
            }else{
                card = JSONObject.parseObject(JSONObject.toJSONString(cards.get(0)));
            }
            //获取动态mid和发布时间，以查询动态全文
            DynamicParamDTO paramDTO = new DynamicParamDTO();
            paramDTO.setMid(card.getJSONObject("mblog").getString("mid"));
            paramDTO.setCreateTime(DateUtils.convertNormalDateToPattern(card.getJSONObject("mblog").getString("created_at")));
            paramDTO.setName(card.getJSONObject("mblog").getJSONObject("user").getString("screen_name"));
            paramDTO.setPageUrl(card.getString("scheme"));
            return paramDTO;
        }
        return null;
    }

    /**
     * 检测mid是否为最新未拉取的
     * @author Slience
     * @date 2022/3/10 16:53
     **/
    private Boolean checkUpdated(String mid){
        log.debug("------当前内存队列大小：" + midQueue.size() + "\n包含值为：" + JSONObject.toJSONString(midQueue));
        if(StringUtils.isNotBlank(mid)) {
            if (!midQueue.contains(mid)) {
                log.debug("midQueue不包含当前mid：" + mid + "，添加到队列中");
                midQueue.add(mid);
                if(midQueue.size() == 50){
                    log.debug("内存队列容量到达50，依次删除最旧的id");
                    midQueue.remove();
                }
                return true;
            }else{
                log.debug("midQueue包含当前mid：" + mid + "，不做更新操作");
                return false;
            }
        }else{
            log.warn("mid为空，不添加到队列");
            return false;
        }
    }
}
