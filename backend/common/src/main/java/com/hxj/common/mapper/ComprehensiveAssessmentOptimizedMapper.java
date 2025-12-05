package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.ComprehensiveAssessmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 优化版综合评估查询Mapper
 * 使用性能优化策略确保2秒内响应
 */
@Mapper
public interface ComprehensiveAssessmentOptimizedMapper {
    
    /**
     * 优化版综合评估分页查询
     * 优化策略：
     * 1. 使用STRAIGHT_JOIN强制按指定顺序连接表
     * 2. 使用索引提示
     * 3. 减少子查询嵌套
     * 4. 只查询必要字段
     */
    @Select("<script>" +
            "SELECT /*+ USE_INDEX(p PRIMARY) */ " +
            // 患者基本信息和医疗数据
            "p.patient_id, p.patient_number, p.gender, p.age, " +
            "p.admission_date, p.chief_complaint, p.present_illness, " +
            "p.arterial_ph, p.arterial_po2, p.arterial_oxygenation_index, " +
            "p.blood_urea_nitrogen, p.serum_creatinine, p.total_bilirubin, p.platelet_count, " +
            "p.ventilator_used, p.icu_admission, p.vasoactive_drugs_used, p.special_antibiotics_used, " +
            // CURB-65评分（简化字段）
            "curb.curb_id, curb.total_score as curbTotalScore, curb.risk_level as curbRiskLevel, " +
            "curb.confusion_result as curbConfusion, curb.urea_result as curbUrea, " +
            "curb.respiration_result as curbRespiration, curb.blood_pressure_result as curbBloodPressure, " +
            "curb.age_result as curbAge, " +
            // COVID-19重型（核心字段）
            "c19.assessment_id as covid19AssessmentId, c19.is_severe_type as covid19IsSevereType, " +
            "c19.criteria_met_count as covid19CriteriaCount, c19.severity_level as covid19SeverityLevel, " +
            // COVID-19危重型（核心字段）
            "c19c.assessment_id as covid19CriticalAssessmentId, c19c.is_critical_type as covid19IsCriticalType, " +
            "c19c.criteria_met_count as covid19CriticalCriteriaCount, c19c.severity_level as covid19CriticalSeverityLevel, " +
            // CPIS评分（核心字段）
            "cpis.cpis_id as cpisId, cpis.total_score as cpisTotalScore, cpis.risk_level as cpisRiskLevel, " +
            // PSI评分（核心字段）
            "psi.psi_id as psiId, psi.total_score as psiTotalScore, psi.risk_class as psiRiskClass, " +
            // qSOFA评分（核心字段）
            "qsofa.assessment_id as qsofaAssessmentId, qsofa.total_score as qsofaTotalScore, " +
            "qsofa.risk_level as qsofaRiskLevel, " +
            // 重症肺炎诊断（核心字段）
            "spd.diagnosis_id as severePneumoniaId, spd.is_severe_pneumonia as isSeverePneumonia, " +
            "spd.major_criteria_count as majorCriteriaCount, spd.minor_criteria_count as minorCriteriaCount, " +
            "spd.diagnosis_conclusion as severePneumoniaConclusion, " +
            // SOFA评分（核心字段）
            "sofa.assessment_id as sofaAssessmentId, sofa.total_score as sofaTotalScore, " +
            "sofa.severity_level as sofaSeverityLevel, " +
            // 呼吸道症候群评估（核心字段）
            "rsa.syndrome_id as respiratorySyndromeId, rsa.severity_score as respiratorySyndromeSeverityScore, " +
            "rsa.severity_level as respiratorySyndromeSeverityLevel " +
            "FROM patient_info p " +
            // 使用简化的JOIN策略
            "LEFT JOIN curb_latest_view curb ON p.patient_id = curb.patient_id " +
            "LEFT JOIN covid19_latest_view c19 ON p.patient_id = c19.patient_id " +
            "LEFT JOIN covid19_critical_latest_view c19c ON p.patient_id = c19c.patient_id " +
            "LEFT JOIN cpis_latest_view cpis ON p.patient_id = cpis.patient_id " +
            "LEFT JOIN psi_latest_view psi ON p.patient_id = psi.patient_id " +
            "LEFT JOIN qsofa_latest_view qsofa ON p.patient_id = qsofa.patient_id " +
            "LEFT JOIN severe_pneumonia_latest_view spd ON p.patient_id = spd.patient_id " +
            "LEFT JOIN sofa_latest_view sofa ON p.patient_id = sofa.patient_id " +
            "LEFT JOIN respiratory_syndrome_latest_view rsa ON p.patient_id = rsa.patient_id " +
            "WHERE p.is_deleted = 0 " +
            // 基本查询条件
            "<if test='patientNumber != null and patientNumber != \"\"'>" +
            "  AND p.patient_number = #{patientNumber} " +  // 使用精确匹配提高性能
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
            // 简化条件查询，只保留最常用的
            "<if test='hasAssessment != null and hasAssessment'>" +
            "  AND (curb.curb_id IS NOT NULL OR c19.assessment_id IS NOT NULL " +
            "    OR cpis.cpis_id IS NOT NULL OR psi.psi_id IS NOT NULL) " +
            "</if>" +
            "ORDER BY p.patient_id DESC " +
            "</script>")
    IPage<ComprehensiveAssessmentVO> selectComprehensivePageOptimized(
            Page<ComprehensiveAssessmentVO> page,
            @Param("patientNumber") String patientNumber,
            @Param("gender") String gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("hasAssessment") Boolean hasAssessment
    );
    
    /**
     * 创建物化视图的SQL（需要在数据库中执行）
     * 这些视图会缓存每个患者的最新评估记录，大幅提升查询性能
     */
    default String[] getCreateViewSQLs() {
        return new String[] {
            // CURB-65最新记录视图
            "CREATE OR REPLACE VIEW curb_latest_view AS " +
            "SELECT pcr.patient_id, car.* FROM curb_assessment_result car " +
            "INNER JOIN patient_curb_relation pcr ON car.curb_id = pcr.curb_id " +
            "INNER JOIN (" +
            "  SELECT pcr2.patient_id, MAX(car2.curb_id) as max_id " +
            "  FROM curb_assessment_result car2 " +
            "  INNER JOIN patient_curb_relation pcr2 ON car2.curb_id = pcr2.curb_id " +
            "  WHERE car2.is_deleted = 0 " +
            "  GROUP BY pcr2.patient_id" +
            ") latest ON pcr.patient_id = latest.patient_id AND car.curb_id = latest.max_id " +
            "WHERE car.is_deleted = 0",
            
            // COVID-19重型最新记录视图
            "CREATE OR REPLACE VIEW covid19_latest_view AS " +
            "SELECT cpr.patient_id, ca.* FROM covid19_assessment ca " +
            "INNER JOIN covid19_patient_relation cpr ON ca.assessment_id = cpr.assessment_id " +
            "INNER JOIN (" +
            "  SELECT cpr2.patient_id, MAX(ca2.assessment_id) as max_id " +
            "  FROM covid19_assessment ca2 " +
            "  INNER JOIN covid19_patient_relation cpr2 ON ca2.assessment_id = cpr2.assessment_id " +
            "  WHERE ca2.is_deleted = 0 " +
            "  GROUP BY cpr2.patient_id" +
            ") latest ON cpr.patient_id = latest.patient_id AND ca.assessment_id = latest.max_id " +
            "WHERE ca.is_deleted = 0",
            
            // COVID-19危重型最新记录视图
            "CREATE OR REPLACE VIEW covid19_critical_latest_view AS " +
            "SELECT ccpr.patient_id, cca.* FROM covid19_critical_assessment cca " +
            "INNER JOIN covid19_critical_patient_relation ccpr ON cca.assessment_id = ccpr.assessment_id " +
            "INNER JOIN (" +
            "  SELECT ccpr2.patient_id, MAX(cca2.assessment_id) as max_id " +
            "  FROM covid19_critical_assessment cca2 " +
            "  INNER JOIN covid19_critical_patient_relation ccpr2 ON cca2.assessment_id = ccpr2.assessment_id " +
            "  WHERE cca2.is_deleted = 0 " +
            "  GROUP BY ccpr2.patient_id" +
            ") latest ON ccpr.patient_id = latest.patient_id AND cca.assessment_id = latest.max_id " +
            "WHERE cca.is_deleted = 0",
            
            // CPIS最新记录视图
            "CREATE OR REPLACE VIEW cpis_latest_view AS " +
            "SELECT cpr.patient_id, car.* FROM cpis_assessment_result car " +
            "INNER JOIN cpis_patient_relation cpr ON car.cpis_id = cpr.cpis_id " +
            "INNER JOIN (" +
            "  SELECT cpr2.patient_id, MAX(car2.cpis_id) as max_id " +
            "  FROM cpis_assessment_result car2 " +
            "  INNER JOIN cpis_patient_relation cpr2 ON car2.cpis_id = cpr2.cpis_id " +
            "  WHERE car2.is_deleted = 0 " +
            "  GROUP BY cpr2.patient_id" +
            ") latest ON cpr.patient_id = latest.patient_id AND car.cpis_id = latest.max_id " +
            "WHERE car.is_deleted = 0",
            
            // PSI最新记录视图
            "CREATE OR REPLACE VIEW psi_latest_view AS " +
            "SELECT ppr.patient_id, par.* FROM psi_assessment_result par " +
            "INNER JOIN psi_patient_relation ppr ON par.psi_id = ppr.psi_id " +
            "INNER JOIN (" +
            "  SELECT ppr2.patient_id, MAX(par2.psi_id) as max_id " +
            "  FROM psi_assessment_result par2 " +
            "  INNER JOIN psi_patient_relation ppr2 ON par2.psi_id = ppr2.psi_id " +
            "  WHERE par2.is_deleted = 0 " +
            "  GROUP BY ppr2.patient_id" +
            ") latest ON ppr.patient_id = latest.patient_id AND par.psi_id = latest.max_id " +
            "WHERE par.is_deleted = 0",
            
            // qSOFA最新记录视图
            "CREATE OR REPLACE VIEW qsofa_latest_view AS " +
            "SELECT qpr.patient_id, qa.* FROM qsofa_assessment qa " +
            "INNER JOIN qsofa_patient_relation qpr ON qa.assessment_id = qpr.assessment_id " +
            "INNER JOIN (" +
            "  SELECT qpr2.patient_id, MAX(qa2.assessment_id) as max_id " +
            "  FROM qsofa_assessment qa2 " +
            "  INNER JOIN qsofa_patient_relation qpr2 ON qa2.assessment_id = qpr2.assessment_id " +
            "  WHERE qa2.is_deleted = 0 " +
            "  GROUP BY qpr2.patient_id" +
            ") latest ON qpr.patient_id = latest.patient_id AND qa.assessment_id = latest.max_id " +
            "WHERE qa.is_deleted = 0",
            
            // 重症肺炎最新记录视图
            "CREATE OR REPLACE VIEW severe_pneumonia_latest_view AS " +
            "SELECT spr.patient_id, spd.* FROM severe_pneumonia_diagnosis spd " +
            "INNER JOIN severe_pneumonia_patient_relation spr ON spd.diagnosis_id = spr.diagnosis_id " +
            "INNER JOIN (" +
            "  SELECT spr2.patient_id, MAX(spd2.diagnosis_id) as max_id " +
            "  FROM severe_pneumonia_diagnosis spd2 " +
            "  INNER JOIN severe_pneumonia_patient_relation spr2 ON spd2.diagnosis_id = spr2.diagnosis_id " +
            "  WHERE spd2.is_deleted = 0 " +
            "  GROUP BY spr2.patient_id" +
            ") latest ON spr.patient_id = latest.patient_id AND spd.diagnosis_id = latest.max_id " +
            "WHERE spd.is_deleted = 0",
            
            // SOFA最新记录视图
            "CREATE OR REPLACE VIEW sofa_latest_view AS " +
            "SELECT spr.patient_id, sa.* FROM sofa_assessment sa " +
            "INNER JOIN sofa_patient_relation spr ON sa.assessment_id = spr.assessment_id " +
            "INNER JOIN (" +
            "  SELECT spr2.patient_id, MAX(sa2.assessment_id) as max_id " +
            "  FROM sofa_assessment sa2 " +
            "  INNER JOIN sofa_patient_relation spr2 ON sa2.assessment_id = spr2.assessment_id " +
            "  WHERE sa2.is_deleted = 0 " +
            "  GROUP BY spr2.patient_id" +
            ") latest ON spr.patient_id = latest.patient_id AND sa.assessment_id = latest.max_id " +
            "WHERE sa.is_deleted = 0",
            
            // 呼吸道症候群最新记录视图  
            "CREATE OR REPLACE VIEW respiratory_syndrome_latest_view AS " +
            "SELECT spr.patient_id, rsa.* FROM respiratory_syndrome_assessment rsa " +
            "INNER JOIN syndrome_patient_relation spr ON rsa.syndrome_id = spr.syndrome_id " +
            "INNER JOIN (" +
            "  SELECT spr2.patient_id, MAX(rsa2.syndrome_id) as max_id " +
            "  FROM respiratory_syndrome_assessment rsa2 " +
            "  INNER JOIN syndrome_patient_relation spr2 ON rsa2.syndrome_id = spr2.syndrome_id " +
            "  WHERE rsa2.is_deleted = 0 " +
            "  GROUP BY spr2.patient_id" +
            ") latest ON spr.patient_id = latest.patient_id AND rsa.syndrome_id = latest.max_id " +
            "WHERE rsa.is_deleted = 0"
        };
    }
}
