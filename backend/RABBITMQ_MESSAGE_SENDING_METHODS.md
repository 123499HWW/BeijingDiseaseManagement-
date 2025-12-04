# RabbitMQ 消息发送方法清单

本文档列出了项目中所有发送消息到RabbitMQ队列的方法及其位置。

## 一、MessageProducerService 定义的发送方法

**文件位置**: `service/src/main/java/com/hxj/service/message/MessageProducerService.java`

### 1. 患者数据处理消息
- **sendPatientImportMessage(PatientDataMessageDTO)** - 行35：发送患者数据导入消息
- **sendPatientImportMessage(ImportTaskMessageDTO)** - 行47：发送导入任务消息
- **sendPatientMigrationMessage(PatientDataMessageDTO)** - 行59：发送患者数据迁移消息
- **sendPatientMigrationMessage(String, String, String, Integer)** - 行71：发送患者数据迁移消息（重载）

### 2. 评估任务消息
- **sendCurbAssessmentMessage(PatientAssessmentMessageDTO)** - 行82：发送CURB-65评估消息
- **sendBatchAssessmentMessage(PatientAssessmentMessageDTO)** - 行94：发送批量评估消息
- **sendBatchAssessmentMessage(String, String, String, Integer)** - 行106：发送批量评估消息（重载）
- **sendSinglePatientCurbAssessment(Long, String, String)** - 行116：发送单个患者CURB-65评估
- **sendBatchPatientCurbAssessment(List<Long>, String, String)** - 行125：发送批量患者CURB-65评估
- **sendComprehensiveAssessmentMessage(PatientAssessmentMessageDTO)** - 行134：发送综合评估消息（8个评估）
- **sendComprehensiveAssessmentMessage(List<Long>, String, String)** - 行148：发送综合评估消息（重载）

### 3. 通知消息
- **sendEmailNotificationMessage(NotificationMessageDTO)** - 行160：发送邮件通知
- **sendSystemNotificationMessage(NotificationMessageDTO)** - 行172：发送系统通知
- **sendAssessmentCompleteNotification(String, String, String, int, int)** - 行184：发送评估完成通知
- **sendDataProcessCompleteNotification(String, String, String, int, int)** - 行201：发送数据处理完成通知

### 4. 通用和延迟消息
- **sendMessage(String, String, BaseMessageDTO)** - 行220：通用消息发送方法
- **sendDelayMessage(String, String, BaseMessageDTO, long)** - 行250：发送延迟消息
- **sendDelayMessageWithTTL(String, String, BaseMessageDTO, long)** - 行260：使用TTL发送延迟消息
- **sendDelayMessageToQueue(String, BaseMessageDTO, long)** - 行290：发送延迟消息到指定队列

## 二、调用发送方法的位置

### 1. ImportTaskService（导入任务服务）
**文件**: `service/src/main/java/com/hxj/service/ImportTaskService.java`

| 方法调用 | 行号 | 说明 |
|---------|------|------|
| sendPatientImportMessage(ImportTaskMessageDTO) | 92 | 创建导入任务后发送到队列处理 |
| sendDataProcessCompleteNotification() | 138 | 导入完成后发送通知 |
| sendPatientMigrationMessage() | 149 | 患者导入成功后触发数据迁移 |
| sendComprehensiveAssessmentMessage() | 158 | 患者导入成功后触发综合评估 |

### 2. MessageQueueController（消息队列控制器）
**文件**: `web/src/main/java/com/hxj/web/controller/MessageQueueController.java`

| 方法调用 | 行号 | 接口路径 | 说明 |
|---------|------|----------|------|
| sendPatientMigrationMessage() | 41 | POST /api/mq/patient/migration | 手动触发患者数据迁移 |
| sendSinglePatientCurbAssessment() | 65 | POST /api/mq/assessment/curb/single/{patientId} | 单个患者CURB-65评估 |
| sendBatchAssessmentMessage() | 92 | POST /api/mq/assessment/curb/batch | 批量CURB-65评估 |
| sendBatchPatientCurbAssessment() | 94 | POST /api/mq/assessment/curb/batch | 指定患者批量评估 |
| sendBatchAssessmentMessage() | 120 | POST /api/mq/assessment/curb/all | 所有患者CURB-65评估 |
| sendAssessmentCompleteNotification() | 145 | POST /api/mq/notification/test | 测试通知消息 |

### 3. AssessmentTestController（评估测试控制器）
**文件**: `web/src/main/java/com/hxj/web/controller/AssessmentTestController.java`

| 方法调用 | 行号 | 接口路径 | 说明 |
|---------|------|----------|------|
| sendComprehensiveAssessmentMessage() | 49 | POST /api/assessment-test/trigger-comprehensive-queue | 通过队列触发综合评估 |
| sendComprehensiveAssessmentMessage() | 128 | POST /api/assessment-test/trigger-all-patients | 评估所有患者 |
| sendComprehensiveAssessmentMessage() | 174 | POST /api/assessment-test/test-single-patient/{patientId} | 测试单个患者评估 |
| sendPatientMigrationMessage() | 201 | POST /api/assessment-test/test-patient-migration | 测试患者数据迁移 |

### 4. PatientAssessmentConsumer（患者评估消费者）
**文件**: `service/src/main/java/com/hxj/service/message/PatientAssessmentConsumer.java`

| 方法调用 | 行号 | 说明 |
|---------|------|------|
| sendDelayMessage() | 61 | 评估失败时延迟重试 |
| sendDataProcessCompleteNotification() | 132 | 综合评估完成后发送通知 |

### 5. MessageConsumerService（消息消费者服务）
**文件**: `service/src/main/java/com/hxj/service/message/MessageConsumerService.java`

| 方法调用 | 行号 | 说明 |
|---------|------|------|
| sendDataProcessCompleteNotification() | 90 | 患者数据迁移完成通知 |
| sendBatchAssessmentMessage() | 99 | 数据迁移后自动触发批量评估 |
| sendAssessmentCompleteNotification() | 137 | CURB-65评估完成通知 |
| sendAssessmentCompleteNotification() | 170 | 批量CURB-65评估完成通知 |
| sendMessage() | 208 | 延迟消息转发 |

## 三、消息队列和路由映射

### 队列列表
1. **patient.import.queue** - 患者数据导入队列
2. **patient.migration.queue** - 患者数据迁移队列
3. **curb.assessment.queue** - CURB-65评估队列
4. **batch.assessment.queue** - 批量评估队列
5. **comprehensive.assessment.queue** - 综合评估队列（8个评估）
6. **email.notification.queue** - 邮件通知队列
7. **system.notification.queue** - 系统通知队列
8. **delay.process.queue** - 延迟处理队列
9. **dead.letter.queue** - 死信队列

### 交换机
1. **patient.exchange** - 患者数据处理交换机
2. **assessment.exchange** - 评估任务交换机
3. **notification.exchange** - 通知消息交换机
4. **delay.exchange** - 延迟消息交换机
5. **dead.letter.exchange** - 死信交换机

## 四、消息流程图

```
1. 患者Excel导入流程：
   用户上传Excel → ImportTaskService.createImportTask()
   → sendPatientImportMessage() → patient.import.queue
   → MessageConsumerService处理
   → 成功后自动触发:
      a) sendPatientMigrationMessage() → patient.migration.queue
      b) sendComprehensiveAssessmentMessage() → comprehensive.assessment.queue

2. 综合评估流程：
   sendComprehensiveAssessmentMessage() → comprehensive.assessment.queue
   → PatientAssessmentConsumer处理
   → 执行8个评估（串行或并行）
   → sendDataProcessCompleteNotification() → system.notification.queue

3. 数据迁移流程：
   sendPatientMigrationMessage() → patient.migration.queue
   → MessageConsumerService处理
   → 迁移完成后自动触发:
      sendBatchAssessmentMessage() → batch.assessment.queue

4. 错误重试流程：
   处理失败 → sendDelayMessage() → delay.exchange
   → 延迟后重新路由到原队列
   → 超过3次重试 → dead.letter.queue
```

## 五、重要说明

1. **自动触发机制**：
   - 患者导入成功后自动触发数据迁移和综合评估
   - 数据迁移成功后自动触发批量评估

2. **手动触发接口**：
   - 提供了多个REST API接口用于手动测试各种消息发送

3. **错误处理**：
   - 所有消息处理失败最多重试3次，每次延迟5秒
   - 超过重试次数的消息进入死信队列

4. **并发处理**：
   - 综合评估支持串行（≤50患者）和并行（>50患者）两种模式
   - 并行模式使用10个线程池

5. **监听器位置**：
   - MessageConsumerService：8个@RabbitListener监听方法
   - PatientAssessmentConsumer：1个@RabbitListener监听方法
