package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PSI评分结果表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("psi_assessment_result")
public class PsiAssessmentResult {

    /**
     * PSI评估ID
     */
    @TableId(value = "psi_id", type = IdType.AUTO)
    private Long psiId;

    // ==================== 人口统计学因素 ====================

    /**
     * 性别评分（女性-10分）
     */
    @TableField("gender_score")
    private Integer genderScore;

    /**
     * 年龄评分
     */
    @TableField("age_score")
    private Integer ageScore;

    // ==================== 基础疾病评分 ====================

    /**
     * 肿瘤评分（+30分）
     */
    @TableField("tumor_score")
    private Integer tumorScore;

    /**
     * 肝病评分（+20分）
     */
    @TableField("liver_disease_score")
    private Integer liverDiseaseScore;

    /**
     * 充血性心力衰竭评分（+10分）
     */
    @TableField("heart_failure_score")
    private Integer heartFailureScore;

    /**
     * 脑血管疾病评分（+10分）
     */
    @TableField("cerebrovascular_disease_score")
    private Integer cerebrovascularDiseaseScore;

    /**
     * 肾病评分（+10分）
     */
    @TableField("kidney_disease_score")
    private Integer kidneyDiseaseScore;

    // ==================== 体格检查评分 ====================

    /**
     * 精神状态改变评分（+20分）
     */
    @TableField("mental_status_change_score")
    private Integer mentalStatusChangeScore;

    /**
     * 心率>125次/分评分（+20分）
     */
    @TableField("heart_rate_score")
    private Integer heartRateScore;

    /**
     * 呼吸频率>30次/分评分（+20分）
     */
    @TableField("respiratory_rate_score")
    private Integer respiratoryRateScore;

    /**
     * 收缩压<90mmHg评分（+20分）
     */
    @TableField("systolic_bp_score")
    private Integer systolicBpScore;

    /**
     * 体温<35℃或>40℃评分（+15分）
     */
    @TableField("temperature_score")
    private Integer temperatureScore;

    // ==================== 实验室检查评分 ====================

    /**
     * 动脉血pH<7.35评分（+30分）
     */
    @TableField("arterial_ph_score")
    private Integer arterialPhScore;

    /**
     * 血尿素氮>30mg/dl评分（+20分）
     */
    @TableField("blood_urea_nitrogen_score")
    private Integer bloodUreaNitrogenScore;

    /**
     * 血钠<130mmol/L评分（+20分）
     */
    @TableField("serum_sodium_score")
    private Integer serumSodiumScore;

    /**
     * 血糖>14mmol/L评分（+10分）
     */
    @TableField("blood_glucose_score")
    private Integer bloodGlucoseScore;

    /**
     * 红细胞压积<30%评分（+10分）
     */
    @TableField("hematocrit_score")
    private Integer hematocritScore;

    /**
     * PaO2<60mmHg评分（+10分）
     */
    @TableField("pao2_score")
    private Integer pao2Score;

    // ==================== 影像学检查评分 ====================

    /**
     * 胸腔积液评分（+10分）
     */
    @TableField("pleural_effusion_score")
    private Integer pleuralEffusionScore;

    // ==================== 评分结果 ====================

    /**
     * PSI总分
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * PSI风险等级（I-V级）
     */
    @TableField("risk_class")
    private String riskClass;

    /**
     * 风险等级描述
     */
    @TableField("risk_description")
    private String riskDescription;

    /**
     * 建议处理方式
     */
    @TableField("recommended_treatment")
    private String recommendedTreatment;

    /**
     * 预期死亡率
     */
    @TableField("mortality_rate")
    private BigDecimal mortalityRate;

    // ==================== 评估详情 ====================

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

    /**
     * 评估备注
     */
    @TableField("assessment_notes")
    private String assessmentNotes;

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

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
