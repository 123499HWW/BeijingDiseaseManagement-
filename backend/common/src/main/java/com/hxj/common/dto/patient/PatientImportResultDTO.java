package com.hxj.common.dto.patient;

import lombok.Data;

import java.util.List;

/**
 * 患者信息导入结果DTO
 */
@Data
public class PatientImportResultDTO {
    
    /**
     * 导入成功数量
     */
    private int successCount;
    
    /**
     * 导入失败数量
     */
    private int errorCount;
    
    /**
     * 总数量
     */
    private int totalCount;
    
    /**
     * 错误信息列表
     */
    private List<String> errorMessages;
    
    /**
     * 是否导入成功
     */
    private boolean success;
    
    public PatientImportResultDTO(int successCount, int errorCount, List<String> errorMessages) {
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.totalCount = successCount + errorCount;
        this.errorMessages = errorMessages;
        this.success = errorCount == 0;
    }
}
