package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.SepsisAssessmentQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.vo.SepsisAssessmentVO;
import com.hxj.service.SepsisAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 脓毒症评估联合查询控制器
 * 提供患者信息、qSOFA和SOFA评分的联合查询接口
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Slf4j
@RestController
@RequestMapping("/api/sepsis-assessment")
@RequiredArgsConstructor
public class SepsisAssessmentController {
    
    private final SepsisAssessmentService sepsisAssessmentService;
    
    /**
     * 分页查询脓毒症评估联合数据
     * 联查patient_info、qsofa_assessment和sofa_assessment表
     * 
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<SepsisAssessmentVO>> querySepsisAssessmentPage(@RequestBody @Validated SepsisAssessmentQueryDTO queryDTO) {
        try {
            log.info("接收到脓毒症评估联合查询请求：{}", queryDTO);
            
            IPage<SepsisAssessmentVO> page = sepsisAssessmentService.querySepsisAssessmentPage(queryDTO);
            
            return Result.success(page);
        } catch (Exception e) {
            log.error("脓毒症评估联合查询失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据患者ID查询脓毒症评估详情
     * 
     * @param patientId 患者ID
     * @return 评估详情
     */
    @GetMapping("/detail/{patientId}")
    public Result<SepsisAssessmentVO> getSepsisAssessmentDetail(@PathVariable Long patientId) {
        try {
            log.info("查询患者{}的脓毒症评估详情", patientId);
            
            SepsisAssessmentVO detail = sepsisAssessmentService.getSepsisAssessmentDetail(patientId);
            
            if (detail == null) {
                return Result.error("未找到患者评估信息");
            }
            
            return Result.success(detail);
        } catch (Exception e) {
            log.error("查询患者脓毒症评估详情失败，patientId: {}", patientId, e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询高风险脓毒症患者
     * qSOFA≥2分或SOFA≥2分的患者
     * 
     * @return 高风险患者列表
     */
    @GetMapping("/high-risk")
    public Result<List<SepsisAssessmentVO>> getHighRiskPatients() {
        try {
            log.info("查询高风险脓毒症患者");
            
            List<SepsisAssessmentVO> patients = sepsisAssessmentService.getHighRiskPatients();
            
            return Result.success(patients);
        } catch (Exception e) {
            log.error("查询高风险脓毒症患者失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取脓毒症评估统计
     * 统计qSOFA和SOFA评估完成情况、风险分布等
     * 
     * @return 统计结果
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getSepsisStatistics() {
        try {
            log.info("开始获取脓毒症评估统计");
            
            Map<String, Object> statistics = new HashMap<>();
            
            // 查询所有患者的评估情况
            SepsisAssessmentQueryDTO queryDTO = new SepsisAssessmentQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10000); // 获取所有数据用于统计
            
            IPage<SepsisAssessmentVO> page = sepsisAssessmentService.querySepsisAssessmentPage(queryDTO);
            List<SepsisAssessmentVO> allRecords = page.getRecords();
            
            // 统计指标
            int totalPatients = allRecords.size();
            int qsofaCompleted = 0;
            int sofaCompleted = 0;
            int bothCompleted = 0;
            int highRiskQsofa = 0;
            int highRiskSofa = 0;
            int criticalSofa = 0;
            int requiresIcu = 0;
            
            Map<String, Integer> riskDistribution = new HashMap<>();
            riskDistribution.put("极高风险", 0);
            riskDistribution.put("高风险", 0);
            riskDistribution.put("中风险", 0);
            riskDistribution.put("低风险", 0);
            riskDistribution.put("未评估", 0);
            
            for (SepsisAssessmentVO vo : allRecords) {
                // 统计完成情况
                if (vo.getQsofaTotalScore() != null) {
                    qsofaCompleted++;
                    if (vo.getQsofaTotalScore() >= 2) {
                        highRiskQsofa++;
                    }
                }
                
                if (vo.getSofaTotalScore() != null) {
                    sofaCompleted++;
                    if (vo.getSofaTotalScore() >= 2) {
                        highRiskSofa++;
                    }
                    if (vo.getSofaTotalScore() >= 10) {
                        criticalSofa++;
                    }
                }
                
                if (vo.getQsofaTotalScore() != null && vo.getSofaTotalScore() != null) {
                    bothCompleted++;
                }
                
                // 统计风险分布
                String risk = vo.getSepsisRiskLevel();
                riskDistribution.put(risk, riskDistribution.getOrDefault(risk, 0) + 1);
                
                // 统计ICU需求
                if (Boolean.TRUE.equals(vo.getRequiresIcu())) {
                    requiresIcu++;
                }
            }
            
            // 计算完成率
            double qsofaCompletionRate = totalPatients > 0 ? 
                    (double) qsofaCompleted / totalPatients * 100 : 0;
            double sofaCompletionRate = totalPatients > 0 ? 
                    (double) sofaCompleted / totalPatients * 100 : 0;
            double bothCompletionRate = totalPatients > 0 ? 
                    (double) bothCompleted / totalPatients * 100 : 0;
            
            // 组装统计结果
            statistics.put("totalPatients", totalPatients);
            statistics.put("qsofaCompleted", qsofaCompleted);
            statistics.put("sofaCompleted", sofaCompleted);
            statistics.put("bothCompleted", bothCompleted);
            statistics.put("qsofaCompletionRate", String.format("%.1f%%", qsofaCompletionRate));
            statistics.put("sofaCompletionRate", String.format("%.1f%%", sofaCompletionRate));
            statistics.put("bothCompletionRate", String.format("%.1f%%", bothCompletionRate));
            statistics.put("highRiskQsofa", highRiskQsofa);
            statistics.put("highRiskSofa", highRiskSofa);
            statistics.put("criticalSofa", criticalSofa);
            statistics.put("requiresIcu", requiresIcu);
            statistics.put("riskDistribution", riskDistribution);
            
            log.info("脓毒症评估统计完成：{}", statistics);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取脓毒症评估统计失败", e);
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }
}
