package com.hxj.common.dto;

import com.hxj.common.dto.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 综合评估查询DTO
 * 包含所有评估表的查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ComprehensiveAssessmentQueryDTO extends BasePageQueryDTO {
    
    // ==================== 患者基本信息 ====================
    /**
     * 患者编号
     */
    private String patientNumber;
    
    // ==================== CURB-65评分 ====================
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
     * 是否为COVID-19重型
     */
    private Boolean covid19IsSevereType;
    
    /**
     * COVID-19重型严重程度
     */
    private String covid19SeverityLevel;
    
    // ==================== COVID-19危重型诊断 ====================
    /**
     * 是否为COVID-19危重型
     */
    private Boolean covid19IsCriticalType;
    
    /**
     * COVID-19危重型严重程度
     */
    private String covid19CriticalSeverityLevel;
    
    // ==================== CPIS评分 ====================
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
     * PSI总分
     */
    private Integer psiTotalScore;
    
    /**
     * PSI风险等级
     */
    private String psiRiskClass;
    
    // ==================== qSOFA评分 ====================
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
     * 是否为重症肺炎
     */
    private Boolean isSeverePneumonia;
    
    // ==================== SOFA评分 ====================
    /**
     * SOFA总分
     */
    private Integer sofaTotalScore;
    
    /**
     * SOFA严重程度
     */
    private String sofaSeverityLevel;
    
    // ==================== 范围查询 ====================
    /**
     * 最小CURB-65分数
     */
    private Integer minCurbScore;
    
    /**
     * 最大CURB-65分数
     */
    private Integer maxCurbScore;
    
    /**
     * 最小PSI分数
     */
    private Integer minPsiScore;
    
    /**
     * 最大PSI分数
     */
    private Integer maxPsiScore;
    
    /**
     * 最小CPIS分数
     */
    private Integer minCpisScore;
    
    /**
     * 最大CPIS分数
     */
    private Integer maxCpisScore;
    
    /**
     * 最小qSOFA分数
     */
    private Integer minQsofaScore;
    
    /**
     * 最大qSOFA分数
     */
    private Integer maxQsofaScore;
    
    /**
     * 最小SOFA分数
     */
    private Integer minSofaScore;
    
    /**
     * 最大SOFA分数
     */
    private Integer maxSofaScore;
}
