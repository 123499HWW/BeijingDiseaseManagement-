package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * qSOFA评分结果实体类
 * qSOFA (Quick Sequential Organ Failure Assessment) 用于快速识别脓毒症高风险患者
 */
@Data
@TableName("qsofa_assessment")
public class QsofaAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评分ID
     */
    @TableId(value = "assessment_id", type = IdType.AUTO)
    private Long assessmentId;

    // ==================== qSOFA评分项 ====================

    /**
     * 意识障碍(0-否,1-是)
     */
    @TableField("consciousness_altered")
    private Boolean consciousnessAltered;

    /**
     * 意识状态描述
     */
    @TableField("consciousness_description")
    private String consciousnessDescription;

    /**
     * 呼吸频率≥22次/分(0-否,1-是)
     */
    @TableField("respiratory_rate_high")
    private Boolean respiratoryRateHigh;

    /**
     * 实际呼吸频率(次/分)
     */
    @TableField("respiratory_rate")
    private Integer respiratoryRate;

    /**
     * 收缩压≤90mmHg(0-否,1-是)
     */
    @TableField("systolic_bp_low")
    private Boolean systolicBpLow;

    /**
     * 实际收缩压(mmHg)
     */
    @TableField("systolic_bp")
    private Integer systolicBp;

    /**
     * 实际舒张压(mmHg)
     */
    @TableField("diastolic_bp")
    private Integer diastolicBp;

    /**
     * 血压值
     */
    @TableField("blood_pressure")
    private String bloodPressure;

    // ==================== 评分结果 ====================

    /**
     * 总分(0-3分)
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 风险等级
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 评估结论
     */
    @TableField("assessment_conclusion")
    private String assessmentConclusion;

    /**
     * 建议措施
     */
    @TableField("recommended_action")
    private String recommendedAction;

    // ==================== 数据来源 ====================

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 评估时间
     */
    @TableField("assessment_time")
    private LocalDateTime assessmentTime;

    /**
     * 评估方法
     */
    @TableField("assessment_method")
    private String assessmentMethod;

    // ==================== 通用字段 ====================

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Integer isDeleted;
}
