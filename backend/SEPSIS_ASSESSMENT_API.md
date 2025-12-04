# 脓毒症评估联合查询API文档

## 接口概述
本接口提供患者信息与脓毒症相关评估表的联合查询功能，包括：
- patient_info（患者信息表）
- qsofa_assessment（qSOFA快速评分表）
- sofa_assessment（SOFA器官功能评分表）

## 接口列表

### 1. 分页查询脓毒症评估联合数据

#### 接口信息
- **接口路径**: `/api/sepsis-assessment/page`
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
    "hasQsofa": null,       // 是否有qSOFA评估
    "hasSofa": null,        // 是否有SOFA评估
    "minQsofaScore": null,  // 最小qSOFA分数（0-3）
    "minSofaScore": null,   // 最小SOFA分数（0-24）
    "isHighRisk": false     // 是否筛选高风险患者
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
                "chiefComplaint": "发热、意识模糊",
                "presentIllness": "患者3天前出现发热...",
                
                // qSOFA评分（快速评估）
                "qsofaId": 1,
                "qsofaRespiratoryRate": 1,      // 呼吸频率≥22次/分
                "qsofaMentalStatus": 1,         // 精神状态改变
                "qsofaSystolicBp": 0,           // 收缩压≤100mmHg
                "qsofaTotalScore": 2,           // 总分（0-3）
                "qsofaRiskLevel": "高风险",
                "qsofaAssessmentTime": "2024-12-01 10:00:00",
                "qsofaAssessor": "王医生",
                
                // SOFA评分（器官功能评估）
                "sofaId": 1,
                "sofaRespirationScore": 2,      // 呼吸系统
                "sofaPao2Fio2": 250,
                "sofaCoagulationScore": 1,      // 凝血系统
                "sofaPlateletCount": 120,
                "sofaLiverScore": 1,            // 肝脏系统
                "sofaBilirubin": 2.5,
                "sofaCardiovascularScore": 2,   // 心血管系统
                "sofaMeanArterialPressure": 65,
                "sofaVasoactiveDrugs": "多巴胺",
                "sofaCnsScore": 2,               // 中枢神经系统
                "sofaGlasgowComaScore": 12,
                "sofaRenalScore": 1,             // 肾脏系统
                "sofaCreatinine": 1.8,
                "sofaUrineOutput": 450,
                "sofaTotalScore": 9,             // 总分（0-24）
                "sofaRiskLevel": "高风险",
                "sofaAssessmentTime": "2024-12-01 10:30:00",
                "sofaAssessor": "李医生",
                
                // 计算的风险评估
                "sepsisScreeningResult": "阳性（疑似脓毒症）",
                "organDysfunctionAssessment": "中度器官功能障碍",
                "sepsisRiskLevel": "高风险",
                "mortalityRisk": "高（30-45%）",
                "requiresIcu": true,
                "recommendedTreatment": "密切监测，3小时内完成初始复苏",
                "completedAssessments": "qSOFA+SOFA",
                "assessmentCompleteness": 100
            }
        ]
    }
}
```

### 2. 查询患者脓毒症评估详情

#### 接口信息
- **接口路径**: `/api/sepsis-assessment/detail/{patientId}`
- **请求方法**: GET
- **路径参数**: patientId - 患者ID

#### 响应参数
返回单个患者的完整脓毒症评估信息，格式同上述records中的单条记录。

### 3. 查询高风险脓毒症患者

#### 接口信息
- **接口路径**: `/api/sepsis-assessment/high-risk`
- **请求方法**: GET
- **说明**: 返回qSOFA≥2分或SOFA≥2分的患者列表

#### 响应参数
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "patientId": 1,
            "patientNumber": "P001",
            "patientName": "张三",
            "age": 65,
            "qsofaTotalScore": 2,
            "sofaTotalScore": 9,
            "sepsisScreeningResult": "阳性（疑似脓毒症）",
            "sepsisRiskLevel": "高风险",
            "mortalityRisk": "高（30-45%）"
        }
    ]
}
```

### 4. 获取脓毒症评估统计

#### 接口信息
- **接口路径**: `/api/sepsis-assessment/statistics`
- **请求方法**: GET

#### 响应参数
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "totalPatients": 1000,
        "qsofaCompleted": 800,
        "sofaCompleted": 600,
        "bothCompleted": 500,
        "qsofaCompletionRate": "80.0%",
        "sofaCompletionRate": "60.0%",
        "bothCompletionRate": "50.0%",
        "highRiskQsofa": 200,
        "highRiskSofa": 150,
        "criticalSofa": 30,
        "requiresIcu": 180,
        "riskDistribution": {
            "极高风险": 30,
            "高风险": 170,
            "中风险": 200,
            "低风险": 400,
            "未评估": 200
        }
    }
}
```

## 评分说明

### qSOFA评分（快速序贯器官衰竭评分）
- **评分项目**（每项1分，总分0-3分）：
  - 呼吸频率≥22次/分
  - 精神状态改变（GCS<15）
  - 收缩压≤100mmHg
- **临床意义**：
  - ≥2分：提示可能存在脓毒症，需进一步评估
  - <2分：低风险

### SOFA评分（序贯器官衰竭评分）
- **评分系统**（6个系统，每个0-4分，总分0-24分）：
  - 呼吸系统：PaO2/FiO2比值
  - 凝血系统：血小板计数
  - 肝脏系统：胆红素水平
  - 心血管系统：血压和血管活性药物
  - 中枢神经系统：Glasgow昏迷评分
  - 肾脏系统：肌酐和尿量
- **死亡风险预测**：
  - 0-1分：<10%
  - 2-3分：10-20%
  - 4-5分：20-30%
  - 6-7分：30-45%
  - 8-9分：45-60%
  - 10-12分：60-80%
  - >12分：>80%

## 风险分级说明

### 脓毒症风险等级
- **极高风险**：SOFA≥10或（qSOFA≥2且SOFA≥6）
- **高风险**：qSOFA≥2或SOFA≥2
- **中风险**：qSOFA=1或SOFA=1
- **低风险**：qSOFA=0且SOFA=0
- **未评估**：无评分数据

### ICU收治建议
- SOFA≥6分
- qSOFA≥2分且存在其他危险因素
- 任一器官系统评分≥3分
- 需要血管活性药物或机械通气

## 使用示例

### CURL请求示例

#### 分页查询高风险患者
```bash
curl -X POST http://localhost:8080/api/sepsis-assessment/page \
  -H "Content-Type: application/json" \
  -d '{
    "pageNum": 1,
    "pageSize": 10,
    "isHighRisk": true
  }'
```

#### 查询详情
```bash
curl -X GET http://localhost:8080/api/sepsis-assessment/detail/1
```

### JavaScript/Axios示例

```javascript
// 查询高风险患者
async function queryHighRiskSepsis() {
    const queryData = {
        pageNum: 1,
        pageSize: 10,
        minQsofaScore: 2  // qSOFA≥2分
    };
    
    try {
        const response = await axios.post('/api/sepsis-assessment/page', queryData);
        const data = response.data.data;
        
        data.records.forEach(record => {
            console.log(`患者${record.patientName}：
                qSOFA评分：${record.qsofaTotalScore}
                SOFA评分：${record.sofaTotalScore}
                风险等级：${record.sepsisRiskLevel}
                死亡风险：${record.mortalityRisk}
                建议：${record.recommendedTreatment}`);
        });
    } catch (error) {
        console.error('查询失败:', error);
    }
}

// 获取统计信息
async function getSepsisStatistics() {
    try {
        const response = await axios.get('/api/sepsis-assessment/statistics');
        const stats = response.data.data;
        
        console.log(`脓毒症评估统计：
            总患者数：${stats.totalPatients}
            qSOFA完成率：${stats.qsofaCompletionRate}
            SOFA完成率：${stats.sofaCompletionRate}
            高风险患者：${stats.highRiskQsofa + stats.highRiskSofa}
            需要ICU：${stats.requiresIcu}`);
    } catch (error) {
        console.error('获取统计失败:', error);
    }
}
```

## 治疗建议模板

### 极高风险（SOFA≥10）
- 立即ICU监护
- 1小时内完成脓毒症集束化治疗
- 液体复苏（30ml/kg晶体液）
- 血培养后立即使用广谱抗生素
- 血管活性药物维持MAP≥65mmHg
- 监测乳酸水平

### 高风险（qSOFA≥2或SOFA≥2）
- 密切监测生命体征
- 3小时内完成初始复苏
- 血培养和乳酸测定
- 早期抗生素治疗
- 评估器官功能

### 中低风险
- 常规监测
- 完善感染指标
- 必要时复查评分

## 数据库索引建议

```sql
-- 为提高查询性能，建议创建以下索引
CREATE INDEX idx_qsofa_patient_time ON qsofa_assessment(patient_id, created_time);
CREATE INDEX idx_qsofa_score ON qsofa_assessment(total_score);
CREATE INDEX idx_sofa_patient_time ON sofa_assessment(patient_id, created_time);
CREATE INDEX idx_sofa_score ON sofa_assessment(total_score);
```

## 注意事项

1. **评估时机**：
   - qSOFA：可在床旁快速完成，用于初筛
   - SOFA：需要实验室检查结果，用于确诊和预后评估

2. **动态监测**：
   - 建议每6-12小时重复评估
   - SOFA评分变化趋势比单次评分更有价值

3. **临床决策**：
   - 评分仅作参考，需结合临床表现综合判断
   - 早期识别、早期干预是改善预后的关键
