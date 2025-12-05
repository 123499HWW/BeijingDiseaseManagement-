package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.SyndromePatientRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 呼吸道症候群患者关联关系Mapper接口
 */
@Mapper
public interface SyndromePatientRelationMapper extends BaseMapper<SyndromePatientRelation> {

    /**
     * 根据患者ID查询症候群ID列表
     */
    List<Long> selectSyndromeIdsByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据症候群ID查询患者信息
     */
    SyndromePatientRelation selectPatientBySyndromeId(@Param("syndromeId") Long syndromeId);

    /**
     * 根据患者编号查询症候群ID列表
     */
    List<Long> selectSyndromeIdsByPatientNumber(@Param("patientNumber") String patientNumber);

    /**
     * 批量插入关联关系
     */
    int batchInsert(@Param("list") List<SyndromePatientRelation> list);

    /**
     * 根据患者ID删除关联关系
     */
    int deleteByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据症候群ID删除关联关系
     */
    int deleteBySyndromeId(@Param("syndromeId") Long syndromeId);

    /**
     * 查询活跃的关联关系
     */
    List<SyndromePatientRelation> selectActiveRelations();
}
