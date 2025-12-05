package com.hxj.service.message;

import com.hxj.common.constants.RabbitMQConstants;
import com.hxj.common.dto.message.PatientAssessmentMessageDTO;
import com.hxj.service.PatientBatchAssessmentService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 患者评估消息消费者
 * 负责处理综合评估消息，按顺序执行所有评估任务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientAssessmentConsumer {

    private final PatientBatchAssessmentService batchAssessmentService;
    private final MessageProducerService messageProducerService;

    /**
     * 监听综合评估队列，处理患者评估消息
     */
    @RabbitListener(queues = RabbitMQConstants.COMPREHENSIVE_ASSESSMENT_QUEUE)
    public void handleComprehensiveAssessment(PatientAssessmentMessageDTO messageDTO,
                                               Channel channel,
                                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("收到综合评估消息: messageId={}, 患者数量={}", 
                messageDTO.getMessageId(), 
                messageDTO.getPatientIds() != null ? messageDTO.getPatientIds().size() : 0);
        
        try {
            // 执行综合评估
            performComprehensiveAssessment(messageDTO);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("综合评估消息处理完成: {}", messageDTO.getMessageId());
            
        } catch (Exception e) {
            log.error("处理综合评估消息失败: {}", messageDTO.getMessageId(), e);
            
            try {
                // 检查重试次数
                if (messageDTO.getRetryCount() < RabbitMQConstants.MAX_RETRY_COUNT) {
                    // 增加重试次数并重新发送
                    messageDTO.setRetryCount(messageDTO.getRetryCount() + 1);
                    log.info("重新发送综合评估消息，重试次数: {}", messageDTO.getRetryCount());
                    
                    // 延迟5秒后重试
                    messageProducerService.sendDelayMessage(
                        RabbitMQConstants.ASSESSMENT_EXCHANGE,
                        RabbitMQConstants.COMPREHENSIVE_ASSESSMENT_ROUTING_KEY,
                        messageDTO,
                        5000
                    );
                    
                    // 确认原消息
                    channel.basicAck(deliveryTag, false);
                } else {
                    // 超过最大重试次数，拒绝消息并发送到死信队列
                    log.error("综合评估消息超过最大重试次数，发送到死信队列: {}", messageDTO.getMessageId());
                    channel.basicNack(deliveryTag, false, false);
                }
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

    /**
     * 执行综合评估
     */
    private void performComprehensiveAssessment(PatientAssessmentMessageDTO messageDTO) {
        List<Long> patientIds = messageDTO.getPatientIds();
        if (patientIds == null || patientIds.isEmpty()) {
            log.warn("患者ID列表为空，无法执行评估");
            return;
        }
        
        log.info("开始执行综合评估，患者数量: {}", patientIds.size());
        
        // 使用批量评估服务执行所有评估
        PatientBatchAssessmentService.BatchAssessmentResult result;
        
        // 根据患者数量决定使用串行还是并行处理
        if (patientIds.size() > 50) {
            // 大批量患者使用并行处理
            log.info("患者数量较多，使用并行处理模式");
            result = batchAssessmentService.performParallelBatchAssessment(patientIds);
        } else {
            // 小批量患者使用串行处理
            log.info("使用串行处理模式");
            result = batchAssessmentService.performBatchAssessment(patientIds);
        }
        
        // 发送完成通知
        sendCompletionNotification(messageDTO, result);
        
        log.info("综合评估完成，结果: {}", result);
    }
    
    
    /**
     * 发送完成通知
     */
    private void sendCompletionNotification(PatientAssessmentMessageDTO messageDTO,
                                           PatientBatchAssessmentService.BatchAssessmentResult result) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("综合评估任务完成，共处理%d个患者\n",
                       messageDTO.getPatientIds().size()));
        message.append("\n评估结果统计：\n");
        
        result.getAssessmentStats().forEach((assessmentType, stat) -> {
            message.append(String.format("- %s: 成功%d个，失败%d个\n",
                          assessmentType, stat.getSuccess(), stat.getFailure()));
        });
        
        // 计算总成功数（每个患者的每个评估都成功才算一个成功）
        int totalSuccessPerPatient = result.getTotalSuccess() / 8; // 8个评估
        
        messageProducerService.sendDataProcessCompleteNotification(
            messageDTO.getUserId(),
            messageDTO.getUserName(),
            "综合评估",
            messageDTO.getPatientIds().size(),
            totalSuccessPerPatient
        );
        
        log.info("评估完成通知已发送: {}", message.toString());
    }
}
