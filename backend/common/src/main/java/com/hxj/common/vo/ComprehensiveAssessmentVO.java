package com.hxj.common.vo;

import lombok.Data;
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
    
    // ==================== CURB-65评分 ====================
    /**
     * CURB-65评分ID
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
    
    /**
     * CURB-65评估时间
     */
    private LocalDateTime curbAssessmentDate;
    
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
    
    /**
     * COVID-19重型评估时间
     */
    private LocalDateTime covid19AssessmentDate;
    
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
    
    /**
     * COVID-19危重型评估时间
     */
    private LocalDateTime covid19CriticalAssessmentDate;
    
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
    
    /**
     * CPIS评估时间
     */
    private LocalDateTime cpisAssessmentDate;
    
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
    
    /**
     * PSI评估时间
     */
    private LocalDateTime psiAssessmentDate;
    
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
    
    /**
     * qSOFA评估时间
     */
    private LocalDateTime qsofaAssessmentDate;
    
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
    
    /**
     * 重症肺炎评估时间
     */
    private LocalDateTime severePneumoniaAssessmentDate;
    
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
    
    /**
     * SOFA评估时间
     */
    private LocalDateTime sofaAssessmentDate;
    
    /**
     * 最新评估时间（所有评估中最近的时间）
     */
    private LocalDateTime latestAssessmentDate;
}
