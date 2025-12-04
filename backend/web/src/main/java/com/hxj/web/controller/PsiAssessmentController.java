package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.PsiAssessmentQueryDTO;
import com.hxj.common.entity.PsiAssessmentResult;
import com.hxj.common.result.Result;
import com.hxj.common.vo.PsiAssessmentPageVO;
import com.hxj.service.PsiAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * PSI评分控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/psi")
@RequiredArgsConstructor
public class PsiAssessmentController {

    private final PsiAssessmentService psiAssessmentService;

    /**
     * 为所有患者执行PSI评分
     */
    @PostMapping("/assess-all")
    public Result<PsiAssessmentService.AssessmentResult> assessAllPatients(
            @RequestParam(defaultValue = "SYSTEM") String createdBy,
            Authentication authentication) {
        createdBy = authentication.getName();

        log.info("开始为所有患者执行PSI评分，操作人: {}", createdBy);
        
        try {
            PsiAssessmentService.AssessmentResult result = 
                psiAssessmentService.assessAllPatients(createdBy);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("PSI评分失败", e);
            return Result.error("评分失败: " + e.getMessage());
        }
    }

    /**
     * 为单个患者执行PSI评分
     */
    @PostMapping("/assess/{patientId}")
    public Result<PsiAssessmentResult> assessSinglePatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "SYSTEM") String createdBy,
            Authentication authentication) {
        
        createdBy = authentication.getName();
        log.info("开始为患者{}执行PSI评分，操作人: {}", patientId, createdBy);
        
        try {
            PsiAssessmentResult result = psiAssessmentService.assessSinglePatient(patientId, createdBy);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("患者{}的PSI评分失败", patientId, e);
            return Result.error("评分失败: " + e.getMessage());
        }
    }

    /**
     * 获取患者的PSI评分结果
     */
    @GetMapping("/result/{patientId}")
    public Result<PsiAssessmentResult> getAssessmentResult(@PathVariable Long patientId) {
        try {
            // 这里需要注入PsiAssessmentResultMapper来查询
            // 为了简化，暂时返回提示
            return Result.error("请使用评分接口获取结果");
            
        } catch (Exception e) {
            log.error("获取患者{}的PSI评分结果失败", patientId, e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询PSI评分结果
     * 支持根据总分、风险等级、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<PsiAssessmentPageVO>> queryPsiAssessmentPage(@RequestBody PsiAssessmentQueryDTO queryDTO) {
        
        log.info("分页查询PSI评分结果，查询条件: {}", queryDTO);
        
        try {
            // 设置默认值
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(10);
            }
            
            // 参数校验
            if (queryDTO.getTotalScore() != null && queryDTO.getTotalScore() < 0) {
                return Result.error("总分不能小于0");
            }
            
            if (queryDTO.getMinTotalScore() != null && queryDTO.getMinTotalScore() < 0) {
                return Result.error("最小总分不能小于0");
            }
            
            if (queryDTO.getMaxTotalScore() != null && queryDTO.getMaxTotalScore() < 0) {
                return Result.error("最大总分不能小于0");
            }
            
            if (queryDTO.getMinTotalScore() != null && queryDTO.getMaxTotalScore() != null &&
                queryDTO.getMinTotalScore() > queryDTO.getMaxTotalScore()) {
                return Result.error("最小总分不能大于最大总分");
            }
            
            if (queryDTO.getRiskClass() != null && !queryDTO.getRiskClass().isEmpty()) {
                if (!queryDTO.getRiskClass().matches("[I-V]{1,3}")) {
                    return Result.error("风险等级必须是I、II、III、IV或V");
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
            IPage<PsiAssessmentPageVO> result = psiAssessmentService.queryPsiAssessmentPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询PSI评分结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
