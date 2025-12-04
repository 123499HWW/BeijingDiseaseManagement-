# 综合查询接口修复说明

## 问题描述
调用 `/api/comprehensive-assessment/page` 接口时，查询不到任何结果。经过日志分析发现：
1. 前端发送了大量默认值（字符串字段为"string"，数字字段为0）
2. 所有条件使用AND连接，导致查询条件过于严格
3. 即使不需要的条件也参与了查询，导致没有匹配的数据

## 修复方案

### 1. Controller层参数清理
在 `ComprehensiveAssessmentController` 中添加了 `cleanInvalidParameters` 方法：

**清理规则**：
- **字符串类型**：如果值为"string"或空字符串，设置为null
- **整数类型**：如果值为0且没有其他相关条件，设置为null
- **范围查询**：如果最小值和最大值都是0，设置为null
- **年龄范围**：如果是0，设置为null

### 2. SQL查询优化
MyBatis的动态SQL已经正确配置：
- 使用 `<if test='条件 != null'>` 判断
- null值不会参与SQL拼接
- 只有有效的查询条件才会加入WHERE子句

## 修复后的效果

### 修复前
```sql
WHERE p.is_deleted = 0 
  AND p.patient_number LIKE '%string%'  -- 错误：不应该查询"string"
  AND p.gender = 'string'               -- 错误：不应该查询"string"
  AND p.age >= 0                        -- 错误：不应该限制年龄>=0
  AND p.age <= 0                        -- 错误：不应该限制年龄<=0
  AND curb.total_score = 0              -- 错误：不是所有都要查询0分
  -- ... 大量无效条件
```

### 修复后
```sql
WHERE p.is_deleted = 0
  -- 只包含实际需要的查询条件
```

## 正确的请求示例

### ✅ 正确示例1：查询所有数据
```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

### ✅ 正确示例2：按条件查询
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "gender": "男",
  "minAge": 60,
  "curbRiskLevel": "高风险"
}
```

### ❌ 错误示例：发送默认值
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "patientNumber": "string",     // 错误：不要发送"string"
  "gender": "string",             // 错误：不要发送"string"
  "curbTotalScore": 0,           // 错误：如果不需要查询0分，不要发送
  "minAge": 0,                   // 错误：不要发送0
  "maxAge": 0                    // 错误：不要发送0
}
```

## 前端建议

1. **不要发送默认值**
   - 字符串字段：如果为空或"string"，不要包含在请求体中
   - 数字字段：如果为0且不是有意查询0，不要包含在请求体中
   - 布尔字段：如果不需要筛选，不要包含在请求体中

2. **只发送需要的条件**
   ```javascript
   // 构建请求体时，过滤无效值
   const params = {};
   if (patientNumber && patientNumber !== 'string') {
     params.patientNumber = patientNumber;
   }
   if (gender && gender !== 'string') {
     params.gender = gender;
   }
   if (minAge && minAge > 0) {
     params.minAge = minAge;
   }
   // ... 其他条件类似
   ```

3. **使用测试文件**
   参考 `comprehensive-assessment-test.json` 中的测试用例

## 性能注意事项

1. **无条件查询**：会返回所有患者的最新评估记录，数据量可能较大
2. **建议添加条件**：至少添加一些筛选条件以提高查询效率
3. **分页限制**：每页最大100条记录

## 验证步骤

1. 重启应用程序
2. 使用正确的请求体调用接口
3. 查看日志中"清理后的查询条件"，确认无效参数已被清理
4. 验证返回结果是否正确
