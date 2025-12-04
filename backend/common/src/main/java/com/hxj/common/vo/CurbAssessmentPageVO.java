package com.hxj.common.vo;

import com.hxj.common.entity.CurbAssessmentResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CURB评分分页查询结果VO
 * 包含CURB评分结果的所有字段和关联的患者编号
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CurbAssessmentPageVO extends CurbAssessmentResult {
    
    /**
     * 患者编号（来自patient_info表）
     */
    private String patientNumber;
    
    /**
     * 性别（来自patient_info表）
     */
    private String gender;
    
    /**
     * 年龄（来自patient_info表）
     */
    private Integer age;
    
    /**
     * 患者ID（来自patient_curb_relation表）
     */
    private Long patientId;
    
    /**
     * 评估时间（来自patient_curb_relation表）
     */
    private String assessmentDate;
    
    /**
     * 评估类型（来自patient_curb_relation表）
     */
    private String assessmentType;
}
