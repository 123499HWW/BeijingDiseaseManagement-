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
     * 是否有CURB评估
     */
    private Boolean hasCurb;
    
    /**
     * 是否有PSI评估
     */
    private Boolean hasPsi;
    
    /**
     * 是否有CPIS评估
     */
    private Boolean hasCpis;
    
    /**
     * 是否有重症肺炎诊断
     */
    private Boolean hasDiagnosis;
    
    /**
     * 风险等级筛选（高风险/中风险/低风险）
     */
    private String riskLevel;
    
    /**
     * 是否重症肺炎
     */
    private Boolean isSeverePneumonia;
    
    /**
     * 排序字段
     */
    private String orderBy = "patientId";
    
    /**
     * 排序方式（ASC/DESC）
     */
    private String orderDirection = "DESC";
}
