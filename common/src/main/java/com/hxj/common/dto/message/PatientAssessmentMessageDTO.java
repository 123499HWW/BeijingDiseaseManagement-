package com.hxj.common.dto.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 患者评估消息DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientAssessmentMessageDTO extends BaseMessageDTO {
    
    /**
     * 患者ID列表（批量评估时使用）
     */
    private List<Long> patientIds;
    
    /**
     * 单个患者ID（单个评估时使用）
     */
    private Long patientId;
    
    /**
     * 评估类型
     * CURB_65: CURB-65评分
     * BATCH: 批量评估
     */
    private String assessmentType;
    
    /**
     * 是否异步处理
     */
    private Boolean async = true;
    
    /**
     * 回调URL（可选）
     */
    private String callbackUrl;
    
    /**
     * 评估描述信息
     */
    private String description;
    
    /**
     * 评估总数
     */
    private Integer totalCount;
    
    public PatientAssessmentMessageDTO() {
        super();
    }
    
    public PatientAssessmentMessageDTO(Long patientId, String assessmentType, String userId, String userName) {
        super();
        this.patientId = patientId;
        this.assessmentType = assessmentType;
        this.setUserId(userId);
        this.setUserName(userName);
    }
    
    public PatientAssessmentMessageDTO(List<Long> patientIds, String assessmentType, String userId, String userName) {
        super();
        this.patientIds = patientIds;
        this.assessmentType = assessmentType;
        this.setUserId(userId);
        this.setUserName(userName);
    }
    
    public PatientAssessmentMessageDTO(String userId, String userName, String assessmentType, String description) {
        super();
        this.assessmentType = assessmentType;
        this.description = description;
        this.setUserId(userId);
        this.setUserName(userName);
    }
}
