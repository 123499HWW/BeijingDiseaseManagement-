package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.SeverePneumoniaPatientRelation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 重症肺炎诊断与患者关联Mapper接口
 */
@Mapper
public interface SeverePneumoniaPatientRelationMapper extends BaseMapper<SeverePneumoniaPatientRelation> {

    /**
     * 根据患者ID查询重症肺炎诊断ID列表
     */
    @Select("SELECT diagnosis_id FROM severe_pneumonia_patient_relation WHERE patient_id = #{patientId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<Long> selectDiagnosisIdsByPatientId(@Param("patientId") Long patientId);

    /**
     * 批量插入关联关系
     */
    @Insert("<script>" +
            "INSERT INTO severe_pneumonia_patient_relation (diagnosis_id, patient_id, patient_number, relation_type, relation_status, " +
            "created_by, updated_by, created_at, updated_at, is_deleted) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.diagnosisId}, #{item.patientId}, #{item.patientNumber}, #{item.relationType}, #{item.relationStatus}, " +
            "#{item.createdBy}, #{item.updatedBy}, #{item.createdAt}, #{item.updatedAt}, #{item.isDeleted})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("list") List<SeverePneumoniaPatientRelation> relations);
}
