package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * PSI评分患者关联关系表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("psi_patient_relation")
public class PsiPatientRelation {

    /**
     * 关联关系ID
     */
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    /**
     * PSI评估ID
     */
    @TableField("psi_id")
    private Long psiId;

    /**
     * 患者ID
     */
    @TableField("patient_id")
    private Long patientId;

    /**
     * 患者编号（冗余字段，便于查询）
     */
    @TableField("patient_number")
    private String patientNumber;

    /**
     * 关联类型（预留字段）
     */
    @TableField("relation_type")
    private String relationType;

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

    // ==================== 通用字段 ====================

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
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}
