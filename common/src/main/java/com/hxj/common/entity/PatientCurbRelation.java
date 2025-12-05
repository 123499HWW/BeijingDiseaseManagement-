package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 患者CURB-65评分关联表实体类
 * 用于连接患者信息表和CURB-65评分结果表
 */
@Data
@TableName("patient_curb_relation")
public class PatientCurbRelation {

    /**
     * 关系ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID - 关联patient_info表
     */
    @TableField("patient_id")
    private Long patientId;

    /**
     * CURB评分结果ID - 关联curb_assessment_result表
     */
    @TableField("curb_id")
    private Long curbId;

    /**
     * 评估时间
     */
    @TableField("assessment_date")
    private LocalDateTime assessmentDate;

    /**
     * 评估类型
     * ADMISSION: 入院时评估
     * FOLLOW_UP: 随访评估
     * DISCHARGE: 出院时评估
     */
    @TableField("assessment_type")
    private String assessmentType;

    /**
     * 备注
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
