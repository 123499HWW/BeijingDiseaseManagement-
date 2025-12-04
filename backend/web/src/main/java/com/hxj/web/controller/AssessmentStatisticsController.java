package com.hxj.web.controller;

import com.hxj.common.result.Result;
import com.hxj.common.dto.statistics.AssessmentStatisticsDTO;
import com.hxj.service.AssessmentStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评估统计控制器
 * 提供各种评分系统的统计分析接口
 */
@RestController
@RequestMapping("/api/assessment-statistics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AssessmentStatisticsController {
    
    private final AssessmentStatisticsService assessmentStatisticsService;
    
    /**
     * 获取所有评分系统的统计数据
     * @return 包含所有评分系统统计数据的DTO
     */
    @GetMapping("/all")
    public Result<AssessmentStatisticsDTO> getAllStatistics() {
        log.info("获取所有评分系统的统计数据");
        
        try {
            AssessmentStatisticsDTO statistics = assessmentStatisticsService.getAllAssessmentStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取指定评分系统的统计数据
     * @param type 评分类型（CURB65、PSI、CPIS、QSOFA、SOFA、SEVERE_PNEUMONIA、COVID19、COVID19_CRITICAL、RESPIRATORY_SYNDROME）
     * @return 统计结果Map，key为风险等级，value为患者数量
     */
    @GetMapping("/by-type/{type}")
    public Result<Map<String, Integer>> getStatisticsByType(@PathVariable String type) {
        log.info("获取{}评分系统的统计数据", type);
        
        try {
            Map<String, Integer> statistics = assessmentStatisticsService.getAssessmentStatisticsByType(type);
            if (statistics.isEmpty()) {
                return Result.error("未找到指定类型的评分数据：" + type);
            }
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取{}统计数据失败", type, e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取评估覆盖率统计
     * @return 包含总患者数、已评估数、未评估数等
     */
    @GetMapping("/coverage")
    public Result<Map<String, Object>> getAssessmentCoverage() {
        log.info("获取评估覆盖率统计");
        
        try {
            AssessmentStatisticsDTO statistics = assessmentStatisticsService.getAllAssessmentStatistics();
            
            Map<String, Object> coverage = new java.util.HashMap<>();
            coverage.put("totalPatients", statistics.getTotalPatients());
            coverage.put("assessedPatients", statistics.getAssessedPatients());
            coverage.put("unassessedPatients", statistics.getUnassessedPatients());
            coverage.put("highRiskPatients", statistics.getHighRiskPatients());
            
            // 计算覆盖率百分比
            if (statistics.getTotalPatients() > 0) {
                double coverageRate = (double) statistics.getAssessedPatients() / statistics.getTotalPatients() * 100;
                coverage.put("coverageRate", String.format("%.2f%%", coverageRate));
                
                double highRiskRate = (double) statistics.getHighRiskPatients() / statistics.getAssessedPatients() * 100;
                coverage.put("highRiskRate", String.format("%.2f%%", highRiskRate));
            } else {
                coverage.put("coverageRate", "0%");
                coverage.put("highRiskRate", "0%");
            }
            
            return Result.success(coverage);
        } catch (Exception e) {
            log.error("获取评估覆盖率统计失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取高风险患者统计
     * @return 各评分系统中高风险患者的数量
     */
    @GetMapping("/high-risk")
    public Result<Map<String, Object>> getHighRiskStatistics() {
        log.info("获取高风险患者统计");
        
        try {
            AssessmentStatisticsDTO statistics = assessmentStatisticsService.getAllAssessmentStatistics();
            
            Map<String, Object> highRisk = new java.util.HashMap<>();
            
            // CURB-65高风险
            highRisk.put("curb65HighRisk", statistics.getCurbStatistics().getOrDefault("高风险", 0));
            
            // PSI Ⅳ级和Ⅴ级
            int psiHighRisk = statistics.getPsiStatistics().getOrDefault("Ⅳ级", 0) + 
                              statistics.getPsiStatistics().getOrDefault("Ⅴ级", 0);
            highRisk.put("psiHighRisk", psiHighRisk);
            
            // CPIS高风险
            highRisk.put("cpisHighRisk", statistics.getCpisStatistics().getOrDefault("高风险", 0));
            
            // qSOFA高风险
            highRisk.put("qsofaHighRisk", statistics.getQsofaStatistics().getOrDefault("高风险", 0));
            
            // SOFA重度和极重度
            int sofaHighRisk = statistics.getSofaStatistics().getOrDefault("重度", 0) + 
                               statistics.getSofaStatistics().getOrDefault("极重度", 0);
            highRisk.put("sofaHighRisk", sofaHighRisk);
            
            // 重症肺炎
            highRisk.put("severePneumonia", statistics.getSeverePneumoniaStatistics().getOrDefault("重症肺炎", 0));
            
            // COVID-19重型（中危和高危）
            int covid19HighRisk = statistics.getCovid19Statistics().getOrDefault("重型（中危）", 0) + 
                                  statistics.getCovid19Statistics().getOrDefault("重型（高危）", 0);
            highRisk.put("covid19HighRisk", covid19HighRisk);
            
            // COVID-19危重型（所有危重型）
            int covid19CriticalHighRisk = 
                statistics.getCovid19CriticalStatistics().getOrDefault("危重型", 0) +
                statistics.getCovid19CriticalStatistics().getOrDefault("危重型（高危）", 0) + 
                statistics.getCovid19CriticalStatistics().getOrDefault("危重型（极危重）", 0);
            highRisk.put("covid19CriticalHighRisk", covid19CriticalHighRisk);
            
            // 呼吸道症候群重度和危重
            int respiratorySyndromeHighRisk = 
                statistics.getRespiratorySyndromeStatistics().getOrDefault("重度", 0) +
                statistics.getRespiratorySyndromeStatistics().getOrDefault("危重", 0);
            highRisk.put("respiratorySyndromeHighRisk", respiratorySyndromeHighRisk);
            
            // 总高风险患者数
            highRisk.put("totalHighRiskPatients", statistics.getHighRiskPatients());
            
            return Result.success(highRisk);
        } catch (Exception e) {
            log.error("获取高风险患者统计失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取评分系统汇总统计（用于图表展示）
     * @return 适合图表展示的统计数据
     */
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummaryStatistics() {
        log.info("获取评分系统汇总统计");
        
        try {
            AssessmentStatisticsDTO statistics = assessmentStatisticsService.getAllAssessmentStatistics();
            
            Map<String, Object> summary = new java.util.HashMap<>();
            
            // 基础统计信息
            summary.put("totalPatients", statistics.getTotalPatients());
            summary.put("assessedPatients", statistics.getAssessedPatients());
            summary.put("unassessedPatients", statistics.getUnassessedPatients());
            summary.put("highRiskPatients", statistics.getHighRiskPatients());
            
            // 各评分系统统计
            summary.put("curb65", statistics.getCurbStatistics());
            summary.put("psi", statistics.getPsiStatistics());
            summary.put("cpis", statistics.getCpisStatistics());
            summary.put("qsofa", statistics.getQsofaStatistics());
            summary.put("sofa", statistics.getSofaStatistics());
            summary.put("severePneumonia", statistics.getSeverePneumoniaStatistics());
            summary.put("covid19", statistics.getCovid19Statistics());
            summary.put("covid19Critical", statistics.getCovid19CriticalStatistics());
            summary.put("respiratorySyndrome", statistics.getRespiratorySyndromeStatistics());
            
            // 添加时间戳
            summary.put("timestamp", statistics.getStatisticsTimestamp());
            
            return Result.success(summary);
        } catch (Exception e) {
            log.error("获取汇总统计失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
}
