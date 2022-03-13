package com.czm.wormforwb.mapper;

import com.czm.wormforwb.pojo.vo.DynamicResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDynamicLogMapper {

    Integer insertDynamicLogBatch(List<DynamicResVO> dynamicResVOs, @Param("logTable") String logTable, @Param("uid") String uid);

}
