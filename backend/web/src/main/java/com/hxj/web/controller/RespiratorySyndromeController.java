package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.RespiratorySyndromeQueryDTO;
import com.hxj.common.entity.RespiratorySyndromeAssessment;
import com.hxj.common.result.Result;
import com.hxj.common.vo.RespiratorySyndromePageVO;
import com.hxj.service.RespiratorySyndromeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 呼吸道症候群评估控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/syndrome")
@RequiredArgsConstructor
public class RespiratorySyndromeController {

    private final RespiratorySyndromeService respiratorySyndromeService;

    /**
     * 为所有患者执行呼吸道症候群评估
     */
    @PostMapping("/assess-all")
    public Result<?> assessAllPatients(@RequestParam(defaultValue = "SYSTEM") String createdBy) {
        try {
            log.info("接收到批量呼吸道症候群评估请求，操作人: {}", createdBy);
            RespiratorySyndromeService.AssessmentResult result = respiratorySyndromeService.assessAllPatients(createdBy);
            return Result.success("批量评估成功", result.toString());
        } catch (Exception e) {
            log.error("批量呼吸道症候群评估失败", e);
            return Result.error("评估失败: " + e.getMessage());
        }
    }

    /**
     * 为单个患者执行呼吸道症候群评估
     */
    @PostMapping("/assess/{patientId}")
    public Result<RespiratorySyndromeAssessment> assessSinglePatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "SYSTEM") String createdBy) {
        try {
            log.info("接收到患者{}的呼吸道症候群评估请求，操作人: {}", patientId, createdBy);
            RespiratorySyndromeAssessment assessment = respiratorySyndromeService.assessSinglePatient(patientId, createdBy);
            return Result.success("评估成功", assessment);
        } catch (Exception e) {
            log.error("患者{}的呼吸道症候群评估失败", patientId, e);
            return Result.error("评估失败: " + e.getMessage());
        }
    }

    /**
     * 获取患者的呼吸道症候群评估结果
     */
    @GetMapping("/result/{patientId}")
    public Result<RespiratorySyndromeAssessment> getAssessmentResult(@PathVariable Long patientId) {
        try {
            log.info("获取患者{}的呼吸道症候群评估结果", patientId);
            RespiratorySyndromeAssessment assessment = respiratorySyndromeService.getPatientAssessment(patientId);
            if (assessment != null) {
                return Result.success("查询成功", assessment);
            } else {
                return Result.error("未找到患者的评估结果");
            }
        } catch (Exception e) {
            log.error("获取患者{}的呼吸道症候群评估结果失败", patientId, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询呼吸道症候群评估结果
     */
    @PostMapping("/page")
    public Result<IPage<RespiratorySyndromePageVO>> queryRespiratorySyndromePage(@RequestBody RespiratorySyndromeQueryDTO query) {
        try {
            log.info("接收到呼吸道症候群评估分页查询请求，查询条件: {}", query);
            
            // 执行分页查询
            IPage<RespiratorySyndromePageVO> result = respiratorySyndromeService.queryRespiratorySyndromePage(query);
            
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("呼吸道症候群评估分页查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
