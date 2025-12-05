package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.SofaAssessmentQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.entity.SofaAssessment;
import com.hxj.common.vo.SofaAssessmentPageVO;
import com.hxj.service.SofaAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SOFA评分控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sofa")
public class SofaAssessmentController {

    @Autowired
    private SofaAssessmentService assessmentService;

    /**
     * 对单个患者进行SOFA评分
     * @param patientId 患者ID
     * @return 评分结果
     */
    @PostMapping("/assess/{patientId}")
    public Result<SofaAssessment> assessSinglePatient(@PathVariable Long patientId) {
        try {
            log.info("开始对患者{}进行SOFA评分", patientId);
            SofaAssessment assessment = assessmentService.assessSinglePatient(patientId);
            log.info("患者{}SOFA评分完成", patientId);
            return Result.success(assessment);
        } catch (Exception e) {
            log.error("患者{}SOFA评分失败", patientId, e);
            return Result.error("评分失败: " + e.getMessage());
        }
    }

    /**
     * 对所有患者进行SOFA评分
     * @return 批量评分结果
     */
    @PostMapping("/assess/all")
    public Result<BatchAssessmentResult> assessAllPatients() {
        try {
            log.info("开始批量SOFA评分");
            BatchAssessmentResult result = assessmentService.assessAllPatients();
            log.info("批量SOFA评分完成");
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量SOFA评分失败", e);
            return Result.error("批量评分失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询SOFA评分结果
     * 支持根据总分、严重程度、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<SofaAssessmentPageVO>> querySofaPage(@RequestBody SofaAssessmentQueryDTO queryDTO) {
        
        log.info("分页查询SOFA评分结果，查询条件: {}", queryDTO);
        
        try {
            // 设置默认值
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(10);
            }
            
            // 参数校验
            if (queryDTO.getTotalScore() != null && (queryDTO.getTotalScore() < 0 || queryDTO.getTotalScore() > 24)) {
                return Result.error("总分必须在0-24之间");
            }
            
            if (queryDTO.getMinTotalScore() != null && (queryDTO.getMinTotalScore() < 0 || queryDTO.getMinTotalScore() > 24)) {
                return Result.error("最小总分必须在0-24之间");
            }
            
            if (queryDTO.getMaxTotalScore() != null && (queryDTO.getMaxTotalScore() < 0 || queryDTO.getMaxTotalScore() > 24)) {
                return Result.error("最大总分必须在0-24之间");
            }
            
            if (queryDTO.getMinTotalScore() != null && queryDTO.getMaxTotalScore() != null &&
                queryDTO.getMinTotalScore() > queryDTO.getMaxTotalScore()) {
                return Result.error("最小总分不能大于最大总分");
            }
            
            if (queryDTO.getSeverityLevel() != null && !queryDTO.getSeverityLevel().isEmpty()) {
                String[] validLevels = {"正常或轻微", "轻度", "中度", "重度", "极重度"};
                boolean isValid = false;
                for (String level : validLevels) {
                    if (level.equals(queryDTO.getSeverityLevel())) {
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    return Result.error("严重程度必须是：正常或轻微、轻度、中度、重度或极重度");
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
            IPage<SofaAssessmentPageVO> result = assessmentService.querySofaPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询SOFA评分结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * @return 状态信息
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("SOFA评分服务运行正常");
    }
}
