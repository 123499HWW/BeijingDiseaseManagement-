package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 体检详细信息实体类
 * 用于存储从patient_info表中physical_examination和chest_ct_report字段拆解出来的详细信息
 */
@Data
@TableName("physical_examination_detail")
public class PhysicalExaminationDetail {

    /**
     * 体检详细信息ID
     */
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    // ==================== 体格检查拆解字段 ====================
    
    /**
     * 体格检查第一段信息（一般状况）
     */
    @TableField("general_condition")
    private String generalCondition;

    /**
     * 体温(T) - 从第二段拆解
     */
    @TableField("temperature")
    private BigDecimal temperature;

    /**
     * 脉搏(P) - 从第二段拆解
     */
    @TableField("pulse")
    private Integer pulse;

    /**
     * 呼吸(R) - 从第二段拆解
     */
    @TableField("respiration")
    private Integer respiration;

    /**
     * 收缩压(SBP) - 从BP字段拆解
     */
    @TableField("systolic_bp")
    private Integer systolicBp;

    /**
     * 舒张压(DBP) - 从BP字段拆解
     */
    @TableField("diastolic_bp")
    private Integer diastolicBp;

    /**
     * 血氧饱和度(SpO2) - 从第二段拆解
     */
    @TableField("spo2")
    private BigDecimal spo2;

    /**
     * 体格检查第三段信息（其他检查）
     */
    @TableField("other_examination")
    private String otherExamination;

    /**
     * 体格检查第四段信息（如果存在）
     */
    @TableField("additional_examination")
    private String additionalExamination;

    // ==================== 胸部CT报告拆解字段 ====================

    /**
     * CT检查方法
     */
    @TableField("ct_examination_method")
    private String ctExaminationMethod;

    /**
     * CT影像所见
     */
    @TableField("ct_imaging_findings")
    private String ctImagingFindings;

    /**
     * CT诊断意见
     */
    @TableField("ct_diagnosis_opinion")
    private String ctDiagnosisOpinion;

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
