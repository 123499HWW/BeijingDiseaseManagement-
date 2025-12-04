package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 呼吸道症候群患者关联关系表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("syndrome_patient_relation")
public class SyndromePatientRelation {

    /**
     * 关联关系ID
     */
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    /**
     * 症候群评估ID
     */
    @TableField("syndrome_id")
    private Long syndromeId;

    /**
     * 患者ID
     */
    @TableField("patient_id")
    private Long patientId;

    /**
     * 患者编号
     */
    @TableField("patient_number")
    private String patientNumber;

    /**
     * 诊断结果
     */
    @TableField("diagnosis")
    private String diagnosis;

    /**
     * 症候群类型
     */
    @TableField("syndrome_type")
    private String syndromeType;

    /**
     * 关联状态（ACTIVE-有效，INACTIVE-无效）
     */
    @TableField("relation_status")
    private String relationStatus;

    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 更新人
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 是否删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;
}
