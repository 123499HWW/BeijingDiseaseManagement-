package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.MultiAssessmentQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.vo.MultiAssessmentVO;
import com.hxj.service.MultiAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 多评估表联合查询控制器
 * 提供患者信息和多个评估结果的联合查询接口
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Slf4j
@RestController
@RequestMapping("/api/multi-assessment")
@RequiredArgsConstructor
public class MultiAssessmentController {
    
    private final MultiAssessmentService multiAssessmentService;
    
    /**
     * 分页查询多评估表联合数据
     * 联查patient_info、curb_assessment_result、psi_assessment_result、
     * cpis_assessment_result和severe_pneumonia_diagnosis表
     * 
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<MultiAssessmentVO>> queryMultiAssessmentPage(@RequestBody @Validated MultiAssessmentQueryDTO queryDTO) {
        try {
            log.info("接收到多评估表联合查询请求：{}", queryDTO);
            
            IPage<MultiAssessmentVO> page = multiAssessmentService.queryMultiAssessmentPage(queryDTO);
            
            return Result.success(page);
        } catch (Exception e) {
            log.error("多评估表联合查询失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据患者ID查询评估详情
     * 
     * @param patientId 患者ID
     * @return 评估详情
     */
    @GetMapping("/detail/{patientId}")
    public Result<MultiAssessmentVO> getAssessmentDetail(@PathVariable Long patientId) {
        try {
            log.info("查询患者{}的评估详情", patientId);
            
            MultiAssessmentVO detail = multiAssessmentService.getAssessmentDetail(patientId);
            
            if (detail == null) {
                return Result.error("未找到患者评估信息");
            }
            
            return Result.success(detail);
        } catch (Exception e) {
            log.error("查询患者评估详情失败，patientId: {}", patientId, e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取多评估表查询统计
     * 统计各评估表的完成情况
     * 
     * @return 统计结果
     */
    @GetMapping("/statistics")
    public Result<?> getMultiAssessmentStatistics() {
        try {
            log.info("开始获取多评估表统计");
            
            // TODO: 实现统计功能
            // 可以统计：
            // 1. 各评估表的完成率
            // 2. 高风险患者数量
            // 3. 重症肺炎患者数量等
            
            return Result.success("统计功能待实现");
        } catch (Exception e) {
            log.error("获取多评估表统计失败", e);
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }
}
