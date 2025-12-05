package com.hxj.common.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 综合评估结果VO
 * 包含患者信息和所有评估结果
 */
@Data
public class ComprehensiveAssessmentVO {
    
    // ==================== 患者基本信息 ====================
    /**
     * 患者ID
     */
    private Long patientId;
    
    /**
     * 患者编号
     */
    private String patientNumber;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 住院日期
     */
    private String admissionDate;
    
    /**
     * 主诉
     */
    private String chiefComplaint;
    
    /**
     * 现病史
     */
    private String presentIllness;
    
    /**
     * 动脉血气pH
     */
    private String arterialPh;
    
    /**
     * 动脉血气pO2(mmHg)
     */
    private String arterialPo2;
    
    /**
     * 动脉血气氧合指数(mmHg)
     */
    private String arterialOxygenationIndex;
    
    /**
     * 血尿素氮(mmol/L)
     */
    private String bloodUreaNitrogen;
    
    /**
     * 血肌酐(μmol/L)
     */
    private String serumCreatinine;
    
    /**
     * 总胆红素(μmol/L)
     */
    private String totalBilirubin;
    
    /**
     * 血小板计数(×10^9/L)
     */
    private String plateletCount;
    
    /**
     * 是否应用呼吸机
     */
    private Boolean ventilatorUsed;
    
    /**
     * 是否入住ICU
     */
    private Boolean icuAdmission;
    
    /**
     * 是否应用血管活性药物
     */
    private Boolean vasoactiveDrugsUsed;
    
    /**
     * 是否应用特殊级/限制级抗生素
     */
    private Boolean specialAntibioticsUsed;
    
    // ==================== CURB-65评分详细字段 ====================
    /**
     * CURB评分ID
     */
    private Long curbId;
    
    /**
     * CURB-65总分
     */
    private Integer curbTotalScore;
    
    /**
     * CURB-65风险等级
     */
    private String curbRiskLevel;
    
    // ==================== COVID-19重型诊断 ====================
    /**
     * COVID-19重型诊断ID
     */
    private Long covid19AssessmentId;
    
    /**
     * 是否为COVID-19重型
     */
    private Boolean covid19IsSevereType;
    
    /**
     * COVID-19重型符合条件数
     */
    private Integer covid19CriteriaCount;
    
    /**
     * COVID-19重型严重程度
     */
    private String covid19SeverityLevel;
    
    // ==================== COVID-19危重型诊断 ====================
    /**
     * COVID-19危重型诊断ID
     */
    private Long covid19CriticalAssessmentId;
    
    /**
     * 是否为COVID-19危重型
     */
    private Boolean covid19IsCriticalType;
    
    /**
     * COVID-19危重型符合条件数
     */
    private Integer covid19CriticalCriteriaCount;
    
    /**
     * COVID-19危重型严重程度
     */
    private String covid19CriticalSeverityLevel;
    
    // ==================== CPIS评分 ====================
    /**
     * CPIS评分ID
     */
    private Long cpisId;
    
    /**
     * CPIS总分
     */
    private Integer cpisTotalScore;
    
    /**
     * CPIS风险等级
     */
    private String cpisRiskLevel;
    
    // ==================== PSI评分 ====================
    /**
     * PSI评分ID
     */
    private Long psiId;
    
    /**
     * PSI总分
     */
    private Integer psiTotalScore;
    
    /**
     * PSI风险等级
     */
    private String psiRiskClass;
    
    // ==================== qSOFA评分 ====================
    /**
     * qSOFA评分ID
     */
    private Long qsofaAssessmentId;
    
    /**
     * qSOFA总分
     */
    private Integer qsofaTotalScore;
    
    /**
     * qSOFA风险等级
     */
    private String qsofaRiskLevel;
    
    // ==================== 重症肺炎诊断 ====================
    /**
     * 重症肺炎诊断ID
     */
    private Long severePneumoniaId;
    
    /**
     * 是否为重症肺炎
     */
    private Boolean isSeverePneumonia;
    
    /**
     * 主要标准符合数
     */
    private Integer majorCriteriaCount;
    
    /**
     * 次要标准符合数
     */
    private Integer minorCriteriaCount;
    
    // ==================== SOFA评分 ====================
    /**
     * SOFA评分ID
     */
    private Long sofaAssessmentId;
    
    /**
     * SOFA总分
     */
    private Integer sofaTotalScore;
    
    /**
     * SOFA严重程度
     */
    private String sofaSeverityLevel;
    
    // ==================== 呼吸道症候群评估 ====================
    /**
     * 呼吸道症候群评估ID
     */
    private Long respiratorySyndromeId;
    
    /**
     * 呼吸道症候群严重程度评分
     */
    private Integer respiratorySyndromeSeverityScore;
    
    /**
     * 呼吸道症候群严重程度等级
     */
    private String respiratorySyndromeSeverityLevel;
    
    // ==================== 综合判断结果 ====================
    /**
     * 社区获得性肺炎风险等级
     * 基于CURB-65、PSI、CPIS和重症肺炎诊断的综合判断
     */
    private String communityAcquiredPneumoniaRisk;
    
    /**
     * 脓毒症风险等级
     * 基于qSOFA和SOFA的综合判断
     */
    private String sepsisRisk;
}
