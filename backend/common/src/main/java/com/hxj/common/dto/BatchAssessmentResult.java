package com.hxj.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 批量评估/诊断结果DTO
 */
@Data
public class BatchAssessmentResult {
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 总数量
     */
    private Integer totalCount;
    
    /**
     * 成功数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failureCount;
    
    /**
     * 跳过数量
     */
    private Integer skipCount;
    
    /**
     * 结果信息
     */
    private String message;
    
    /**
     * 错误列表
     */
    private List<String> errors;
    
    /**
     * 构造函数
     */
    public BatchAssessmentResult() {
        this.totalCount = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.skipCount = 0;
    }
}
