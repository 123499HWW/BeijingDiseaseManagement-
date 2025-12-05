package com.hxj.common.dto;

import lombok.Data;

/**
 * CURB评分分页查询条件DTO
 */
@Data
public class CurbAssessmentQueryDTO {
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 风险等级
     */
    private String riskLevel;
    
    /**
     * 性别（男/女）
     */
    private String gender;
    
    /**
     * 最小年龄
     */
    private Integer minAge;
    
    /**
     * 最大年龄
     */
    private Integer maxAge;
    
    /**
     * 当前页码（默认1）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数（默认10）
     */
    private Integer pageSize = 10;
}
