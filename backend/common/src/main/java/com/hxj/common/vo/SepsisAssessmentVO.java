package com.hxj.common.vo;

import lombok.Data;
import java.util.Date;

/**
 * 脓毒症评估联查结果VO
 * 包含患者信息、qSOFA和SOFA评分
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Data
public class SepsisAssessmentVO {
    
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
    
    /**
     * 血压相关数据
     */
    private Double systolicPressure;
    private Double diastolicPressure;
    
    /**
     * 实验室检查数据
     */
    private Double arterialPh;
    private Double arterialPo2;
    private Double bloodUreaNitrogen;
    private Double serumCreatinine;
    private Double totalBilirubin;
    private Double plateletCount;
    
    /**
     * 其他临床数据
     */
    private Integer ventilatorUsed;
    private Integer icuAdmission;
    private Integer vasoactiveDrugsUsed;
    
    // ========== qSOFA评分 (qsofa_assessment) ==========
    /**
     * qSOFA评估ID
     */
    private Long qsofaId;
    
    /**
     * 呼吸频率≥22次/分
     */
    private Integer qsofaRespiratoryRate;
    
    /**
     * 精神状态改变
     */
    private Integer qsofaMentalStatus;
    
    /**
     * 收缩压≤100mmHg
     */
    private Integer qsofaSystolicBp;
    
    /**
     * qSOFA总分（0-3分）
     */
    private Integer qsofaTotalScore;
    
    /**
     * qSOFA风险等级
     */
    private String qsofaRiskLevel;
    
    /**
     * qSOFA评估时间
     */
    private Date qsofaAssessmentTime;
    
    /**
     * qSOFA评估者
     */
    private String qsofaAssessor;
    
    // ========== SOFA评分 (sofa_assessment) ==========
    /**
     * SOFA评估ID
     */
    private Long sofaId;
    
    /**
     * 呼吸系统评分（PaO2/FiO2）
     */
    private Integer sofaRespirationScore;
    
    /**
     * 呼吸系统指标值
     */
    private Double sofaPao2Fio2;
    
    /**
     * 凝血系统评分（血小板）
     */
    private Integer sofaCoagulationScore;
    
    /**
     * 血小板计数
     */
    private Double sofaPlateletCount;
    
    /**
     * 肝脏系统评分（胆红素）
     */
    private Integer sofaLiverScore;
    
    /**
     * 胆红素值
     */
    private Double sofaBilirubin;
    
    /**
     * 心血管系统评分（血压/血管活性药物）
     */
    private Integer sofaCardiovascularScore;
    
    /**
     * 平均动脉压
     */
    private Double sofaMeanArterialPressure;
    
    /**
     * 血管活性药物使用情况
     */
    private String sofaVasoactiveDrugs;
    
    /**
     * 中枢神经系统评分（Glasgow昏迷评分）
     */
    private Integer sofaCnsScore;
    
    /**
     * Glasgow昏迷评分值
     */
    private Integer sofaGlasgowComaScore;
    
    /**
     * 肾脏系统评分（肌酐/尿量）
     */
    private Integer sofaRenalScore;
    
    /**
     * 肌酐值
     */
    private Double sofaCreatinine;
    
    /**
     * 24小时尿量
     */
    private Double sofaUrineOutput;
    
    /**
     * SOFA总分（0-24分）
     */
    private Integer sofaTotalScore;
    
    /**
     * SOFA风险等级
     */
    private String sofaRiskLevel;
    
    /**
     * SOFA评估时间
     */
    private Date sofaAssessmentTime;
    
    /**
     * SOFA评估者
     */
    private String sofaAssessor;
    
    // ========== 计算字段和风险评估 ==========
    /**
     * 脓毒症筛查结果（基于qSOFA）
     */
    private String sepsisScreeningResult;
    
    /**
     * 器官功能障碍评估（基于SOFA）
     */
    private String organDysfunctionAssessment;
    
    /**
     * 脓毒症风险等级（综合评估）
     */
    private String sepsisRiskLevel;
    
    /**
     * 死亡风险预测
     */
    private String mortalityRisk;
    
    /**
     * SOFA评分变化趋势
     */
    private String sofaScoreTrend;
    
    /**
     * 建议治疗方案
     */
    private String recommendedTreatment;
    
    /**
     * 是否需要ICU
     */
    private Boolean requiresIcu;
    
    /**
     * 已完成的评估类型
     */
    private String completedAssessments;
    
    /**
     * 评估完整度（百分比）
     */
    private Integer assessmentCompleteness;
}
