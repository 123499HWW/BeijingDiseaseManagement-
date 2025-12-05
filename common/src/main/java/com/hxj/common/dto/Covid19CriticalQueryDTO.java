package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * COVID-19危重型诊断分页查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Covid19CriticalQueryDTO extends BasePageQueryDTO {
    
    /**
     * 是否危重型
     */
    private Boolean isCriticalType;
    
    /**
     * 满足标准数
     */
    private Integer criteriaCount;
    
    /**
     * 严重程度等级
     */
    private String severityLevel;
}
