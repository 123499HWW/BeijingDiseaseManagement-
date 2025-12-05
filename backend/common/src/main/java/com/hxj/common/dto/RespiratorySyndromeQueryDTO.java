package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 呼吸道症候群评估分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RespiratorySyndromeQueryDTO extends BasePageQueryDTO {
    
    /**
     * 患者编号
     */
    private String patientNumber;
    
    /**
     * 严重程度等级
     */
    private String severityLevel;
    
    /**
     * 最低严重程度评分
     */
    private Integer minSeverityScore;
    
    /**
     * 最高严重程度评分
     */
    private Integer maxSeverityScore;
    
    /**
     * 最少危险因素数量
     */
    private Integer minRiskFactorsCount;
    
    /**
     * 最多危险因素数量
     */
    private Integer maxRiskFactorsCount;
    
    /**
     * 是否有呼吸困难
     */
    private Integer hasDyspnea;
    
    /**
     * 是否有意识障碍
     */
    private Integer hasConsciousnessDisorder;
    
    /**
     * 是否心动过速
     */
    private Integer isTachycardia;
    
    /**
     * 是否低血压
     */
    private Integer isHypotension;
    
    /**
     * 是否低氧血症
     */
    private Integer isHypoxemia;
    
    /**
     * 是否酸中毒
     */
    private Integer isAcidosis;
    
    /**
     * 是否氧合障碍
     */
    private Integer isOxygenationDisorder;
    
    /**
     * 是否使用呼吸机
     */
    private Integer usesVentilator;
    
    /**
     * 是否住ICU
     */
    private Integer inIcu;
    
    /**
     * CT是否显示感染
     */
    private Integer ctShowsInfection;
    
    /**
     * CT是否多肺叶受累
     */
    private Integer ctMultiLobeInvolved;
    
    /**
     * 评估人
     */
    private String assessor;
}
