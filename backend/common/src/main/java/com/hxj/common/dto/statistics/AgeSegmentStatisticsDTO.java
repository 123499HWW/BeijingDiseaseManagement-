package com.hxj.common.dto.statistics;

import lombok.Data;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 年龄分段统计DTO
 * 用于统计不同年龄段患者的各项评估指标分布
 */
@Data
public class AgeSegmentStatisticsDTO {
    
    /**
     * 年龄段名称（如："0-17"、"18-29"等）
     */
    private String ageRange;
    
    /**
     * 该年龄段的患者总数
     */
    private Integer totalPatients;
    
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
     * key: 严重程度等级（MILD、MODERATE、SEVERE、CRITICAL）
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
        
        totalPatients = 0;
    }
}
