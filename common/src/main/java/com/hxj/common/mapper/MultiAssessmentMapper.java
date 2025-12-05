package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.MultiAssessmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 多评估表联合查询Mapper
 * 联查patient_info、curb_assessment_result、psi_assessment_result、
 * cpis_assessment_result和severe_pneumonia_diagnosis表
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Mapper
public interface MultiAssessmentMapper extends BaseMapper<MultiAssessmentVO> {
    
    /**
     * 联表查询患者信息和多个评估结果
     * 
     * @param page 分页对象
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
            
            // CURB-65评分
            "curb.curb_id AS curbId, " +
            "curb.confusion_result AS curbConfusionResult, " +
            "curb.urea_result AS curbUreaResult, " +
            "curb.respiration_result AS curbRespirationResult, " +
            "curb.blood_pressure_result AS curbBloodPressureResult, " +
            "curb.age_result AS curbAgeResult, " +
            "curb.total_score AS curbTotalScore, " +
            "curb.risk_level AS curbRiskLevel, " +
            "curb.created_at AS curbAssessmentTime, " +
            
            // PSI评分
            "psi.psi_id AS psiId, " +
            "psi.age_score AS psiAgeScore, " +
            "psi.mental_status_change_score AS psiMentalStatusChangeScore, " +
            "psi.heart_rate_score AS psiHeartRateScore, " +
            "psi.respiratory_rate_score AS psiRespiratoryRateScore, " +
            "psi.systolic_bp_score AS psiSystolicBpScore, " +
            "psi.temperature_score AS psiTemperatureScore, " +
            "psi.arterial_ph_score AS psiArterialPhScore, " +
            "psi.pao2_score AS psiPao2Score, " +
            "psi.pleural_effusion_score AS psiPleuralEffusionScore, " +
            "psi.total_score AS psiTotalScore, " +
            "psi.risk_class AS psiRiskLevel, " +
            "psi.recommended_treatment AS psiRiskDescription, " +
            "psi.created_at AS psiAssessmentTime, " +
            
            // CPIS评分
            "cpis.cpis_id AS cpisId, " +
            "cpis.temperature_score AS cpisTemperatureScore, " +
            "cpis.secretion_score AS cpisSecretionScore, " +
            "cpis.oxygenation_index_score AS cpisOxygenationIndexScore, " +
            "cpis.chest_xray_score AS cpisChestXrayScore, " +
            "cpis.culture_score AS cpisCultureScore, " +
            "cpis.total_score AS cpisTotalScore, " +
            "cpis.risk_level AS cpisRiskLevel, " +
            "cpis.created_at AS cpisAssessmentTime, " +
            
            // 重症肺炎诊断
            "spd.diagnosis_id AS diagnosisId, " +
            "spd.mechanical_ventilation AS mechanicalVentilation, " +
            "spd.vasoactive_drugs AS vasoactiveDrugs, " +
            "spd.respiratory_rate_high AS respiratoryRateHigh, " +
            "spd.respiratory_rate AS respiratoryRate, " +
            "spd.oxygenation_index_low AS oxygenationIndexLow, " +
            "spd.oxygenation_index AS oxygenationIndex, " +
            "spd.consciousness_disorder AS consciousnessDisorder, " +
            "spd.urea_nitrogen_high AS ureaNitrogenHigh, " +
            "spd.urea_nitrogen AS ureaNitrogen, " +
            "spd.hypotension AS hypotension, " +
            "spd.is_severe_pneumonia AS isSeverePneumonia, " +
            "spd.diagnosis_conclusion AS diagnosisConclusion, " +
            "spd.created_at AS diagnosisTime, " +
            
            // 计算已完成的评估数量
            "(CASE WHEN curb.curb_id IS NOT NULL THEN 1 ELSE 0 END + " +
            " CASE WHEN psi.psi_id IS NOT NULL THEN 1 ELSE 0 END + " +
            " CASE WHEN cpis.cpis_id IS NOT NULL THEN 1 ELSE 0 END + " +
            " CASE WHEN spd.diagnosis_id IS NOT NULL THEN 1 ELSE 0 END) AS completedAssessmentCount " +
            
            "FROM patient_info p " +
            
            // 左连接CURB评估（通过关联表取最新的一条记录）
            "LEFT JOIN (" +
            "  SELECT pcr.patient_id, c1.* FROM curb_assessment_result c1 " +
            "  INNER JOIN patient_curb_relation pcr ON c1.curb_id = pcr.curb_id " +
            "  INNER JOIN (" +
            "    SELECT pcr2.patient_id, MAX(c2.created_at) AS max_time " +
            "    FROM curb_assessment_result c2 " +
            "    INNER JOIN patient_curb_relation pcr2 ON c2.curb_id = pcr2.curb_id " +
            "    WHERE c2.is_deleted = 0 AND pcr2.is_deleted = 0 " +
            "    GROUP BY pcr2.patient_id" +
            "  ) c2 ON pcr.patient_id = c2.patient_id AND c1.created_at = c2.max_time " +
            "  WHERE c1.is_deleted = 0 AND pcr.is_deleted = 0" +
            ") curb ON p.patient_id = curb.patient_id " +
            
            // 左连接PSI评估（通过关联表取最新的一条记录）
            "LEFT JOIN (" +
            "  SELECT ppr.patient_id, p1.* FROM psi_assessment_result p1 " +
            "  INNER JOIN psi_patient_relation ppr ON p1.psi_id = ppr.psi_id " +
            "  INNER JOIN (" +
            "    SELECT ppr2.patient_id, MAX(p2.created_at) AS max_time " +
            "    FROM psi_assessment_result p2 " +
            "    INNER JOIN psi_patient_relation ppr2 ON p2.psi_id = ppr2.psi_id " +
            "    WHERE p2.is_deleted = 0 AND ppr2.is_deleted = 0 " +
            "    GROUP BY ppr2.patient_id" +
            "  ) p2 ON ppr.patient_id = p2.patient_id AND p1.created_at = p2.max_time " +
            "  WHERE p1.is_deleted = 0 AND ppr.is_deleted = 0" +
            ") psi ON p.patient_id = psi.patient_id " +
            
            // 左连接CPIS评估（通过关联表取最新的一条记录）
            "LEFT JOIN (" +
            "  SELECT cpr.patient_id, cp1.* FROM cpis_assessment_result cp1 " +
            "  INNER JOIN cpis_patient_relation cpr ON cp1.cpis_id = cpr.cpis_id " +
            "  INNER JOIN (" +
            "    SELECT cpr2.patient_id, MAX(cp2.created_at) AS max_time " +
            "    FROM cpis_assessment_result cp2 " +
            "    INNER JOIN cpis_patient_relation cpr2 ON cp2.cpis_id = cpr2.cpis_id " +
            "    WHERE cp2.is_deleted = 0 AND cpr2.is_deleted = 0 " +
            "    GROUP BY cpr2.patient_id" +
            "  ) cp2 ON cpr.patient_id = cp2.patient_id AND cp1.created_at = cp2.max_time " +
            "  WHERE cp1.is_deleted = 0 AND cpr.is_deleted = 0" +
            ") cpis ON p.patient_id = cpis.patient_id " +
            
            // 左连接重症肺炎诊断（通过关联表取最新的一条记录）
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, s1.* FROM severe_pneumonia_diagnosis s1 " +
            "  INNER JOIN severe_pneumonia_patient_relation spr ON s1.diagnosis_id = spr.diagnosis_id " +
            "  INNER JOIN (" +
            "    SELECT spr2.patient_id, MAX(s2.created_at) AS max_time " +
            "    FROM severe_pneumonia_diagnosis s2 " +
            "    INNER JOIN severe_pneumonia_patient_relation spr2 ON s2.diagnosis_id = spr2.diagnosis_id " +
            "    WHERE s2.is_deleted = 0 AND spr2.is_deleted = 0 " +
            "    GROUP BY spr2.patient_id" +
            "  ) s2 ON spr.patient_id = s2.patient_id AND s1.created_at = s2.max_time " +
            "  WHERE s1.is_deleted = 0 AND spr.is_deleted = 0" +
            ") spd ON p.patient_id = spd.patient_id " +
            
            "WHERE p.is_deleted = 0 " +
            "ORDER BY p.patient_id DESC " +
            "</script>")
    IPage<MultiAssessmentVO> selectMultiAssessmentPage(Page<MultiAssessmentVO> page);
    
    /**
     * 根据患者ID查询详细评估信息
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
            
            // CURB-65评分
            "curb.curb_id AS curbId, " +
            "curb.confusion_result AS curbConfusionResult, " +
            "curb.urea_result AS curbUreaResult, " +
            "curb.respiration_result AS curbRespirationResult, " +
            "curb.blood_pressure_result AS curbBloodPressureResult, " +
            "curb.age_result AS curbAgeResult, " +
            "curb.total_score AS curbTotalScore, " +
            "curb.risk_level AS curbRiskLevel, " +
            "curb.created_at AS curbAssessmentTime, " +
            
            // PSI评分
            "psi.psi_id AS psiId, " +
            "psi.age_score AS psiAgeScore, " +
            "psi.mental_status_change_score AS psiMentalStatusChangeScore, " +
            "psi.heart_rate_score AS psiHeartRateScore, " +
            "psi.respiratory_rate_score AS psiRespiratoryRateScore, " +
            "psi.systolic_bp_score AS psiSystolicBpScore, " +
            "psi.temperature_score AS psiTemperatureScore, " +
            "psi.arterial_ph_score AS psiArterialPhScore, " +
            "psi.pao2_score AS psiPao2Score, " +
            "psi.pleural_effusion_score AS psiPleuralEffusionScore, " +
            "psi.total_score AS psiTotalScore, " +
            "psi.risk_class AS psiRiskLevel, " +
            "psi.recommended_treatment AS psiRiskDescription, " +
            "psi.created_at AS psiAssessmentTime, " +
            
            // CPIS评分
            "cpis.cpis_id AS cpisId, " +
            "cpis.temperature_score AS cpisTemperatureScore, " +
            "cpis.secretion_score AS cpisSecretionScore, " +
            "cpis.oxygenation_index_score AS cpisOxygenationIndexScore, " +
            "cpis.chest_xray_score AS cpisChestXrayScore, " +
            "cpis.culture_score AS cpisCultureScore, " +
            "cpis.total_score AS cpisTotalScore, " +
            "cpis.risk_level AS cpisRiskLevel, " +
            "cpis.created_at AS cpisAssessmentTime, " +
            
            // 重症肺炎诊断
            "spd.diagnosis_id AS diagnosisId, " +
            "spd.mechanical_ventilation AS mechanicalVentilation, " +
            "spd.vasoactive_drugs AS vasoactiveDrugs, " +
            "spd.respiratory_rate_high AS respiratoryRateHigh, " +
            "spd.respiratory_rate AS respiratoryRate, " +
            "spd.oxygenation_index_low AS oxygenationIndexLow, " +
            "spd.oxygenation_index AS oxygenationIndex, " +
            "spd.consciousness_disorder AS consciousnessDisorder, " +
            "spd.urea_nitrogen_high AS ureaNitrogenHigh, " +
            "spd.urea_nitrogen AS ureaNitrogen, " +
            "spd.hypotension AS hypotension, " +
            "spd.is_severe_pneumonia AS isSeverePneumonia, " +
            "spd.diagnosis_conclusion AS diagnosisConclusion, " +
            "spd.created_time AS diagnosisTime " +
            
            "FROM patient_info p " +
            "LEFT JOIN (" +
            "  SELECT pcr.patient_id, c.* FROM curb_assessment_result c " +
            "  INNER JOIN patient_curb_relation pcr ON c.curb_id = pcr.curb_id " +
            "  WHERE c.is_deleted = 0 AND pcr.is_deleted = 0 AND pcr.patient_id = #{patientId} " +
            "  ORDER BY c.created_at DESC LIMIT 1" +
            ") curb ON p.patient_id = curb.patient_id " +
            "LEFT JOIN (" +
            "  SELECT ppr.patient_id, psi.* FROM psi_assessment_result psi " +
            "  INNER JOIN psi_patient_relation ppr ON psi.psi_id = ppr.psi_id " +
            "  WHERE psi.is_deleted = 0 AND ppr.is_deleted = 0 AND ppr.patient_id = #{patientId} " +
            "  ORDER BY psi.created_at DESC LIMIT 1" +
            ") psi ON p.patient_id = psi.patient_id " +
            "LEFT JOIN (" +
            "  SELECT cpr.patient_id, cp.* FROM cpis_assessment_result cp " +
            "  INNER JOIN cpis_patient_relation cpr ON cp.cpis_id = cpr.cpis_id " +
            "  WHERE cp.is_deleted = 0 AND cpr.is_deleted = 0 AND cpr.patient_id = #{patientId} " +
            "  ORDER BY cp.created_at DESC LIMIT 1" +
            ") cpis ON p.patient_id = cpis.patient_id " +
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, spd.* FROM severe_pneumonia_diagnosis spd " +
            "  INNER JOIN severe_pneumonia_patient_relation spr ON spd.diagnosis_id = spr.diagnosis_id " +
            "  WHERE spd.is_deleted = 0 AND spr.is_deleted = 0 AND spr.patient_id = #{patientId} " +
            "  ORDER BY spd.created_at DESC LIMIT 1" +
            ") spd ON p.patient_id = spd.patient_id " +
            "WHERE p.patient_id = #{patientId} AND p.is_deleted = 0 " +
            "LIMIT 1")
    MultiAssessmentVO selectByPatientId(@Param("patientId") Long patientId);
}
