package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.PsiPatientRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PSI患者关联关系Mapper接口
 */
@Mapper
public interface PsiPatientRelationMapper extends BaseMapper<PsiPatientRelation> {

    /**
     * 根据患者ID查询PSI评分ID列表
     */
    List<Long> selectPsiIdsByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据PSI评分ID查询患者信息
     */
    PsiPatientRelation selectPatientByPsiId(@Param("psiId") Long psiId);

    /**
     * 根据患者编号查询PSI评分ID列表
     */
    List<Long> selectPsiIdsByPatientNumber(@Param("patientNumber") String patientNumber);

    /**
     * 批量插入关联关系
     */
    int batchInsert(@Param("list") List<PsiPatientRelation> list);

    /**
     * 根据患者ID删除关联关系
     */
    int deleteByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据PSI评分ID删除关联关系
     */
    int deleteByPsiId(@Param("psiId") Long psiId);
}
