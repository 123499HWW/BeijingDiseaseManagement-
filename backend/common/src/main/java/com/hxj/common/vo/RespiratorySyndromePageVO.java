package com.hxj.common.vo;

import com.hxj.common.entity.RespiratorySyndromeAssessment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 呼吸道症候群评估分页查询返回VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RespiratorySyndromePageVO extends RespiratorySyndromeAssessment {
    
    /**
     * 患者编号
     */
    private String patientNumber;
    
    /**
     * 患者性别
     */
    private String gender;
    
    /**
     * 患者年龄
     */
    private Integer age;
    
    /**
     * 关联ID
     */
    private Long relationId;
    
    /**
     * 患者ID
     */
    private Long patientId;
    
    /**
     * 关联状态
     */
    private String relationStatus;
    
    /**
     * 诊断结果
     */
    private String diagnosis;
}
