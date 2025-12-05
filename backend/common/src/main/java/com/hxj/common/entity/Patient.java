package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hxj.common.enums.Gender;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者信息实体类
 */
@Data
@TableName("patient_info")
public class Patient {

    /**
     * 患者ID
     */
    @TableId(value = "patient_id", type = IdType.AUTO)
    private Long patientId;

    /**
     * 患者编号 - 唯一标识，格式如P0001
     */
    @TableField("patient_number")
    private String patientNumber;

    /**
     * 就诊次数
     */
    @TableField("visit_count")
    private Integer visitCount;

    /**
     * 住院日期
     */
    @TableField("admission_date")
    private LocalDate admissionDate;

    /**
     * 性别
     */
    @TableField("gender")
    private Gender gender;

    /**
     * 年龄
     */
    @TableField("age")
    private Integer age;

    /**
     * 主诉
     */
    @TableField("chief_complaint")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @TableField("present_illness")
    private String presentIllness;

    /**
     * 查体
     */
    @TableField("physical_examination")
    private String physicalExamination;

    // ==================== 动脉血气指标 ====================

    /**
     * 动脉血气pH
     */
    @TableField("arterial_ph")
    private BigDecimal arterialPh;

    /**
     * 动脉血气pO2(mmHg)
     */
    @TableField("arterial_po2")
    private BigDecimal arterialPo2;

    /**
     * 动脉血气氧合指数(mmHg)
     */
    @TableField("arterial_oxygenation_index")
    private BigDecimal arterialOxygenationIndex;

    /**
     * 动脉血气pCO2(mmHg)
     */
    @TableField("arterial_pco2")
    private BigDecimal arterialPco2;

    // ==================== 血液检查指标 ====================

    /**
     * 血常规血小板计数(×10^9/L)
     */
    @TableField("platelet_count")
    private BigDecimal plateletCount;

    /**
     * 血尿素氮(mmol/L)
     */
    @TableField("blood_urea_nitrogen")
    private BigDecimal bloodUreaNitrogen;

    /**
     * 血肌酐(μmol/L)
     */
    @TableField("serum_creatinine")
    private BigDecimal serumCreatinine;

    /**
     * 总胆红素(μmol/L)
     */
    @TableField("total_bilirubin")
    private BigDecimal totalBilirubin;

    // ==================== 胸部CT相关 ====================

    /**
     * 是否开具胸部CT
     */
    @TableField("chest_ct_ordered")
    private Boolean chestCtOrdered;

    /**
     * 胸部CT报告
     */
    @TableField("chest_ct_report")
    private String chestCtReport;

    // ==================== 药物使用情况 ====================

    /**
     * 是否应用多巴胺
     */
    @TableField("dopamine_used")
    private Boolean dopamineUsed;

    /**
     * 是否应用多巴酚丁胺
     */
    @TableField("dobutamine_used")
    private Boolean dobutamineUsed;

    /**
     * 是否应用去甲肾上腺素
     */
    @TableField("norepinephrine_used")
    private Boolean norepinephrineUsed;

    /**
     * 是否应用血管活性药物
     */
    @TableField("vasoactive_drugs_used")
    private Boolean vasoactiveDrugsUsed;

    /**
     * 是否应用特殊级/限制级抗生素
     */
    @TableField("special_antibiotics_used")
    private Boolean specialAntibioticsUsed;

    /**
     * 抗生素种类
     */
    @TableField("antibiotic_types")
    private String antibioticTypes;

    // ==================== 治疗设备使用 ====================

    /**
     * 是否应用呼吸机
     */
    @TableField("ventilator_used")
    private Boolean ventilatorUsed;

    /**
     * 是否入住ICU
     */
    @TableField("icu_admission")
    private Boolean icuAdmission;

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

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
