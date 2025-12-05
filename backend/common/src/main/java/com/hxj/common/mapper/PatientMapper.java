package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者信息Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    
}
