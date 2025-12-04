# 重症肺炎诊断分页查询接口文档

## 接口概述
本接口用于分页查询重症肺炎诊断结果，支持根据诊断结果、标准满足数、性别、年龄范围进行条件筛选。

## 接口信息

### 基本信息
- **接口地址**: `/api/severe-pneumonia/page`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求体参数

请求体为JSON格式，包含以下字段：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| isSeverePneumonia | Boolean | 否 | - | 是否为重症肺炎 |
| majorCriteriaCount | Integer | 否 | - | 主要标准满足数 |
| minorCriteriaCount | Integer | 否 | - | 次要标准满足数 |
| gender | String | 否 | - | 性别（男/女） |
| minAge | Integer | 否 | - | 最小年龄 |
| maxAge | Integer | 否 | - | 最大年龄 |
| pageNum | Integer | 否 | 1 | 当前页码 |
| pageSize | Integer | 否 | 10 | 每页记录数 |

### 请求体示例

```json
{
  "isSeverePneumonia": true,
  "majorCriteriaCount": 1,
  "minorCriteriaCount": 3,
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
        // severe_pneumonia_diagnosis表的所有字段
        "diagnosisId": 1,
        "isSeverePneumonia": true,
        "mechanicalVentilation": 1,
        "vasopressors": 1,
        "respiratoryRate": 1,
        "oxygenationIndex": 1,
        "multilobeInfiltrates": 1,
        "confusion": 0,
        "bloodUreaNitrogen": 1,
        "hypotension": 0,
        "majorCriteriaCount": 2,
        "minorCriteriaCount": 3,
        "diagnosticBasis": "符合重症肺炎诊断标准",
        "diagnosisConclusion": "重症肺炎",
        "recommendedAction": "ICU治疗",
        // patient_info表的字段
        "patientNumber": "P0001",
        "gender": "男",
        "age": 65,
        // severe_pneumonia_patient_relation表的字段
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
| diagnosisId | Long | 诊断ID |
| isSeverePneumonia | Boolean | 是否为重症肺炎 |
| mechanicalVentilation | Integer | 机械通气（0/1） |
| vasopressors | Integer | 血管活性药物（0/1） |
| respiratoryRate | Integer | 呼吸频率≥30（0/1） |
| oxygenationIndex | Integer | 氧合指数≤250（0/1） |
| multilobeInfiltrates | Integer | 多肺叶浸润（0/1） |
| confusion | Integer | 意识障碍（0/1） |
| bloodUreaNitrogen | Integer | 血尿素氮≥7mmol/L（0/1） |
| hypotension | Integer | 低血压需要液体复苏（0/1） |
| majorCriteriaCount | Integer | 主要标准满足数 |
| minorCriteriaCount | Integer | 次要标准满足数 |
| diagnosticBasis | String | 诊断依据 |
| diagnosisConclusion | String | 诊断结论 |
| recommendedAction | String | 建议处理措施 |
| patientNumber | String | 患者编号 |
| gender | String | 性别 |
| age | Integer | 年龄 |
| patientId | Long | 患者ID |
| assessmentDate | String | 评估时间 |
| assessmentType | String | 评估类型 |

## 使用示例

### 1. 查询所有诊断结果（默认分页）
```bash
POST /api/severe-pneumonia/page
Content-Type: application/json

{}
```

### 2. 查询重症肺炎患者
```bash
POST /api/severe-pneumonia/page
Content-Type: application/json

{
  "isSeverePneumonia": true
}
```

### 3. 查询满足主要标准的患者
```bash
POST /api/severe-pneumonia/page
Content-Type: application/json

{
  "majorCriteriaCount": 1
}
```

### 4. 查询60岁以上男性患者
```bash
POST /api/severe-pneumonia/page
Content-Type: application/json

{
  "gender": "男",
  "minAge": 60
}
```

### 5. 完整组合查询
```bash
POST /api/severe-pneumonia/page
Content-Type: application/json

{
  "isSeverePneumonia": true,
  "majorCriteriaCount": 1,
  "minorCriteriaCount": 3,
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

1. **主要标准满足数错误**
   - 错误信息：主要标准满足数不能小于0
   - 原因：majorCriteriaCount参数为负数

2. **次要标准满足数错误**
   - 错误信息：次要标准满足数不能小于0
   - 原因：minorCriteriaCount参数为负数

3. **性别参数错误**
   - 错误信息：性别必须是：男或女
   - 原因：gender参数值不正确

4. **年龄参数错误**
   - 错误信息：最小年龄不能小于0 / 最大年龄不能小于0
   - 原因：年龄参数为负数

5. **年龄范围错误**
   - 错误信息：最小年龄不能大于最大年龄
   - 原因：minAge > maxAge

## 注意事项

1. **诊断标准**
   - 主要标准（满足1项）：机械通气、血管活性药物
   - 次要标准（满足3项）：呼吸频率≥30、氧合指数≤250、多肺叶浸润、意识障碍、血尿素氮≥7、低血压

2. **分页限制**
   - 建议每页记录数不超过100条
   - 页码从1开始，不是从0开始

3. **数据完整性**
   - 返回的患者编号可能为空（如果患者信息被删除）
   - 三表关联查询，确保数据一致性
