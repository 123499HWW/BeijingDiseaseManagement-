package com.hxj.web.controller;

import com.hxj.common.dto.message.PatientAssessmentMessageDTO;
import com.hxj.common.dto.message.PatientDataMessageDTO;
import com.hxj.common.result.Result;
import com.hxj.service.message.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息队列管理控制器
 * 提供消息队列相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
public class MessageQueueController {

    private final MessageProducerService messageProducerService;

    // ==================== 患者数据处理消息 ====================

    /**
     * 发送患者数据迁移消息
     */
    @PostMapping("/patient/migration")
    public Result<String> sendPatientMigrationMessage(
            @RequestParam(defaultValue = "SYSTEM") String userId,
            @RequestParam(defaultValue = "SYSTEM") String userName,
            @RequestParam(defaultValue = "100") Integer totalCount) {
        
        log.info("发送患者数据迁移消息，操作人: {}, 总数: {}", userId, totalCount);
        
        try {
            // 使用正确的构造函数，包含 totalCount 参数
            PatientDataMessageDTO messageDTO = new PatientDataMessageDTO(userId, userName, "MIGRATION", totalCount);
            messageProducerService.sendPatientMigrationMessage(messageDTO);
            
            return Result.success("患者数据迁移消息发送成功，预计处理数量: " + totalCount);
            
        } catch (Exception e) {
            log.error("发送患者数据迁移消息失败", e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    // ==================== 评估任务消息 ====================

    /**
     * 发送单个患者CURB-65评估消息
     */
    @PostMapping("/assessment/curb/single/{patientId}")
    public Result<String> sendSingleCurbAssessmentMessage(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "SYSTEM") String userId,
            @RequestParam(defaultValue = "SYSTEM") String userName) {
        
        log.info("发送单个患者CURB-65评估消息，患者ID: {}, 操作人: {}", patientId, userId);
        
        try {
            messageProducerService.sendSinglePatientCurbAssessment(patientId, userId, userName);
            return Result.success("CURB-65评估消息发送成功");
            
        } catch (Exception e) {
            log.error("发送CURB-65评估消息失败", e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送批量患者CURB-65评估消息
     */
    @PostMapping("/assessment/curb/batch")
    public Result<String> sendBatchCurbAssessmentMessage(
            @RequestBody List<Long> patientIds,
            @RequestParam(defaultValue = "SYSTEM") String userId,
            @RequestParam(defaultValue = "SYSTEM") String userName) {
        
        log.info("发送批量患者CURB-65评估消息，患者数量: {}, 操作人: {}", patientIds.size(), userId);
        
        try {
            if (patientIds == null || patientIds.isEmpty()) {
                // 如果没有指定患者ID，则评估所有患者
                PatientAssessmentMessageDTO messageDTO = new PatientAssessmentMessageDTO();
                messageDTO.setAssessmentType("CURB_65_ALL");
                messageDTO.setUserId(userId);
                messageDTO.setUserName(userName);
                messageProducerService.sendBatchAssessmentMessage(messageDTO);
            } else {
                messageProducerService.sendBatchPatientCurbAssessment(patientIds, userId, userName);
            }
            
            return Result.success("批量CURB-65评估消息发送成功");
            
        } catch (Exception e) {
            log.error("发送批量CURB-65评估消息失败", e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送所有患者CURB-65评估消息
     */
    @PostMapping("/assessment/curb/all")
    public Result<String> sendAllPatientsCurbAssessmentMessage(
            @RequestParam(defaultValue = "SYSTEM") String userId,
            @RequestParam(defaultValue = "SYSTEM") String userName) {
        
        log.info("发送所有患者CURB-65评估消息，操作人: {}", userId);
        
        try {
            PatientAssessmentMessageDTO messageDTO = new PatientAssessmentMessageDTO();
            messageDTO.setAssessmentType("CURB_65_ALL");
            messageDTO.setUserId(userId);
            messageDTO.setUserName(userName);
            messageProducerService.sendBatchAssessmentMessage(messageDTO);
            
            return Result.success("所有患者CURB-65评估消息发送成功");
            
        } catch (Exception e) {
            log.error("发送所有患者CURB-65评估消息失败", e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    // ==================== 通知消息 ====================

    /**
     * 发送测试通知消息
     */
    @PostMapping("/notification/test")
    public Result<String> sendTestNotificationMessage(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "SYSTEM") String userId,
            @RequestParam(defaultValue = "SYSTEM") String userName) {
        
        log.info("发送测试通知消息，操作人: {}", userId);
        
        try {
            messageProducerService.sendAssessmentCompleteNotification(userId, userName, title, 1, 1);
            return Result.success("测试通知消息发送成功");
            
        } catch (Exception e) {
            log.error("发送测试通知消息失败", e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    // ==================== 队列管理 ====================

    /**
     * 获取队列状态信息
     */
    @GetMapping("/status")
    public Result<String> getQueueStatus() {
        
        log.info("获取消息队列状态信息");
        
        try {
            // 这里可以集成RabbitMQ Management API来获取队列状态
            // 暂时返回简单信息
            return Result.success("消息队列服务正常运行");
            
        } catch (Exception e) {
            log.error("获取队列状态失败", e);
            return Result.error("获取状态失败: " + e.getMessage());
        }
    }
}
