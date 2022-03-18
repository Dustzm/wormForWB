package com.czm.wormforwb.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DynamicResExtVO extends DynamicResVO{

    //动态包含图片url集合
    private List<String> pics;

    public DynamicResExtVO(DynamicResVO dynamicResVO, List<String> pics){
        super.setMid(dynamicResVO.getMid());
        super.setBid(dynamicResVO.getBid());
        super.setName(dynamicResVO.getName());
        super.setText(dynamicResVO.getText());
        super.setCreateTime(dynamicResVO.getCreateTime());
        super.setPageUrl(dynamicResVO.getPageUrl());
        super.setAttitudesCount(dynamicResVO.getAttitudesCount());
        super.setCommentsCount(dynamicResVO.getCommentsCount());
        super.setRepostsCount(dynamicResVO.getRepostsCount());
        this.pics = pics;
    }


}
