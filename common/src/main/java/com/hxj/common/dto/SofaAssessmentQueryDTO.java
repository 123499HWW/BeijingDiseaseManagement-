package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SOFA评分分页查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SofaAssessmentQueryDTO extends BasePageQueryDTO {
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 严重程度等级
     */
    private String severityLevel;
    
    /**
     * 最小总分
     */
    private Integer minTotalScore;
    
    /**
     * 最大总分
     */
    private Integer maxTotalScore;
}
