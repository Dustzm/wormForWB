package com.czm.wormforwb.service;

import com.czm.wormforwb.pojo.DynamicLog;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.dto.DynamicParamDTO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;

import java.util.List;

public interface WBQueryService {

    List<DynamicResVO> monitorDynamic(String monitorUids);

    List<String> getPicsByBid(String bid);

    List<DynamicLog> getDynamicLogYesterday(User usr);

    DynamicResVO getDynamicContent(DynamicParamDTO paramDTO);

}
