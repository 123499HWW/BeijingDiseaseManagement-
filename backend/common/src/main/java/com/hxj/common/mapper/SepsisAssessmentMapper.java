package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.SepsisAssessmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 脓毒症评估联合查询Mapper
 * 联查patient_info、qsofa_assessment和sofa_assessment表
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Mapper
public interface SepsisAssessmentMapper extends BaseMapper<SepsisAssessmentVO> {
    
    /**
     * 联表查询患者信息、qSOFA和SOFA评分
     * 
     * @param page 分页对象
     * @param patientNumber 患者编号（可选）
     * @param patientName 患者姓名（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @param hasQsofa 是否有qSOFA评估（可选）
     * @param hasSofa 是否有SOFA评估（可选）
     * @param minQsofaScore 最小qSOFA分数（可选）
     * @param minSofaScore 最小SOFA分数（可选）
     * @return 分页查询结果
     */
    @Select("<script>" +
            "SELECT " +
            // 患者基本信息
            "p.patient_id AS patientId, " +
            "CONCAT('P', LPAD(p.patient_id, 6, '0')) AS patientNumber, " +
            "CONCAT('患者', p.patient_id) AS patientName, " +
            "p.gender, " +
            "p.age, " +
            "p.admission_date AS admissionDate, " +
            "p.chief_complaint AS chiefComplaint, " +
            "p.present_illness AS presentIllness, " +
            "NULL AS systolicPressure, " +
            "NULL AS diastolicPressure, " +
            "p.arterial_ph AS arterialPh, " +
            "p.arterial_po2 AS arterialPo2, " +
            "p.blood_urea_nitrogen AS bloodUreaNitrogen, " +
            "p.serum_creatinine AS serumCreatinine, " +
            "p.total_bilirubin AS totalBilirubin, " +
            "p.platelet_count AS plateletCount, " +
            "p.ventilator_used AS ventilatorUsed, " +
            "p.icu_admission AS icuAdmission, " +
            "p.vasoactive_drugs_used AS vasoactiveDrugsUsed, " +
            
            // qSOFA评分
            "qsofa.assessment_id AS qsofaId, " +
            "qsofa.respiratory_rate_high AS qsofaRespiratoryRate, " +
            "qsofa.consciousness_altered AS qsofaMentalStatus, " +
            "qsofa.systolic_bp_low AS qsofaSystolicBp, " +
            "qsofa.total_score AS qsofaTotalScore, " +
            "qsofa.risk_level AS qsofaRiskLevel, " +
            "qsofa.created_at AS qsofaAssessmentTime, " +
            "qsofa.created_by AS qsofaAssessor, " +
            
            // SOFA评分
            "sofa.assessment_id AS sofaId, " +
            "sofa.respiration_score AS sofaRespirationScore, " +
            "sofa.oxygenation_index AS sofaPao2Fio2, " +
            "sofa.coagulation_score AS sofaCoagulationScore, " +
            "sofa.platelet_count AS sofaPlateletCount, " +
            "sofa.liver_score AS sofaLiverScore, " +
            "sofa.bilirubin AS sofaBilirubin, " +
            "sofa.cardiovascular_score AS sofaCardiovascularScore, " +
            "sofa.mean_arterial_pressure AS sofaMeanArterialPressure, " +
            "sofa.vasoactive_drugs AS sofaVasoactiveDrugs, " +
            "sofa.cns_score AS sofaCnsScore, " +
            "sofa.glasgow_coma_score AS sofaGlasgowComaScore, " +
            "sofa.renal_score AS sofaRenalScore, " +
            "sofa.creatinine AS sofaCreatinine, " +
            "sofa.urine_output AS sofaUrineOutput, " +
            "sofa.total_score AS sofaTotalScore, " +
            "sofa.severity_level AS sofaRiskLevel, " +
            "sofa.created_at AS sofaAssessmentTime, " +
            "sofa.created_by AS sofaAssessor, " +
            
            // 计算已完成的评估
            "(CASE WHEN qsofa.assessment_id IS NOT NULL AND sofa.assessment_id IS NOT NULL THEN 'qSOFA+SOFA' " +
            " WHEN qsofa.assessment_id IS NOT NULL THEN 'qSOFA' " +
            " WHEN sofa.assessment_id IS NOT NULL THEN 'SOFA' " +
            " ELSE '未评估' END) AS completedAssessments, " +
            
            // 计算评估完整度
            "(CASE WHEN qsofa.assessment_id IS NOT NULL THEN 50 ELSE 0 END + " +
            " CASE WHEN sofa.assessment_id IS NOT NULL THEN 50 ELSE 0 END) AS assessmentCompleteness " +
            
            "FROM patient_info p " +
            
            // 左连接qSOFA评估（通过关联表获取最新的一条记录）
            "LEFT JOIN (" +
            "  SELECT qpr.patient_id, q1.* FROM qsofa_assessment q1 " +
            "  INNER JOIN qsofa_patient_relation qpr ON q1.assessment_id = qpr.assessment_id " +
            "  INNER JOIN (" +
            "    SELECT qpr2.patient_id, MAX(q2.assessment_id) AS max_id " +
            "    FROM qsofa_assessment q2 " +
            "    INNER JOIN qsofa_patient_relation qpr2 ON q2.assessment_id = qpr2.assessment_id " +
            "    WHERE q2.is_deleted = 0 " +
            "    GROUP BY qpr2.patient_id" +
            "  ) latest_q ON qpr.patient_id = latest_q.patient_id AND q1.assessment_id = latest_q.max_id " +
            "  WHERE q1.is_deleted = 0" +
            ") qsofa ON p.patient_id = qsofa.patient_id " +
            
            // 左连接SOFA评估（通过关联表获取最新的一条记录）
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, s1.* FROM sofa_assessment s1 " +
            "  INNER JOIN sofa_patient_relation spr ON s1.assessment_id = spr.assessment_id " +
            "  INNER JOIN (" +
            "    SELECT spr2.patient_id, MAX(s2.assessment_id) AS max_id " +
            "    FROM sofa_assessment s2 " +
            "    INNER JOIN sofa_patient_relation spr2 ON s2.assessment_id = spr2.assessment_id " +
            "    WHERE s2.is_deleted = 0 " +
            "    GROUP BY spr2.patient_id" +
            "  ) latest_s ON spr.patient_id = latest_s.patient_id AND s1.assessment_id = latest_s.max_id " +
            "  WHERE s1.is_deleted = 0" +
            ") sofa ON p.patient_id = sofa.patient_id " +
            
            "WHERE p.is_deleted = 0 " +
            
            // 动态查询条件
            "<if test='patientNumber != null and patientNumber != \"\"'>" +
            "  AND CONCAT('P', LPAD(p.patient_id, 6, '0')) LIKE CONCAT('%', #{patientNumber}, '%') " +
            "</if>" +
            "<if test='patientName != null and patientName != \"\"'>" +
            "  AND CONCAT('患者', p.patient_id) LIKE CONCAT('%', #{patientName}, '%') " +
            "</if>" +
            "<if test='gender != null and gender != \"\"'>" +
            "  AND p.gender = #{gender} " +
            "</if>" +
            "<if test='minAge != null'>" +
            "  AND p.age &gt;= #{minAge} " +
            "</if>" +
            "<if test='maxAge != null'>" +
            "  AND p.age &lt;= #{maxAge} " +
            "</if>" +
            "<if test='hasQsofa != null and hasQsofa'>" +
            "  AND qsofa.qsofa_id IS NOT NULL " +
            "</if>" +
            "<if test='hasSofa != null and hasSofa'>" +
            "  AND sofa.sofa_id IS NOT NULL " +
            "</if>" +
            "<if test='minQsofaScore != null'>" +
            "  AND qsofa.total_score &gt;= #{minQsofaScore} " +
            "</if>" +
            "<if test='minSofaScore != null'>" +
            "  AND sofa.total_score &gt;= #{minSofaScore} " +
            "</if>" +
            "ORDER BY p.patient_id DESC " +
            "</script>")
    IPage<SepsisAssessmentVO> selectSepsisAssessmentPage(
            Page<SepsisAssessmentVO> page,
            @Param("patientNumber") String patientNumber,
            @Param("patientName") String patientName,
            @Param("gender") String gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("hasQsofa") Boolean hasQsofa,
            @Param("hasSofa") Boolean hasSofa,
            @Param("minQsofaScore") Integer minQsofaScore,
            @Param("minSofaScore") Integer minSofaScore
    );
    
    /**
     * 根据患者ID查询脓毒症评估详情
     * 
     * @param patientId 患者ID
     * @return 评估详情
     */
    @Select("SELECT " +
            // 患者基本信息
            "p.patient_id AS patientId, " +
            "CONCAT('P', LPAD(p.patient_id, 6, '0')) AS patientNumber, " +
            "CONCAT('患者', p.patient_id) AS patientName, " +
            "p.gender, " +
            "p.age, " +
            "p.admission_date AS admissionDate, " +
            "p.chief_complaint AS chiefComplaint, " +
            "p.present_illness AS presentIllness, " +
            "NULL AS systolicPressure, " +
            "NULL AS diastolicPressure, " +
            "p.arterial_ph AS arterialPh, " +
            "p.arterial_po2 AS arterialPo2, " +
            "p.blood_urea_nitrogen AS bloodUreaNitrogen, " +
            "p.serum_creatinine AS serumCreatinine, " +
            "p.total_bilirubin AS totalBilirubin, " +
            "p.platelet_count AS plateletCount, " +
            "p.ventilator_used AS ventilatorUsed, " +
            "p.icu_admission AS icuAdmission, " +
            "p.vasoactive_drugs_used AS vasoactiveDrugsUsed, " +
            
            // qSOFA评分
            "qsofa.assessment_id AS qsofaId, " +
            "qsofa.respiratory_rate_high AS qsofaRespiratoryRate, " +
            "qsofa.consciousness_altered AS qsofaMentalStatus, " +
            "qsofa.systolic_bp_low AS qsofaSystolicBp, " +
            "qsofa.total_score AS qsofaTotalScore, " +
            "qsofa.risk_level AS qsofaRiskLevel, " +
            "qsofa.created_at AS qsofaAssessmentTime, " +
            "qsofa.created_by AS qsofaAssessor, " +
            
            // SOFA评分
            "sofa.assessment_id AS sofaId, " +
            "sofa.respiration_score AS sofaRespirationScore, " +
            "sofa.oxygenation_index AS sofaPao2Fio2, " +
            "sofa.coagulation_score AS sofaCoagulationScore, " +
            "sofa.platelet_count AS sofaPlateletCount, " +
            "sofa.liver_score AS sofaLiverScore, " +
            "sofa.bilirubin AS sofaBilirubin, " +
            "sofa.cardiovascular_score AS sofaCardiovascularScore, " +
            "sofa.mean_arterial_pressure AS sofaMeanArterialPressure, " +
            "sofa.vasoactive_drugs AS sofaVasoactiveDrugs, " +
            "sofa.cns_score AS sofaCnsScore, " +
            "sofa.glasgow_coma_score AS sofaGlasgowComaScore, " +
            "sofa.renal_score AS sofaRenalScore, " +
            "sofa.creatinine AS sofaCreatinine, " +
            "sofa.urine_output AS sofaUrineOutput, " +
            "sofa.total_score AS sofaTotalScore, " +
            "sofa.severity_level AS sofaRiskLevel, " +
            "sofa.created_at AS sofaAssessmentTime, " +
            "sofa.created_by AS sofaAssessor " +
            
            "FROM patient_info p " +
            "LEFT JOIN (" +
            "  SELECT qpr.patient_id, qa.* FROM qsofa_assessment qa " +
            "  INNER JOIN qsofa_patient_relation qpr ON qa.assessment_id = qpr.assessment_id " +
            "  WHERE qa.is_deleted = 0 " +
            "  ORDER BY qa.assessment_id DESC " +
            "  LIMIT 1" +
            ") qsofa ON p.patient_id = qsofa.patient_id " +
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, sa.* FROM sofa_assessment sa " +
            "  INNER JOIN sofa_patient_relation spr ON sa.assessment_id = spr.assessment_id " +
            "  WHERE sa.is_deleted = 0 " +
            "  ORDER BY sa.assessment_id DESC " +
            "  LIMIT 1" +
            ") sofa ON p.patient_id = sofa.patient_id " +
            "WHERE p.patient_id = #{patientId} AND p.is_deleted = 0 " +
            "LIMIT 1")
    SepsisAssessmentVO selectByPatientId(@Param("patientId") Long patientId);
    
    /**
     * 查询高风险脓毒症患者（qSOFA≥2或SOFA≥2）
     * 
     * @return 高风险患者列表
     */
    @Select("SELECT DISTINCT " +
            "p.patient_id AS patientId, " +
            "CONCAT('P', LPAD(p.patient_id, 6, '0')) AS patientNumber, " +
            "CONCAT('患者', p.patient_id) AS patientName, " +
            "p.age, " +
            "qsofa.total_score AS qsofaTotalScore, " +
            "sofa.total_score AS sofaTotalScore " +
            "FROM patient_info p " +
            "LEFT JOIN (" +
            "  SELECT qpr.patient_id, MAX(qa.total_score) as total_score " +
            "  FROM qsofa_assessment qa " +
            "  INNER JOIN qsofa_patient_relation qpr ON qa.assessment_id = qpr.assessment_id " +
            "  WHERE qa.is_deleted = 0 " +
            "  GROUP BY qpr.patient_id" +
            ") qsofa ON p.patient_id = qsofa.patient_id " +
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, MAX(sa.total_score) as total_score " +
            "  FROM sofa_assessment sa " +
            "  INNER JOIN sofa_patient_relation spr ON sa.assessment_id = spr.assessment_id " +
            "  WHERE sa.is_deleted = 0 " +
            "  GROUP BY spr.patient_id" +
            ") sofa ON p.patient_id = sofa.patient_id " +
            "WHERE p.is_deleted = 0 " +
            "AND (qsofa.total_score >= 2 OR sofa.total_score >= 2) " +
            "ORDER BY " +
            "GREATEST(IFNULL(qsofa.total_score, 0), IFNULL(sofa.total_score, 0)) DESC")
    List<SepsisAssessmentVO> selectHighRiskPatients();
}
