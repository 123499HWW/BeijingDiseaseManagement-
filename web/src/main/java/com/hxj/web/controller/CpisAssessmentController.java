package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.CpisAssessmentQueryDTO;
import com.hxj.common.entity.CpisAssessmentResult;
import com.hxj.common.result.Result;
import com.hxj.common.vo.CpisAssessmentPageVO;
import com.hxj.service.CpisAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * CPIS评分控制器
 * Clinical Pulmonary Infection Score (临床肺部感染评分)
 */
@Slf4j
@RestController
@RequestMapping("/api/cpis")
@RequiredArgsConstructor
public class CpisAssessmentController {

    private final CpisAssessmentService cpisAssessmentService;

    /**
     * 为单个患者执行CPIS评分
     * 
     * @param patientId 患者ID
     * @return CPIS评分结果
     */
    @PostMapping("/assess/{patientId}")
    public Result<CpisAssessmentResult> assessSinglePatient(@PathVariable Long patientId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            log.info("用户{}请求为患者{}执行CPIS评分", operatorUsername, patientId);
            
            CpisAssessmentResult result = cpisAssessmentService.assessSinglePatient(patientId, operatorUsername);
            
            return Result.success("CPIS评分成功", result);
        } catch (Exception e) {
            log.error("CPIS评分失败，患者ID: {}", patientId, e);
            return Result.error("CPIS评分失败: " + e.getMessage());
        }
    }

    /**
     * 为所有患者执行CPIS评分（批量评分）
     * 
     * @return 评分统计结果
     */
    @PostMapping("/assess/all")
    public Result<CpisAssessmentService.AssessmentResult> assessAllPatients() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            log.info("用户{}请求为所有患者执行CPIS评分", operatorUsername);
            
            CpisAssessmentService.AssessmentResult result = cpisAssessmentService.assessAllPatients(operatorUsername);
            
            String message = String.format("CPIS评分完成，总数: %d, 成功: %d, 失败: %d, 跳过: %d",
                    result.getTotalCount(), result.getSuccessCount(), 
                    result.getFailureCount(), result.getSkipCount());
            
            return Result.success(message, result);
        } catch (Exception e) {
            log.error("批量CPIS评分失败", e);
            return Result.error("批量CPIS评分失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询CPIS评分结果
     * 支持根据总分、风险等级、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<CpisAssessmentPageVO>> queryCpisAssessmentPage(@RequestBody CpisAssessmentQueryDTO queryDTO) {
        
        log.info("分页查询CPIS评分结果，查询条件: {}", queryDTO);
        
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
            
            if (queryDTO.getRiskLevel() != null && !queryDTO.getRiskLevel().isEmpty()) {
                if (!"低风险".equals(queryDTO.getRiskLevel()) && !"高风险".equals(queryDTO.getRiskLevel())) {
                    return Result.error("风险等级必须是：低风险或高风险");
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
            IPage<CpisAssessmentPageVO> result = cpisAssessmentService.queryCpisAssessmentPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询CPIS评分结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
