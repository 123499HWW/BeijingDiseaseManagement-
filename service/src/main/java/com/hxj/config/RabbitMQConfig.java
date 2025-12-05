package com.hxj.config;

import com.hxj.common.constants.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 * 定义交换机、队列、绑定关系等
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    // ==================== 消息转换器 ====================
    
    /**
     * JSON消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        
        // 设置发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送成功: {}", correlationData);
            } else {
                log.error("消息发送失败: {}, 原因: {}", correlationData, cause);
            }
        });
        
        // 设置返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息被退回: {}, 退回码: {}, 退回原因: {}", 
                     returned.getMessage(), returned.getReplyCode(), returned.getReplyText());
        });
        
        return rabbitTemplate;
    }

    /**
     * 监听器容器工厂配置
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        
        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        
        // 设置预取数量
        factory.setPrefetchCount(1);
        
        // 设置确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        
        return factory;
    }

    // ==================== 交换机定义 ====================
    
    /**
     * 患者数据处理交换机
     */
    @Bean
    public DirectExchange patientExchange() {
        return ExchangeBuilder.directExchange(RabbitMQConstants.PATIENT_EXCHANGE)
                .durable(true)
                .build();
    }
    
    /**
     * 评估任务交换机
     */
    @Bean
    public DirectExchange assessmentExchange() {
        return ExchangeBuilder.directExchange(RabbitMQConstants.ASSESSMENT_EXCHANGE)
                .durable(true)
                .build();
    }
    
    /**
     * 通知消息交换机
     */
    @Bean
    public DirectExchange notificationExchange() {
        return ExchangeBuilder.directExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }
    
    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
    }
    
    /**
     * TTL延迟交换机（用于延迟消息的临时存储）
     */
    @Bean
    public DirectExchange delayExchange() {
        return ExchangeBuilder.directExchange(RabbitMQConstants.DELAY_EXCHANGE)
                .durable(true)
                .build();
    }

    // ==================== 队列定义 ====================
    
    /**
     * 患者数据导入队列
     */
    @Bean
    public Queue patientImportQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PATIENT_IMPORT_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL)
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * 患者数据迁移队列
     */
    @Bean
    public Queue patientMigrationQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PATIENT_MIGRATION_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL)
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * CURB-65评估队列
     */
    @Bean
    public Queue curbAssessmentQueue() {
        return QueueBuilder.durable(RabbitMQConstants.CURB_ASSESSMENT_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL)
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * 批量评估队列
     */
    @Bean
    public Queue batchAssessmentQueue() {
        return QueueBuilder.durable(RabbitMQConstants.BATCH_ASSESSMENT_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL)
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * 综合评估队列
     */
    @Bean
    public Queue comprehensiveAssessmentQueue() {
        return QueueBuilder.durable(RabbitMQConstants.COMPREHENSIVE_ASSESSMENT_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL * 2) // 综合评估需要更长时间
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * 邮件通知队列
     */
    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder.durable(RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL)
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * 系统通知队列
     */
    @Bean
    public Queue systemNotificationQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SYSTEM_NOTIFICATION_QUEUE)
                .withArgument("x-message-ttl", RabbitMQConstants.MESSAGE_TTL)
                .withArgument("x-max-length", RabbitMQConstants.QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DEAD_LETTER_ROUTING_KEY)
                .build();
    }
    
    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DEAD_LETTER_QUEUE)
                .build();
    }
    
    /**
     * TTL延迟队列（消息过期后转发到死信交换机）
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DELAY_QUEUE)
                // 设置死信交换机
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                // 设置死信路由键
                .withArgument("x-dead-letter-routing-key", "delay.expired")
                .build();
    }
    
    /**
     * 延迟消息处理队列（接收过期的延迟消息）
     */
    @Bean
    public Queue delayProcessQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DELAY_PROCESS_QUEUE)
                .build();
    }

    // ==================== 绑定关系定义 ====================
    
    /**
     * 患者数据导入队列绑定
     */
    @Bean
    public Binding patientImportBinding() {
        return BindingBuilder.bind(patientImportQueue())
                .to(patientExchange())
                .with(RabbitMQConstants.PATIENT_IMPORT_ROUTING_KEY);
    }
    
    /**
     * 患者数据迁移队列绑定
     */
    @Bean
    public Binding patientMigrationBinding() {
        return BindingBuilder.bind(patientMigrationQueue())
                .to(patientExchange())
                .with(RabbitMQConstants.PATIENT_MIGRATION_ROUTING_KEY);
    }
    
    /**
     * CURB-65评估队列绑定
     */
    @Bean
    public Binding curbAssessmentBinding() {
        return BindingBuilder.bind(curbAssessmentQueue())
                .to(assessmentExchange())
                .with(RabbitMQConstants.CURB_ASSESSMENT_ROUTING_KEY);
    }
    
    /**
     * 批量评估队列绑定
     */
    @Bean
    public Binding batchAssessmentBinding() {
        return BindingBuilder.bind(batchAssessmentQueue())
                .to(assessmentExchange())
                .with(RabbitMQConstants.BATCH_ASSESSMENT_ROUTING_KEY);
    }
    
    /**
     * 综合评估队列绑定
     */
    @Bean
    public Binding comprehensiveAssessmentBinding() {
        return BindingBuilder.bind(comprehensiveAssessmentQueue())
                .to(assessmentExchange())
                .with(RabbitMQConstants.COMPREHENSIVE_ASSESSMENT_ROUTING_KEY);
    }
    
    /**
     * 邮件通知队列绑定
     */
    @Bean
    public Binding emailNotificationBinding() {
        return BindingBuilder.bind(emailNotificationQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_ROUTING_KEY);
    }
    
    /**
     * 系统通知队列绑定
     */
    @Bean
    public Binding systemNotificationBinding() {
        return BindingBuilder.bind(systemNotificationQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.SYSTEM_NOTIFICATION_ROUTING_KEY);
    }
    
    /**
     * 死信队列绑定
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.DEAD_LETTER_ROUTING_KEY);
    }
    
    /**
     * TTL延迟队列绑定
     */
    @Bean
    public Binding delayQueueBinding() {
        return BindingBuilder.bind(delayQueue())
                .to(delayExchange())
                .with("delay");
    }
    
    /**
     * 延迟消息处理队列绑定（绑定到死信交换机）
     */
    @Bean
    public Binding delayProcessBinding() {
        return BindingBuilder.bind(delayProcessQueue())
                .to(deadLetterExchange())
                .with("delay.expired");
    }
}
