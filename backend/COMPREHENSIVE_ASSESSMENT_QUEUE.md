# 患者信息导入后自动评估功能实现文档

## 功能概述

实现了患者信息导入后自动通过RabbitMQ队列依次完成8个评估表的数据分析，包括：
- curb_assessment_result (CURB-65评分)
- psi_assessment_result (PSI评分)
- cpis_assessment_result (CPIS评分)
- qsofa_assessment (qSOFA评分)
- sofa_assessment (SOFA评分)
- covid19_assessment (COVID-19重型诊断)
- covid19_critical_assessment (COVID-19危重型诊断)
- severe_pneumonia_diagnosis (重症肺炎诊断)

## 架构设计

### 1. 消息队列架构

```
患者导入 -> ImportTaskService -> RabbitMQ综合评估队列 -> PatientAssessmentConsumer -> 8个评估服务
```

### 2. 核心组件

#### 2.1 队列配置
- **交换机**: `assessment.exchange`
- **队列**: `comprehensive.assessment.queue`
- **路由键**: `comprehensive.assessment`
- **消息TTL**: 60分钟（综合评估需要更长时间）

#### 2.2 消息流程

1. **患者导入触发**
   - ImportTaskService在患者导入成功后自动发送评估消息
   - 提取成功导入的患者ID列表
   - 发送到综合评估队列

2. **消息消费处理**
   - PatientAssessmentConsumer监听队列
   - 接收患者ID列表
   - 调用PatientBatchAssessmentService执行评估

3. **批量评估执行**
   - 串行模式：按顺序执行8个评估（患者数≤50）
   - 并行模式：并发执行8个评估（患者数>50）

## 实现细节

### 1. ImportTaskService改造

```java
// 患者导入成功后自动触发评估
if (("PATIENT".equals(messageDTO.getImportType()) || "PATIENT_DATA".equals(messageDTO.getImportType())) 
        && task.getSuccessCount() > 0) {
    // 1. 发送数据迁移消息
    messageProducerService.sendPatientMigrationMessage(...);
    
    // 2. 发送综合评估消息
    List<Long> patientIds = getImportedPatientIds(taskId);
    if (!patientIds.isEmpty()) {
        messageProducerService.sendComprehensiveAssessmentMessage(patientIds, userId, userName);
    }
}
```

### 2. PatientAssessmentConsumer消费者

```java
@RabbitListener(queues = RabbitMQConstants.COMPREHENSIVE_ASSESSMENT_QUEUE)
public void handleComprehensiveAssessment(PatientAssessmentMessageDTO messageDTO, 
                                         Channel channel, 
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
    try {
        // 执行综合评估
        performComprehensiveAssessment(messageDTO);
        // 手动确认消息
        channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
        // 错误处理和重试逻辑
    }
}
```

### 3. PatientBatchAssessmentService批量评估服务

提供两种执行模式：
- **串行执行**: `performBatchAssessment()`
- **并行执行**: `performParallelBatchAssessment()`

每个评估的执行逻辑：
1. 先尝试批量评估（调用各服务的performBatchAssessment方法）
2. 如果批量失败，降级为单个患者逐个处理
3. 统计成功和失败数量

### 4. 错误处理机制

- **重试机制**: 最多重试3次，每次延迟5秒
- **死信队列**: 超过重试次数的消息发送到死信队列
- **降级处理**: 批量评估失败时降级为单个处理
- **异常隔离**: 单个患者评估失败不影响其他患者

## 测试接口

### 1. 通过队列触发评估
```
POST /api/assessment-test/trigger-comprehensive-queue
Body: [1, 2, 3] // 患者ID列表
```

### 2. 同步执行评估
```
POST /api/assessment-test/trigger-comprehensive-sync
Body: [1, 2, 3]
Params: parallel=true // 是否并行
```

### 3. 评估所有患者
```
POST /api/assessment-test/trigger-all-patients
Params: maxCount=100
```

### 4. 测试单个患者
```
POST /api/assessment-test/test-single-patient/{patientId}
```

## 性能优化

### 1. 批量处理优化
- 小批量（≤50）使用串行处理，减少线程开销
- 大批量（>50）使用并行处理，提高吞吐量
- 线程池大小：10个固定线程

### 2. 队列配置优化
- 消息预取数量：1（确保负载均衡）
- 并发消费者：3-10（动态调整）
- 队列最大长度：10000条消息

### 3. 超时控制
- 单个评估超时：5分钟
- 整体任务超时：30分钟
- 消息TTL：60分钟

## 监控和日志

### 1. 关键日志点
- 患者导入完成时记录
- 评估消息发送时记录
- 每个评估开始和结束记录
- 异常和重试记录

### 2. 统计信息
- 总患者数
- 各评估成功/失败数
- 处理耗时
- 重试次数

### 3. 通知机制
- 评估完成后发送系统通知
- 包含详细的统计结果

## 部署注意事项

1. **RabbitMQ配置**
   - 确保RabbitMQ服务正常运行
   - 创建必要的交换机和队列
   - 配置合适的队列参数

2. **数据库索引**
   - 确保patient_info表的主键索引
   - 各评估表的patient_id索引
   - import_task_detail表的相关索引

3. **资源配置**
   - JVM内存：建议至少2GB
   - 数据库连接池：最少20个连接
   - RabbitMQ连接数：根据并发量调整

## 故障处理

### 1. 队列积压
- 增加消费者数量
- 检查评估服务性能
- 考虑分批处理

### 2. 评估失败
- 查看具体错误日志
- 检查患者数据完整性
- 手动重试失败的患者

### 3. 系统恢复
- 未确认的消息会自动重新投递
- 死信队列中的消息需要手动处理
- 可通过测试接口重新触发评估

## 使用示例

### 1. Excel导入触发自动评估

```bash
# 1. 上传Excel文件
POST /api/import/excel/upload

# 2. 系统自动流程
- 解析Excel数据
- 保存患者信息到数据库
- 发送数据迁移消息
- 发送综合评估消息
- 执行8个评估
- 发送完成通知
```

### 2. 手动批量评估

```bash
# 获取需要评估的患者ID
GET /api/patient/list

# 触发评估
POST /api/assessment-test/trigger-comprehensive-queue
Body: [1, 2, 3, 4, 5]
```

### 3. 查看评估结果

```bash
# 查看CURB-65评估结果
POST /api/assessment/curb/page

# 查看综合评估结果
POST /api/comprehensive-assessment/page
```

## 扩展性

### 1. 添加新评估类型
1. 创建新的评估服务类
2. 在PatientBatchAssessmentService中添加调用
3. 更新评估统计逻辑

### 2. 自定义评估策略
- 可以根据患者类型选择性执行评估
- 可以设置评估优先级
- 可以实现评估结果缓存

### 3. 集成其他系统
- 可以通过Webhook通知外部系统
- 可以导出评估结果到其他格式
- 可以与AI模型集成进行预测

## 版本记录

- v1.0.0 (2024-12-03): 初始版本，实现基本的自动评估功能
  - 支持8种评估类型
  - 实现串行和并行处理
  - 集成RabbitMQ消息队列
  - 提供测试接口
