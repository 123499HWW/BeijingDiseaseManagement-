package com.hxj.common.vo;

import lombok.Data;
import java.util.Date;

/**
 * 多评估表联查结果VO
 * 包含患者信息和多个评估结果
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Data
public class MultiAssessmentVO {
    
    // ========== 患者基本信息 (patient_info) ==========
    /**
     * 患者ID
     */
    private Long patientId;
    
    /**
     * 患者编号
     */
    private String patientNumber;
    
    /**
     * 患者姓名
     */
    private String patientName;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 入院日期
     */
    private Date admissionDate;
    
    /**
     * 主诉
     */
    private String chiefComplaint;
    
    /**
     * 现病史
     */
    private String presentIllness;
    
    // ========== CURB-65评分 (curb_assessment_result) ==========
    /**
     * CURB评估ID
     */
    private Long curbId;
    
    /**
     * 意识障碍结果
     */
    private Integer curbConfusionResult;
    
    /**
     * 尿素氮结果
     */
    private Integer curbUreaResult;
    
    /**
     * 呼吸频率结果
     */
    private Integer curbRespirationResult;
    
    /**
     * 血压结果
     */
    private Integer curbBloodPressureResult;
    
    /**
     * 年龄结果
     */
    private Integer curbAgeResult;
    
    /**
     * CURB总分
     */
    private Integer curbTotalScore;
    
    /**
     * CURB风险等级
     */
    private String curbRiskLevel;
    
    /**
     * CURB评估时间
     */
    private Date curbAssessmentTime;
    
    // ========== PSI评分 (psi_assessment_result) ==========
    /**
     * PSI评估ID
     */
    private Long psiId;
    
    /**
     * PSI年龄分数
     */
    private Integer psiAgeScore;
    
    /**
     * PSI精神状态改变分数
     */
    private Integer psiMentalStatusChangeScore;
    
    /**
     * PSI心率分数
     */
    private Integer psiHeartRateScore;
    
    /**
     * PSI呼吸频率分数
     */
    private Integer psiRespiratoryRateScore;
    
    /**
     * PSI收缩压分数
     */
    private Integer psiSystolicBpScore;
    
    /**
     * PSI体温分数
     */
    private Integer psiTemperatureScore;
    
    /**
     * PSI动脉血pH分数
     */
    private Integer psiArterialPhScore;
    
    /**
     * PSI氧分压分数
     */
    private Integer psiPao2Score;
    
    /**
     * PSI胸腔积液分数
     */
    private Integer psiPleuralEffusionScore;
    
    /**
     * PSI总分
     */
    private Integer psiTotalScore;
    
    /**
     * PSI风险等级
     */
    private String psiRiskLevel;
    
    /**
     * PSI风险描述
     */
    private String psiRiskDescription;
    
    /**
     * PSI评估时间
     */
    private Date psiAssessmentTime;
    
    // ========== CPIS评分 (cpis_assessment_result) ==========
    /**
     * CPIS评估ID
     */
    private Long cpisId;
    
    /**
     * CPIS体温分数
     */
    private Integer cpisTemperatureScore;
    
    /**
     * CPIS白细胞分数 (已废弃，字段不存在)
     */
    @Deprecated
    private Integer cpisWhiteBloodCellScore = 0;
    
    /**
     * CPIS分泌物分数
     */
    private Integer cpisSecretionScore;
    
    /**
     * CPIS氧合指数分数
     */
    private Integer cpisOxygenationIndexScore;
    
    /**
     * CPIS胸片分数
     */
    private Integer cpisChestXrayScore;
    
    /**
     * CPIS气管吸取物培养分数
     */
    private Integer cpisCultureScore;
    
    /**
     * CPIS总分
     */
    private Integer cpisTotalScore;
    
    /**
     * CPIS风险等级
     */
    private String cpisRiskLevel;
    
    /**
     * CPIS评估时间
     */
    private Date cpisAssessmentTime;
    
    // ========== 重症肺炎诊断 (severe_pneumonia_diagnosis) ==========
    /**
     * 诊断ID
     */
    private Long diagnosisId;
    
    /**
     * 是否机械通气
     */
    private Integer mechanicalVentilation;
    
    /**
     * 是否使用血管活性药物
     */
    private Integer vasoactiveDrugs;
    
    /**
     * 呼吸频率是否过高
     */
    private Integer respiratoryRateHigh;
    
    /**
     * 呼吸频率值
     */
    private Double respiratoryRate;
    
    /**
     * 氧合指数是否过低
     */
    private Integer oxygenationIndexLow;
    
    /**
     * 氧合指数值
     */
    private Double oxygenationIndex;
    
    /**
     * 是否意识障碍
     */
    private Integer consciousnessDisorder;
    
    /**
     * 尿素氮是否过高
     */
    private Integer ureaNitrogenHigh;
    
    /**
     * 尿素氮值
     */
    private Double ureaNitrogen;
    
    /**
     * 是否低血压
     */
    private Integer hypotension;
    
    /**
     * 是否重症肺炎
     */
    private Integer isSeverePneumonia;
    
    /**
     * 诊断结论
     */
    private String diagnosisConclusion;
    
    /**
     * 诊断时间
     */
    private Date diagnosisTime;
    
    // ========== 统计和风险信息 ==========
    /**
     * 社区获得性肺炎风险（基于CURB和PSI计算）
     */
    private String communityAcquiredPneumoniaRisk;
    
    /**
     * 呼吸机相关肺炎风险（基于CPIS）
     */
    private String ventilatorPneumoniaRisk;
    
    /**
     * 综合风险评级
     */
    private String overallRiskLevel;
    
    /**
     * 已完成的评估数量
     */
    private Integer completedAssessmentCount;
}
