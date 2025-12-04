package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * qSOFA评分分页查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QsofaAssessmentQueryDTO extends BasePageQueryDTO {
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 风险等级
     */
    private String riskLevel;
    
    /**
     * 最小总分
     */
    private Integer minScore;
    
    /**
     * 最大总分
     */
    private Integer maxScore;
}
