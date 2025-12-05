package com.hxj.common.dto.patient;

import com.hxj.common.enums.Gender;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 患者信息创建请求DTO
 */
@Data
public class PatientCreateRequest {

    /**
     * 患者编号
     */
    @NotNull
    private String patientNumber;

    /**
     * 就诊次数
     */
    @NotNull(message = "就诊次数不能为空")
    @Min(value = 1, message = "就诊次数必须大于0")
    private Integer visitCount;

    /**
     * 住院日期
     */
    @NotNull(message = "住院日期不能为空")
    private LocalDate admissionDate;

    /**
     * 性别
     */
    @NotNull(message = "性别不能为空")
    private Gender gender;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;

    /**
     * 主诉
     */
    @NotBlank(message = "主诉不能为空")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @NotBlank(message = "现病史不能为空")
    private String presentIllness;

    /**
     * 查体
     */
    @NotBlank(message = "查体不能为空")
    private String physicalExamination;

    // ==================== 动脉血气指标 ====================

    /**
     * 动脉血气pH
     */
    private BigDecimal arterialPh;

    /**
     * 动脉血气pO2(mmHg)
     */
    private BigDecimal arterialPo2;

    /**
     * 动脉血气氧合指数(mmHg)
     */
    private BigDecimal arterialOxygenationIndex;

    /**
     * 动脉血气pCO2(mmHg)
     */
    private BigDecimal arterialPco2;

    // ==================== 血液检查指标 ====================

    /**
     * 血常规血小板计数(×10^9/L)
     */
    private BigDecimal plateletCount;

    /**
     * 血尿素氮(mmol/L)
     */
    private BigDecimal bloodUreaNitrogen;

    /**
     * 血肌酐(μmol/L)
     */
    private BigDecimal serumCreatinine;

    /**
     * 总胆红素(μmol/L)
     */
    private BigDecimal totalBilirubin;

    // ==================== 胸部CT相关 ====================

    /**
     * 是否开具胸部CT
     */
    private Boolean chestCtOrdered;

    /**
     * 胸部CT报告
     */
    private String chestCtReport;

    // ==================== 药物使用情况 ====================

    /**
     * 是否应用多巴胺
     */
    private Boolean dopamineUsed;

    /**
     * 是否应用多巴酚丁胺
     */
    private Boolean dobutamineUsed;

    /**
     * 是否应用去甲肾上腺素
     */
    private Boolean norepinephrineUsed;

    /**
     * 是否应用血管活性药物
     */
    private Boolean vasoactiveDrugsUsed;

    /**
     * 是否应用特殊级/限制级抗生素
     */
    private Boolean specialAntibioticsUsed;

    /**
     * 抗生素种类
     */
    private String antibioticTypes;

    // ==================== 治疗设备使用 ====================

    /**
     * 是否应用呼吸机
     */
    private Boolean ventilatorUsed;

    /**
     * 是否入住ICU
     */
    private Boolean icuAdmission;

    /**
     * 备注
     */
    private String remark;
}
