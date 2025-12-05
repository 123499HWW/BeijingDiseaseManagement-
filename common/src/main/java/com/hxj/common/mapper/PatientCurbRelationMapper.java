package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.PatientCurbRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者CURB-65评分关联Mapper接口
 */
@Mapper
public interface PatientCurbRelationMapper extends BaseMapper<PatientCurbRelation> {
}
