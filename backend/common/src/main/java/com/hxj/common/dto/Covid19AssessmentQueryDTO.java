package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * COVID-19重型诊断分页查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Covid19AssessmentQueryDTO extends BasePageQueryDTO {
    
    /**
     * 是否重型
     */
    private Boolean isSevereType;
    
    /**
     * 满足标准数
     */
    private Integer criteriaCount;
    
    /**
     * 严重程度等级
     */
    private String severityLevel;
}
