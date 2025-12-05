# qSOFA评分分页查询接口文档

## 接口概述
本接口用于分页查询qSOFA评分结果，支持根据总分、风险等级、性别、年龄范围进行条件筛选。qSOFA用于快速识别脓毒症高风险患者。

## 接口信息

### 基本信息
- **接口地址**: `/api/qsofa/page`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求体参数

请求体为JSON格式，包含以下字段：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| totalScore | Integer | 否 | - | 总分（0-3分） |
| riskLevel | String | 否 | - | 风险等级（低风险/高风险） |
| minScore | Integer | 否 | - | 最小总分（0-3分） |
| maxScore | Integer | 否 | - | 最大总分（0-3分） |
| gender | String | 否 | - | 性别（男/女） |
| minAge | Integer | 否 | - | 最小年龄 |
| maxAge | Integer | 否 | - | 最大年龄 |
| pageNum | Integer | 否 | 1 | 当前页码 |
| pageSize | Integer | 否 | 10 | 每页记录数 |

### 请求体示例

```json
{
  "totalScore": 2,
  "riskLevel": "高风险",
  "minScore": 2,
  "maxScore": 3,
  "gender": "男",
  "minAge": 60,
  "maxAge": 80,
  "pageNum": 1,
  "pageSize": 10
}
```

## 响应格式

### 成功响应

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "records": [
      {
        // qsofa_assessment表的所有字段
        "qsofaId": 1,
        "consciousnessDisorder": 1,
        "respiratoryRate": 1,
        "systolicBp": 1,
        "totalScore": 3,
        "riskLevel": "高风险",
        "assessmentTime": "2024-01-01T10:30:00",
        "assessmentConclusion": "qSOFA评分3分（≥2分），提示脓毒症高风险",
        "recommendedAction": "建议立即进行进一步评估",
        // patient_info表的字段
        "patientNumber": "P0001",
        "gender": "男",
        "age": 65,
        // qsofa_patient_relation表的字段
        "patientId": 1,
        "assessmentDate": "2024-01-01 10:30:00",
        "assessmentType": "ADMISSION"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| qsofaId | Long | qSOFA评分ID |
| consciousnessDisorder | Integer | 意识障碍（0/1） |
| respiratoryRate | Integer | 呼吸频率≥22次/分（0/1） |
| systolicBp | Integer | 收缩压≤90mmHg（0/1） |
| totalScore | Integer | 总分（0-3分） |
| riskLevel | String | 风险等级（低风险/高风险） |
| assessmentTime | LocalDateTime | 评估时间 |
| assessmentConclusion | String | 评估结论 |
| recommendedAction | String | 建议处理措施 |
| patientNumber | String | 患者编号 |
| gender | String | 性别 |
| age | Integer | 年龄 |
| patientId | Long | 患者ID |
| assessmentDate | String | 评估时间 |
| assessmentType | String | 评估类型（ADMISSION/FOLLOW_UP/DISCHARGE） |

## qSOFA评分标准

### 评分项目（每项1分，总分0-3分）
1. **意识障碍**：Glasgow昏迷评分<15分
2. **呼吸频率**：≥22次/分
3. **收缩压**：≤90mmHg

### 风险分级
- **<2分**：低风险
- **≥2分**：高风险（脓毒症高风险）

## 使用示例

### 1. 查询所有评分结果（默认分页）
```bash
POST /api/qsofa/page
Content-Type: application/json

{}
```

### 2. 查询高风险患者
```bash
POST /api/qsofa/page
Content-Type: application/json

{
  "riskLevel": "高风险"
}
```

### 3. 查询总分为2-3分的患者
```bash
POST /api/qsofa/page
Content-Type: application/json

{
  "minScore": 2,
  "maxScore": 3
}
```

### 4. 查询60岁以上男性高风险患者
```bash
POST /api/qsofa/page
Content-Type: application/json

{
  "riskLevel": "高风险",
  "gender": "男",
  "minAge": 60
}
```

### 5. 完整组合查询
```bash
POST /api/qsofa/page
Content-Type: application/json

{
  "totalScore": 2,
  "riskLevel": "高风险",
  "gender": "女",
  "minAge": 50,
  "maxAge": 70,
  "pageNum": 1,
  "pageSize": 20
}
```

## 错误处理

### 错误响应格式
```json
{
  "success": false,
  "message": "错误信息",
  "data": null
}
```

### 常见错误

1. **总分范围错误**
   - 错误信息：总分必须在0-3之间
   - 原因：totalScore参数不在0-3范围内

2. **最小/最大总分范围错误**
   - 错误信息：最小总分必须在0-3之间 / 最大总分必须在0-3之间
   - 原因：minScore或maxScore参数不在0-3范围内

3. **总分范围逻辑错误**
   - 错误信息：最小总分不能大于最大总分
   - 原因：minScore > maxScore

4. **风险等级错误**
   - 错误信息：风险等级必须是：低风险或高风险
   - 原因：riskLevel参数值不正确

5. **性别参数错误**
   - 错误信息：性别必须是：男或女
   - 原因：gender参数值不正确

6. **年龄参数错误**
   - 错误信息：最小年龄不能小于0 / 最大年龄不能小于0
   - 原因：年龄参数为负数

7. **年龄范围错误**
   - 错误信息：最小年龄不能大于最大年龄
   - 原因：minAge > maxAge

## 注意事项

1. **qSOFA的临床意义**
   - qSOFA评分≥2分提示脓毒症高风险
   - 应立即进行进一步评估和干预
   - 包括完整SOFA评分、乳酸检测、血培养等

2. **分页限制**
   - 建议每页记录数不超过100条
   - 页码从1开始，不是从0开始

3. **数据完整性**
   - 返回的患者编号可能为空（如果患者信息被删除）
   - 三表关联查询，确保数据一致性
