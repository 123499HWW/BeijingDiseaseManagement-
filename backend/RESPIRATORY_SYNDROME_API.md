# 呼吸道症候群严重程度评估API文档

## 1. 概述

呼吸道症候群严重程度评估系统用于评估患者呼吸道症候群的严重程度，包括症状、体征、检验、检查、治疗等多个维度的指标评估。

## 2. 数据库表结构

### 2.1 respiratory_syndrome_assessment（呼吸道症候群评估表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| syndrome_id | BIGINT | 症候群评估ID（主键） |
| **症状指标** | | |
| has_dyspnea | TINYINT | 是否有呼吸困难 |
| dyspnea_level | VARCHAR | 呼吸困难程度 |
| has_consciousness_disorder | TINYINT | 是否有意识障碍 |
| consciousness_level | VARCHAR | 意识障碍程度 |
| **体征指标** | | |
| temperature | DECIMAL | 体温（℃） |
| temperature_level | VARCHAR | 体温分级 |
| heart_rate | INT | 心率（bpm） |
| is_tachycardia | TINYINT | 是否心动过速 |
| systolic_bp | INT | 收缩压 |
| diastolic_bp | INT | 舒张压 |
| is_hypotension | TINYINT | 是否低血压 |
| oxygen_saturation | DECIMAL | 血氧饱和度 |
| is_hypoxemia | TINYINT | 是否低氧血症 |
| **检验指标** | | |
| arterial_ph | DECIMAL | 动脉血pH值 |
| is_acidosis | TINYINT | 是否酸中毒 |
| pao2 | DECIMAL | 氧分压 |
| is_hypoxemia_pao2 | TINYINT | 是否低氧血症（PaO2） |
| pao2_fio2_ratio | DECIMAL | 氧合指数 |
| is_oxygenation_disorder | TINYINT | 是否氧合障碍 |
| paco2 | DECIMAL | 二氧化碳分压 |
| is_hypercapnia | TINYINT | 是否高碳酸血症 |
| platelet_count | DECIMAL | 血小板计数 |
| is_thrombocytopenia | TINYINT | 是否血小板减少 |
| blood_urea_nitrogen | DECIMAL | 血尿素氮 |
| is_bun_elevated | TINYINT | 是否BUN升高 |
| creatinine | DECIMAL | 肌酐 |
| is_creatinine_elevated | TINYINT | 是否肌酐升高 |
| total_bilirubin | DECIMAL | 总胆红素 |
| is_bilirubin_elevated | TINYINT | 是否胆红素升高 |
| **检查指标** | | |
| has_chest_ct | TINYINT | 是否进行胸部CT |
| ct_shows_infection | TINYINT | CT是否显示感染 |
| ct_multi_lobe_involved | TINYINT | CT是否多肺叶受累 |
| ct_findings | TEXT | CT检查发现 |
| **治疗指标** | | |
| uses_dopamine | TINYINT | 是否使用多巴胺 |
| uses_dobutamine | TINYINT | 是否使用多巴酚丁胺 |
| uses_norepinephrine | TINYINT | 是否使用去甲肾上腺素 |
| uses_special_antibiotics | TINYINT | 是否使用特殊级抗生素 |
| antibiotics_list | TEXT | 抗生素列表 |
| uses_ventilator | TINYINT | 是否使用呼吸机 |
| ventilator_mode | VARCHAR | 呼吸机模式 |
| **治疗场所** | | |
| in_icu | TINYINT | 是否住ICU |
| icu_admission_date | DATETIME | ICU入住时间 |
| icu_discharge_date | DATETIME | ICU出院时间 |
| **评估结果** | | |
| severity_score | INT | 严重程度评分 |
| severity_level | VARCHAR | 严重程度等级 |
| risk_factors_count | INT | 危险因素数量 |
| assessment_summary | TEXT | 评估总结 |

### 2.2 syndrome_patient_relation（症候群患者关联关系表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| relation_id | BIGINT | 关联关系ID（主键） |
| syndrome_id | BIGINT | 症候群评估ID |
| patient_id | BIGINT | 患者ID |
| patient_number | VARCHAR | 患者编号 |
| diagnosis | VARCHAR | 诊断结果 |
| syndrome_type | VARCHAR | 症候群类型 |
| relation_status | VARCHAR | 关联状态 |
| remark | VARCHAR | 备注信息 |

## 3. API接口

### 3.1 批量评估所有患者

**接口地址:** `POST /api/syndrome/assess-all`

**请求参数:**
```json
{
  "createdBy": "SYSTEM"  // 可选，默认为SYSTEM
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "批量评估成功",
  "data": "呼吸道症候群评估结果: 总数=100, 成功=100（新增=60, 更新=40）, 失败=0"
}
```

### 3.2 单个患者评估

**接口地址:** `POST /api/syndrome/assess/{patientId}`

**请求参数:**
- 路径参数: `patientId` - 患者ID
- 查询参数: `createdBy` - 创建人（可选）

**响应示例:**
```json
{
  "code": 200,
  "message": "评估成功",
  "data": {
    "syndromeId": 1,
    "hasDyspnea": 1,
    "hasConsciousnessDisorder": 0,
    "temperature": 38.8,
    "temperatureLevel": "FEVER",
    "heartRate": 135,
    "isTachycardia": 1,
    "systolicBp": 85,
    "isHypotension": 1,
    "oxygenSaturation": 91.5,
    "isHypoxemia": 1,
    "isAcidosis": 0,
    "isHypoxemiaPao2": 1,
    "pao2Fio2Ratio": 280.5,
    "isOxygenationDisorder": 1,
    "isBunElevated": 1,
    "ctShowsInfection": 1,
    "ctMultiLobeInvolved": 0,
    "usesDopamine": 1,
    "usesDobutamine": 0,
    "usesNorepinephrine": 0,
    "usesSpecialAntibiotics": 1,
    "usesVentilator": 1,
    "inIcu": 1,
    "severityScore": 11,
    "severityLevel": "CRITICAL",
    "riskFactorsCount": 11,
    "assessmentSummary": "危重症患者，存在多个高危因素，需要立即收入ICU治疗"
  }
}
```

### 3.3 获取患者评估结果

**接口地址:** `GET /api/syndrome/result/{patientId}`

**请求参数:**
- 路径参数: `patientId` - 患者ID

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    // 与3.2响应数据结构相同
  }
}
```

### 3.4 分页查询评估结果

**接口地址:** `POST /api/syndrome/page`

**请求参数:**
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "patientNumber": "P001",  // 可选
  "gender": "男",            // 可选
  "minAge": 18,             // 可选
  "maxAge": 60,             // 可选
  "severityLevel": "CRITICAL",  // 可选：MILD/MODERATE/SEVERE/CRITICAL
  "minSeverityScore": 8,    // 可选
  "maxSeverityScore": 18,   // 可选
  "hasDyspnea": 1,         // 可选：是否呼吸困难
  "isHypoxemia": 1,        // 可选：是否低氧血症
  "usesVentilator": 1,     // 可选：是否使用呼吸机
  "inIcu": 1               // 可选：是否住ICU
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "syndromeId": 1,
        "patientNumber": "P001",
        "gender": "男",
        "age": 65,
        "patientId": 100,
        "diagnosis": "呼吸困难，发热",
        "severityLevel": "CRITICAL",
        "severityScore": 11,
        "riskFactorsCount": 11,
        "hasDyspnea": 1,
        "temperature": 38.8,
        "isTachycardia": 1,
        "isHypotension": 1,
        "isHypoxemia": 1,
        "usesVentilator": 1,
        "inIcu": 1,
        "assessmentTime": "2024-12-04 14:30:00",
        "assessmentSummary": "危重症患者，存在多个高危因素，需要立即收入ICU治疗"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

## 4. 评估指标说明

### 评估指标汇总（共18项）

| 分类 | 指标项 | 数量 | 分值 |
|------|--------|------|------|
| 症状 | 呼吸困难、意识障碍 | 2项 | 各1分 |
| 体征 | 体温、心率、血压、血氧饱和度 | 4项 | 各1分 |
| 检验 | pH值、PaO2、氧合指数、BUN | 4项 | 各1分 |
| 检查 | CT感染征象、CT多肺叶受累 | 2项 | 各1分 |
| 治疗药物 | 多巴胺、多巴酚丁胺、去甲肾上腺素、特殊级抗生素 | 4项 | 各1分 |
| 治疗设备 | 呼吸机 | 1项 | 1分 |
| 治疗场所 | ICU入住 | 1项 | 1分 |
| **合计** | **18个指标** | **18项** | **最高18分** |

### 4.1 症状指标

| 指标 | 数据来源 | 阈值 | 评分 | 说明 |
|------|----------|------|------|------|
| 呼吸困难 | chief_complaint | 包含"呼吸困难" | +1 | 主诉中查询 |
| 意识障碍 | physical_examination_detail.general_condition | 包含"意识障碍" | +1 | 一般状况中查询 |

### 4.2 体征指标

| 指标 | 数据来源 | 阈值 | 评分 | 说明 |
|------|----------|------|------|------|
| 体温 | physical_examination_detail.temperature | >38.5℃ 或 >40℃ | +1 | 发热或高热 |
| 心率 | physical_examination_detail.pulse | >130bpm | +1 | 心动过速 |
| 血压 | physical_examination_detail.systolic_bp/diastolic_bp | <90/60mmHg | +1 | 低血压 |
| 血氧饱和度 | physical_examination_detail.spo2 | <93% | +1 | 低氧血症 |

### 4.3 检验指标

| 指标 | 数据来源 | 阈值 | 评分 | 说明 |
|------|----------|------|------|------|
| pH值 | patient_info.arterial_ph | <7.35 | +1 | 酸中毒 |
| PaO2 | patient_info.arterial_po2 | <60mmHg | +1 | 低氧血症 |
| 氧合指数 | patient_info.arterial_oxygenation_index | ≤300mmHg | +1 | 氧合障碍 |
| BUN | patient_info.blood_urea_nitrogen | >7mmol/L | +1 | 尿素氮升高 |

### 4.4 检查指标

| 指标 | 数据来源 | 阈值 | 评分 | 说明 |
|------|----------|------|------|------|
| CT感染征象 | physical_examination_detail.ct_imaging_findings | 包含"肺炎/感染/炎症" | +1 | CT显示感染 |
| CT多肺叶受累 | physical_examination_detail.ct_imaging_findings | 包含"多肺叶/双肺/全肺" | +1 | 病变范围广 |

### 4.5 治疗指标

| 指标 | 数据来源 | 阈值 | 评分 | 说明 |
|------|----------|------|------|------|
| 多巴胺 | patient_info.dopamine_used | 使用 | +1 | 血管活性药物 |
| 多巴酚丁胺 | patient_info.dobutamine_used | 使用 | +1 | 血管活性药物 |
| 去甲肾上腺素 | patient_info.norepinephrine_used | 使用 | +1 | 血管活性药物 |
| 特殊级抗生素 | patient_info.special_antibiotics_used | 使用 | +1 | 限制级抗生素 |
| 呼吸机 | patient_info.ventilator_used | 使用 | +1 | 机械通气 |

### 4.6 治疗场所

| 指标 | 数据来源 | 阈值 | 评分 | 说明 |
|------|----------|------|------|------|
| ICU入住 | patient_info.icu_admission | 是 | +1 | 重症监护 |

## 5. 严重程度分级

根据总评分确定严重程度等级（统一评分体系：每项指标异常+1分，共18项）：

| 等级 | 评分范围 | 建议处置 | 说明 |
|------|----------|----------|------|
| MILD（轻度） | 0-2分 | 门诊治疗或观察 | 少于3个危险因素 |
| MODERATE（中度） | 3-5分 | 住院治疗和监测 | 3-5个危险因素 |
| SEVERE（重度） | 6-7分 | 密切监护和积极治疗 | 6-7个危险因素 |
| CRITICAL（危重） | ≥8分 | 立即收入ICU治疗 | 8个或以上危险因素 |

## 6. 使用说明

1. **批量评估**: 适用于初次部署或定期全面评估，会自动更新已有评估结果
2. **单个评估**: 适用于新入院患者或病情变化时，会覆盖原有评估结果
3. **结果查询**: 用于查看已评估患者的最新结果

## 7. 注意事项

1. 评估数据来源于患者基本信息、体检详细信息等相关表
2. 每个患者只保留最新的评估结果（历史记录通过逻辑删除保留）
3. **批量评估时会自动更新所有患者的评估结果**（包括已有评估的患者）
4. 评估结果会自动计算严重程度评分和等级
5. 建议定期（如每日）执行批量评估以更新所有患者状态

## 8. 扩展功能（待开发）

1. **统计分析**: 按日期、科室、严重程度等维度统计
2. **趋势分析**: 追踪患者病情变化趋势
3. **预警机制**: 自动预警高危患者
4. **报表导出**: 生成评估报告和统计报表
5. **集合群体指标**: 计算每日症候群病例的各项指标比例
