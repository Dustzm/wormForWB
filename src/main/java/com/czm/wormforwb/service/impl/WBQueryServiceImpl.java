package com.czm.wormforwb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.czm.wormforwb.mapper.UserDynamicLogMapper;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.dto.DynamicParamDTO;
import com.czm.wormforwb.pojo.vo.DynamicFlagVO;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.czm.wormforwb.service.WBQueryService;
import com.czm.wormforwb.utils.DBUtils;
import com.czm.wormforwb.utils.DateUtils;
import com.czm.wormforwb.utils.HttpUtils;
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

    @Value("${sina.dynamic.port}")
    private String bidUrl;

    //每次获取的动态数量
    @Value("${sina.dynamic.count}")
    private String dynamicCount;

    @Resource
    RestTemplate restTemplate;

    @Resource
    UserDynamicLogMapper userDynamicLogMapper;

    private static final BlockingDeque<String> midQueue = new LinkedBlockingDeque<>(101);

    /**
     * 初始化已获取的mid到队列中，防止系统重启后重复推送
     **/
    @Override
    public void initQueue(){
        midQueue.clear();
        List<DynamicFlagVO> mids = userDynamicLogMapper.queryUpdatedMids(DBUtils.getLogTableName());
        mids.forEach(mid->{
            midQueue.add(mid.getFlag());
        });
        log.debug("------初始化队列，今日各用户已监控的最新mid:" + JSONObject.toJSONString(mids) + "，添加至队列:" + midQueue);
    }

    /**
     * 获取当前最新动态内容
     * @author Slience
     * @date 2022/3/10 15:53
     **/
    @Override
    public List<DynamicResVO> monitorDynamic(String uid, String monitorUids) {
        List<DynamicResVO> res = new ArrayList<>();
        String[] uidList = monitorUids.split(",");

        for(String muid : uidList){
            DynamicParamDTO paramDTO = getUpdatedMid(muid);
            if(paramDTO != null){
                if(checkUpdated(uid, paramDTO.getMid())){
                    DynamicResVO dynamicResVO = getDynamicContent(paramDTO);
                    if(dynamicResVO != null){
                        res.add(dynamicResVO);
                    }
                }
            }
        }
        log.debug("微博动态获取完毕：" + JSONObject.toJSONString(res));
        return res;
    }

    /**
     * 将动态图片存至本地
     * @param bid 动态bid
     * @return String 图片url
     **/
    @Override
    public List<String> getPicsByBid(String bid) {
        StringBuilder wbUrl = new StringBuilder(bidUrl);
        log.debug("------微博动态详情接口调用开始------");
        wbUrl.append("?").append("id=").append(bid);
        log.debug("------请求url拼接完毕：" + wbUrl + "，请求开始");
        String response = restTemplate.getForEntity(wbUrl.toString(),String.class).getBody();
        //log.debug("------微博动态详情接口调用成功：" + response);
        List<String> res = new ArrayList<>();
        JSONObject rsJson = JSONObject.parseObject(response);
        Integer isOk = rsJson.getInteger("ok");
        if(1 == isOk){
            JSONObject dataJson = rsJson.getJSONObject("data");
            JSONArray pics = dataJson.getJSONArray("pics");
            if(pics == null||pics.size() == 0){
                log.debug("------该微博动态:" + bid + "详情无图片内容------");
                return res;
            }
            for(Object pic : pics){
                JSONObject picJson = JSONObject.parseObject(JSONObject.toJSONString(pic));
                String picUrl = picJson.getJSONObject("large").getString("url");
                String[] picArray = picUrl.split("/");
                res.add(HttpUtils.download(picUrl,picArray[picArray.length-1]));
            }
            log.debug("------图片url封装完毕：" + JSONObject.toJSONString(res));
            return res;
        }
        log.warn("------微博动态详情接口请求异常------");
        return res;
    }

    /**
     * 获取昨日动态日志
     * @param user 用户实体
     * @return List 日志列表
     **/
    @Override
    public List<DynamicLogVO> getDynamicLogYesterday(User user) {
        String logTableName;
        String logInfoTableName;
        //月初1号跨表
        if(DateUtils.getNowDate().equals(DateUtils.getFirstDayThisMonth())){
            logTableName = DBUtils.getLastMonthLogTableName();
            logInfoTableName = DBUtils.getLastMonthLogInfoTableName();
        }else {
            logTableName = DBUtils.getLogTableName();
            logInfoTableName = DBUtils.getLogInfoTableName();
        }
        return userDynamicLogMapper.queryDynamicLogYesterdayByUid(logTableName, logInfoTableName, user.getUid());
    }

    @Override
    public List<DynamicLogVO> getDynamicLogToday(User user) {
        return userDynamicLogMapper.queryDynamicLogTodayByUid(DBUtils.getLogTableName(), DBUtils.getLogInfoTableName(), user.getUid());
    }

    /**
     * 根据最新mid获取动态内容
     * @author Slience
     * @date 2022/3/10 14:56
     **/
    @Override
    public DynamicResVO getDynamicContent(DynamicParamDTO paramDTO){
        StringBuilder wbUrl = new StringBuilder(contentUrl);
        log.debug("------微博动态内容接口调用开始------");
        wbUrl.append("?").append("id=").append(paramDTO.getMid());
        log.debug("------请求url拼接完毕：" + wbUrl + "，请求开始");
        try{
            String response = restTemplate.getForEntity(wbUrl.toString(),String.class).getBody();
            //log.debug("------微博动态内容接口调用成功：" + response);
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
                dynamicResVO.setBid(paramDTO.getBid());
                dynamicResVO.setPageUrl(paramDTO.getPageUrl());
                dynamicResVO.setCreateTime(paramDTO.getCreateTime());
                dynamicResVO.setPics(StringUtils.concatStringList(getPicsByBid(paramDTO.getBid())));
                log.debug("------微博动态内容接口封装完毕，结果：" + JSONObject.toJSONString(dynamicResVO));
                return dynamicResVO;
            }
            log.debug("------微博动态内容接口返回异常------");
            return null;
        }catch (Exception e){
            log.error("------微博动态内容接口异常:",e);
            return null;
        }

    }

    /**
     * 获取最新动态的mid
     * @author Slience
     * @date 2022/3/10 14:54
     **/
    @Override
    public DynamicParamDTO getUpdatedMid(String uid){
        StringBuilder wbUrl = new StringBuilder(midUrl);
        log.debug("------微博动态监视id接口调用开始,id:" + uid +"------");
        wbUrl.append("?type=uid").append("&");
        wbUrl.append("value=").append(uid).append("&");
        wbUrl.append("containerid=107603").append(uid).append("&");
        wbUrl.append("count=").append(dynamicCount);
        log.debug("------请求url拼接完毕：" + wbUrl + "，请求开始");
        String response = restTemplate.getForEntity(wbUrl.toString(),String.class).getBody();
        //log.debug("------微博动态监视id接口调用成功：" + response);
        JSONObject rsJson = JSONObject.parseObject(response);
        Integer isOk = rsJson.getInteger("ok");
        if(1 == isOk){
            //接口调用成功
            JSONArray cards = rsJson.getJSONObject("data").getJSONArray("cards");
            JSONObject card = JSONObject.parseObject(JSONObject.toJSONString(cards.get(0)));
            Integer cardType = card.getInteger("card_type");
            //正常动态
            if(9 == cardType){
                Integer isTop = card.getJSONObject("mblog").getInteger("isTop");
                //判断是否存在置顶动态
                if(isTop != null && isTop == 1){
                    card = JSONObject.parseObject(JSONObject.toJSONString(cards.get(1)));
                }
                String createTime = DateUtils.convertNormalDateToPattern(card.getJSONObject("mblog").getString("created_at"));
                if(!DateUtils.isToday(createTime)){
                    log.debug("该动态不是今天发布，不获取");
                    return null;
                }
                //获取动态mid和发布时间，以查询动态全文
                DynamicParamDTO paramDTO = new DynamicParamDTO();
                paramDTO.setMid(card.getJSONObject("mblog").getString("mid"));
                paramDTO.setBid(card.getJSONObject("mblog").getString("bid"));
                paramDTO.setCreateTime(DateUtils.convertNormalDateToPattern(card.getJSONObject("mblog").getString("created_at")));
                paramDTO.setName(card.getJSONObject("mblog").getJSONObject("user").getString("screen_name"));
                paramDTO.setPageUrl(card.getString("scheme"));
                return paramDTO;
            }else{
                log.debug("该博主状态异常，不获取动态");
                return null;
            }
        }
        return null;
    }

    /**
     * 检测mid是否为最新未拉取的
     * @author Slience
     * @date 2022/3/10 16:53
     **/
    private Boolean checkUpdated(String uid,String mid) {
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(mid)) {
            log.error("------mid或uid为空{},{}", uid, mid);
            return false;
        }
        String mflag = uid + "&" + mid;
        log.debug("------当前内存队列大小：" + midQueue.size() + "\n包含值为：" + JSONObject.toJSONString(midQueue));
        if (!midQueue.contains(mflag)) {
            log.debug("midQueue不包含当前mid：" + mflag + "，添加到队列中");
            midQueue.add(mflag);
            if (midQueue.size() == 100) {
                log.debug("内存队列容量到达100，依次删除最旧的id");
                midQueue.remove();
            }
            return true;
        } else {
            log.debug("midQueue包含当前mflag：" + mflag + "，不做更新操作");
            return false;
        }
    }
}
