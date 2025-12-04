package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.ComprehensiveAssessmentQueryDTO;
import com.hxj.common.dto.statistics.AgeSegmentStatisticsDTO;
import com.hxj.common.dto.statistics.DiseaseStatisticsDTO;
import com.hxj.common.dto.statistics.GenderStatisticsDTO;
import com.hxj.common.dto.statistics.MonthlyStatisticsDTO;
import com.hxj.common.result.Result;
import com.hxj.common.vo.ComprehensiveAssessmentVO;
import com.hxj.service.ComprehensiveAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 综合评估控制器
 * 提供多表连接的综合查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/comprehensive-assessment")
public class ComprehensiveAssessmentController {
    
    @Autowired
    private ComprehensiveAssessmentService comprehensiveAssessmentService;
    
    /**
     * 综合评估分页查询
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    public Result<IPage<ComprehensiveAssessmentVO>> queryComprehensivePage(@RequestBody @Validated ComprehensiveAssessmentQueryDTO queryDTO) {
        long startTime = System.currentTimeMillis();
        log.info("收到综合评估分页查询请求: {}", queryDTO);
        
        try {
            // 参数校验
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(10);
            }
            if (queryDTO.getPageSize() > 100) {
                log.warn("请求的每页记录数过大: {}, 限制为100", queryDTO.getPageSize());
                queryDTO.setPageSize(100);
            }
            
            // 清理无效参数（前端可能发送默认值）
            cleanInvalidParameters(queryDTO);
            
            // 验证年龄范围
            if (queryDTO.getMinAge() != null && queryDTO.getMaxAge() != null) {
                if (queryDTO.getMinAge() > queryDTO.getMaxAge()) {
                    return Result.error("最小年龄不能大于最大年龄");
                }
            }
            
            // 验证分数范围
            if (!validateScoreRanges(queryDTO)) {
                return Result.error("分数范围设置错误：最小值不能大于最大值");
            }
            
            // 执行查询
            IPage<ComprehensiveAssessmentVO> result = comprehensiveAssessmentService.queryComprehensivePage(queryDTO);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 记录性能指标
            Map<String, Object> performanceMetrics = new HashMap<>();
            performanceMetrics.put("executionTime", executionTime + "ms");
            performanceMetrics.put("totalRecords", result.getTotal());
            performanceMetrics.put("currentPageRecords", result.getRecords().size());
            
            if (executionTime > 2000) {
                log.warn("综合评估查询响应时间超过2秒: {}ms", executionTime);
                performanceMetrics.put("warning", "查询时间超过2秒，建议优化查询条件");
            }
            
            log.info("综合评估分页查询成功，性能指标: {}", performanceMetrics);
            
            return Result.success(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("综合评估分页查询失败，执行时间: {}ms, 错误: ", endTime - startTime, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取单个患者的综合评估结果
     * 
     * @param patientId 患者ID
     * @return 综合评估结果
     */
    @GetMapping("/patient/{patientId}")
    public Result<ComprehensiveAssessmentVO> getPatientComprehensiveAssessment(@PathVariable Long patientId) {
        log.info("获取患者综合评估，患者ID: {}", patientId);
        
        try {
            if (patientId == null || patientId <= 0) {
                return Result.error("患者ID无效");
            }
            
            ComprehensiveAssessmentVO assessment = comprehensiveAssessmentService.getPatientComprehensiveAssessment(patientId);
            
            if (assessment == null) {
                return Result.error("未找到该患者的评估记录");
            }
            
            log.info("成功获取患者综合评估，患者ID: {}, 患者编号: {}", 
                    patientId, assessment.getPatientNumber());
            
            return Result.success(assessment);
        } catch (Exception e) {
            log.error("获取患者综合评估失败，患者ID: {}, 错误: ", patientId, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取查询条件选项
     * 
     * @return 查询条件选项
     */
    @GetMapping("/query-options")
    public Result<Map<String, Object>> getQueryOptions() {
        Map<String, Object> options = new HashMap<>();
        
        // 性别选项
        options.put("genderOptions", Arrays.asList("男", "女"));
        
        // CURB-65风险等级
        options.put("curbRiskLevels", Arrays.asList("低风险", "中风险", "高风险"));
        
        // COVID-19严重程度
        options.put("covid19SeverityLevels", Arrays.asList("非重型", "重型", "重型（中危）", "重型（高危）"));
        options.put("covid19CriticalSeverityLevels", Arrays.asList("非危重型", "危重型", "危重型（中危）", "危重型（高危）"));
        
        // CPIS风险等级
        options.put("cpisRiskLevels", Arrays.asList("低风险", "高风险"));
        
        // PSI风险等级
        options.put("psiRiskClasses", Arrays.asList("I级", "II级", "III级", "IV级", "V级"));
        
        // qSOFA风险等级
        options.put("qsofaRiskLevels", Arrays.asList("低风险", "高风险"));
        
        // SOFA严重程度
        options.put("sofaSeverityLevels", Arrays.asList("轻度", "中度", "重度", "极重度"));
        
        // 呼吸道症候群严重程度
        options.put("respiratorySyndromeSeverityLevels", Arrays.asList("MILD", "MODERATE", "SEVERE", "CRITICAL"));
        
        // 分数范围建议
        Map<String, Map<String, Integer>> scoreRanges = new HashMap<>();
        scoreRanges.put("curb", Map.of("min", 0, "max", 5));
        scoreRanges.put("cpis", Map.of("min", 0, "max", 5));  // 简化版CPIS
        scoreRanges.put("psi", Map.of("min", 0, "max", 400));
        scoreRanges.put("qsofa", Map.of("min", 0, "max", 3));
        scoreRanges.put("sofa", Map.of("min", 0, "max", 24));
        scoreRanges.put("respiratorySyndrome", Map.of("min", 0, "max", 18));  // 呼吸道症候群评分
        options.put("scoreRanges", scoreRanges);
        
        return Result.success(options);
    }
    
    /**
     * 验证分数范围
     * 
     * @param queryDTO 查询条件
     * @return 是否有效
     */
    private boolean validateScoreRanges(ComprehensiveAssessmentQueryDTO queryDTO) {
        // CURB-65分数范围
        if (queryDTO.getMinCurbScore() != null && queryDTO.getMaxCurbScore() != null 
                && queryDTO.getMinCurbScore() > queryDTO.getMaxCurbScore()) {
            return false;
        }
        
        // CPIS分数范围
        if (queryDTO.getMinCpisScore() != null && queryDTO.getMaxCpisScore() != null 
                && queryDTO.getMinCpisScore() > queryDTO.getMaxCpisScore()) {
            return false;
        }
        
        // PSI分数范围
        if (queryDTO.getMinPsiScore() != null && queryDTO.getMaxPsiScore() != null 
                && queryDTO.getMinPsiScore() > queryDTO.getMaxPsiScore()) {
            return false;
        }
        
        // qSOFA分数范围
        if (queryDTO.getMinQsofaScore() != null && queryDTO.getMaxQsofaScore() != null 
                && queryDTO.getMinQsofaScore() > queryDTO.getMaxQsofaScore()) {
            return false;
        }
        
        // SOFA分数范围
        if (queryDTO.getMinSofaScore() != null && queryDTO.getMaxSofaScore() != null 
                && queryDTO.getMinSofaScore() > queryDTO.getMaxSofaScore()) {
            return false;
        }
        
        // 呼吸道症候群分数范围
        if (queryDTO.getMinRespiratorySyndromeScore() != null && queryDTO.getMaxRespiratorySyndromeScore() != null 
                && queryDTO.getMinRespiratorySyndromeScore() > queryDTO.getMaxRespiratorySyndromeScore()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 清理无效参数
     * 将前端发送的默认值转换为null，避免参与查询
     * 
     * @param queryDTO 查询条件
     */
    private void cleanInvalidParameters(ComprehensiveAssessmentQueryDTO queryDTO) {
        // 清理字符串类型的默认值
        if ("string".equals(queryDTO.getPatientNumber()) || "".equals(queryDTO.getPatientNumber())) {
            queryDTO.setPatientNumber(null);
        }
        if ("string".equals(queryDTO.getGender()) || "".equals(queryDTO.getGender())) {
            queryDTO.setGender(null);
        }
        if ("string".equals(queryDTO.getCurbRiskLevel()) || "".equals(queryDTO.getCurbRiskLevel())) {
            queryDTO.setCurbRiskLevel(null);
        }
        if ("string".equals(queryDTO.getCovid19SeverityLevel()) || "".equals(queryDTO.getCovid19SeverityLevel())) {
            queryDTO.setCovid19SeverityLevel(null);
        }
        if ("string".equals(queryDTO.getCovid19CriticalSeverityLevel()) || "".equals(queryDTO.getCovid19CriticalSeverityLevel())) {
            queryDTO.setCovid19CriticalSeverityLevel(null);
        }
        if ("string".equals(queryDTO.getCpisRiskLevel()) || "".equals(queryDTO.getCpisRiskLevel())) {
            queryDTO.setCpisRiskLevel(null);
        }
        if ("string".equals(queryDTO.getPsiRiskClass()) || "".equals(queryDTO.getPsiRiskClass())) {
            queryDTO.setPsiRiskClass(null);
        }
        if ("string".equals(queryDTO.getQsofaRiskLevel()) || "".equals(queryDTO.getQsofaRiskLevel())) {
            queryDTO.setQsofaRiskLevel(null);
        }
        if ("string".equals(queryDTO.getSofaSeverityLevel()) || "".equals(queryDTO.getSofaSeverityLevel())) {
            queryDTO.setSofaSeverityLevel(null);
        }
        if ("string".equals(queryDTO.getRespiratorySyndromeSeverityLevel()) || "".equals(queryDTO.getRespiratorySyndromeSeverityLevel())) {
            queryDTO.setRespiratorySyndromeSeverityLevel(null);
        }
        
        // 清理数字类型的默认值（0值）
        // 总分如果是0，可能是有效值，但如果同时设置了0并且没有其他条件，应该清除
        if (queryDTO.getCurbTotalScore() != null && queryDTO.getCurbTotalScore() == 0 
            && queryDTO.getMinCurbScore() == null && queryDTO.getMaxCurbScore() == null) {
            queryDTO.setCurbTotalScore(null);
        }
        if (queryDTO.getCpisTotalScore() != null && queryDTO.getCpisTotalScore() == 0
            && queryDTO.getMinCpisScore() == null && queryDTO.getMaxCpisScore() == null) {
            queryDTO.setCpisTotalScore(null);
        }
        if (queryDTO.getPsiTotalScore() != null && queryDTO.getPsiTotalScore() == 0
            && queryDTO.getMinPsiScore() == null && queryDTO.getMaxPsiScore() == null) {
            queryDTO.setPsiTotalScore(null);
        }
        if (queryDTO.getQsofaTotalScore() != null && queryDTO.getQsofaTotalScore() == 0
            && queryDTO.getMinQsofaScore() == null && queryDTO.getMaxQsofaScore() == null) {
            queryDTO.setQsofaTotalScore(null);
        }
        if (queryDTO.getSofaTotalScore() != null && queryDTO.getSofaTotalScore() == 0
            && queryDTO.getMinSofaScore() == null && queryDTO.getMaxSofaScore() == null) {
            queryDTO.setSofaTotalScore(null);
        }
        
        // 清理范围查询的默认值
        if (queryDTO.getMinAge() != null && queryDTO.getMinAge() == 0) {
            queryDTO.setMinAge(null);
        }
        if (queryDTO.getMaxAge() != null && queryDTO.getMaxAge() == 0) {
            queryDTO.setMaxAge(null);
        }
        
        // 清理分数范围的0值
        if (queryDTO.getMinCurbScore() != null && queryDTO.getMinCurbScore() == 0 && queryDTO.getMaxCurbScore() != null && queryDTO.getMaxCurbScore() == 0) {
            queryDTO.setMinCurbScore(null);
            queryDTO.setMaxCurbScore(null);
        }
        if (queryDTO.getMinCpisScore() != null && queryDTO.getMinCpisScore() == 0 && queryDTO.getMaxCpisScore() != null && queryDTO.getMaxCpisScore() == 0) {
            queryDTO.setMinCpisScore(null);
            queryDTO.setMaxCpisScore(null);
        }
        if (queryDTO.getMinPsiScore() != null && queryDTO.getMinPsiScore() == 0 && queryDTO.getMaxPsiScore() != null && queryDTO.getMaxPsiScore() == 0) {
            queryDTO.setMinPsiScore(null);
            queryDTO.setMaxPsiScore(null);
        }
        if (queryDTO.getMinQsofaScore() != null && queryDTO.getMinQsofaScore() == 0 && queryDTO.getMaxQsofaScore() != null && queryDTO.getMaxQsofaScore() == 0) {
            queryDTO.setMinQsofaScore(null);
            queryDTO.setMaxQsofaScore(null);
        }
        if (queryDTO.getMinSofaScore() != null && queryDTO.getMinSofaScore() == 0 && queryDTO.getMaxSofaScore() != null && queryDTO.getMaxSofaScore() == 0) {
            queryDTO.setMinSofaScore(null);
            queryDTO.setMaxSofaScore(null);
        }
        
        log.info("清理后的查询条件: {}", queryDTO);
    }
    
    /**
     * 按年龄分段统计综合评估数据
     * 统计各年龄段患者的评估结果分布情况
     * 
     * @return 年龄分段统计结果
     */
    @GetMapping("/age-segment-statistics")
    public Result<List<AgeSegmentStatisticsDTO>> getAgeSegmentStatistics() {
        log.info("获取年龄分段统计数据");
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 调用Service层获取统计数据
            List<AgeSegmentStatisticsDTO> statistics = comprehensiveAssessmentService.getAgeSegmentStatistics();
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 记录统计信息
            if (statistics != null && !statistics.isEmpty()) {
                int totalPatients = statistics.stream()
                    .mapToInt(AgeSegmentStatisticsDTO::getTotalPatients)
                    .sum();
                
                log.info("年龄分段统计完成，总耗时：{}ms，统计患者总数：{}，分段数：{}", 
                    executionTime, totalPatients, statistics.size());
                
                // 如果执行时间过长，记录警告
                if (executionTime > 5000) {
                    log.warn("年龄分段统计执行时间超过5秒：{}ms", executionTime);
                }
                
                return Result.success(statistics);
            } else {
                log.warn("年龄分段统计结果为空");
                return Result.error("未找到统计数据");
            }
            
        } catch (Exception e) {
            log.error("年龄分段统计失败", e);
            return Result.error("统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 按性别分类统计综合评估数据
     * 统计男性和女性患者的评估结果分布情况
     * 
     * @return 性别分类统计结果
     */
    @GetMapping("/gender-statistics")
    public Result<List<GenderStatisticsDTO>> getGenderStatistics() {
        log.info("获取性别分类统计数据");
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 调用Service层获取统计数据
            List<GenderStatisticsDTO> statistics = comprehensiveAssessmentService.getGenderStatistics();
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 记录统计信息
            if (statistics != null && !statistics.isEmpty()) {
                int totalMale = 0;
                int totalFemale = 0;
                
                for (GenderStatisticsDTO stat : statistics) {
                    if ("男".equals(stat.getGender())) {
                        totalMale = stat.getTotalPatients();
                    } else if ("女".equals(stat.getGender())) {
                        totalFemale = stat.getTotalPatients();
                    }
                }
                
                int totalPatients = totalMale + totalFemale;
                
                log.info("性别分类统计完成，总耗时：{}ms，男性：{}人，女性：{}人，总计：{}人", 
                    executionTime, totalMale, totalFemale, totalPatients);
                
                // 如果执行时间过长，记录警告
                if (executionTime > 5000) {
                    log.warn("性别分类统计执行时间超过5秒：{}ms", executionTime);
                }
                
                return Result.success(statistics);
            } else {
                log.warn("性别分类统计结果为空");
                return Result.error("未找到统计数据");
            }
            
        } catch (Exception e) {
            log.error("性别分类统计失败", e);
            return Result.error("统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 按月份统计综合评估数据
     * 基于患者入院日期(admission_date)统计每月各种疾病的患者分布
     * 
     * @return 月度统计结果
     */
    @GetMapping("/monthly-statistics")
    public Result<List<MonthlyStatisticsDTO>> getMonthlyStatistics() {
        log.info("获取月度统计数据");
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 调用Service层获取统计数据
            List<MonthlyStatisticsDTO> statistics = comprehensiveAssessmentService.getMonthlyStatistics();
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 记录统计信息
            if (statistics != null && !statistics.isEmpty()) {
                int totalPatients = statistics.stream()
                    .mapToInt(MonthlyStatisticsDTO::getTotalPatients)
                    .sum();
                
                // 找出患者数最多的月份
                MonthlyStatisticsDTO peakMonth = statistics.stream()
                    .max((a, b) -> Integer.compare(a.getTotalPatients(), b.getTotalPatients()))
                    .orElse(null);
                
                log.info("月度统计完成，总耗时：{}ms，统计月份数：{}，总患者数：{}，高峰月份：{} ({}人)", 
                    executionTime, statistics.size(), totalPatients,
                    peakMonth != null ? peakMonth.getMonth() : "N/A",
                    peakMonth != null ? peakMonth.getTotalPatients() : 0);
                
                // 如果执行时间过长，记录警告
                if (executionTime > 5000) {
                    log.warn("月度统计执行时间超过5秒：{}ms", executionTime);
                }
                
                return Result.success(statistics);
            } else {
                log.warn("月度统计结果为空");
                return Result.error("未找到统计数据");
            }
            
        } catch (Exception e) {
            log.error("月度统计失败", e);
            return Result.error("统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 优化版综合评估分页查询
     * 使用数据库视图和查询优化，确保响应时间控制在2秒内
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @PostMapping("/page-optimized")
    public Result<IPage<ComprehensiveAssessmentVO>> queryComprehensivePageOptimized(@RequestBody @Validated ComprehensiveAssessmentQueryDTO queryDTO) {
        long startTime = System.currentTimeMillis();
        log.info("收到优化版综合评估分页查询请求: {}", queryDTO);
        
        try {
            // 参数校验和设置默认值
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(10);
            }
            // 限制每页最大50条记录以保证性能
            if (queryDTO.getPageSize() > 50) {
                log.warn("请求的每页记录数过大: {}, 限制为50", queryDTO.getPageSize());
                queryDTO.setPageSize(50);
            }
            
            // 验证年龄范围
            if (queryDTO.getMinAge() != null && queryDTO.getMaxAge() != null) {
                if (queryDTO.getMinAge() > queryDTO.getMaxAge()) {
                    return Result.error("最小年龄不能大于最大年龄");
                }
            }
            
            // 执行优化查询
            IPage<ComprehensiveAssessmentVO> result = comprehensiveAssessmentService.queryComprehensivePageOptimized(queryDTO);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 构建响应元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime + "ms");
            metadata.put("totalRecords", result.getTotal());
            metadata.put("currentPageRecords", result.getRecords().size());
            metadata.put("optimized", true);
            
            // 性能监控
            if (executionTime > 2000) {
                log.warn("优化版查询响应时间超过2秒: {}ms，请检查数据库索引和视图", executionTime);
                metadata.put("warning", "响应时间超过2秒，建议进一步优化");
            } else if (executionTime < 500) {
                metadata.put("performance", "excellent");
            } else if (executionTime < 1000) {
                metadata.put("performance", "good");
            } else {
                metadata.put("performance", "acceptable");
            }
            
            log.info("优化版查询成功，性能指标: {}", metadata);
            
            // 可以将metadata作为扩展字段返回
            return Result.success(result);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("优化版查询失败，执行时间: {}ms, 错误: ", endTime - startTime, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 带缓存的综合评估分页查询
     * 对于相同的查询条件，使用缓存提升响应速度
     * 
     * @param queryDTO 查询条件
     * @param useCache 是否使用缓存（默认true）
     * @return 分页结果
     */
    @PostMapping("/page-cached")
    public Result<IPage<ComprehensiveAssessmentVO>> queryComprehensivePageCached(
            @RequestBody @Validated ComprehensiveAssessmentQueryDTO queryDTO,
            @RequestParam(value = "useCache", defaultValue = "true") boolean useCache) {
        
        long startTime = System.currentTimeMillis();
        log.info("收到缓存版综合评估查询请求，使用缓存: {}", useCache);
        
        try {
            // 参数校验
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(10);
            }
            if (queryDTO.getPageSize() > 50) {
                queryDTO.setPageSize(50);
            }
            
            // 执行查询（带缓存）
            IPage<ComprehensiveAssessmentVO> result = comprehensiveAssessmentService
                .queryComprehensivePageWithCache(queryDTO, useCache);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.info("缓存版查询成功，耗时: {}ms, 使用缓存: {}", executionTime, useCache);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("缓存版查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取疾病分类统计
     * 统计各种疾病的患者数量分布
     * 
     * @return 疾病统计结果
     */
    @GetMapping("/disease-statistics")
    public Result<DiseaseStatisticsDTO> getDiseaseStatistics() {
        try {
            log.info("开始获取疾病分类统计");
            long startTime = System.currentTimeMillis();
            
            DiseaseStatisticsDTO statistics = comprehensiveAssessmentService.getDiseaseStatistics();
            
            long endTime = System.currentTimeMillis();
            log.info("疾病分类统计完成，耗时: {}ms", (endTime - startTime));
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取疾病分类统计失败", e);
            return Result.error("获取疾病分类统计失败: " + e.getMessage());
        }
    }
}
