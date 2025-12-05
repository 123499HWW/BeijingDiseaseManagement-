package com.hxj.common.vo;

import com.hxj.common.entity.Covid19CriticalAssessment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * COVID-19危重型诊断分页查询结果VO
 * 包含诊断结果的所有字段和关联的患者信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Covid19CriticalPageVO extends Covid19CriticalAssessment {
    
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
     * 患者ID（来自covid19_critical_patient_relation表）
     */
    private Long patientId;
    
    /**
     * 评估日期（来自covid19_critical_patient_relation表）
     */
    private String assessmentDate;
    
    /**
     * 评估类型（来自covid19_critical_patient_relation表）
     */
    private String assessmentType;
}
