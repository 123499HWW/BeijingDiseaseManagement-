# COVID-19诊断分页查询接口文档

本文档包含COVID-19重型和危重型两个诊断接口的详细说明。

## 一、COVID-19重型诊断分页查询接口

### 接口概述
本接口用于分页查询COVID-19重型诊断结果，支持根据诊断结果、满足标准数、严重程度、性别、年龄范围进行条件筛选。

### 基本信息
- **接口地址**: `/api/covid19/page`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求体参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| isSevereType | Boolean | 否 | - | 是否为重型 |
| criteriaCount | Integer | 否 | - | 满足标准数 |
| severityLevel | String | 否 | - | 严重程度等级 |
| gender | String | 否 | - | 性别（男/女） |
| minAge | Integer | 否 | - | 最小年龄 |
| maxAge | Integer | 否 | - | 最大年龄 |
| pageNum | Integer | 否 | 1 | 当前页码 |
| pageSize | Integer | 否 | 10 | 每页记录数 |

### 响应示例

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "records": [
      {
        // covid19_assessment表字段
        "assessmentId": 1,
        "respiratoryFailure": 1,
        "oxygenIndex200To300": 0,
        "oxygenIndexBelow200": 1,
        "fingerOxygenBelow93": 1,
        "respiratoryRateAbove30": 1,
        "lungImagingProgression": 1,
        "clinicalSymptomWorsen": 0,
        "criteriaCount": 4,
        "isSevereType": true,
        "severityLevel": "重型（高危）",
        "assessmentTime": "2024-01-01T10:30:00",
        "assessmentConclusion": "满足4项重型诊断标准",
        "diagnosisBasis": "呼吸衰竭；氧合指数<200mmHg；指氧<93%；呼吸频率>30次/分",
        "recommendedAction": "建议立即收入重症病房",
        // patient_info表字段
        "patientNumber": "P0001",
        "gender": "男",
        "age": 65,
        // covid19_patient_relation表字段
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

### COVID-19重型诊断标准

满足以下任一条即可诊断为重型：
1. **呼吸衰竭**，需要机械通气
2. **氧合指数** 200-300mmHg
3. **氧合指数** <200mmHg  
4. **指氧饱和度** ≤93%(静息状态)
5. **呼吸频率** ≥30次/分
6. **肺部影像学** 24-48小时内病灶进展>50%
7. **临床症状** 进行性加重

### 严重程度分级
- **非重型**：0项标准
- **重型**：1项标准
- **重型（高危）**：2-3项标准
- **重型（极高危）**：≥4项标准

---

## 二、COVID-19危重型诊断分页查询接口

### 接口概述
本接口用于分页查询COVID-19危重型诊断结果，支持根据诊断结果、满足标准数、严重程度、性别、年龄范围进行条件筛选。

### 基本信息
- **接口地址**: `/api/covid19-critical/page`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求体参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| isCriticalType | Boolean | 否 | - | 是否为危重型 |
| criteriaCount | Integer | 否 | - | 满足标准数 |
| severityLevel | String | 否 | - | 严重程度等级 |
| gender | String | 否 | - | 性别（男/女） |
| minAge | Integer | 否 | - | 最小年龄 |
| maxAge | Integer | 否 | - | 最大年龄 |
| pageNum | Integer | 否 | 1 | 当前页码 |
| pageSize | Integer | 否 | 10 | 每页记录数 |

### 响应示例

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "records": [
      {
        // covid19_critical_assessment表字段
        "criticalId": 1,
        "respiratoryFailureNeedVentilator": 1,
        "shock": 1,
        "otherOrganFailure": 0,
        "criteriaCount": 2,
        "isCriticalType": true,
        "severityLevel": "危重型（高危）",
        "assessmentTime": "2024-01-01T10:30:00",
        "assessmentConclusion": "满足2项危重型诊断标准",
        "diagnosisBasis": "呼吸衰竭需要机械通气；出现休克",
        "recommendedAction": "立即收入ICU，启动危重症救治流程",
        // patient_info表字段
        "patientNumber": "P0001",
        "gender": "男",
        "age": 70,
        // covid19_critical_patient_relation表字段
        "patientId": 1,
        "assessmentDate": "2024-01-01 10:30:00",
        "assessmentType": "ADMISSION"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### COVID-19危重型诊断标准

满足以下任一条即可诊断为危重型：
1. **呼吸衰竭**且需要机械通气
2. **出现休克**
3. **合并其他器官功能衰竭**需ICU监护治疗

### 严重程度分级
- **非危重型**：0项标准
- **危重型**：1项标准
- **危重型（高危）**：2项标准
- **危重型（极危重）**：≥3项标准

---

## 三、通用使用示例

### 1. 查询所有重型患者
```bash
POST /api/covid19/page
Content-Type: application/json

{
  "isSevereType": true
}
```

### 2. 查询所有危重型患者
```bash
POST /api/covid19-critical/page
Content-Type: application/json

{
  "isCriticalType": true
}
```

### 3. 查询60岁以上男性重型高危患者
```bash
POST /api/covid19/page
Content-Type: application/json

{
  "severityLevel": "重型（高危）",
  "gender": "男",
  "minAge": 60
}
```

### 4. 查询满足2项以上危重标准的患者
```bash
POST /api/covid19-critical/page
Content-Type: application/json

{
  "criteriaCount": 2,
  "pageNum": 1,
  "pageSize": 20
}
```

---

## 四、错误处理

### 错误响应格式
```json
{
  "success": false,
  "message": "错误信息",
  "data": null
}
```

### 常见错误

| 错误类型 | 错误信息 | 原因 |
|----------|----------|------|
| 参数错误 | 满足标准数不能小于0 | criteriaCount < 0 |
| 参数错误 | 严重程度等级必须是：非重型、重型、重型（高危）或重型（极高危） | severityLevel值不正确 |
| 参数错误 | 严重程度等级必须是：非危重型、危重型、危重型（高危）或危重型（极危重） | severityLevel值不正确 |
| 参数错误 | 性别必须是：男或女 | gender值不正确 |
| 参数错误 | 最小年龄不能小于0 | minAge < 0 |
| 参数错误 | 最大年龄不能小于0 | maxAge < 0 |
| 参数错误 | 最小年龄不能大于最大年龄 | minAge > maxAge |

---

## 五、注意事项

### 1. 临床意义
- **重型患者**：需要严密监护，预防病情进展
- **危重型患者**：需要ICU治疗，全力救治
- 动态监测评估结果对判断病情演变有重要意义

### 2. 数据限制
- 氧合指数需要从physical_examination_detail表获取
- 部分临床症状判断依赖主诉和现病史文本分析
- 影像学进展需要人工判断，系统可能无法自动识别

### 3. 分页限制
- 建议每页记录数不超过100条
- 页码从1开始，不是从0开始

### 4. 数据完整性
- 三表关联查询（主表 + relation表 + patient_info表）
- 返回的患者编号可能为空（如果患者信息被删除）
- 使用is_deleted标识软删除状态

### 5. 评估类型说明
- **ADMISSION**：入院评估
- **FOLLOW_UP**：随访评估  
- **DISCHARGE**：出院评估
