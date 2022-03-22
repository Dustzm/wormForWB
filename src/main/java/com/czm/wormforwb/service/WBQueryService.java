package com.czm.wormforwb.service;

import com.czm.wormforwb.pojo.DynamicLog;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.pojo.dto.DynamicParamDTO;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;

import java.util.List;

public interface WBQueryService {

    void initQueue();

    List<DynamicResVO> monitorDynamic(String monitorUids);

    List<String> getPicsByBid(String bid);

    List<DynamicLogVO> getDynamicLogYesterday(User user);

    List<DynamicLogVO> getDynamicLogToday(User user);

    DynamicResVO getDynamicContent(DynamicParamDTO paramDTO);

}
