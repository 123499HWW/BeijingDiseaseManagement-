# 综合评估触发流程优化总结

## 修改背景
原流程在患者Excel导入后，同时发送数据迁移和综合评估两个消息，可能造成评估在数据迁移完成前执行，导致评估基于未迁移的原始数据。

## 主要变更

### 原流程
```
Excel导入完成
    ├── 发送数据迁移消息 → 迁移队列
    └── 发送综合评估消息 → 评估队列（可能先执行）
```

### 新流程
```
Excel导入完成
    └── 发送数据迁移消息（含taskId） → 迁移队列
            └── 迁移完成后
                    └── 自动触发综合评估消息 → 评估队列
```

## 代码修改详情

### 1. ImportTaskService.java (行142-163)
**修改内容**：
- 移除了直接发送综合评估消息的代码
- 在迁移消息中添加taskId，用于后续获取患者列表
- 将`getImportedPatientIds()`方法从private改为public

```java
// 修改前
if (患者导入成功) {
    发送数据迁移消息();
    发送综合评估消息(patientIds);  // 立即发送
}

// 修改后
if (患者导入成功) {
    PatientDataMessageDTO migrationMessage = new PatientDataMessageDTO(...);
    migrationMessage.setTaskId(taskId);  // 传递taskId
    发送数据迁移消息(migrationMessage);
    // 综合评估将在迁移完成后自动触发
}
```

### 2. PatientDataMessageDTO.java (行46-49)
**新增字段**：
```java
/**
 * 导入任务ID（用于获取导入的患者列表）
 */
private Long taskId;
```

### 3. MessageConsumerService.java (行94-116)
**修改内容**：
- 将原来的CURB-65评估改为综合评估（8个评估）
- 根据taskId获取患者列表
- 添加备用方案：如果没有taskId，获取所有已迁移患者

```java
// 修改前
if (result.getSuccessCount() > 0) {
    发送批量CURB65评估消息();  // 只触发一个评估
}

// 修改后  
if (result.getSuccessCount() > 0) {
    List<Long> patientIds;
    if (messageDTO.getTaskId() != null) {
        patientIds = importTaskService.getImportedPatientIds(messageDTO.getTaskId());
    } else {
        patientIds = patientDataMigrationService.getMigratedPatientIds();
    }
    
    if (!patientIds.isEmpty()) {
        发送综合评估消息(patientIds);  // 触发8个评估
    }
}
```

### 4. PatientDataMigrationService.java (行34-48)
**新增方法**：
```java
public List<Long> getMigratedPatientIds() {
    // 获取所有已迁移的患者ID列表（备用方案）
}
```

## 优势

### ✅ 数据一致性
- 确保评估基于迁移后的结构化数据
- 避免评估和迁移的竞态条件

### ✅ 流程简化  
- 减少ImportTaskService的职责
- 迁移服务自动触发后续流程

### ✅ 容错性
- 提供两种获取患者ID的方式（taskId和备用方案）
- 保持原有的重试和死信队列机制

## 影响范围

### 受影响的消息队列
1. **patient.migration.queue** - 增加了taskId字段
2. **comprehensive.assessment.queue** - 触发时机改变

### 受影响的服务
1. **ImportTaskService** - 简化了逻辑
2. **MessageConsumerService** - 增强了迁移后处理
3. **PatientDataMigrationService** - 新增查询方法

## 测试要点

### 1. 功能测试
- 验证Excel导入后只发送迁移消息
- 验证迁移完成后自动触发综合评估
- 验证8个评估都正常执行

### 2. 数据验证
- 确认评估使用的是迁移后的数据
- 验证taskId正确传递
- 验证患者ID列表正确获取

### 3. 异常测试
- 测试taskId为空时的备用方案
- 测试迁移失败时不触发评估
- 测试部分患者迁移失败的情况

## 部署注意事项

1. **向后兼容**：新代码兼容旧消息格式（taskId可为null）
2. **编译状态**：BUILD SUCCESS - 所有模块编译成功
3. **依赖关系**：无需修改依赖配置

## 后续优化建议

1. **性能优化**
   - 考虑分批处理大量患者
   - 优化数据库查询

2. **监控增强**
   - 添加迁移到评估的时间间隔监控
   - 记录taskId传递链路

3. **配置化**
   - 是否自动触发评估可配置
   - 评估类型可配置（全部或部分）
