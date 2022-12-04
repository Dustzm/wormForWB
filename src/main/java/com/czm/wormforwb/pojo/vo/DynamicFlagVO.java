package com.czm.wormforwb.pojo.vo;

import lombok.Data;

/**
 * 重复标志
 * @author Slience
 * @date 2022/3/22 20:20
 **/
@Data
public class DynamicFlagVO {
    private String mid;

    private String uid;

    public String getFlag(){
        return this.uid + "&" + this.mid;
    }
}
