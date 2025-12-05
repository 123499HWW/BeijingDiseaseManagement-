package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 患者体检信息关系表实体类
 * 用于连接患者信息表和体检详细信息表
 */
@Data
@TableName("patient_examination_relation")
public class PatientExaminationRelation {

    /**
     * 关系ID
     */
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    /**
     * 患者ID - 关联patient_info表
     */
    @TableField("patient_id")
    private Long patientId;

    /**
     * 体检详细信息ID - 关联physical_examination_detail表
     */
    @TableField("detail_id")
    private Long detailId;

    /**
     * 关系类型 - 用于标识数据来源
     * PHYSICAL_EXAM: 来源于physical_examination字段
     * CHEST_CT: 来源于chest_ct_report字段
     * COMBINED: 两个字段的组合数据
     */
    @TableField("relation_type")
    private String relationType;

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
     * 是否删除(0-未删除,1-已删除)
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
