package com.hxj.web.controller;

import com.hxj.common.dto.message.PatientAssessmentMessageDTO;
import com.hxj.common.entity.Patient;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.result.Result;
import com.hxj.service.PatientBatchAssessmentService;
import com.hxj.service.message.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评估测试控制器
 * 提供手动触发评估的测试接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessment-test")
public class AssessmentTestController {

    private final MessageProducerService messageProducerService;
    private final PatientBatchAssessmentService batchAssessmentService;
    private final PatientMapper patientMapper;

    /**
     * 手动触发综合评估（通过消息队列）
     */
    @PostMapping("/trigger-comprehensive-queue")
    public Result<Map<String, Object>> triggerComprehensiveAssessmentViaQueue(
            @RequestBody List<Long> patientIds,
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam(defaultValue = "管理员") String userName) {
        
        if (patientIds == null || patientIds.isEmpty()) {
            return Result.error("患者ID列表不能为空");
        }
        
        log.info("手动触发综合评估（队列方式），患者数量: {}", patientIds.size());
        
        try {
            // 发送综合评估消息到队列
            messageProducerService.sendComprehensiveAssessmentMessage(patientIds, userId, userName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "综合评估任务已提交到队列");
            result.put("patientCount", patientIds.size());
            result.put("patientIds", patientIds);
            result.put("assessmentTypes", new String[]{
                "CURB-65", "PSI", "CPIS", "qSOFA", "SOFA", 
                "COVID-19重型", "COVID-19危重型", "重症肺炎"
            });
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("触发综合评估失败", e);
            return Result.error("触发评估失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发综合评估（同步执行）
     */
    @PostMapping("/trigger-comprehensive-sync")
    public Result<PatientBatchAssessmentService.BatchAssessmentResult> triggerComprehensiveAssessmentSync(
            @RequestBody List<Long> patientIds,
            @RequestParam(defaultValue = "false") boolean parallel) {
        
        if (patientIds == null || patientIds.isEmpty()) {
            return Result.error("患者ID列表不能为空");
        }
        
        log.info("手动触发综合评估（同步方式），患者数量: {}, 并行处理: {}", patientIds.size(), parallel);
        
        try {
            PatientBatchAssessmentService.BatchAssessmentResult result;
            
            if (parallel) {
                result = batchAssessmentService.performParallelBatchAssessment(patientIds);
            } else {
                result = batchAssessmentService.performBatchAssessment(patientIds);
            }
            
            log.info("综合评估完成: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("同步执行综合评估失败", e);
            return Result.error("评估执行失败: " + e.getMessage());
        }
    }

    /**
     * 触发所有患者的综合评估
     */
    @PostMapping("/trigger-all-patients")
    public Result<Map<String, Object>> triggerAssessmentForAllPatients(
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam(defaultValue = "管理员") String userName,
            @RequestParam(defaultValue = "100") int maxCount) {
        
        log.info("开始获取所有患者进行评估，最大数量限制: {}", maxCount);
        
        try {
            // 查询所有有效患者
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Patient> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            queryWrapper.eq(Patient::getIsDeleted, 0)
                       .last("LIMIT " + maxCount);
            List<Patient> patients = patientMapper.selectList(queryWrapper);
            
            if (patients.isEmpty()) {
                return Result.error("没有找到患者数据");
            }
            
            List<Long> patientIds = patients.stream()
                    .map(Patient::getPatientId)
                    .collect(Collectors.toList());
            
            log.info("找到{}个患者，开始发送评估任务", patientIds.size());
            
            // 发送综合评估消息到队列
            messageProducerService.sendComprehensiveAssessmentMessage(patientIds, userId, userName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "评估任务已提交");
            result.put("totalPatients", patientIds.size());
            result.put("maxProcessed", maxCount);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("触发所有患者评估失败", e);
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取评估队列状态
     */
    @GetMapping("/queue-status")
    public Result<Map<String, String>> getQueueStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("comprehensiveQueue", "comprehensive.assessment.queue");
        status.put("note", "请通过RabbitMQ管理界面查看详细队列状态");
        status.put("managementUrl", "http://localhost:15672");
        return Result.success(status);
    }

    /**
     * 测试单个患者的所有评估
     */
    @PostMapping("/test-single-patient/{patientId}")
    public Result<Map<String, Object>> testSinglePatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam(defaultValue = "管理员") String userName) {
        
        log.info("测试单个患者评估，患者ID: {}", patientId);
        
        try {
            // 验证患者是否存在
            Patient patient = patientMapper.selectById(patientId);
            if (patient == null) {
                return Result.error("患者不存在");
            }
            
            // 发送评估消息
            List<Long> patientIds = List.of(patientId);
            messageProducerService.sendComprehensiveAssessmentMessage(patientIds, userId, userName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "评估任务已提交");
            result.put("patientId", patientId);
            result.put("patientNumber", patient.getPatientNumber());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("测试单个患者评估失败", e);
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试患者数据迁移
     */
    @PostMapping("/test-patient-migration")
    public Result<Map<String, Object>> testPatientMigration(
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam(defaultValue = "管理员") String userName,
            @RequestParam(defaultValue = "100") int totalCount) {
        
        log.info("测试患者数据迁移，用户: {}", userName);
        
        try {
            // 发送患者数据迁移消息
            messageProducerService.sendPatientMigrationMessage(userId, userName, "手动触发迁移", totalCount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "患者数据迁移任务已提交到队列");
            result.put("userId", userId);
            result.put("userName", userName);
            result.put("totalCount", totalCount);
            result.put("queueName", "patient.migration.queue");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("触发患者数据迁移失败", e);
            return Result.error("触发迁移失败: " + e.getMessage());
        }
    }
}
