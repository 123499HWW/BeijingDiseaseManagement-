package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.ComprehensiveAssessmentQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.vo.ComprehensiveAssessmentVO;
import com.hxj.service.ComprehensiveAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
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
        
        // 分数范围建议
        Map<String, Map<String, Integer>> scoreRanges = new HashMap<>();
        scoreRanges.put("curb", Map.of("min", 0, "max", 5));
        scoreRanges.put("cpis", Map.of("min", 0, "max", 5));  // 简化版CPIS
        scoreRanges.put("psi", Map.of("min", 0, "max", 400));
        scoreRanges.put("qsofa", Map.of("min", 0, "max", 3));
        scoreRanges.put("sofa", Map.of("min", 0, "max", 24));
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
}
