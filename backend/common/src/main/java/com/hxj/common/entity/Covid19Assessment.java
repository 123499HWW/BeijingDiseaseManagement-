package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * COVID-19重型诊断结果实体类
 */
@Data
@TableName("covid19_assessment")
public class Covid19Assessment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 诊断ID
     */
    @TableId(value = "assessment_id", type = IdType.AUTO)
    private Long assessmentId;

    // ==================== 重型诊断标准 ====================

    /**
     * 呼吸频率≥30次/分(0-否,1-是)
     */
    @TableField("respiratory_rate_high")
    private Boolean respiratoryRateHigh;

    /**
     * 实际呼吸频率(次/分)
     */
    @TableField("respiratory_rate")
    private Integer respiratoryRate;

    /**
     * 氧饱和度≤93%(0-否,1-是)
     */
    @TableField("oxygen_saturation_low")
    private Boolean oxygenSaturationLow;

    /**
     * 实际氧饱和度(%)
     */
    @TableField("oxygen_saturation")
    private BigDecimal oxygenSaturation;

    /**
     * 氧合指数≤300mmHg(0-否,1-是)
     */
    @TableField("oxygenation_index_low")
    private Boolean oxygenationIndexLow;

    /**
     * 实际氧合指数(mmHg)
     */
    @TableField("oxygenation_index")
    private BigDecimal oxygenationIndex;

    /**
     * 肺部病灶进展>50%(0-否,1-是)
     */
    @TableField("lung_lesion_progression")
    private Boolean lungLesionProgression;

    /**
     * 肺部病灶描述
     */
    @TableField("lung_lesion_description")
    private String lungLesionDescription;

    // ==================== 诊断结果 ====================

    /**
     * 满足标准数量
     */
    @TableField("criteria_met_count")
    private Integer criteriaMetCount;

    /**
     * 是否为重型(0-否,1-是)
     */
    @TableField("is_severe_type")
    private Boolean isSevereType;

    /**
     * 严重程度
     */
    @TableField("severity_level")
    private String severityLevel;

    /**
     * 诊断依据
     */
    @TableField("diagnosis_basis")
    private String diagnosisBasis;

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
