package com.hxj.common.dto.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 通知消息DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationMessageDTO extends BaseMessageDTO {
    
    /**
     * 通知类型
     * EMAIL: 邮件通知
     * SMS: 短信通知
     * SYSTEM: 系统通知
     */
    private String notificationType;
    
    /**
     * 接收者列表
     */
    private List<String> recipients;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 模板ID（使用模板时）
     */
    private String templateId;
    
    /**
     * 模板参数
     */
    private Map<String, Object> templateParams;
    
    /**
     * 优先级
     * HIGH: 高优先级
     * NORMAL: 普通优先级
     * LOW: 低优先级
     */
    private String priority = "NORMAL";
    
    /**
     * 是否立即发送
     */
    private Boolean immediate = false;
    
    public NotificationMessageDTO() {
        super();
    }
    
    public NotificationMessageDTO(String notificationType, List<String> recipients, 
                                 String title, String content, String userId, String userName) {
        super();
        this.notificationType = notificationType;
        this.recipients = recipients;
        this.title = title;
        this.content = content;
        this.setUserId(userId);
        this.setUserName(userName);
    }
}
