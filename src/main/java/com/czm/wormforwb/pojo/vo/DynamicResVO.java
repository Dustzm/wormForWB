package com.czm.wormforwb.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 动态内容封装VO
 * @author Slience
 * @date 2022/3/10 14:47
 **/
@Data
public class DynamicResVO {

    //mid
    private String mid;

    //bid
    private String bid;

    //博主名称
    private String name;

    //正文
    private String text;

    //图片url
    private String pics;

    //原文链接
    private String pageUrl;

    //发表时间
    private String createTime;

    //转发数
    private Integer repostsCount;

    //评论数
    private Integer commentsCount;

    //点赞数
    private Integer attitudesCount;
}
