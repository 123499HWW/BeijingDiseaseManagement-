package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.QsofaPatientRelation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * qSOFA评分与患者关联Mapper接口
 */
@Mapper
public interface QsofaPatientRelationMapper extends BaseMapper<QsofaPatientRelation> {

    /**
     * 根据患者ID查询qSOFA评分ID列表
     */
    @Select("SELECT assessment_id FROM qsofa_patient_relation WHERE patient_id = #{patientId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<Long> selectAssessmentIdsByPatientId(@Param("patientId") Long patientId);

    /**
     * 批量插入关联关系
     */
    @Insert("<script>" +
            "INSERT INTO qsofa_patient_relation (assessment_id, patient_id, patient_number, relation_type, relation_status, " +
            "created_by, updated_by, created_at, updated_at, is_deleted) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.assessmentId}, #{item.patientId}, #{item.patientNumber}, #{item.relationType}, #{item.relationStatus}, " +
            "#{item.createdBy}, #{item.updatedBy}, #{item.createdAt}, #{item.updatedAt}, #{item.isDeleted})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("list") List<QsofaPatientRelation> relations);
}
