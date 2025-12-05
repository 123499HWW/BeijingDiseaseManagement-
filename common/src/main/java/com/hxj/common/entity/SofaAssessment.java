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
 * SOFA评分结果实体类
 * SOFA (Sequential Organ Failure Assessment) 用于评估器官功能衰竭的严重程度
 */
@Data
@TableName("sofa_assessment")
public class SofaAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评分ID
     */
    @TableId(value = "assessment_id", type = IdType.AUTO)
    private Long assessmentId;

    // ==================== 呼吸系统 ====================

    /**
     * 氧合指数(PaO2/FiO2)
     */
    @TableField("oxygenation_index")
    private BigDecimal oxygenationIndex;

    /**
     * 呼吸系统评分(0-4分)
     */
    @TableField("respiration_score")
    private Integer respirationScore;

    // ==================== 凝血系统 ====================

    /**
     * 血小板计数(×10^9/L)
     */
    @TableField("platelet_count")
    private BigDecimal plateletCount;

    /**
     * 凝血系统评分(0-4分)
     */
    @TableField("coagulation_score")
    private Integer coagulationScore;

    // ==================== 肝脏系统 ====================

    /**
     * 总胆红素(μmol/L)
     */
    @TableField("bilirubin")
    private BigDecimal bilirubin;

    /**
     * 肝脏系统评分(0-4分)
     */
    @TableField("liver_score")
    private Integer liverScore;

    // ==================== 心血管系统 ====================

    /**
     * 平均动脉压(mmHg)
     */
    @TableField("mean_arterial_pressure")
    private BigDecimal meanArterialPressure;

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
     * 是否使用血管活性药物(0-否,1-是)
     */
    @TableField("vasoactive_drugs")
    private Boolean vasoactiveDrugs;

    /**
     * 多巴胺剂量(μg/kg/min)
     */
    @TableField("dopamine_dose")
    private BigDecimal dopamineDose;

    /**
     * 多巴酚丁胺剂量(μg/kg/min)
     */
    @TableField("dobutamine_dose")
    private BigDecimal dobutamineDose;

    /**
     * 去甲肾上腺素剂量(μg/kg/min)
     */
    @TableField("norepinephrine_dose")
    private BigDecimal norepinephrineDose;

    /**
     * 心血管系统评分(0-4分)
     */
    @TableField("cardiovascular_score")
    private Integer cardiovascularScore;

    // ==================== 神经系统 ====================

    /**
     * 格拉斯哥昏迷评分(3-15分)
     */
    @TableField("glasgow_coma_score")
    private Integer glasgowComaScore;

    /**
     * 中枢神经系统评分(0-4分)
     */
    @TableField("cns_score")
    private Integer cnsScore;

    // ==================== 肾脏系统 ====================

    /**
     * 肌酐(μmol/L)
     */
    @TableField("creatinine")
    private BigDecimal creatinine;

    /**
     * 尿量(ml/d)
     */
    @TableField("urine_output")
    private BigDecimal urineOutput;

    /**
     * 肾脏系统评分(0-4分)
     */
    @TableField("renal_score")
    private Integer renalScore;

    // ==================== 评分结果 ====================

    /**
     * 总分(0-24分)
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 器官衰竭数量
     */
    @TableField("organ_failures")
    private Integer organFailures;

    /**
     * 严重程度
     */
    @TableField("severity_level")
    private String severityLevel;

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
