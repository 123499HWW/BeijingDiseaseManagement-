package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.PhysicalExaminationDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 体检详细信息Mapper接口
 */
@Mapper
public interface PhysicalExaminationDetailMapper extends BaseMapper<PhysicalExaminationDetail> {

    /**
     * 根据患者ID查询体检详细信息
     */
    List<PhysicalExaminationDetail> selectByPatientId(@Param("patientId") Long patientId);
}
