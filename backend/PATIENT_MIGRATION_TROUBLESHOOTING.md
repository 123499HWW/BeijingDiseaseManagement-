# 患者数据迁移失败排查指南

## 问题描述
在患者Excel导入成功后，第一步"发送数据迁移消息"执行失败。

## 系统架构
```
Excel导入 -> ImportTaskService -> 发送迁移消息 -> RabbitMQ队列 -> MessageConsumerService -> PatientDataMigrationService
```

## 可能的失败原因及解决方案

### 1. RabbitMQ连接问题

**症状**：
- 无法连接到RabbitMQ服务器
- 错误日志显示 "Connection refused" 或 "Connection timeout"

**检查步骤**：
```bash
# 检查RabbitMQ服务是否运行
systemctl status rabbitmq-server
# 或Windows下
sc query RabbitMQ

# 检查端口是否开放
netstat -an | grep 5672
```

**解决方案**：
- 启动RabbitMQ服务
- 检查application.yml中的RabbitMQ配置
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 2. 队列未创建

**症状**：
- 错误日志显示 "Queue not found: patient.migration.queue"

**检查步骤**：
```bash
# 通过RabbitMQ管理界面检查
http://localhost:15672

# 或使用命令行
rabbitmqctl list_queues
```

**解决方案**：
- 重启应用，让RabbitMQConfig自动创建队列
- 手动创建队列（通过管理界面）

### 3. 消息序列化问题

**症状**：
- 错误日志显示序列化异常
- PatientDataMessageDTO无法正确序列化

**检查点**：
- PatientDataMessageDTO是否实现Serializable接口
- 是否有正确的无参构造函数
- 字段是否都可序列化

### 4. 数据迁移服务错误

**症状**：
- 消息发送成功但处理失败
- PatientDataMigrationService抛出异常

**常见原因**：
1. **数据库连接问题**
   - 检查数据库连接池配置
   - 验证表是否存在

2. **数据完整性问题**
   - physical_examination字段为空
   - 患者数据格式不正确

3. **事务问题**
   - 事务超时
   - 死锁

### 5. 消费者未启动

**症状**：
- 消息堆积在队列中
- 没有消费者监听队列

**检查步骤**：
```java
// 确认MessageConsumerService被Spring管理
@Service
@Component // 确保有这些注解

// 确认@RabbitListener注解正确
@RabbitListener(queues = RabbitMQConstants.PATIENT_MIGRATION_QUEUE)
```

## 测试方法

### 1. 手动测试数据迁移
使用新增的测试接口：
```bash
POST http://localhost:8080/api/assessment-test/test-patient-migration
Content-Type: application/json

{
  "userId": "admin",
  "userName": "管理员",
  "totalCount": 10
}
```

### 2. 查看RabbitMQ队列状态
```bash
# 访问管理界面
http://localhost:15672
用户名/密码: guest/guest

# 查看队列
Queues -> patient.migration.queue
```

### 3. 查看日志

**查看关键日志位置**：
```java
// ImportTaskService.java - 发送消息时
log.info("患者数据导入完成，开始触发数据迁移和综合评估");

// MessageProducerService.java - 消息发送
log.info("发送患者数据迁移消息: {}", messageDTO.getMessageId());

// MessageConsumerService.java - 消息接收
log.info("开始处理患者数据迁移消息: messageId={}", messageDTO.getMessageId());

// PatientDataMigrationService.java - 迁移执行
log.info("开始执行患者体检数据迁移，操作人: {}", createdBy);
```

### 4. 监控死信队列
如果消息处理失败3次，会进入死信队列：
```
队列名：dead.letter.queue
```

## 常用排查命令

### 1. 查看所有队列消息数量
```bash
rabbitmqctl list_queues name messages_ready messages_unacknowledged
```

### 2. 清空队列（谨慎使用）
```bash
rabbitmqctl purge_queue patient.migration.queue
```

### 3. 查看消费者
```bash
rabbitmqctl list_consumers
```

## 紧急修复方案

### 方案1：跳过数据迁移
如果数据迁移不是必须的，可以临时注释掉：
```java
// ImportTaskService.java
// 临时注释掉迁移消息发送
// messageProducerService.sendPatientMigrationMessage(...);
```

### 方案2：直接调用迁移服务
绕过消息队列，直接调用服务：
```java
// 直接注入服务
@Autowired
private PatientDataMigrationService migrationService;

// 直接调用
migrationService.migrateAllPatientData("admin");
```

### 方案3：手动处理死信消息
从死信队列重新发送消息到原队列进行重试。

## 预防措施

1. **添加健康检查**
   - 实现RabbitMQ健康检查端点
   - 监控队列深度

2. **完善错误处理**
   - 增加更详细的错误日志
   - 实现消息补偿机制

3. **性能优化**
   - 批量处理患者数据
   - 使用并发消费者

4. **监控告警**
   - 队列堆积告警
   - 处理失败率告警

## 联系支持

如果以上方法都无法解决问题：
1. 收集完整的错误日志
2. 记录重现步骤
3. 检查系统资源（CPU、内存、磁盘）
4. 联系技术支持团队
