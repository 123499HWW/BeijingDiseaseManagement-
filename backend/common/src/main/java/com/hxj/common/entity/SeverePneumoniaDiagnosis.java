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
 * 重症肺炎诊断结果实体类
 */
@Data
@TableName("severe_pneumonia_diagnosis")
public class SeverePneumoniaDiagnosis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 诊断ID
     */
    @TableId(value = "diagnosis_id", type = IdType.AUTO)
    private Long diagnosisId;

    // ==================== 主要标准 ====================

    /**
     * 是否使用有创机械通气(0-否,1-是)
     */
    @TableField("mechanical_ventilation")
    private Boolean mechanicalVentilation;

    /**
     * 是否需要血管活性药物(0-否,1-是)
     */
    @TableField("vasoactive_drugs")
    private Boolean vasoactiveDrugs;

    // ==================== 次要标准 ====================

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
     * PaO2/FiO2≤250mmHg(0-否,1-是)
     */
    @TableField("oxygenation_index_low")
    private Boolean oxygenationIndexLow;

    /**
     * 实际氧合指数(mmHg)
     */
    @TableField("oxygenation_index")
    private BigDecimal oxygenationIndex;

    /**
     * 多肺叶浸润(0-否,1-是)
     */
    @TableField("multilobar_infiltrates")
    private Boolean multilobarInfiltrates;

    /**
     * 浸润描述
     */
    @TableField("infiltrates_description")
    private String infiltratesDescription;

    /**
     * 意识障碍或定向障碍(0-否,1-是)
     */
    @TableField("consciousness_disorder")
    private Boolean consciousnessDisorder;

    /**
     * 意识状态描述
     */
    @TableField("consciousness_description")
    private String consciousnessDescription;

    /**
     * 血尿素氮≥7mmol/L(0-否,1-是)
     */
    @TableField("urea_nitrogen_high")
    private Boolean ureaNitrogenHigh;

    /**
     * 实际血尿素氮值(mmol/L)
     */
    @TableField("urea_nitrogen")
    private BigDecimal ureaNitrogen;

    /**
     * 低血压需要液体复苏(0-否,1-是)
     */
    @TableField("hypotension")
    private Boolean hypotension;

    /**
     * 血压值
     */
    @TableField("blood_pressure")
    private String bloodPressure;

    // ==================== 诊断结果 ====================

    /**
     * 满足主要标准数量
     */
    @TableField("major_criteria_count")
    private Integer majorCriteriaCount;

    /**
     * 满足次要标准数量
     */
    @TableField("minor_criteria_count")
    private Integer minorCriteriaCount;

    /**
     * 是否为重症肺炎(0-否,1-是)
     */
    @TableField("is_severe_pneumonia")
    private Boolean isSeverePneumonia;

    /**
     * 诊断依据
     */
    @TableField("diagnosis_basis")
    private String diagnosisBasis;

    /**
     * 诊断结论
     */
    @TableField("diagnosis_conclusion")
    private String diagnosisConclusion;

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
     * 诊断时间
     */
    @TableField("diagnosis_time")
    private LocalDateTime diagnosisTime;

    /**
     * 诊断方法
     */
    @TableField("diagnosis_method")
    private String diagnosisMethod;

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
