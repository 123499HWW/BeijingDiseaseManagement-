package com.hxj.common.dto.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入任务消息DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportTaskMessageDTO extends BaseMessageDTO {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务编号
     */
    private String taskNumber;
    
    /**
     * 导入类型
     * PATIENT_DATA: 患者数据导入
     * USER_DATA: 用户数据导入
     */
    private String importType;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 是否覆盖已存在数据
     */
    private Boolean overwrite = false;
    
    /**
     * 批次大小
     */
    private Integer batchSize = 100;
    
    /**
     * 是否验证数据
     */
    private Boolean validateData = true;
    
    /**
     * 回调URL（可选）
     */
    private String callbackUrl;
    
    public ImportTaskMessageDTO() {
        super();
    }
    
    public ImportTaskMessageDTO(Long taskId, String importType, String filePath, 
                               String fileName, String userId, String userName) {
        super();
        this.taskId = taskId;
        this.importType = importType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.setUserId(userId);
        this.setUserName(userName);
    }
}
