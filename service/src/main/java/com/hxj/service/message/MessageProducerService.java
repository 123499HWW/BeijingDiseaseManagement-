package com.hxj.service.message;

import com.hxj.common.constants.RabbitMQConstants;
import com.hxj.common.dto.message.BaseMessageDTO;
import com.hxj.common.dto.message.ImportTaskMessageDTO;
import com.hxj.common.dto.message.NotificationMessageDTO;
import com.hxj.common.dto.message.PatientAssessmentMessageDTO;
import com.hxj.common.dto.message.PatientDataMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息生产者服务
 * 负责发送各种类型的消息到RabbitMQ
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    // ==================== 患者数据处理消息 ====================

    /**
     * 发送患者数据导入消息
     */
    public void sendPatientImportMessage(PatientDataMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_PATIENT_IMPORT);
        sendMessage(RabbitMQConstants.PATIENT_EXCHANGE, 
                   RabbitMQConstants.PATIENT_IMPORT_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送患者数据导入消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送导入任务消息
     */
    public void sendPatientImportMessage(ImportTaskMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_PATIENT_IMPORT);
        sendMessage(RabbitMQConstants.PATIENT_EXCHANGE, 
                   RabbitMQConstants.PATIENT_IMPORT_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送导入任务消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送患者数据迁移消息
     */
    public void sendPatientMigrationMessage(PatientDataMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_PATIENT_MIGRATION);
        sendMessage(RabbitMQConstants.PATIENT_EXCHANGE, 
                   RabbitMQConstants.PATIENT_MIGRATION_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送患者数据迁移消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送患者数据迁移消息（重载方法）
     */
    public void sendPatientMigrationMessage(String userId, String userName, String operationType, Integer totalCount) {
        PatientDataMessageDTO messageDTO = new PatientDataMessageDTO(
            userId, userName, operationType, totalCount);
        sendPatientMigrationMessage(messageDTO);
    }

    // ==================== 评估任务消息 ====================

    /**
     * 发送CURB-65评估消息
     */
    public void sendCurbAssessmentMessage(PatientAssessmentMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_CURB_ASSESSMENT);
        sendMessage(RabbitMQConstants.ASSESSMENT_EXCHANGE, 
                   RabbitMQConstants.CURB_ASSESSMENT_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送CURB-65评估消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送批量评估消息
     */
    public void sendBatchAssessmentMessage(PatientAssessmentMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_BATCH_ASSESSMENT);
        sendMessage(RabbitMQConstants.ASSESSMENT_EXCHANGE, 
                   RabbitMQConstants.BATCH_ASSESSMENT_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送批量评估消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送批量评估消息（重载方法）
     */
    public void sendBatchAssessmentMessage(String userId, String userName, String assessmentType, Integer totalCount) {
        PatientAssessmentMessageDTO messageDTO = new PatientAssessmentMessageDTO(
            userId, userName, "CURB_65_BATCH", assessmentType);
        messageDTO.setTotalCount(totalCount);
        sendBatchAssessmentMessage(messageDTO);
    }

    /**
     * 发送单个患者CURB-65评估消息
     */
    public void sendSinglePatientCurbAssessment(Long patientId, String userId, String userName) {
        PatientAssessmentMessageDTO messageDTO = new PatientAssessmentMessageDTO(
            patientId, "CURB_65", userId, userName);
        sendCurbAssessmentMessage(messageDTO);
    }

    /**
     * 发送批量患者CURB-65评估消息
     */
    public void sendBatchPatientCurbAssessment(List<Long> patientIds, String userId, String userName) {
        PatientAssessmentMessageDTO messageDTO = new PatientAssessmentMessageDTO(
            patientIds, "CURB_65_BATCH", userId, userName);
        sendBatchAssessmentMessage(messageDTO);
    }
    
    /**
     * 发送综合评估消息（执行所有8个评估）
     */
    public void sendComprehensiveAssessmentMessage(PatientAssessmentMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_COMPREHENSIVE_ASSESSMENT);
        sendMessage(RabbitMQConstants.ASSESSMENT_EXCHANGE, 
                   RabbitMQConstants.COMPREHENSIVE_ASSESSMENT_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送综合评估消息: {}, 患者ID列表大小: {}", 
                messageDTO.getMessageId(), 
                messageDTO.getPatientIds() != null ? messageDTO.getPatientIds().size() : 0);
    }
    
    /**
     * 发送综合评估消息（重载方法）
     */
    public void sendComprehensiveAssessmentMessage(List<Long> patientIds, String userId, String userName) {
        PatientAssessmentMessageDTO messageDTO = new PatientAssessmentMessageDTO(
            patientIds, "COMPREHENSIVE", userId, userName);
        messageDTO.setDescription("患者导入后自动执行综合评估");
        sendComprehensiveAssessmentMessage(messageDTO);
    }

    // ==================== 通知消息 ====================

    /**
     * 发送邮件通知消息
     */
    public void sendEmailNotificationMessage(NotificationMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_EMAIL_NOTIFICATION);
        sendMessage(RabbitMQConstants.NOTIFICATION_EXCHANGE, 
                   RabbitMQConstants.EMAIL_NOTIFICATION_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送邮件通知消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送系统通知消息
     */
    public void sendSystemNotificationMessage(NotificationMessageDTO messageDTO) {
        messageDTO.setMessageType(RabbitMQConstants.MESSAGE_TYPE_SYSTEM_NOTIFICATION);
        sendMessage(RabbitMQConstants.NOTIFICATION_EXCHANGE, 
                   RabbitMQConstants.SYSTEM_NOTIFICATION_ROUTING_KEY, 
                   messageDTO);
        
        log.info("发送系统通知消息: {}", messageDTO.getMessageId());
    }

    /**
     * 发送评估完成通知
     */
    public void sendAssessmentCompleteNotification(String userId, String userName, 
                                                  String assessmentType, int totalCount, int successCount) {
        NotificationMessageDTO messageDTO = new NotificationMessageDTO();
        messageDTO.setNotificationType("SYSTEM");
        messageDTO.setTitle("评估任务完成通知");
        messageDTO.setContent(String.format("您的%s评估任务已完成，共处理%d条记录，成功%d条", 
                                          assessmentType, totalCount, successCount));
        messageDTO.setUserId(userId);
        messageDTO.setUserName(userName);
        messageDTO.setPriority("NORMAL");
        
        sendSystemNotificationMessage(messageDTO);
    }

    /**
     * 发送数据处理完成通知
     */
    public void sendDataProcessCompleteNotification(String userId, String userName, 
                                                   String operationType, int totalCount, int successCount) {
        NotificationMessageDTO messageDTO = new NotificationMessageDTO();
        messageDTO.setNotificationType("SYSTEM");
        messageDTO.setTitle("数据处理完成通知");
        messageDTO.setContent(String.format("您的%s任务已完成，共处理%d条记录，成功%d条", 
                                          operationType, totalCount, successCount));
        messageDTO.setUserId(userId);
        messageDTO.setUserName(userName);
        messageDTO.setPriority("NORMAL");
        
        sendSystemNotificationMessage(messageDTO);
    }

    // ==================== 通用发送方法 ====================

    /**
     * 通用消息发送方法（公共方法，供延迟消息处理器使用）
     */
    public void sendMessage(String exchange, String routingKey, BaseMessageDTO messageDTO) {
        try {
            // 设置消息属性
            MessageProperties properties = new MessageProperties();
            properties.setHeader(RabbitMQConstants.MESSAGE_TYPE_HEADER, messageDTO.getMessageType());
            properties.setHeader(RabbitMQConstants.USER_ID_HEADER, messageDTO.getUserId());
            properties.setHeader(RabbitMQConstants.OPERATION_TIME_HEADER, LocalDateTime.now().toString());
            properties.setHeader(RabbitMQConstants.RETRY_COUNT_HEADER, messageDTO.getRetryCount());
            
            // 发送消息
            rabbitTemplate.convertAndSend(exchange, routingKey, messageDTO, message -> {
                MessageProperties msgProps = message.getMessageProperties();
                msgProps.getHeaders().putAll(properties.getHeaders());
                return message;
            });
            
            log.debug("消息发送成功: exchange={}, routingKey={}, messageId={}", 
                     exchange, routingKey, messageDTO.getMessageId());
            
        } catch (Exception e) {
            log.error("消息发送失败: exchange={}, routingKey={}, messageId={}", 
                     exchange, routingKey, messageDTO.getMessageId(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送延迟消息
     * 使用TTL+DLX方式实现延迟消息
     */
    public void sendDelayMessage(String exchange, String routingKey, BaseMessageDTO messageDTO, long delayMillis) {
        sendDelayMessageWithTTL(exchange, routingKey, messageDTO, delayMillis);
    }

    /**
     * 发送延迟消息（使用TTL+DLX方式）
     * 1. 先发送到延迟队列，设置TTL
     * 2. 消息过期后自动转发到死信交换机
     * 3. 最终路由到目标队列进行处理
     */
    public void sendDelayMessageWithTTL(String targetExchange, String targetRoutingKey, BaseMessageDTO messageDTO, long delayMillis) {
        try {
            // 先发送到延迟队列
            rabbitTemplate.convertAndSend(RabbitMQConstants.DELAY_EXCHANGE, "delay", messageDTO, message -> {
                MessageProperties properties = message.getMessageProperties();
                // 设置消息TTL（过期时间）
                properties.setExpiration(String.valueOf(delayMillis));
                // 设置目标交换机和路由键，用于延迟处理后的转发
                properties.setHeader("target-exchange", targetExchange);
                properties.setHeader("target-routing-key", targetRoutingKey);
                properties.setHeader(RabbitMQConstants.MESSAGE_TYPE_HEADER, messageDTO.getMessageType());
                properties.setHeader(RabbitMQConstants.USER_ID_HEADER, messageDTO.getUserId());
                properties.setHeader(RabbitMQConstants.OPERATION_TIME_HEADER, LocalDateTime.now().toString());
                properties.setHeader("delay-start-time", System.currentTimeMillis());
                return message;
            });
            
            log.info("TTL延迟消息发送成功: targetExchange={}, targetRoutingKey={}, delay={}ms, messageId={}", 
                    targetExchange, targetRoutingKey, delayMillis, messageDTO.getMessageId());
            
        } catch (Exception e) {
            log.error("TTL延迟消息发送失败: targetExchange={}, targetRoutingKey={}, messageId={}", 
                     targetExchange, targetRoutingKey, messageDTO.getMessageId(), e);
            throw new RuntimeException("TTL延迟消息发送失败", e);
        }
    }
    
    /**
     * 发送延迟消息到指定队列（简化版本）
     */
    public void sendDelayMessageToQueue(String targetQueue, BaseMessageDTO messageDTO, long delayMillis) {
        // 使用默认交换机和队列名作为路由键
        sendDelayMessageWithTTL("", targetQueue, messageDTO, delayMillis);
    }
}
