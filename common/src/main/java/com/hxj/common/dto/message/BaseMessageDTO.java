package com.hxj.common.dto.message;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础消息DTO
 */
@Data
public class BaseMessageDTO {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 操作用户ID
     */
    private String userId;
    
    /**
     * 操作用户名
     */
    private String userName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 备注信息
     */
    private String remark;
    
    public BaseMessageDTO() {
        this.createTime = LocalDateTime.now();
        this.messageId = generateMessageId();
    }
    
    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }
}
