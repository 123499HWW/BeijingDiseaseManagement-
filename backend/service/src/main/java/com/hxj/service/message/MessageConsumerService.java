package com.hxj.service.message;

import com.hxj.common.constants.RabbitMQConstants;
import com.hxj.common.dto.message.BaseMessageDTO;
import com.hxj.common.dto.message.ImportTaskMessageDTO;
import com.hxj.common.dto.message.NotificationMessageDTO;
import com.hxj.common.dto.message.PatientAssessmentMessageDTO;
import com.hxj.common.dto.message.PatientDataMessageDTO;
import com.hxj.service.CurbAssessmentService;
import com.hxj.service.ImportTaskService;
import com.hxj.service.PatientDataMigrationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 消息消费者服务
 * 负责处理从RabbitMQ接收到的各种类型消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerService {

    private final CurbAssessmentService curbAssessmentService;
    private final ImportTaskService importTaskService;
    private final PatientDataMigrationService patientDataMigrationService;
    private final MessageProducerService messageProducerService;

    // ==================== 患者数据处理消息消费 ====================

    /**
     * 处理患者数据导入消息
     */
    @RabbitListener(queues = RabbitMQConstants.PATIENT_IMPORT_QUEUE)
    public void handlePatientImportMessage(ImportTaskMessageDTO messageDTO, Message message, Channel channel) {
        log.info("=== 消费者接收到消息 === messageDTO: {}", messageDTO);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            // 直接处理ImportTaskMessageDTO
            if (messageDTO != null) {
                log.info("开始处理导入任务消息: messageId={}, taskId={}, importType={}", 
                        messageDTO.getMessageId(), messageDTO.getTaskId(), messageDTO.getImportType());
                
                // 执行导入任务
                importTaskService.executeImportTask(messageDTO);
                
                log.info("导入任务处理完成: taskId={}", messageDTO.getTaskId());
                
            } else {
                log.warn("接收到空的导入任务消息");
            }
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理患者数据导入消息失败: {}", messageDTO, e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    /**
     * 处理患者数据迁移消息
     */
    @RabbitListener(queues = RabbitMQConstants.PATIENT_MIGRATION_QUEUE)
    public void handlePatientMigrationMessage(PatientDataMessageDTO messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("开始处理患者数据迁移消息: messageId={}, userId={}, operationType={}, totalCount={}", 
                    messageDTO.getMessageId(), messageDTO.getUserId(), 
                    messageDTO.getOperationType(), messageDTO.getTotalCount());
            
            // 调用患者数据迁移服务
            PatientDataMigrationService.MigrationResult result = 
                patientDataMigrationService.migrateAllPatientData(messageDTO.getUserId());
            
            log.info("患者数据迁移处理完成: totalCount={}, successCount={}, failureCount={}", 
                    result.getTotalCount(), result.getSuccessCount(), result.getFailureCount());
            
            // 发送完成通知
            messageProducerService.sendDataProcessCompleteNotification(
                messageDTO.getUserId(), messageDTO.getUserName(), 
                "数据迁移", result.getTotalCount(), result.getSuccessCount());

            // 如果迁移成功且有数据，自动触发综合评估（8个评估）
            if (result.getSuccessCount() > 0) {
                log.info("数据迁移完成，开始触发综合评估: successCount={}", result.getSuccessCount());
                
                // 如果有taskId，获取具体的患者ID列表；否则获取所有已迁移的患者
                List<Long> patientIds;
                if (messageDTO.getTaskId() != null) {
                    // 从导入任务获取患者ID列表
                    patientIds = importTaskService.getImportedPatientIds(messageDTO.getTaskId());
                    log.info("从导入任务{}获取到{}个患者ID", messageDTO.getTaskId(), patientIds.size());
                } else {
                    // 获取所有已迁移的患者ID（备用方案）
                    patientIds = patientDataMigrationService.getMigratedPatientIds();
                    log.info("获取到所有已迁移的{}个患者ID", patientIds.size());
                }
                
                if (!patientIds.isEmpty()) {
                    // 发送综合评估消息到队列（执行8个评估）
                    messageProducerService.sendComprehensiveAssessmentMessage(
                        patientIds, messageDTO.getUserId(), messageDTO.getUserName());
                    log.info("已发送综合评估消息，患者数量: {}", patientIds.size());
                }
            }
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理患者数据迁移消息失败: {}", messageDTO.getMessageId(), e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    // ==================== 评估任务消息消费 ====================

    /**
     * 处理CURB-65评估消息
     */
    @RabbitListener(queues = RabbitMQConstants.CURB_ASSESSMENT_QUEUE)
    public void handleCurbAssessmentMessage(PatientAssessmentMessageDTO messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("开始处理CURB-65评估消息: {}", messageDTO.getMessageId());
            
            if (messageDTO.getPatientId() != null) {
                // 单个患者评估
                // 这里需要先获取患者信息，然后调用评估服务
                // 暂时只记录日志
                log.info("单个患者CURB-65评估: patientId={}", messageDTO.getPatientId());
                
            } else if (messageDTO.getPatientIds() != null && !messageDTO.getPatientIds().isEmpty()) {
                // 多个患者评估
                log.info("多个患者CURB-65评估: patientIds={}", messageDTO.getPatientIds());
            }
            
            // 发送完成通知
            messageProducerService.sendAssessmentCompleteNotification(
                messageDTO.getUserId(), messageDTO.getUserName(), 
                "CURB-65评估", 1, 1);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理CURB-65评估消息失败: {}", messageDTO.getMessageId(), e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    /**
     * 处理批量评估消息
     */
    @RabbitListener(queues = RabbitMQConstants.BATCH_ASSESSMENT_QUEUE)
    public void handleBatchAssessmentMessage(PatientAssessmentMessageDTO messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("开始处理批量评估消息: messageId={}, userId={}, assessmentType={}, description={}", 
                    messageDTO.getMessageId(), messageDTO.getUserId(), 
                    messageDTO.getAssessmentType(), messageDTO.getDescription());
            
            // 调用批量CURB-65评估服务
            CurbAssessmentService.AssessmentResult result = 
                curbAssessmentService.assessAllPatients(messageDTO.getUserId());
            
            log.info("批量CURB-65评估处理完成: totalCount={}, successCount={}, failureCount={}", 
                    result.getTotalCount(), result.getSuccessCount(), result.getFailureCount());
            
            // 发送完成通知
            messageProducerService.sendAssessmentCompleteNotification(
                messageDTO.getUserId(), messageDTO.getUserName(), 
                "批量CURB-65评估", result.getTotalCount(), result.getSuccessCount());
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理批量评估消息失败: {}", messageDTO.getMessageId(), e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    // ==================== 延迟消息处理 ====================

    /**
     * 处理延迟消息（从死信队列接收过期的延迟消息）
     */
    @RabbitListener(queues = RabbitMQConstants.DELAY_PROCESS_QUEUE)
    public void handleDelayMessage(BaseMessageDTO messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("开始处理延迟消息: messageId={}", messageDTO.getMessageId());
            
            // 获取目标交换机和路由键
            String targetExchange = (String) message.getMessageProperties().getHeaders().get("target-exchange");
            String targetRoutingKey = (String) message.getMessageProperties().getHeaders().get("target-routing-key");
            Long delayStartTime = (Long) message.getMessageProperties().getHeaders().get("delay-start-time");
            
            if (delayStartTime != null) {
                long actualDelay = System.currentTimeMillis() - delayStartTime;
                log.info("延迟消息实际延迟时间: {}ms", actualDelay);
            }
            
            // 转发到目标交换机和路由键
            if (targetExchange != null && targetRoutingKey != null) {
                // 使用MessageProducerService转发消息
                messageProducerService.sendMessage(targetExchange, targetRoutingKey, messageDTO);
                log.info("延迟消息转发成功: targetExchange={}, targetRoutingKey={}, messageId={}", 
                        targetExchange, targetRoutingKey, messageDTO.getMessageId());
            } else {
                log.warn("延迟消息缺少目标信息: targetExchange={}, targetRoutingKey={}, messageId={}", 
                        targetExchange, targetRoutingKey, messageDTO.getMessageId());
            }
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理延迟消息失败: messageId={}", messageDTO.getMessageId(), e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    // ==================== 通知消息消费 ====================

    /**
     * 处理邮件通知消息
     */
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void handleEmailNotificationMessage(NotificationMessageDTO messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("开始处理邮件通知消息: {}", messageDTO.getMessageId());
            
            // 这里可以集成邮件发送服务
            log.info("邮件通知处理: recipients={}, title={}, content={}", 
                    messageDTO.getRecipients(), messageDTO.getTitle(), messageDTO.getContent());
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理邮件通知消息失败: {}", messageDTO.getMessageId(), e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    /**
     * 处理系统通知消息
     */
    @RabbitListener(queues = RabbitMQConstants.SYSTEM_NOTIFICATION_QUEUE)
    public void handleSystemNotificationMessage(NotificationMessageDTO messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("开始处理系统通知消息: {}", messageDTO.getMessageId());
            
            // 这里可以将通知保存到数据库或推送到前端
            log.info("系统通知处理: title={}, content={}, userId={}", 
                    messageDTO.getTitle(), messageDTO.getContent(), messageDTO.getUserId());
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理系统通知消息失败: {}", messageDTO.getMessageId(), e);
            handleMessageError(messageDTO, message, channel, deliveryTag, e);
        }
    }

    /**
     * 处理死信队列消息
     */
    @RabbitListener(queues = RabbitMQConstants.DEAD_LETTER_QUEUE)
    public void handleDeadLetterMessage(Object messageDTO, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.error("收到死信消息: {}", message.getMessageProperties().getHeaders());
            
            // 记录死信消息，用于后续分析和处理
            String originalQueue = (String) message.getMessageProperties().getHeaders()
                .get(RabbitMQConstants.ORIGINAL_QUEUE_HEADER);
            String failureReason = (String) message.getMessageProperties().getHeaders()
                .get(RabbitMQConstants.FAILURE_REASON_HEADER);
            
            log.error("死信消息详情: originalQueue={}, failureReason={}, message={}", 
                     originalQueue, failureReason, messageDTO);
            
            // 手动确认死信消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理死信消息失败", e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("拒绝死信消息失败", ioException);
            }
        }
    }

    /**
     * 处理消息错误
     */
    private void handleMessageError(Object messageDTO, Message message, Channel channel, 
                                   long deliveryTag, Exception e) {
        try {
            // 获取重试次数
            Integer retryCount = (Integer) message.getMessageProperties().getHeaders()
                .getOrDefault(RabbitMQConstants.RETRY_COUNT_HEADER, 0);
            
            if (retryCount < RabbitMQConstants.MAX_RETRY_COUNT) {
                // 增加重试次数并重新发送到队列
                message.getMessageProperties().getHeaders()
                    .put(RabbitMQConstants.RETRY_COUNT_HEADER, retryCount + 1);
                message.getMessageProperties().getHeaders()
                    .put(RabbitMQConstants.FAILURE_REASON_HEADER, e.getMessage());
                
                // 拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
                
                log.warn("消息处理失败，将重试: retryCount={}, messageId={}", 
                        retryCount + 1, messageDTO);
                
            } else {
                // 超过最大重试次数，发送到死信队列
                message.getMessageProperties().getHeaders()
                    .put(RabbitMQConstants.FAILURE_REASON_HEADER, e.getMessage());
                message.getMessageProperties().getHeaders()
                    .put(RabbitMQConstants.ORIGINAL_QUEUE_HEADER, 
                         message.getMessageProperties().getConsumerQueue());
                
                // 拒绝消息，不重新入队（会进入死信队列）
                channel.basicNack(deliveryTag, false, false);
                
                log.error("消息处理失败，已达到最大重试次数，发送到死信队列: messageId={}", messageDTO);
            }
            
        } catch (IOException ioException) {
            log.error("处理消息错误时发生异常", ioException);
        }
    }
}
