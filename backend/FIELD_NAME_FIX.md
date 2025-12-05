# 字段名错误修复说明

## 错误信息
```
Unknown column 'c19.criteria_count' in 'field list'
Unknown column 'c19c.criteria_count' in 'field list'
```

## 问题原因
在 `ComprehensiveAssessmentMapper.java` 的SQL查询中使用了错误的字段名：
- 使用了 `criteria_count` 
- 实际字段名是 `criteria_met_count`

## 相关表结构

### covid19_assessment 表
| 数据库字段名 | Java属性名 | 说明 |
|------------|-----------|------|
| criteria_met_count | criteriaMetCount | 满足标准数量 |
| is_severe_type | isSevereType | 是否为重型 |
| severity_level | severityLevel | 严重程度 |

### covid19_critical_assessment 表
| 数据库字段名 | Java属性名 | 说明 |
|------------|-----------|------|
| criteria_met_count | criteriaMetCount | 满足标准数量 |
| is_critical_type | isCriticalType | 是否为危重型 |
| severity_level | severityLevel | 严重程度 |

## 修复内容

### 修改位置
文件：`d:\javaCode\hxj\respiratory_infection\common\src\main\java\com\hxj\common\mapper\ComprehensiveAssessmentMapper.java`

### 修改前
```java
// COVID-19重型
"c19.criteria_count as covid19CriteriaCount, " +
// COVID-19危重型  
"c19c.criteria_count as covid19CriticalCriteriaCount, " +
```

### 修改后
```java
// COVID-19重型
"c19.criteria_met_count as covid19CriteriaCount, " +
// COVID-19危重型
"c19c.criteria_met_count as covid19CriticalCriteriaCount, " +
```

## 验证步骤

1. **重启应用程序**
2. **调用接口测试**
   ```json
   POST /api/comprehensive-assessment/page
   {
     "pageNum": 1,
     "pageSize": 10
   }
   ```
3. **确认无SQL错误**

## 其他注意事项

### 字段映射关系
- **数据库字段**：`criteria_met_count`（下划线命名）
- **Java实体属性**：`criteriaMetCount`（驼峰命名）  
- **VO字段**：`covid19CriteriaCount`（前缀+驼峰）
- **SQL别名**：`covid19CriteriaCount`（保持与VO一致）

### MyBatis映射规则
MyBatis会自动进行下划线到驼峰的转换：
- `criteria_met_count` → `criteriaMetCount` ✅
- `criteria_count` → `criteriaCount` ❌（字段不存在）

## 状态
✅ **已修复** - 字段名已更正为正确的 `criteria_met_count`
