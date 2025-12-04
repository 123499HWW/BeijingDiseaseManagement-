package com.hxj.common.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 脓毒症评估联合查询条件DTO
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Data
public class SepsisAssessmentQueryDTO {
    
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
     * 患者编号（支持模糊查询）
     */
    private String patientNumber;
    
    /**
     * 患者姓名（支持模糊查询）
     */
    private String patientName;
    
    /**
     * 性别（男/女）
     */
    private String gender;
    
    /**
     * 最小年龄
     */
    @Min(value = 0, message = "年龄不能小于0")
    private Integer minAge;
    
    /**
     * 最大年龄
     */
    @Max(value = 150, message = "年龄不能大于150")
    private Integer maxAge;
    
    /**
     * 是否有qSOFA评估
     */
    private Boolean hasQsofa;
    
    /**
     * 是否有SOFA评估
     */
    private Boolean hasSofa;
    
    /**
     * 最小qSOFA分数
     */
    @Min(value = 0, message = "qSOFA分数不能小于0")
    @Max(value = 3, message = "qSOFA分数不能大于3")
    private Integer minQsofaScore;
    
    /**
     * 最小SOFA分数
     */
    @Min(value = 0, message = "SOFA分数不能小于0")
    @Max(value = 24, message = "SOFA分数不能大于24")
    private Integer minSofaScore;
    
    /**
     * 风险等级筛选
     */
    private String riskLevel;
    
    /**
     * 是否高风险（qSOFA≥2或SOFA≥2）
     */
    private Boolean isHighRisk;
    
    /**
     * 是否需要ICU
     */
    private Boolean requiresIcu;
    
    /**
     * 排序字段
     */
    private String orderBy = "patientId";
    
    /**
     * 排序方式（ASC/DESC）
     */
    private String orderDirection = "DESC";
}
