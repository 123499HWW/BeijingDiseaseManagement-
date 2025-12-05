package com.hxj.common.dto.statistics;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 疾病分类统计DTO
 * 用于统计各种疾病的患者数量分布
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Data
public class DiseaseStatisticsDTO {
    
    /**
     * 统计时间
     */
    private String statisticsTime;
    
    /**
     * 总患者数
     */
    private Integer totalPatients;
    
    /**
     * 社区获得性肺炎风险统计
     */
    private Map<String, Integer> communityPneumoniaRiskStats;
    
    /**
     * 社区获得性肺炎高风险患者数
     */
    private Integer communityPneumoniaHighRiskCount;
    
    /**
     * 社区获得性肺炎低风险患者数
     */
    private Integer communityPneumoniaLowRiskCount;
    
    /**
     * 脓毒症风险统计
     */
    private Map<String, Integer> sepsisRiskStats;
    
    /**
     * 脓毒症高风险患者数
     */
    private Integer sepsisHighRiskCount;
    
    /**
     * 脓毒症低风险患者数
     */
    private Integer sepsisLowRiskCount;
    
    /**
     * COVID-19重型评估统计
     */
    private Map<String, Integer> covid19Stats;
    
    /**
     * COVID-19重型患者数
     */
    private Integer covid19SevereCount;
    
    /**
     * COVID-19非重型患者数
     */
    private Integer covid19NonSevereCount;
    
    /**
     * COVID-19危重型评估统计
     */
    private Map<String, Integer> covid19CriticalStats;
    
    /**
     * COVID-19危重型患者数
     */
    private Integer covid19CriticalCount;
    
    /**
     * COVID-19非危重型患者数
     */
    private Integer covid19NonCriticalCount;
    
    /**
     * 呼吸道症候群评估统计
     */
    private Map<String, Integer> respiratorySyndromeStats;
    
    /**
     * 呼吸道症候群各级别患者数
     */
    private Map<String, Integer> respiratorySyndromeLevelStats;
    
    /**
     * 已评估患者总数
     */
    private Integer assessedPatients;
    
    /**
     * 未评估患者总数
     */
    private Integer unassessedPatients;
    
    /**
     * 初始化统计数据
     */
    public void initializeStatistics() {
        this.communityPneumoniaRiskStats = new HashMap<>();
        this.communityPneumoniaRiskStats.put("高风险", 0);
        this.communityPneumoniaRiskStats.put("低风险", 0);
        this.communityPneumoniaRiskStats.put("无数据", 0);
        
        this.sepsisRiskStats = new HashMap<>();
        this.sepsisRiskStats.put("高风险", 0);
        this.sepsisRiskStats.put("低风险", 0);
        this.sepsisRiskStats.put("无数据", 0);
        
        this.covid19Stats = new HashMap<>();
        this.covid19Stats.put("重型", 0);
        this.covid19Stats.put("非重型", 0);
        this.covid19Stats.put("未评估", 0);
        
        this.covid19CriticalStats = new HashMap<>();
        this.covid19CriticalStats.put("危重型", 0);
        this.covid19CriticalStats.put("非危重型", 0);
        this.covid19CriticalStats.put("未评估", 0);
        
        this.respiratorySyndromeStats = new HashMap<>();
        this.respiratorySyndromeStats.put("已评估", 0);
        this.respiratorySyndromeStats.put("未评估", 0);
        
        this.respiratorySyndromeLevelStats = new HashMap<>();
        this.respiratorySyndromeLevelStats.put("轻度", 0);
        this.respiratorySyndromeLevelStats.put("中度", 0);
        this.respiratorySyndromeLevelStats.put("重度", 0);
        this.respiratorySyndromeLevelStats.put("无数据", 0);
        
        this.communityPneumoniaHighRiskCount = 0;
        this.communityPneumoniaLowRiskCount = 0;
        this.sepsisHighRiskCount = 0;
        this.sepsisLowRiskCount = 0;
        this.covid19SevereCount = 0;
        this.covid19NonSevereCount = 0;
        this.covid19CriticalCount = 0;
        this.covid19NonCriticalCount = 0;
        this.assessedPatients = 0;
        this.unassessedPatients = 0;
        this.totalPatients = 0;
    }
}