package com.hxj.common.dto.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 患者数据处理消息DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDataMessageDTO extends BaseMessageDTO {
    
    /**
     * 操作类型
     * IMPORT: 数据导入
     * MIGRATION: 数据迁移
     * EXPORT: 数据导出
     */
    private String operationType;
    
    /**
     * 文件路径（导入/导出时使用）
     */
    private String filePath;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 数据总数
     */
    private Integer totalCount;
    
    /**
     * 批次大小
     */
    private Integer batchSize = 100;
    
    /**
     * 是否覆盖已存在数据
     */
    private Boolean overwrite = false;
    
    /**
     * 导入任务ID（用于获取导入的患者列表）
     */
    private Long taskId;
    
    public PatientDataMessageDTO() {
        super();
    }
    
    public PatientDataMessageDTO(String operationType, String userId, String userName) {
        super();
        this.operationType = operationType;
        this.setUserId(userId);
        this.setUserName(userName);
    }
    
    public PatientDataMessageDTO(String userId, String userName, String operationType, Integer totalCount) {
        super();
        this.setUserId(userId);
        this.setUserName(userName);
        this.operationType = operationType;
        this.totalCount = totalCount;
    }
}
