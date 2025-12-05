# 多评估表联合查询API文档

## 接口概述
本接口提供患者信息与多个评估结果表的联合查询功能，包括：
- patient_info（患者信息表）
- curb_assessment_result（CURB-65评分表）
- psi_assessment_result（PSI评分表）
- cpis_assessment_result（CPIS评分表）
- severe_pneumonia_diagnosis（重症肺炎诊断表）

## 接口列表

### 1. 分页查询多评估表联合数据

#### 接口信息
- **接口路径**: `/api/multi-assessment/page`
- **请求方法**: POST
- **Content-Type**: application/json

#### 请求参数
```json
{
    "pageNum": 1,           // 页码，默认1
    "pageSize": 10,         // 每页大小，默认10，最大100
    "patientNumber": "",    // 患者编号（支持模糊查询）
    "patientName": "",      // 患者姓名（支持模糊查询）
    "gender": "",           // 性别（男/女）
    "minAge": null,         // 最小年龄
    "maxAge": null,         // 最大年龄
    "hasCurb": null,        // 是否有CURB评估
    "hasPsi": null,         // 是否有PSI评估
    "hasCpis": null,        // 是否有CPIS评估
    "hasDiagnosis": null,   // 是否有重症肺炎诊断
    "riskLevel": "",        // 风险等级筛选
    "isSeverePneumonia": null  // 是否重症肺炎
}
```

#### 响应参数
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "current": 1,
        "size": 10,
        "total": 100,
        "pages": 10,
        "records": [
            {
                // 患者基本信息
                "patientId": 1,
                "patientNumber": "P001",
                "patientName": "张三",
                "gender": "男",
                "age": 65,
                "admissionDate": "2024-12-01",
                "chiefComplaint": "发热、咳嗽3天",
                "presentIllness": "患者3天前出现发热...",
                
                // CURB-65评分
                "curbId": 1,
                "curbConfusionResult": 0,
                "curbUreaResult": 1,
                "curbRespirationResult": 1,
                "curbBloodPressureResult": 0,
                "curbAgeResult": 1,
                "curbTotalScore": 3,
                "curbRiskLevel": "高风险",
                "curbAssessmentTime": "2024-12-01 10:00:00",
                
                // PSI评分
                "psiId": 1,
                "psiAgeScore": 65,
                "psiMentalStatusChangeScore": 0,
                "psiHeartRateScore": 10,
                "psiRespiratoryRateScore": 20,
                "psiSystolicBpScore": 0,
                "psiTemperatureScore": 0,
                "psiArterialPhScore": 0,
                "psiPao2Score": 10,
                "psiPleuralEffusionScore": 0,
                "psiTotalScore": 105,
                "psiRiskLevel": "IV级",
                "psiRiskDescription": "住院治疗",
                "psiAssessmentTime": "2024-12-01 10:30:00",
                
                // CPIS评分
                "cpisId": 1,
                "cpisTemperatureScore": 1,
                "cpisSecretionScore": 1,
                "cpisOxygenationIndexScore": 2,
                "cpisChestXrayScore": 2,
                "cpisCultureScore": 0,
                "cpisTotalScore": 7,
                "cpisRiskLevel": "高风险",
                "cpisAssessmentTime": "2024-12-01 11:00:00",
                
                // 重症肺炎诊断
                "diagnosisId": 1,
                "mechanicalVentilation": 0,
                "vasoactiveDrugs": 0,
                "respiratoryRateHigh": 1,
                "respiratoryRate": 32,
                "oxygenationIndexLow": 1,
                "oxygenationIndex": 220,
                "consciousnessDisorder": 0,
                "ureaNitrogenHigh": 1,
                "ureaNitrogen": 8.5,
                "hypotension": 0,
                "isSeverePneumonia": 1,
                "diagnosisConclusion": "重症肺炎",
                "diagnosisTime": "2024-12-01 11:30:00",
                
                // 计算的风险信息
                "communityAcquiredPneumoniaRisk": "高风险",
                "ventilatorPneumoniaRisk": "高风险",
                "overallRiskLevel": "高风险",
                "completedAssessmentCount": 4
            }
        ]
    }
}
```

### 2. 查询患者评估详情

#### 接口信息
- **接口路径**: `/api/multi-assessment/detail/{patientId}`
- **请求方法**: GET
- **路径参数**: patientId - 患者ID

#### 响应参数
返回单个患者的完整评估信息，格式同上述records中的单条记录。

### 3. 获取统计信息

#### 接口信息
- **接口路径**: `/api/multi-assessment/statistics`
- **请求方法**: GET

#### 响应参数（待实现）
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "totalPatients": 1000,
        "curbCompletedCount": 800,
        "psiCompletedCount": 750,
        "cpisCompletedCount": 600,
        "diagnosisCompletedCount": 500,
        "highRiskCount": 200,
        "severePneumoniaCount": 100
    }
}
```

## 使用示例

### CURL请求示例

#### 分页查询
```bash
curl -X POST http://localhost:8080/api/multi-assessment/page \
  -H "Content-Type: application/json" \
  -d '{
    "pageNum": 1,
    "pageSize": 10,
    "hasCurb": true,
    "hasPsi": true
  }'
```

#### 查询详情
```bash
curl -X GET http://localhost:8080/api/multi-assessment/detail/1
```

### JavaScript/Axios示例

```javascript
// 分页查询
async function queryMultiAssessment() {
    const queryData = {
        pageNum: 1,
        pageSize: 10,
        patientNumber: 'P001',
        hasCurb: true,
        hasPsi: true
    };
    
    try {
        const response = await axios.post('/api/multi-assessment/page', queryData);
        const data = response.data.data;
        console.log('查询结果:', data);
        
        // 处理数据
        data.records.forEach(record => {
            console.log(`患者${record.patientName}的综合风险等级：${record.overallRiskLevel}`);
        });
    } catch (error) {
        console.error('查询失败:', error);
    }
}

// 查询详情
async function getPatientDetail(patientId) {
    try {
        const response = await axios.get(`/api/multi-assessment/detail/${patientId}`);
        const detail = response.data.data;
        console.log('患者评估详情:', detail);
    } catch (error) {
        console.error('查询失败:', error);
    }
}
```

## 风险等级说明

### 社区获得性肺炎风险（基于CURB-65和PSI）
- **高风险**: PSI IV-V级或CURB≥3
- **中风险**: PSI II-III级或CURB=2
- **低风险**: PSI I级或CURB<2

### 呼吸机相关肺炎风险（基于CPIS）
- **高风险**: CPIS>6
- **低风险**: CPIS≤6

### 综合风险评级
- **高风险**: 任一评估为高风险或诊断为重症肺炎
- **中风险**: 有中风险评估且无高风险
- **低风险**: 所有评估均为低风险
- **未评估**: 无任何评估数据

## 注意事项

1. **数据时效性**: 查询返回每个患者各评估表的最新记录
2. **性能优化**: 
   - 使用了子查询优化，只获取最新的评估记录
   - 建议为patient_id和created_time创建联合索引
3. **分页限制**: 每页最大100条记录，防止大量数据查询
4. **模糊查询**: patientNumber和patientName支持模糊查询，使用LIKE '%keyword%'

## 数据库索引建议

```sql
-- 为提高查询性能，建议创建以下索引
CREATE INDEX idx_curb_patient_time ON curb_assessment_result(patient_id, created_time);
CREATE INDEX idx_psi_patient_time ON psi_assessment_result(patient_id, created_time);
CREATE INDEX idx_cpis_patient_time ON cpis_assessment_result(patient_id, created_time);
CREATE INDEX idx_spd_patient_time ON severe_pneumonia_diagnosis(patient_id, created_time);
CREATE INDEX idx_patient_number ON patient_info(patient_number);
CREATE INDEX idx_patient_name ON patient_info(patient_name);
```

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查参数格式和必填项 |
| 404 | 患者不存在 | 检查患者ID是否正确 |
| 500 | 服务器内部错误 | 查看服务器日志 |
