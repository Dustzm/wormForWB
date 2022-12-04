package com.czm.wormforwb.pojo;

import lombok.Data;

import java.util.List;

/**
 * 用户实体类
 * @author Slience
 * @date 2022/3/11 11:30
 **/
@Data
public class User {

    //用户id
    private String uid;

    //用户名称
    private String name;

    //接收邮箱地址
    private String email;

    //订阅博主uid,多个uid使用英文逗号隔开
    private String subIds;

}
