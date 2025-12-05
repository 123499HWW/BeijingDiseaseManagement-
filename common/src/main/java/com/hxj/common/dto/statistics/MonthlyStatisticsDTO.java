package com.hxj.common.dto.statistics;

import lombok.Data;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 月度统计DTO
 * 用于统计每月患者的各项评估指标分布
 */
@Data
public class MonthlyStatisticsDTO {
    
    /**
     * 月份（格式：YYYY-MM）
     */
    private String month;
    
    /**
     * 该月份的患者总数
     */
    private Integer totalPatients;
    
    /**
     * 该月新入院患者数
     */
    private Integer newAdmissions;
    
    /**
     * COVID-19严重程度分布
     * key: 严重程度等级（非重型、重型、重型（中危）、重型（高危））
     * value: 人数
     */
    private Map<String, Integer> covid19SeverityDistribution = new LinkedHashMap<>();
    
    /**
     * COVID-19危重程度分布
     * key: 危重程度等级（非危重型、危重型、危重型（高危）、危重型（极危重））
     * value: 人数
     */
    private Map<String, Integer> covid19CriticalDistribution = new LinkedHashMap<>();
    
    /**
     * 呼吸道症候群严重程度分布
     * key: 严重程度等级（轻度、中度、重度、危重）
     * value: 人数
     */
    private Map<String, Integer> respiratorySyndromeDistribution = new LinkedHashMap<>();
    
    /**
     * 社区获得性肺炎风险分布
     * key: 风险等级（低风险、中风险、高风险、极高风险）
     * value: 人数
     */
    private Map<String, Integer> communityPneumoniaRiskDistribution = new LinkedHashMap<>();
    
    /**
     * 脓毒症风险分布
     * key: 风险等级（低风险、中风险、高风险、极高风险）
     * value: 人数
     */
    private Map<String, Integer> sepsisRiskDistribution = new LinkedHashMap<>();
    
    /**
     * 重症肺炎诊断分布
     * key: 诊断结果（重症肺炎、非重症肺炎）
     * value: 人数
     */
    private Map<String, Integer> severePneumoniaDiagnosisDistribution = new LinkedHashMap<>();
    
    /**
     * CURB-65风险等级分布
     * key: 风险等级（低风险、中风险、高风险）
     * value: 人数
     */
    private Map<String, Integer> curbRiskDistribution = new LinkedHashMap<>();
    
    /**
     * PSI风险等级分布
     * key: 风险等级（I级、II级、III级、IV级、V级）
     * value: 人数
     */
    private Map<String, Integer> psiRiskDistribution = new LinkedHashMap<>();
    
    /**
     * CPIS风险等级分布
     * key: 风险等级（低风险、高风险）
     * value: 人数
     */
    private Map<String, Integer> cpisRiskDistribution = new LinkedHashMap<>();
    
    /**
     * qSOFA风险等级分布
     * key: 风险等级（低风险、高风险）
     * value: 人数
     */
    private Map<String, Integer> qsofaRiskDistribution = new LinkedHashMap<>();
    
    /**
     * SOFA严重程度分布
     * key: 严重程度（轻度、中度、重度、极重度）
     * value: 人数
     */
    private Map<String, Integer> sofaSeverityDistribution = new LinkedHashMap<>();
    
    /**
     * 性别分布
     * key: 性别（男、女）
     * value: 人数
     */
    private Map<String, Integer> genderDistribution = new LinkedHashMap<>();
    
    /**
     * 年龄段分布
     * key: 年龄段（0-17、18-29、30-44、45-59、60-74、75+）
     * value: 人数
     */
    private Map<String, Integer> ageGroupDistribution = new LinkedHashMap<>();
    
    /**
     * 初始化所有分布统计
     */
    public void initializeDistributions() {
        // 初始化COVID-19严重程度分布
        covid19SeverityDistribution.put("非重型", 0);
        covid19SeverityDistribution.put("重型", 0);
        covid19SeverityDistribution.put("重型（中危）", 0);
        covid19SeverityDistribution.put("重型（高危）", 0);
        covid19SeverityDistribution.put("无数据", 0);
        
        // 初始化COVID-19危重程度分布
        covid19CriticalDistribution.put("非危重型", 0);
        covid19CriticalDistribution.put("危重型", 0);
        covid19CriticalDistribution.put("危重型（高危）", 0);
        covid19CriticalDistribution.put("危重型（极危重）", 0);
        covid19CriticalDistribution.put("无数据", 0);
        
        // 初始化呼吸道症候群分布
        respiratorySyndromeDistribution.put("轻度", 0);
        respiratorySyndromeDistribution.put("中度", 0);
        respiratorySyndromeDistribution.put("重度", 0);
        respiratorySyndromeDistribution.put("危重", 0);
        respiratorySyndromeDistribution.put("无数据", 0);
        
        // 初始化社区获得性肺炎风险分布
        communityPneumoniaRiskDistribution.put("低风险", 0);
        communityPneumoniaRiskDistribution.put("中风险", 0);
        communityPneumoniaRiskDistribution.put("高风险", 0);
        communityPneumoniaRiskDistribution.put("极高风险", 0);
        communityPneumoniaRiskDistribution.put("无数据", 0);
        
        // 初始化脓毒症风险分布
        sepsisRiskDistribution.put("低风险", 0);
        sepsisRiskDistribution.put("中风险", 0);
        sepsisRiskDistribution.put("高风险", 0);
        sepsisRiskDistribution.put("极高风险", 0);
        sepsisRiskDistribution.put("无数据", 0);
        
        // 初始化重症肺炎诊断分布
        severePneumoniaDiagnosisDistribution.put("重症肺炎", 0);
        severePneumoniaDiagnosisDistribution.put("非重症肺炎", 0);
        severePneumoniaDiagnosisDistribution.put("无数据", 0);
        
        // 初始化CURB-65风险分布
        curbRiskDistribution.put("低风险", 0);
        curbRiskDistribution.put("中风险", 0);
        curbRiskDistribution.put("高风险", 0);
        curbRiskDistribution.put("无数据", 0);
        
        // 初始化PSI风险分布
        psiRiskDistribution.put("I级", 0);
        psiRiskDistribution.put("II级", 0);
        psiRiskDistribution.put("III级", 0);
        psiRiskDistribution.put("IV级", 0);
        psiRiskDistribution.put("V级", 0);
        psiRiskDistribution.put("无数据", 0);
        
        // 初始化CPIS风险分布
        cpisRiskDistribution.put("低风险", 0);
        cpisRiskDistribution.put("高风险", 0);
        cpisRiskDistribution.put("无数据", 0);
        
        // 初始化qSOFA风险分布
        qsofaRiskDistribution.put("低风险", 0);
        qsofaRiskDistribution.put("高风险", 0);
        qsofaRiskDistribution.put("无数据", 0);
        
        // 初始化SOFA严重程度分布
        sofaSeverityDistribution.put("轻度", 0);
        sofaSeverityDistribution.put("中度", 0);
        sofaSeverityDistribution.put("重度", 0);
        sofaSeverityDistribution.put("极重度", 0);
        sofaSeverityDistribution.put("无数据", 0);
        
        // 初始化性别分布
        genderDistribution.put("男", 0);
        genderDistribution.put("女", 0);
        genderDistribution.put("未知", 0);
        
        // 初始化年龄段分布
        ageGroupDistribution.put("0-17", 0);
        ageGroupDistribution.put("18-29", 0);
        ageGroupDistribution.put("30-44", 0);
        ageGroupDistribution.put("45-59", 0);
        ageGroupDistribution.put("60-74", 0);
        ageGroupDistribution.put("75+", 0);
        
        totalPatients = 0;
        newAdmissions = 0;
    }
}
