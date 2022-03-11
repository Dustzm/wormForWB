package com.czm.wormforwb.service;

import com.czm.wormforwb.pojo.vo.DynamicResVO;

import java.util.List;

public interface WBQueryService {

    List<DynamicResVO> monitorDynamic(String monitorUids);

}
