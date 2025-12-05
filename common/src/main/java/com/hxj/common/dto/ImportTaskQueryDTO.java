package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 导入任务分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportTaskQueryDTO extends BasePageQueryDTO {
    
    /**
     * 任务状态：PENDING/PROCESSING/COMPLETED/FAILED/CANCELLED
     */
    private String taskStatus;
    
    /**
     * 导入类型：PATIENT/EXAMINATION/LAB_RESULT等
     */
    private String importType;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 开始时间（起）
     */
    private LocalDateTime startTimeBegin;
    
    /**
     * 开始时间（止）
     */
    private LocalDateTime startTimeEnd;
    
    /**
     * 结束时间（起）
     */
    private LocalDateTime endTimeBegin;
    
    /**
     * 结束时间（止）
     */
    private LocalDateTime endTimeEnd;
    
    /**
     * 最小成功数
     */
    private Integer minSuccessCount;
    
    /**
     * 最大成功数
     */
    private Integer maxSuccessCount;
    
    /**
     * 最小失败数
     */
    private Integer minFailureCount;
    
    /**
     * 最大失败数
     */
    private Integer maxFailureCount;
    
    /**
     * 文件名（模糊查询）
     */
    private String fileName;
}
