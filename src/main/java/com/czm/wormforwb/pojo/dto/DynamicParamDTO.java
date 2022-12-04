package com.czm.wormforwb.pojo.dto;

import lombok.Data;

import java.util.Date;

/**
 * 微博动态中间传参
 * @author Slience
 * @date 2022/3/10 15:20
 **/
@Data
public class DynamicParamDTO {

    private String createTime;

    private String mid;

    private String bid;

    private String name;

    private String pageUrl;

}
