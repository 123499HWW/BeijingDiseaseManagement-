package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PSI评分分页查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PsiAssessmentQueryDTO extends BasePageQueryDTO {
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 风险等级（I-V级）
     */
    private String riskClass;
    
    /**
     * 最小总分
     */
    private Integer minTotalScore;
    
    /**
     * 最大总分
     */
    private Integer maxTotalScore;
}
