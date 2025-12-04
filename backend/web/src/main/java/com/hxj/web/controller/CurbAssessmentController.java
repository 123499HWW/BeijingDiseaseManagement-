package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.CurbAssessmentQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.entity.CurbAssessmentResult;
import com.hxj.common.vo.CurbAssessmentPageVO;
import com.hxj.service.CurbAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * CURB-65评分评估控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/assessment/curb")
@RequiredArgsConstructor
public class CurbAssessmentController {

    private final CurbAssessmentService curbAssessmentService;

    /**
     * 为所有患者执行CURB-65评分评估
     */
    @PostMapping("/assess-all")
    public Result<CurbAssessmentService.AssessmentResult> assessAllPatients(
            Authentication authentication) {
        String createdBy = authentication.getName();

        log.info("开始为所有患者执行CURB-65评分评估，操作人: {}", createdBy);
        
        try {
            CurbAssessmentService.AssessmentResult result = 
                curbAssessmentService.assessAllPatients(createdBy);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("CURB-65评分评估失败", e);
            return Result.error("评估失败: " + e.getMessage());
        }
    }

    /**
     * 为单个患者执行CURB-65评分评估
     */
    @PostMapping("/assess/{patientId}")
    public Result<CurbAssessmentResult> assessSinglePatient(
            @PathVariable Long patientId,
            Authentication authentication) {

        String createdBy = authentication.getName();

        log.info("开始为患者{}执行CURB-65评分评估，操作人: {}", patientId, createdBy);
        
        try {
            // 首先需要获取患者信息
            // 这里需要注入PatientMapper来查询患者信息
            // 为了简化，暂时返回错误提示
            return Result.error("请使用批量评估接口");
            
        } catch (Exception e) {
            log.error("患者{}的CURB-65评分评估失败", patientId, e);
            return Result.error("评估失败: " + e.getMessage());
        }
    }

    /**
     * 查询患者的CURB-65评分结果
     */
    @GetMapping("/result/{patientId}")
    public Result<CurbAssessmentResult> getCurbResult(@PathVariable Long patientId) {
        
        log.info("查询患者{}的CURB-65评分结果", patientId);
        
        try {
            // 这里需要实现查询逻辑
            // 暂时返回提示信息
            return Result.error("功能开发中");
            
        } catch (Exception e) {
            log.error("查询患者{}的CURB-65评分结果失败", patientId, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取CURB-65评分统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getCurbStatistics() {
        
        log.info("获取CURB-65评分统计信息");
        
        try {
            // 这里需要实现统计查询逻辑
            // 暂时返回提示信息
            return Result.error("功能开发中");
            
        } catch (Exception e) {
            log.error("获取CURB-65评分统计信息失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询CURB-65评分结果
     * 支持根据总分、风险等级、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<CurbAssessmentPageVO>> queryCurbAssessmentPage(@RequestBody CurbAssessmentQueryDTO queryDTO) {
        
        log.info("分页查询CURB评分结果，查询条件: {}", queryDTO);
        
        try {
            // 设置默认值
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
                queryDTO.getPageNum() ;
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(10);
            }
            
            // 参数校验
            if (queryDTO.getTotalScore() != null && (queryDTO.getTotalScore() < 0 || queryDTO.getTotalScore() > 5)) {
                return Result.error("总分必须在0-5之间");
            }
            
            if (queryDTO.getRiskLevel() != null && !queryDTO.getRiskLevel().isEmpty()) {
                if (!"低风险".equals(queryDTO.getRiskLevel()) &&
                    !"中风险".equals(queryDTO.getRiskLevel()) &&
                    !"高风险".equals(queryDTO.getRiskLevel())) {
                    return Result.error("风险等级必须是：低风险、中风险或高风险");
                }
            }
            
            if (queryDTO.getGender() != null && !queryDTO.getGender().isEmpty()) {
                if (!"男".equals(queryDTO.getGender()) && !"女".equals(queryDTO.getGender())) {
                    return Result.error("性别必须是：男或女");
                }
            }
            
            if (queryDTO.getMinAge() != null && queryDTO.getMinAge() < 0) {
                return Result.error("最小年龄不能小于0");
            }
            
            if (queryDTO.getMaxAge() != null && queryDTO.getMaxAge() < 0) {
                return Result.error("最大年龄不能小于0");
            }
            
            if (queryDTO.getMinAge() != null && queryDTO.getMaxAge() != null && 
                queryDTO.getMinAge() > queryDTO.getMaxAge()) {
                return Result.error("最小年龄不能大于最大年龄");
            }
            
            // 调用Service层执行查询
            IPage<CurbAssessmentPageVO> result = curbAssessmentService.queryCurbAssessmentPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询CURB评分结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
