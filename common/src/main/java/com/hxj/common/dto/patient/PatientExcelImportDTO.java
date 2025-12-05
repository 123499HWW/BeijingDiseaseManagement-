package com.hxj.common.dto.patient;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 患者信息Excel导入DTO
 */
@Data
public class PatientExcelImportDTO {
    
    @ExcelProperty("患者ID")
    private String patientNumber;
    
    @ExcelProperty("就诊次数")
    private String visitCount;
    
    @ExcelProperty("住院日期")
    private String admissionDate;
    
    @ExcelProperty("性别")
    private String gender;
    
    @ExcelProperty("年龄")
    private String age;
    
    @ExcelProperty("主诉")
    private String chiefComplaint;
    
    @ExcelProperty("现病史")
    private String presentIllness;
    
    @ExcelProperty("查体")
    private String physicalExamination;
    
    @ExcelProperty("动脉血气pH")
    private String arterialBloodGasPh;
    
    @ExcelProperty("动脉血气pO2(mmHg)")
    private String arterialBloodGasPo2;
    
    @ExcelProperty("动脉血气氧合指数(mmHg)")
    private String arterialBloodGasOxygenationIndex;
    
    @ExcelProperty("动脉血气pCO2(mmHg)")
    private String arterialBloodGasPco2;
    
    @ExcelProperty("血常规血小板计数(×10^9/L)")
    private String plateletCount;
    
    @ExcelProperty("血尿素氮(mmol/L)")
    private String bloodUreaNitrogen;
    
    @ExcelProperty("血肌酐(μmol/L)")
    private String serumCreatinine;
    
    @ExcelProperty("总胆红素(μmol/L)")
    private String totalBilirubin;
    
    @ExcelProperty("是否开具胸部CT")
    private String chestCtOrdered;
    
    @ExcelProperty("胸部CT报告")
    private String chestCtReport;
    
    @ExcelProperty("是否应用多巴胺")
    private String dopamineUsed;
    
    @ExcelProperty("是否应用多巴酚丁胺")
    private String dobutamineUsed;
    
    @ExcelProperty("是否应用去甲肾上腺素")
    private String norepinephrineUsed;
    
    @ExcelProperty("是否应用血管活性药物")
    private String vasoactiveDrugUsed;
    
    @ExcelProperty("是否应用特殊级/限制级抗生素")
    private String specialAntibioticUsed;
    
    @ExcelProperty("抗生素种类")
    private String antibioticType;
    
    @ExcelProperty("是否应用呼吸机")
    private String ventilatorUsed;
    
    @ExcelProperty("是否入住ICU")
    private String icuAdmission;
    
    // 用于存储行号，便于错误定位
    private String rowIndex;
}
