package com.czm.wormforwb.pojo;

import lombok.Data;

@Data
public class DynamicLog {

    //uid
    private String uid;

    //mid
    private String mid;

    //bid
    private String bid;

    //博主名称
    private String name;

    //原文链接
    private String pageUrl;

    //发表时间
    private String createTime;

}
