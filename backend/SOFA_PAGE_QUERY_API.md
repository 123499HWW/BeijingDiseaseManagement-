# SOFA评分分页查询接口文档

## 接口概述
本接口用于分页查询SOFA（序贯器官功能衰竭评估）评分结果，支持根据总分、严重程度、性别、年龄范围进行条件筛选。SOFA用于评估器官功能衰竭的严重程度。

## 接口信息

### 基本信息
- **接口地址**: `/api/sofa/page`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求体参数

请求体为JSON格式，包含以下字段：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| totalScore | Integer | 否 | - | 总分（0-24分） |
| severityLevel | String | 否 | - | 严重程度等级 |
| minTotalScore | Integer | 否 | - | 最小总分（0-24分） |
| maxTotalScore | Integer | 否 | - | 最大总分（0-24分） |
| gender | String | 否 | - | 性别（男/女） |
| minAge | Integer | 否 | - | 最小年龄 |
| maxAge | Integer | 否 | - | 最大年龄 |
| pageNum | Integer | 否 | 1 | 当前页码 |
| pageSize | Integer | 否 | 10 | 每页记录数 |

### 请求体示例

```json
{
  "totalScore": 10,
  "severityLevel": "中度",
  "minTotalScore": 8,
  "maxTotalScore": 11,
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
        // sofa_assessment表的所有字段
        "sofaId": 1,
        "respiratoryScore": 2,
        "coagulationScore": 1,
        "liverScore": 2,
        "cardiovascularScore": 3,
        "neurologyScore": 0,
        "renalScore": 2,
        "totalScore": 10,
        "organFailureCount": 4,
        "severityLevel": "中度",
        "assessmentTime": "2024-01-01T10:30:00",
        "assessmentConclusion": "SOFA评分10分，4个器官系统受累，病情严重",
        "recommendedAction": "建议入住ICU，密切监测器官功能",
        // patient_info表的字段
        "patientNumber": "P0001",
        "gender": "男",
        "age": 65,
        // sofa_patient_relation表的字段
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
| sofaId | Long | SOFA评分ID |
| respiratoryScore | Integer | 呼吸系统评分（0-4分） |
| coagulationScore | Integer | 凝血系统评分（0-4分） |
| liverScore | Integer | 肝脏系统评分（0-4分） |
| cardiovascularScore | Integer | 心血管系统评分（0-4分） |
| neurologyScore | Integer | 神经系统评分（0-4分） |
| renalScore | Integer | 肾脏系统评分（0-4分） |
| totalScore | Integer | 总分（0-24分） |
| organFailureCount | Integer | 器官衰竭数量 |
| severityLevel | String | 严重程度等级 |
| assessmentTime | LocalDateTime | 评估时间 |
| assessmentConclusion | String | 评估结论 |
| recommendedAction | String | 建议处理措施 |
| patientNumber | String | 患者编号 |
| gender | String | 性别 |
| age | Integer | 年龄 |
| patientId | Long | 患者ID |
| assessmentDate | String | 评估时间 |
| assessmentType | String | 评估类型 |

## SOFA评分标准

### 器官系统评分（每个系统0-4分）

#### 1. 呼吸系统（基于PaO2/FiO2）
- 0分：≥400
- 1分：<400
- 2分：<300
- 3分：<200（机械通气）
- 4分：<100（机械通气）

#### 2. 凝血系统（基于血小板计数）
- 0分：≥150×10^9/L
- 1分：<150×10^9/L
- 2分：<100×10^9/L
- 3分：<50×10^9/L
- 4分：<20×10^9/L

#### 3. 肝脏系统（基于总胆红素）
- 0分：<20μmol/L
- 1分：20-32μmol/L
- 2分：33-101μmol/L
- 3分：102-204μmol/L
- 4分：>204μmol/L

#### 4. 心血管系统（基于血压和血管活性药物）
- 0分：MAP≥70mmHg
- 1分：MAP<70mmHg
- 2分：多巴胺≤5μg/kg/min或多巴酚丁胺（任何剂量）
- 3分：多巴胺>5μg/kg/min或肾上腺素≤0.1μg/kg/min
- 4分：多巴胺>15μg/kg/min或肾上腺素>0.1μg/kg/min

#### 5. 神经系统（基于格拉斯哥昏迷评分GCS）
- 0分：GCS 15
- 1分：GCS 13-14
- 2分：GCS 10-12
- 3分：GCS 6-9
- 4分：GCS <6

#### 6. 肾脏系统（基于肌酐）
- 0分：<110μmol/L
- 1分：110-170μmol/L
- 2分：171-299μmol/L
- 3分：300-440μmol/L或尿量<500ml/d
- 4分：>440μmol/L或尿量<200ml/d

### 严重程度分级
- **0-3分**：正常或轻微
- **4-7分**：轻度
- **8-11分**：中度
- **12-14分**：重度
- **≥15分**：极重度

## 使用示例

### 1. 查询所有评分结果（默认分页）
```bash
POST /api/sofa/page
Content-Type: application/json

{}
```

### 2. 查询中度及以上患者
```bash
POST /api/sofa/page
Content-Type: application/json

{
  "minTotalScore": 8
}
```

### 3. 查询特定严重程度
```bash
POST /api/sofa/page
Content-Type: application/json

{
  "severityLevel": "重度"
}
```

### 4. 查询60岁以上男性重症患者
```bash
POST /api/sofa/page
Content-Type: application/json

{
  "minTotalScore": 12,
  "gender": "男",
  "minAge": 60
}
```

### 5. 完整组合查询
```bash
POST /api/sofa/page
Content-Type: application/json

{
  "totalScore": 10,
  "severityLevel": "中度",
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
   - 错误信息：总分必须在0-24之间
   - 原因：totalScore参数不在0-24范围内

2. **最小/最大总分范围错误**
   - 错误信息：最小总分必须在0-24之间 / 最大总分必须在0-24之间
   - 原因：minTotalScore或maxTotalScore参数不在0-24范围内

3. **总分范围逻辑错误**
   - 错误信息：最小总分不能大于最大总分
   - 原因：minTotalScore > maxTotalScore

4. **严重程度错误**
   - 错误信息：严重程度必须是：正常或轻微、轻度、中度、重度或极重度
   - 原因：severityLevel参数值不正确

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

1. **SOFA评分的临床意义**
   - SOFA评分用于评估器官功能障碍的严重程度
   - 评分越高，器官功能障碍越严重，预后越差
   - 动态监测SOFA评分变化对判断病情演变有重要意义

2. **数据限制**
   - 神经系统评分（GCS）在当前系统中可能无法获取
   - 某些血管活性药物的使用情况需要从medication_detail表获取

3. **分页限制**
   - 建议每页记录数不超过100条
   - 页码从1开始，不是从0开始

4. **数据完整性**
   - 返回的患者编号可能为空（如果患者信息被删除）
   - 三表关联查询，确保数据一致性
