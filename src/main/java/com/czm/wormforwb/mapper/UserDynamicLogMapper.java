package com.czm.wormforwb.mapper;

import com.czm.wormforwb.pojo.DynamicLog;
import com.czm.wormforwb.pojo.vo.DynamicLogVO;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDynamicLogMapper {

    Integer insertDynamicLogBatch(List<DynamicResVO> dynamicResVOs, @Param("logTable") String logTable, @Param("uid") String uid);

    Integer insertDynamicInfo(List<DynamicResVO> dynamicResVOs, @Param("logTable") String logTable);

    List<DynamicLogVO> queryDynamicLogYesterdayByUid(@Param("logTable") String logTable, @Param("dynamicInfoTable") String dynamicInfoTable, @Param("uid") String uid);

    List<DynamicLogVO> queryDynamicLogTodayByUid(@Param("logTable") String logTable, @Param("dynamicInfoTable") String dynamicInfoTable, @Param("uid") String uid);

    List<String> queryMids(@Param("dynamicInfoTable") String dynamicInfoTable);

    List<String> queryUpdatedMids(@Param("logTable") String logTable);
}
