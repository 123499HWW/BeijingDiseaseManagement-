package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 呼吸道症候群评估表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("respiratory_syndrome_assessment")
public class RespiratorySyndromeAssessment {

    /**
     * 症候群评估ID
     */
    @TableId(value = "syndrome_id", type = IdType.AUTO)
    private Long syndromeId;

    // ==================== 症状指标 ====================

    /**
     * 是否有呼吸困难（0-否，1-是）
     */
    @TableField("has_dyspnea")
    private Integer hasDyspnea;

    /**
     * 呼吸困难程度（MILD-轻度，MODERATE-中度，SEVERE-重度）
     */
    @TableField("dyspnea_level")
    private String dyspneaLevel;

    /**
     * 是否有意识障碍（0-否，1-是）
     */
    @TableField("has_consciousness_disorder")
    private Integer hasConsciousnessDisorder;

    /**
     * 意识障碍程度（嗜睡、昏睡、昏迷等）
     */
    @TableField("consciousness_level")
    private String consciousnessLevel;

    // ==================== 体征指标 ====================

    /**
     * 体温（℃）
     */
    @TableField("temperature")
    private BigDecimal temperature;

    /**
     * 体温分级（正常、发热≥38.5、高热≥40.0）
     */
    @TableField("temperature_level")
    private String temperatureLevel;

    /**
     * 心率（bpm）
     */
    @TableField("heart_rate")
    private Integer heartRate;

    /**
     * 是否心动过速（>130bpm）
     */
    @TableField("is_tachycardia")
    private Integer isTachycardia;

    /**
     * 收缩压（mmHg）
     */
    @TableField("systolic_bp")
    private Integer systolicBp;

    /**
     * 舒张压（mmHg）
     */
    @TableField("diastolic_bp")
    private Integer diastolicBp;

    /**
     * 是否低血压（<90/60mmHg）
     */
    @TableField("is_hypotension")
    private Integer isHypotension;

    /**
     * 血氧饱和度（%）
     */
    @TableField("oxygen_saturation")
    private BigDecimal oxygenSaturation;

    /**
     * 是否低氧血症（<93%）
     */
    @TableField("is_hypoxemia")
    private Integer isHypoxemia;

    // ==================== 检验指标 - 动脉血气分析 ====================

    /**
     * 动脉血pH值
     */
    @TableField("arterial_ph")
    private BigDecimal arterialPh;

    /**
     * 是否酸中毒（pH<7.35）
     */
    @TableField("is_acidosis")
    private Integer isAcidosis;

    /**
     * 氧分压PaO2（mmHg）
     */
    @TableField("pao2")
    private BigDecimal pao2;

    /**
     * 是否低氧血症（PaO2<60mmHg）
     */
    @TableField("is_hypoxemia_pao2")
    private Integer isHypoxemiaPao2;

    /**
     * 氧合指数PaO2/FiO2
     */
    @TableField("pao2_fio2_ratio")
    private BigDecimal pao2Fio2Ratio;

    /**
     * 是否氧合障碍（PaO2/FiO2<300）
     */
    @TableField("is_oxygenation_disorder")
    private Integer isOxygenationDisorder;

    /**
     * 二氧化碳分压PaCO2（mmHg）
     */
    @TableField("paco2")
    private BigDecimal paco2;

    /**
     * 是否高碳酸血症（PaCO2>50mmHg）
     */
    @TableField("is_hypercapnia")
    private Integer isHypercapnia;

    // ==================== 检验指标 - 血常规 ====================

    /**
     * 血小板计数（×10^9/L）
     */
    @TableField("platelet_count")
    private BigDecimal plateletCount;

    /**
     * 是否血小板减少（<100×10^9/L）
     */
    @TableField("is_thrombocytopenia")
    private Integer isThrombocytopenia;

    // ==================== 检验指标 - 血生化 ====================

    /**
     * 血尿素氮BUN（mmol/L）
     */
    @TableField("blood_urea_nitrogen")
    private BigDecimal bloodUreaNitrogen;

    /**
     * 是否BUN升高（>7mmol/L）
     */
    @TableField("is_bun_elevated")
    private Integer isBunElevated;

    /**
     * 肌酐Cr（μmol/L）
     */
    @TableField("creatinine")
    private BigDecimal creatinine;

    /**
     * 是否肌酐升高
     */
    @TableField("is_creatinine_elevated")
    private Integer isCreatinineElevated;

    /**
     * 总胆红素TBIL（μmol/L）
     */
    @TableField("total_bilirubin")
    private BigDecimal totalBilirubin;

    /**
     * 是否胆红素升高
     */
    @TableField("is_bilirubin_elevated")
    private Integer isBilirubinElevated;

    // ==================== 检查指标 ====================

    /**
     * 是否进行胸部CT检查
     */
    @TableField("has_chest_ct")
    private Integer hasChestCt;

    /**
     * CT是否显示肺炎等感染征象
     */
    @TableField("ct_shows_infection")
    private Integer ctShowsInfection;

    /**
     * CT是否显示多肺叶受累
     */
    @TableField("ct_multi_lobe_involved")
    private Integer ctMultiLobeInvolved;

    /**
     * CT检查发现详情
     */
    @TableField("ct_findings")
    private String ctFindings;

    // ==================== 治疗指标 ====================

    /**
     * 是否使用多巴胺
     */
    @TableField("uses_dopamine")
    private Integer usesDopamine;

    /**
     * 是否使用多巴酚丁胺
     */
    @TableField("uses_dobutamine")
    private Integer usesDobutamine;

    /**
     * 是否使用去甲肾上腺素
     */
    @TableField("uses_norepinephrine")
    private Integer usesNorepinephrine;

    /**
     * 是否使用特殊级/限制级抗生素
     */
    @TableField("uses_special_antibiotics")
    private Integer usesSpecialAntibiotics;

    /**
     * 使用的抗生素列表
     */
    @TableField("antibiotics_list")
    private String antibioticsList;

    /**
     * 是否使用呼吸机
     */
    @TableField("uses_ventilator")
    private Integer usesVentilator;

    /**
     * 呼吸机模式
     */
    @TableField("ventilator_mode")
    private String ventilatorMode;

    // ==================== 治疗场所 ====================

    /**
     * 是否住ICU
     */
    @TableField("in_icu")
    private Integer inIcu;

    /**
     * ICU入住时间
     */
    @TableField("icu_admission_date")
    private LocalDateTime icuAdmissionDate;

    /**
     * ICU出院时间
     */
    @TableField("icu_discharge_date")
    private LocalDateTime icuDischargeDate;

    // ==================== 严重程度评估 ====================

    /**
     * 严重程度评分
     */
    @TableField("severity_score")
    private Integer severityScore;

    /**
     * 严重程度等级（轻度、中度、重度、危重）
     */
    @TableField("severity_level")
    private String severityLevel;

    /**
     * 危险因素数量
     */
    @TableField("risk_factors_count")
    private Integer riskFactorsCount;

    /**
     * 评估总结
     */
    @TableField("assessment_summary")
    private String assessmentSummary;

    // ==================== 评估信息 ====================

    /**
     * 评估时间
     */
    @TableField("assessment_time")
    private LocalDateTime assessmentTime;

    /**
     * 评估方式（AUTO-自动，MANUAL-手动）
     */
    @TableField("assessment_method")
    private String assessmentMethod;

    /**
     * 评估人员
     */
    @TableField("assessor")
    private String assessor;

    /**
     * 评估备注
     */
    @TableField("assessment_notes")
    private String assessmentNotes;

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
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;
}
