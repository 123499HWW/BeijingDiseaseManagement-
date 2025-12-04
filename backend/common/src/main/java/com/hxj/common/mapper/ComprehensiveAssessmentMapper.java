package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.ComprehensiveAssessmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 综合评估查询Mapper
 * 提供多表连接的综合查询功能
 */
@Mapper
public interface ComprehensiveAssessmentMapper {
    
    /**
     * 综合评估分页查询
     * 注意：为了性能优化，每个评估表只获取最新的一条记录
     */
    @Select("<script>" +
            "SELECT " +
            "p.patient_id, p.patient_number, p.gender, p.age, " +
            "p.admission_date, p.chief_complaint, p.present_illness, " +
            "p.arterial_ph, p.arterial_po2, p.arterial_oxygenation_index, " +
            "p.blood_urea_nitrogen, p.serum_creatinine, p.total_bilirubin, p.platelet_count, " +
            "p.ventilator_used, p.icu_admission, p.vasoactive_drugs_used, p.special_antibiotics_used, " +
            // CURB-65评分
            "curb.curb_id, curb.total_score as curbTotalScore, curb.risk_level as curbRiskLevel, " +
            // COVID-19重型
            "c19.assessment_id as covid19AssessmentId, c19.is_severe_type as covid19IsSevereType, " +
            "c19.criteria_met_count as covid19CriteriaCount, c19.severity_level as covid19SeverityLevel, " +
            // COVID-19危重型
            "c19c.assessment_id as covid19CriticalAssessmentId, c19c.is_critical_type as covid19IsCriticalType, " +
            "c19c.criteria_met_count as covid19CriticalCriteriaCount, c19c.severity_level as covid19CriticalSeverityLevel, " +
            // CPIS评分
            "cpis.cpis_id as cpisId, cpis.total_score as cpisTotalScore, cpis.risk_level as cpisRiskLevel, " +
            // PSI评分
            "psi.psi_id as psiId, psi.total_score as psiTotalScore, psi.risk_class as psiRiskClass, " +
            // qSOFA评分
            "qsofa.assessment_id as qsofaAssessmentId, qsofa.total_score as qsofaTotalScore, " +
            "qsofa.risk_level as qsofaRiskLevel, " +
            // 重症肺炎诊断
            "spd.diagnosis_id as severePneumoniaId, spd.is_severe_pneumonia as isSeverePneumonia, " +
            "spd.major_criteria_count as majorCriteriaCount, spd.minor_criteria_count as minorCriteriaCount, " +
            // SOFA评分
            "sofa.assessment_id as sofaAssessmentId, sofa.total_score as sofaTotalScore, " +
            "sofa.severity_level as sofaSeverityLevel, " +
            // 呼吸道症候群评估
            "rsa.syndrome_id as respiratorySyndromeId, rsa.severity_score as respiratorySyndromeSeverityScore, " +
            "rsa.severity_level as respiratorySyndromeSeverityLevel " +
            "FROM patient_info p " +
            // CURB-65 LEFT JOIN（使用子查询获取最新记录）
            "LEFT JOIN (" +
            "  SELECT pcr.patient_id, car.* FROM curb_assessment_result car " +
            "  INNER JOIN patient_curb_relation pcr ON car.curb_id = pcr.curb_id " +
            "  WHERE car.is_deleted = 0 AND car.curb_id IN (" +
            "    SELECT MAX(car2.curb_id) FROM curb_assessment_result car2 " +
            "    INNER JOIN patient_curb_relation pcr2 ON car2.curb_id = pcr2.curb_id " +
            "    WHERE car2.is_deleted = 0 GROUP BY pcr2.patient_id" +
            "  )" +
            ") curb ON p.patient_id = curb.patient_id " +
            // COVID-19重型 LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT cpr.patient_id, ca.* FROM covid19_assessment ca " +
            "  INNER JOIN covid19_patient_relation cpr ON ca.assessment_id = cpr.assessment_id " +
            "  WHERE ca.is_deleted = 0 AND ca.assessment_id IN (" +
            "    SELECT MAX(ca2.assessment_id) FROM covid19_assessment ca2 " +
            "    INNER JOIN covid19_patient_relation cpr2 ON ca2.assessment_id = cpr2.assessment_id " +
            "    WHERE ca2.is_deleted = 0 GROUP BY cpr2.patient_id" +
            "  )" +
            ") c19 ON p.patient_id = c19.patient_id " +
            // COVID-19危重型 LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT ccpr.patient_id, cca.* FROM covid19_critical_assessment cca " +
            "  INNER JOIN covid19_critical_patient_relation ccpr ON cca.assessment_id = ccpr.assessment_id " +
            "  WHERE cca.is_deleted = 0 AND cca.assessment_id IN (" +
            "    SELECT MAX(cca2.assessment_id) FROM covid19_critical_assessment cca2 " +
            "    INNER JOIN covid19_critical_patient_relation ccpr2 ON cca2.assessment_id = ccpr2.assessment_id " +
            "    WHERE cca2.is_deleted = 0 GROUP BY ccpr2.patient_id" +
            "  )" +
            ") c19c ON p.patient_id = c19c.patient_id " +
            // CPIS LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT cpr.patient_id, car.* FROM cpis_assessment_result car " +
            "  INNER JOIN cpis_patient_relation cpr ON car.cpis_id = cpr.cpis_id " +
            "  WHERE car.is_deleted = 0 AND car.cpis_id IN (" +
            "    SELECT MAX(car2.cpis_id) FROM cpis_assessment_result car2 " +
            "    INNER JOIN cpis_patient_relation cpr2 ON car2.cpis_id = cpr2.cpis_id " +
            "    WHERE car2.is_deleted = 0 GROUP BY cpr2.patient_id" +
            "  )" +
            ") cpis ON p.patient_id = cpis.patient_id " +
            // PSI LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT ppr.patient_id, par.* FROM psi_assessment_result par " +
            "  INNER JOIN psi_patient_relation ppr ON par.psi_id = ppr.psi_id " +
            "  WHERE par.is_deleted = 0 AND par.psi_id IN (" +
            "    SELECT MAX(par2.psi_id) FROM psi_assessment_result par2 " +
            "    INNER JOIN psi_patient_relation ppr2 ON par2.psi_id = ppr2.psi_id " +
            "    WHERE par2.is_deleted = 0 GROUP BY ppr2.patient_id" +
            "  )" +
            ") psi ON p.patient_id = psi.patient_id " +
            // qSOFA LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT qpr.patient_id, qa.* FROM qsofa_assessment qa " +
            "  INNER JOIN qsofa_patient_relation qpr ON qa.assessment_id = qpr.assessment_id " +
            "  WHERE qa.is_deleted = 0 AND qa.assessment_id IN (" +
            "    SELECT MAX(qa2.assessment_id) FROM qsofa_assessment qa2 " +
            "    INNER JOIN qsofa_patient_relation qpr2 ON qa2.assessment_id = qpr2.assessment_id " +
            "    WHERE qa2.is_deleted = 0 GROUP BY qpr2.patient_id" +
            "  )" +
            ") qsofa ON p.patient_id = qsofa.patient_id " +
            // 重症肺炎诊断 LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, spd2.* FROM severe_pneumonia_diagnosis spd2 " +
            "  INNER JOIN severe_pneumonia_patient_relation spr ON spd2.diagnosis_id = spr.diagnosis_id " +
            "  WHERE spd2.is_deleted = 0 AND spd2.diagnosis_id IN (" +
            "    SELECT MAX(spd3.diagnosis_id) FROM severe_pneumonia_diagnosis spd3 " +
            "    INNER JOIN severe_pneumonia_patient_relation spr2 ON spd3.diagnosis_id = spr2.diagnosis_id " +
            "    WHERE spd3.is_deleted = 0 GROUP BY spr2.patient_id" +
            "  )" +
            ") spd ON p.patient_id = spd.patient_id " +
            // SOFA LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, sa.* FROM sofa_assessment sa " +
            "  INNER JOIN sofa_patient_relation spr ON sa.assessment_id = spr.assessment_id " +
            "  WHERE sa.is_deleted = 0 AND sa.assessment_id IN (" +
            "    SELECT MAX(sa2.assessment_id) FROM sofa_assessment sa2 " +
            "    INNER JOIN sofa_patient_relation spr2 ON sa2.assessment_id = spr2.assessment_id " +
            "    WHERE sa2.is_deleted = 0 GROUP BY spr2.patient_id" +
            "  )" +
            ") sofa ON p.patient_id = sofa.patient_id " +
            // 呼吸道症候群评估 LEFT JOIN
            "LEFT JOIN (" +
            "  SELECT spr.patient_id, rsa2.* FROM respiratory_syndrome_assessment rsa2 " +
            "  INNER JOIN syndrome_patient_relation spr ON rsa2.syndrome_id = spr.syndrome_id " +
            "  WHERE rsa2.is_deleted = 0 AND rsa2.syndrome_id IN (" +
            "    SELECT MAX(rsa3.syndrome_id) FROM respiratory_syndrome_assessment rsa3 " +
            "    INNER JOIN syndrome_patient_relation spr2 ON rsa3.syndrome_id = spr2.syndrome_id " +
            "    WHERE rsa3.is_deleted = 0 GROUP BY spr2.patient_id" +
            "  )" +
            ") rsa ON p.patient_id = rsa.patient_id " +
            "WHERE p.is_deleted = 0 " +
            // 动态查询条件
            "<if test='patientNumber != null and patientNumber != \"\"'>" +
            "  AND p.patient_number LIKE CONCAT('%', #{patientNumber}, '%') " +
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
            // CURB-65条件
            "<if test='curbTotalScore != null'>" +
            "  AND curb.total_score = #{curbTotalScore} " +
            "</if>" +
            "<if test='curbRiskLevel != null and curbRiskLevel != \"\"'>" +
            "  AND curb.risk_level = #{curbRiskLevel} " +
            "</if>" +
            "<if test='minCurbScore != null'>" +
            "  AND curb.total_score &gt;= #{minCurbScore} " +
            "</if>" +
            "<if test='maxCurbScore != null'>" +
            "  AND curb.total_score &lt;= #{maxCurbScore} " +
            "</if>" +
            // COVID-19重型条件
            "<if test='covid19IsSevereType != null'>" +
            "  AND c19.is_severe_type = #{covid19IsSevereType} " +
            "</if>" +
            "<if test='covid19SeverityLevel != null and covid19SeverityLevel != \"\"'>" +
            "  AND c19.severity_level = #{covid19SeverityLevel} " +
            "</if>" +
            // COVID-19危重型条件
            "<if test='covid19IsCriticalType != null'>" +
            "  AND c19c.is_critical_type = #{covid19IsCriticalType} " +
            "</if>" +
            "<if test='covid19CriticalSeverityLevel != null and covid19CriticalSeverityLevel != \"\"'>" +
            "  AND c19c.severity_level = #{covid19CriticalSeverityLevel} " +
            "</if>" +
            // CPIS条件
            "<if test='cpisTotalScore != null'>" +
            "  AND cpis.total_score = #{cpisTotalScore} " +
            "</if>" +
            "<if test='cpisRiskLevel != null and cpisRiskLevel != \"\"'>" +
            "  AND cpis.risk_level = #{cpisRiskLevel} " +
            "</if>" +
            "<if test='minCpisScore != null'>" +
            "  AND cpis.total_score &gt;= #{minCpisScore} " +
            "</if>" +
            "<if test='maxCpisScore != null'>" +
            "  AND cpis.total_score &lt;= #{maxCpisScore} " +
            "</if>" +
            // PSI条件
            "<if test='psiTotalScore != null'>" +
            "  AND psi.total_score = #{psiTotalScore} " +
            "</if>" +
            "<if test='psiRiskClass != null and psiRiskClass != \"\"'>" +
            "  AND psi.risk_class = #{psiRiskClass} " +
            "</if>" +
            "<if test='minPsiScore != null'>" +
            "  AND psi.total_score &gt;= #{minPsiScore} " +
            "</if>" +
            "<if test='maxPsiScore != null'>" +
            "  AND psi.total_score &lt;= #{maxPsiScore} " +
            "</if>" +
            // qSOFA条件
            "<if test='qsofaTotalScore != null'>" +
            "  AND qsofa.total_score = #{qsofaTotalScore} " +
            "</if>" +
            "<if test='qsofaRiskLevel != null and qsofaRiskLevel != \"\"'>" +
            "  AND qsofa.risk_level = #{qsofaRiskLevel} " +
            "</if>" +
            "<if test='minQsofaScore != null'>" +
            "  AND qsofa.total_score &gt;= #{minQsofaScore} " +
            "</if>" +
            "<if test='maxQsofaScore != null'>" +
            "  AND qsofa.total_score &lt;= #{maxQsofaScore} " +
            "</if>" +
            // 重症肺炎条件
            "<if test='isSeverePneumonia != null'>" +
            "  AND spd.is_severe_pneumonia = #{isSeverePneumonia} " +
            "</if>" +
            // SOFA条件
            "<if test='sofaTotalScore != null'>" +
            "  AND sofa.total_score = #{sofaTotalScore} " +
            "</if>" +
            "<if test='sofaSeverityLevel != null and sofaSeverityLevel != \"\"'>" +
            "  AND sofa.severity_level = #{sofaSeverityLevel} " +
            "</if>" +
            "<if test='minSofaScore != null'>" +
            "  AND sofa.total_score &gt;= #{minSofaScore} " +
            "</if>" +
            "<if test='maxSofaScore != null'>" +
            "  AND sofa.total_score &lt;= #{maxSofaScore} " +
            "</if>" +
            // 呼吸道症候群条件
            "<if test='respiratorySyndromeSeverityLevel != null and respiratorySyndromeSeverityLevel != \"\"'>" +
            "  AND rsa.severity_level = #{respiratorySyndromeSeverityLevel} " +
            "</if>" +
            "<if test='minRespiratorySyndromeScore != null'>" +
            "  AND rsa.severity_score &gt;= #{minRespiratorySyndromeScore} " +
            "</if>" +
            "<if test='maxRespiratorySyndromeScore != null'>" +
            "  AND rsa.severity_score &lt;= #{maxRespiratorySyndromeScore} " +
            "</if>" +
            "ORDER BY p.patient_id DESC" +
            "</script>")
    IPage<ComprehensiveAssessmentVO> selectComprehensivePage(
            Page<ComprehensiveAssessmentVO> page,
            @Param("patientNumber") String patientNumber,
            @Param("gender") String gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("curbTotalScore") Integer curbTotalScore,
            @Param("curbRiskLevel") String curbRiskLevel,
            @Param("minCurbScore") Integer minCurbScore,
            @Param("maxCurbScore") Integer maxCurbScore,
            @Param("covid19IsSevereType") Boolean covid19IsSevereType,
            @Param("covid19SeverityLevel") String covid19SeverityLevel,
            @Param("covid19IsCriticalType") Boolean covid19IsCriticalType,
            @Param("covid19CriticalSeverityLevel") String covid19CriticalSeverityLevel,
            @Param("cpisTotalScore") Integer cpisTotalScore,
            @Param("cpisRiskLevel") String cpisRiskLevel,
            @Param("minCpisScore") Integer minCpisScore,
            @Param("maxCpisScore") Integer maxCpisScore,
            @Param("psiTotalScore") Integer psiTotalScore,
            @Param("psiRiskClass") String psiRiskClass,
            @Param("minPsiScore") Integer minPsiScore,
            @Param("maxPsiScore") Integer maxPsiScore,
            @Param("qsofaTotalScore") Integer qsofaTotalScore,
            @Param("qsofaRiskLevel") String qsofaRiskLevel,
            @Param("minQsofaScore") Integer minQsofaScore,
            @Param("maxQsofaScore") Integer maxQsofaScore,
            @Param("isSeverePneumonia") Boolean isSeverePneumonia,
            @Param("sofaTotalScore") Integer sofaTotalScore,
            @Param("sofaSeverityLevel") String sofaSeverityLevel,
            @Param("minSofaScore") Integer minSofaScore,
            @Param("maxSofaScore") Integer maxSofaScore,
            @Param("respiratorySyndromeSeverityLevel") String respiratorySyndromeSeverityLevel,
            @Param("minRespiratorySyndromeScore") Integer minRespiratorySyndromeScore,
            @Param("maxRespiratorySyndromeScore") Integer maxRespiratorySyndromeScore
    );
}
