package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.PatientExaminationRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者体检信息关系Mapper接口
 */
@Mapper
public interface PatientExaminationRelationMapper extends BaseMapper<PatientExaminationRelation> {
}
