package com.hxj.common.dto.statistics;

import lombok.Data;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 评估统计DTO
 * 用于统计各个评分中每个程度的患者数量
 */
@Data
public class AssessmentStatisticsDTO {
    
    /**
     * CURB-65评分统计（低风险、中风险、高风险）
     */
    private Map<String, Integer> curbStatistics = new LinkedHashMap<>();
    
    /**
     * PSI评分统计（Ⅰ级、Ⅱ级、Ⅲ级、Ⅳ级、Ⅴ级）
     */
    private Map<String, Integer> psiStatistics = new LinkedHashMap<>();
    
    /**
     * CPIS评分统计（低风险、高风险）
     */
    private Map<String, Integer> cpisStatistics = new LinkedHashMap<>();
    
    /**
     * qSOFA评分统计（低风险、高风险）
     */
    private Map<String, Integer> qsofaStatistics = new LinkedHashMap<>();
    
    /**
     * SOFA评分统计（正常或轻微、轻度、中度、重度、极重度）
     */
    private Map<String, Integer> sofaStatistics = new LinkedHashMap<>();
    
    /**
     * 重症肺炎诊断统计（重症肺炎、非重症肺炎）
     */
    private Map<String, Integer> severePneumoniaStatistics = new LinkedHashMap<>();
    
    /**
     * COVID-19重型诊断统计（非重型、重型、重型（中危）、重型（高危））
     */
    private Map<String, Integer> covid19Statistics = new LinkedHashMap<>();
    
    /**
     * COVID-19危重型诊断统计（非危重型、危重型、危重型（高危）、危重型（极危重））
     */
    private Map<String, Integer> covid19CriticalStatistics = new LinkedHashMap<>();
    
    /**
     * 呼吸道症候群评估统计（轻度、中度、重度、危重）
     */
    private Map<String, Integer> respiratorySyndromeStatistics = new LinkedHashMap<>();
    
    /**
     * 总患者数
     */
    private Integer totalPatients;
    
    /**
     * 已评估患者数（至少完成一项评估）
     */
    private Integer assessedPatients;
    
    /**
     * 未评估患者数
     */
    private Integer unassessedPatients;
    
    /**
     * 高风险患者数（任一评分达到高风险/重症标准）
     */
    private Integer highRiskPatients;
    
    /**
     * 统计时间戳
     */
    private Long statisticsTimestamp;
    
    /**
     * 初始化统计数据
     */
    public void initializeStatistics() {
        // 初始化CURB-65统计
        curbStatistics.put("低风险", 0);
        curbStatistics.put("中风险", 0);
        curbStatistics.put("高风险", 0);
        
        // 初始化PSI统计
        psiStatistics.put("Ⅰ级", 0);
        psiStatistics.put("Ⅱ级", 0);
        psiStatistics.put("Ⅲ级", 0);
        psiStatistics.put("Ⅳ级", 0);
        psiStatistics.put("Ⅴ级", 0);
        
        // 初始化CPIS统计
        cpisStatistics.put("低风险", 0);
        cpisStatistics.put("高风险", 0);
        
        // 初始化qSOFA统计
        qsofaStatistics.put("低风险", 0);
        qsofaStatistics.put("高风险", 0);
        
        // 初始化SOFA统计
        sofaStatistics.put("正常或轻微", 0);
        sofaStatistics.put("轻度", 0);
        sofaStatistics.put("中度", 0);
        sofaStatistics.put("重度", 0);
        sofaStatistics.put("极重度", 0);
        
        // 初始化重症肺炎统计
        severePneumoniaStatistics.put("重症肺炎", 0);
        severePneumoniaStatistics.put("非重症肺炎", 0);
        
        // 初始化COVID-19重型统计
        covid19Statistics.put("非重型", 0);
        covid19Statistics.put("重型", 0);
        covid19Statistics.put("重型（中危）", 0);
        covid19Statistics.put("重型（高危）", 0);
        
        // 初始化COVID-19危重型统计
        covid19CriticalStatistics.put("非危重型", 0);
        covid19CriticalStatistics.put("危重型", 0);
        covid19CriticalStatistics.put("危重型（高危）", 0);
        covid19CriticalStatistics.put("危重型（极危重）", 0);
        
        // 初始化呼吸道症候群统计
        respiratorySyndromeStatistics.put("轻度", 0);
        respiratorySyndromeStatistics.put("中度", 0);
        respiratorySyndromeStatistics.put("重度", 0);
        respiratorySyndromeStatistics.put("危重", 0);
        
        // 设置时间戳
        statisticsTimestamp = System.currentTimeMillis();
    }
}
