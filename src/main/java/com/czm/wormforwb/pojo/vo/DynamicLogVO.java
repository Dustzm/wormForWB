package com.czm.wormforwb.pojo.vo;

import com.czm.wormforwb.pojo.DynamicLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicLogVO extends DynamicLog {

    private String pics;

    private String text;


}
