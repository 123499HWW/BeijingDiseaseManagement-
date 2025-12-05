package com.hxj.common.constants;

/**
 * RabbitMQ消息队列常量定义
 */
public class RabbitMQConstants {

    // ==================== 交换机定义 ====================
    
    /**
     * 患者数据处理交换机
     */
    public static final String PATIENT_EXCHANGE = "patient.exchange";
    
    /**
     * 评估任务交换机
     */
    public static final String ASSESSMENT_EXCHANGE = "assessment.exchange";
    
    /**
     * 通知消息交换机
     */
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    
    /**
     * 死信交换机
     */
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
    
    /**
     * 延迟消息交换机
     */
    public static final String DELAY_EXCHANGE = "delay.exchange";

    // ==================== 队列定义 ====================
    
    /**
     * 患者数据导入队列
     */
    public static final String PATIENT_IMPORT_QUEUE = "patient.import.queue";
    
    /**
     * 患者数据迁移队列
     */
    public static final String PATIENT_MIGRATION_QUEUE = "patient.migration.queue";
    
    /**
     * CURB-65评估队列
     */
    public static final String CURB_ASSESSMENT_QUEUE = "curb.assessment.queue";
    
    /**
     * 批量评估队列
     */
    public static final String BATCH_ASSESSMENT_QUEUE = "batch.assessment.queue";
    
    /**
     * 综合评估队列 - 用于按顺序执行所有评估任务
     */
    public static final String COMPREHENSIVE_ASSESSMENT_QUEUE = "comprehensive.assessment.queue";
    
    /**
     * 邮件通知队列
     */
    public static final String EMAIL_NOTIFICATION_QUEUE = "email.notification.queue";
    
    /**
     * 系统通知队列
     */
    public static final String SYSTEM_NOTIFICATION_QUEUE = "system.notification.queue";
    
    /**
     * 死信队列
     */
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    
    /**
     * TTL延迟队列
     */
    public static final String DELAY_QUEUE = "delay.queue";
    
    /**
     * 延迟消息处理队列
     */
    public static final String DELAY_PROCESS_QUEUE = "delay.process.queue";

    // ==================== 路由键定义 ====================
    
    /**
     * 患者数据导入路由键
     */
    public static final String PATIENT_IMPORT_ROUTING_KEY = "patient.import";
    
    /**
     * 患者数据迁移路由键
     */
    public static final String PATIENT_MIGRATION_ROUTING_KEY = "patient.migration";
    
    /**
     * CURB-65评估路由键
     */
    public static final String CURB_ASSESSMENT_ROUTING_KEY = "curb.assessment";
    
    /**
     * 批量评估路由键
     */
    public static final String BATCH_ASSESSMENT_ROUTING_KEY = "batch.assessment";
    
    /**
     * 综合评估路由键
     */
    public static final String COMPREHENSIVE_ASSESSMENT_ROUTING_KEY = "comprehensive.assessment";
    
    /**
     * 邮件通知路由键
     */
    public static final String EMAIL_NOTIFICATION_ROUTING_KEY = "email.notification";
    
    /**
     * 系统通知路由键
     */
    public static final String SYSTEM_NOTIFICATION_ROUTING_KEY = "system.notification";
    
    /**
     * 死信路由键
     */
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter";

    // ==================== 消息属性 ====================
    
    /**
     * 消息TTL（毫秒）- 30分钟
     */
    public static final int MESSAGE_TTL = 30 * 60 * 1000;
    
    /**
     * 队列最大长度
     */
    public static final int QUEUE_MAX_LENGTH = 10000;
    
    /**
     * 最大重试次数
     */
    public static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 重试延迟时间（毫秒）
     */
    public static final int RETRY_DELAY = 5000;

    // ==================== 消息头定义 ====================
    
    /**
     * 重试次数头
     */
    public static final String RETRY_COUNT_HEADER = "x-retry-count";
    
    /**
     * 原始队列头
     */
    public static final String ORIGINAL_QUEUE_HEADER = "x-original-queue";
    
    /**
     * 失败原因头
     */
    public static final String FAILURE_REASON_HEADER = "x-failure-reason";
    
    /**
     * 消息类型头
     */
    public static final String MESSAGE_TYPE_HEADER = "x-message-type";
    
    /**
     * 用户ID头
     */
    public static final String USER_ID_HEADER = "x-user-id";
    
    /**
     * 操作时间头
     */
    public static final String OPERATION_TIME_HEADER = "x-operation-time";

    // ==================== 消息类型定义 ====================
    
    /**
     * 患者导入消息类型
     */
    public static final String MESSAGE_TYPE_PATIENT_IMPORT = "PATIENT_IMPORT";
    
    /**
     * 患者迁移消息类型
     */
    public static final String MESSAGE_TYPE_PATIENT_MIGRATION = "PATIENT_MIGRATION";
    
    /**
     * CURB评估消息类型
     */
    public static final String MESSAGE_TYPE_CURB_ASSESSMENT = "CURB_ASSESSMENT";
    
    /**
     * 批量评估消息类型
     */
    public static final String MESSAGE_TYPE_BATCH_ASSESSMENT = "BATCH_ASSESSMENT";
    
    /**
     * 综合评估消息类型
     */
    public static final String MESSAGE_TYPE_COMPREHENSIVE_ASSESSMENT = "COMPREHENSIVE_ASSESSMENT";
    
    /**
     * 邮件通知消息类型
     */
    public static final String MESSAGE_TYPE_EMAIL_NOTIFICATION = "EMAIL_NOTIFICATION";
    
    /**
     * 系统通知消息类型
     */
    public static final String MESSAGE_TYPE_SYSTEM_NOTIFICATION = "SYSTEM_NOTIFICATION";
}
