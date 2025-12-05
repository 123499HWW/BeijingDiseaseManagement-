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
 * COVID-19危重型诊断结果实体类
 */
@Data
@TableName("covid19_critical_assessment")
public class Covid19CriticalAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 诊断ID
     */
    @TableId(value = "assessment_id", type = IdType.AUTO)
    private Long assessmentId;

    // ==================== 危重型诊断标准 ====================

    // 1. 呼吸衰竭相关
    /**
     * 是否呼吸衰竭(0-否,1-是)
     */
    @TableField("respiratory_failure")
    private Boolean respiratoryFailure;

    /**
     * 是否需要机械通气(0-否,1-是)
     */
    @TableField("mechanical_ventilation")
    private Boolean mechanicalVentilation;

    /**
     * 氧合指数(mmHg)
     */
    @TableField("oxygenation_index")
    private BigDecimal oxygenationIndex;

    /**
     * 二氧化碳分压(mmHg)
     */
    @TableField("co2_partial_pressure")
    private BigDecimal co2PartialPressure;

    /**
     * 是否使用呼吸机(0-否,1-是)
     */
    @TableField("ventilator_used")
    private Boolean ventilatorUsed;

    // 2. 休克相关
    /**
     * 是否出现休克(0-否,1-是)
     */
    @TableField("shock_present")
    private Boolean shockPresent;

    /**
     * 收缩压(mmHg)
     */
    @TableField("systolic_bp")
    private Integer systolicBp;

    /**
     * 舒张压(mmHg)
     */
    @TableField("diastolic_bp")
    private Integer diastolicBp;

    /**
     * 血压值
     */
    @TableField("blood_pressure")
    private String bloodPressure;

    // 3. ICU监护相关
    /**
     * 是否ICU监护(0-否,1-是)
     */
    @TableField("icu_admission")
    private Boolean icuAdmission;

    /**
     * 是否重症监护(0-否,1-是)
     */
    @TableField("intensive_care")
    private Boolean intensiveCare;

    // 4. 器官衰竭相关
    /**
     * 是否其他器官衰竭(0-否,1-是)
     */
    @TableField("organ_failure")
    private Boolean organFailure;

    /**
     * 器官衰竭数量
     */
    @TableField("organ_failure_count")
    private Integer organFailureCount;

    /**
     * 器官衰竭详情
     */
    @TableField("organ_failure_details")
    private String organFailureDetails;

    /**
     * SOFA评分
     */
    @TableField("sofa_score")
    private Integer sofaScore;

    // ==================== 诊断结果 ====================

    /**
     * 满足标准数量
     */
    @TableField("criteria_met_count")
    private Integer criteriaMetCount;

    /**
     * 是否为危重型(0-否,1-是)
     */
    @TableField("is_critical_type")
    private Boolean isCriticalType;

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
