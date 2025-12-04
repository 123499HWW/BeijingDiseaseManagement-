# API接口修复总结

## 修复的接口
`POST /api/mq/patient/migration` - 患者数据迁移消息接口

## 问题描述
接口在创建 `PatientDataMessageDTO` 对象时缺少必要的 `totalCount` 参数，导致消息处理时无法获取需要迁移的患者数量信息。

## 修复前的代码
```java
@PostMapping("/patient/migration")
public Result<String> sendPatientMigrationMessage(
        @RequestParam(defaultValue = "SYSTEM") String userId,
        @RequestParam(defaultValue = "SYSTEM") String userName) {
    
    // 问题：使用了只有3个参数的构造函数，缺少totalCount
    PatientDataMessageDTO messageDTO = new PatientDataMessageDTO("MIGRATION", userId, userName);
    messageProducerService.sendPatientMigrationMessage(messageDTO);
    ...
}
```

## 修复后的代码
```java
@PostMapping("/patient/migration")
public Result<String> sendPatientMigrationMessage(
        @RequestParam(defaultValue = "SYSTEM") String userId,
        @RequestParam(defaultValue = "SYSTEM") String userName,
        @RequestParam(defaultValue = "100") Integer totalCount) {  // 新增参数
    
    // 使用正确的4参数构造函数
    PatientDataMessageDTO messageDTO = new PatientDataMessageDTO(userId, userName, "MIGRATION", totalCount);
    messageProducerService.sendPatientMigrationMessage(messageDTO);
    ...
}
```

## 主要变更
1. **新增参数**: 添加了 `totalCount` 请求参数，默认值为 100
2. **修正构造函数调用**: 使用包含4个参数的构造函数 `PatientDataMessageDTO(userId, userName, operationType, totalCount)`
3. **改进日志**: 日志中增加了 totalCount 的记录
4. **优化返回信息**: 返回消息中包含预计处理数量

## 测试方法

### 使用curl测试
```bash
curl -X POST "http://localhost:8080/api/mq/patient/migration?userId=admin&userName=管理员&totalCount=50" \
     -H "Content-Type: application/json"
```

### 使用Postman测试
- **URL**: `POST http://localhost:8080/api/mq/patient/migration`
- **参数**:
  - userId: admin（可选，默认SYSTEM）
  - userName: 管理员（可选，默认SYSTEM）
  - totalCount: 50（可选，默认100）

## 相关接口
为了更方便地测试，还有其他可用的测试接口：

1. **直接测试迁移（新增）**
   ```
   POST /api/assessment-test/test-patient-migration
   ```

2. **直接调用迁移服务**
   ```
   POST /api/migration/patient-examination
   ```

## 验证步骤

1. **检查RabbitMQ队列**
   - 访问 http://localhost:15672
   - 查看 `patient.migration.queue` 队列是否有消息

2. **查看应用日志**
   ```
   # 发送端日志
   grep "发送患者数据迁移消息" app.log
   
   # 接收端日志
   grep "开始处理患者数据迁移消息" app.log
   
   # 执行日志
   grep "开始执行患者体检数据迁移" app.log
   ```

3. **检查数据库**
   - 验证 `physical_examination_detail` 表是否有新数据
   - 验证 `patient_examination_relation` 表是否建立了关联

## 注意事项
1. 确保RabbitMQ服务正在运行
2. 确保数据库连接正常
3. 如果迁移失败，检查死信队列 `dead.letter.queue`
4. totalCount 参数应该根据实际患者数量设置，避免设置过大导致内存问题

## 后续优化建议
1. 添加参数验证，确保 totalCount 在合理范围内（如1-10000）
2. 实现批量处理，避免一次性加载过多数据
3. 添加进度查询接口，实时了解迁移进度
4. 增加迁移结果的详细统计信息返回
