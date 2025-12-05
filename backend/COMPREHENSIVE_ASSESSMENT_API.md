# 综合评估查询API文档

## 概述
综合评估查询接口提供了一个统一的入口，用于查询患者的所有评估结果，包括CURB-65、PSI、CPIS、qSOFA、SOFA评分，以及COVID-19重型/危重型诊断和重症肺炎诊断结果。

## 性能要求
- **响应时间**：2秒内
- **最大分页大小**：100条/页
- **优化措施**：
  - 数据库索引优化
  - 子查询获取最新记录
  - 分页限制
  - 查询条件优化

## 接口列表

### 1. 综合评估分页查询
**接口地址**：`POST /api/comprehensive-assessment/page`

**请求参数**：
```json
{
  // 基础分页参数
  "pageNum": 1,
  "pageSize": 10,
  
  // 患者基本信息
  "patientNumber": "P001",  // 患者编号（支持模糊查询）
  "gender": "男",           // 性别
  "minAge": 18,             // 最小年龄
  "maxAge": 80,             // 最大年龄
  
  // CURB-65评分条件
  "curbTotalScore": 3,      // CURB-65总分
  "curbRiskLevel": "高风险", // CURB-65风险等级
  "minCurbScore": 0,        // 最小CURB-65分数
  "maxCurbScore": 5,        // 最大CURB-65分数
  
  // COVID-19重型诊断条件
  "covid19IsSevereType": true,        // 是否为COVID-19重型
  "covid19SeverityLevel": "重型",      // COVID-19重型严重程度
  
  // COVID-19危重型诊断条件
  "covid19IsCriticalType": true,           // 是否为COVID-19危重型
  "covid19CriticalSeverityLevel": "危重型", // COVID-19危重型严重程度
  
  // CPIS评分条件
  "cpisTotalScore": 6,      // CPIS总分
  "cpisRiskLevel": "高风险", // CPIS风险等级
  "minCpisScore": 0,        // 最小CPIS分数
  "maxCpisScore": 10,       // 最大CPIS分数
  
  // PSI评分条件
  "psiTotalScore": 90,      // PSI总分
  "psiRiskClass": "III级",  // PSI风险等级
  "minPsiScore": 0,         // 最小PSI分数
  "maxPsiScore": 400,       // 最大PSI分数
  
  // qSOFA评分条件
  "qsofaTotalScore": 2,     // qSOFA总分
  "qsofaRiskLevel": "高风险", // qSOFA风险等级
  "minQsofaScore": 0,       // 最小qSOFA分数
  "maxQsofaScore": 3,       // 最大qSOFA分数
  
  // 重症肺炎诊断条件
  "isSeverePneumonia": true, // 是否为重症肺炎
  
  // SOFA评分条件
  "sofaTotalScore": 8,       // SOFA总分
  "sofaSeverityLevel": "重度", // SOFA严重程度
  "minSofaScore": 0,         // 最小SOFA分数
  "maxSofaScore": 24         // 最大SOFA分数
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        // 患者基本信息
        "patientId": 1,
        "patientNumber": "P001",
        "gender": "男",
        "age": 65,
        
        // CURB-65评分
        "curbId": 101,
        "curbTotalScore": 3,
        "curbRiskLevel": "高风险",
        "curbAssessmentDate": "2024-01-15T10:30:00",
        
        // COVID-19重型诊断
        "covid19AssessmentId": 201,
        "covid19IsSevereType": true,
        "covid19CriteriaCount": 2,
        "covid19SeverityLevel": "重型（中危）",
        "covid19AssessmentDate": "2024-01-15T11:00:00",
        
        // COVID-19危重型诊断
        "covid19CriticalAssessmentId": 301,
        "covid19IsCriticalType": false,
        "covid19CriticalCriteriaCount": 0,
        "covid19CriticalSeverityLevel": "非危重型",
        "covid19CriticalAssessmentDate": "2024-01-15T11:30:00",
        
        // CPIS评分
        "cpisId": 401,
        "cpisTotalScore": 4,
        "cpisRiskLevel": "低风险",
        "cpisAssessmentDate": "2024-01-15T12:00:00",
        
        // PSI评分
        "psiId": 501,
        "psiTotalScore": 90,
        "psiRiskClass": "III级",
        "psiAssessmentDate": "2024-01-15T12:30:00",
        
        // qSOFA评分
        "qsofaAssessmentId": 601,
        "qsofaTotalScore": 2,
        "qsofaRiskLevel": "高风险",
        "qsofaAssessmentDate": "2024-01-15T13:00:00",
        
        // 重症肺炎诊断
        "severePneumoniaId": 701,
        "isSeverePneumonia": true,
        "majorCriteriaCount": 1,
        "minorCriteriaCount": 3,
        "severePneumoniaAssessmentDate": "2024-01-15T13:30:00",
        
        // SOFA评分
        "sofaAssessmentId": 801,
        "sofaTotalScore": 8,
        "sofaSeverityLevel": "中度",
        "sofaAssessmentDate": "2024-01-15T14:00:00",
        
        // 最新评估时间
        "latestAssessmentDate": "2024-01-15T14:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 2. 获取单个患者综合评估
**接口地址**：`GET /api/comprehensive-assessment/patient/{patientId}`

**路径参数**：
- `patientId`：患者ID

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 同上述记录格式
  }
}
```

### 3. 获取查询条件选项
**接口地址**：`GET /api/comprehensive-assessment/query-options`

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "genderOptions": ["男", "女"],
    "curbRiskLevels": ["低风险", "中风险", "高风险"],
    "covid19SeverityLevels": ["非重型", "重型", "重型（中危）", "重型（高危）"],
    "covid19CriticalSeverityLevels": ["非危重型", "危重型", "危重型（中危）", "危重型（高危）"],
    "cpisRiskLevels": ["低风险", "高风险"],
    "psiRiskClasses": ["I级", "II级", "III级", "IV级", "V级"],
    "qsofaRiskLevels": ["低风险", "高风险"],
    "sofaSeverityLevels": ["轻度", "中度", "重度", "极重度"],
    "scoreRanges": {
      "curb": {"min": 0, "max": 5},
      "cpis": {"min": 0, "max": 5},
      "psi": {"min": 0, "max": 400},
      "qsofa": {"min": 0, "max": 3},
      "sofa": {"min": 0, "max": 24}
    }
  }
}
```

## 性能优化建议

### 1. 查询优化
- 使用具体的查询条件，避免全表扫描
- 限制分页大小，推荐每页10-20条
- 使用精确匹配而非模糊查询（患者编号除外）

### 2. 索引使用
确保以下索引已创建：
- 患者表：`patient_number`, `(gender, age)`, `is_deleted`
- 各评估表：`(total_score, risk_level)`, `(is_deleted, created_at)`
- 关联表：`patient_id`, 主键字段

### 3. 缓存策略
- 对热点数据进行缓存
- 查询条件选项可缓存24小时
- 患者综合评估结果可缓存5分钟

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误（如年龄/分数范围不合理） |
| 404 | 未找到数据 |
| 500 | 服务器内部错误 |
| 504 | 查询超时（超过2秒） |

## 注意事项

1. **数据一致性**：每个评估表只返回最新的一条记录
2. **NULL处理**：如果某个评估没有记录，对应字段返回NULL
3. **性能监控**：系统会记录每次查询的执行时间
4. **分页限制**：最大分页大小为100条
5. **查询超时**：查询超过2秒会记录警告日志

## 使用示例

### 示例1：查询所有高风险患者
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "curbRiskLevel": "高风险",
  "qsofaRiskLevel": "高风险"
}
```

### 示例2：查询特定年龄段的重症患者
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "minAge": 60,
  "maxAge": 80,
  "isSeverePneumonia": true,
  "covid19IsSevereType": true
}
```

### 示例3：按分数范围查询
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "minCurbScore": 3,
  "maxCurbScore": 5,
  "minSofaScore": 6,
  "maxSofaScore": 24
}
```
