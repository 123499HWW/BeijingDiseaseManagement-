package com.hxj.common.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 多评估表联合查询条件DTO
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Data
public class MultiAssessmentQueryDTO {
    
    /**
     * 页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    private Integer pageSize = 10;
    
    /**
     * 社区获得性肺炎风险（高风险/中风险/低风险/未评估）
     */
    private String communityAcquiredPneumoniaRisk;
    
    /**
     * 是否重症肺炎（0-否，1-是，null-不限）
     */
    private Integer isSeverePneumonia;
}
