# 综合评估接口增强文档

## 更新概述
对 `/api/comprehensive-assessment/page` 接口进行了增强：
1. 添加了更多patient_info表字段和respiratory_syndrome_assessment表的严重程度相关字段到返回结果中
2. 新增了"社区获得性肺炎"和"脓毒症"两个综合风险判断字段
3. 支持AND/OR逻辑控制风险判断规则
4. 删除了所有评估日期字段，简化返回数据结构

## 主要修改内容

### 1. ComprehensiveAssessmentVO 增强

#### 新增患者信息字段
- `admissionDate` - 住院日期
- `chiefComplaint` - 主诉
- `presentIllness` - 现病史
- `arterialPh` - 动脉血气pH
- `arterialPo2` - 动脉血气pO2(mmHg)
- `arterialOxygenationIndex` - 动脉血气氧合指数(mmHg)
- `bloodUreaNitrogen` - 血尿素氮(mmol/L)
- `serumCreatinine` - 血肌酐(μmol/L)
- `totalBilirubin` - 总胆红素(μmol/L)
- `plateletCount` - 血小板计数(×10^9/L)
- `ventilatorUsed` - 是否应用呼吸机
- `icuAdmission` - 是否入住ICU
- `vasoactiveDrugsUsed` - 是否应用血管活性药物
- `specialAntibioticsUsed` - 是否应用特殊级/限制级抗生素

#### 新增呼吸道症候群评估字段
- `respiratorySyndromeId` - 呼吸道症候群评估ID
- `respiratorySyndromeSeverityScore` - 呼吸道症候群严重程度评分（0-18分）
- `respiratorySyndromeSeverityLevel` - 呼吸道症候群严重程度等级（MILD/MODERATE/SEVERE/CRITICAL）

#### 新增综合风险判断字段
- `communityAcquiredPneumoniaRisk` - 社区获得性肺炎风险等级（高风险/低风险）
- `sepsisRisk` - 脓毒症风险等级（高风险/低风险）

#### 已删除字段（简化返回结构）
为简化数据结构，以下评估日期字段已被删除：
- `curbAssessmentDate` - CURB-65评估时间
- `covid19AssessmentDate` - COVID-19重型评估时间
- `covid19CriticalAssessmentDate` - COVID-19危重型评估时间
- `cpisAssessmentDate` - CPIS评估时间
- `psiAssessmentDate` - PSI评估时间
- `qsofaAssessmentDate` - qSOFA评估时间
- `severePneumoniaAssessmentDate` - 重症肺炎评估时间
- `sofaAssessmentDate` - SOFA评估时间
- `respiratorySyndromeAssessmentDate` - 呼吸道症候群评估时间
- `latestAssessmentDate` - 最新评估时间

### 2. ComprehensiveAssessmentQueryDTO 增强

#### 新增查询条件
- `respiratorySyndromeSeverityLevel` - 呼吸道症候群严重程度等级
- `minRespiratorySyndromeScore` - 最小呼吸道症候群严重程度评分
- `maxRespiratorySyndromeScore` - 最大呼吸道症候群严重程度评分
- `logicalOperator` - 逻辑控制参数（true: AND逻辑，false: OR逻辑，默认true）

### 3. SQL查询优化

#### 修改内容
1. 在SELECT语句中添加patient_info表的更多字段
2. 添加呼吸道症候群评估表的LEFT JOIN
3. 添加呼吸道症候群相关的WHERE条件
4. 删除所有评估时间字段，简化返回结果

#### SQL关联表结构
```sql
-- 新增的LEFT JOIN
LEFT JOIN (
  SELECT spr.patient_id, rsa2.* 
  FROM respiratory_syndrome_assessment rsa2 
  INNER JOIN syndrome_patient_relation spr ON rsa2.syndrome_id = spr.syndrome_id 
  WHERE rsa2.is_deleted = 0 AND rsa2.syndrome_id IN (
    SELECT MAX(rsa3.syndrome_id) FROM respiratory_syndrome_assessment rsa3 
    INNER JOIN syndrome_patient_relation spr2 ON rsa3.syndrome_id = spr2.syndrome_id 
    WHERE rsa3.is_deleted = 0 GROUP BY spr2.patient_id
  )
) rsa ON p.patient_id = rsa.patient_id
```

### 4. 综合风险判断逻辑

#### 社区获得性肺炎风险判断

**判断条件：**
- CURB-65风险等级 = "高风险"
- PSI风险等级 = "V级"
- CPIS风险等级 = "高风险"
- 重症肺炎诊断 = true

**逻辑控制：**
- `logicalOperator = true`（AND逻辑）：需要**同时满足所有条件**才判定为高风险
- `logicalOperator = false`（OR逻辑）：**满足任一条件**即判定为高风险

#### 脓毒症风险判断

**判断条件：**
- qSOFA风险等级 = "高风险"
- SOFA严重程度 ≥ "中度"（包括中度、重度、极重度）

**逻辑控制：**
- `logicalOperator = true`（AND逻辑）：需要**同时满足所有条件**才判定为高风险
- `logicalOperator = false`（OR逻辑）：**满足任一条件**即判定为高风险

### 5. Controller层增强

#### 参数校验增强
- 添加呼吸道症候群分数范围验证
- 添加呼吸道症候群严重程度等级的参数清理

#### 查询选项增强
- 新增呼吸道症候群严重程度等级选项：MILD, MODERATE, SEVERE, CRITICAL
- 新增呼吸道症候群分数范围建议：0-18分

## API接口响应示例

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
        
        // 新增的患者详细信息
        "admissionDate": "2024-01-15",
        "chiefComplaint": "发热、咳嗽3天",
        "presentIllness": "患者3天前无明显诱因出现发热...",
        "arterialPh": "7.35",
        "arterialPo2": "75.5",
        "arterialOxygenationIndex": "280",
        "bloodUreaNitrogen": "8.5",
        "serumCreatinine": "98",
        "totalBilirubin": "15.2",
        "plateletCount": "180",
        "ventilatorUsed": true,
        "icuAdmission": true,
        "vasoactiveDrugsUsed": false,
        "specialAntibioticsUsed": true,
        
        // 各项评估结果（原有）
        "curbTotalScore": 3,
        "curbRiskLevel": "中风险",
        // ... 其他评估结果
        
        // 新增的呼吸道症候群评估
        "respiratorySyndromeId": 1,
        "respiratorySyndromeSeverityScore": 12,
        "respiratorySyndromeSeverityLevel": "CRITICAL",
        
        // 综合风险判断结果
        "communityAcquiredPneumoniaRisk": "高风险",
        "sepsisRisk": "低风险"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

## 查询条件示例

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "patientNumber": "P001",
  "gender": "男",
  "minAge": 60,
  "maxAge": 80,
  
  // 逻辑控制（可选，默认true）
  "logicalOperator": true,  // true: AND逻辑，false: OR逻辑
  
  // 呼吸道症候群查询条件
  "respiratorySyndromeSeverityLevel": "CRITICAL",
  "minRespiratorySyndromeScore": 8,
  "maxRespiratorySyndromeScore": 18,
  
  // 其他评估条件
  "curbRiskLevel": "高风险",
  "psiRiskClass": "IV级"
}
```

## 性能优化

1. **索引优化建议**
   - 确保syndrome_patient_relation表的patient_id字段有索引
   - 确保respiratory_syndrome_assessment表的syndrome_id和is_deleted字段有复合索引

2. **查询优化**
   - 使用子查询获取每个患者的最新评估记录
   - 只在需要时加载额外字段，避免不必要的数据传输

## 使用场景说明

### 逻辑控制参数使用建议

#### AND逻辑（logicalOperator = true，默认）
**适用场景：** 严格筛选高危患者
- 用于识别**确定的**高风险患者
- 减少假阳性，提高诊断特异性
- 适合资源有限时，需要优先处理最危急患者

**示例：** 只有当患者的CURB-65、PSI、CPIS都提示高风险，且诊断为重症肺炎时，才判定为社区获得性肺炎高风险。

#### OR逻辑（logicalOperator = false）
**适用场景：** 敏感筛查，避免遗漏
- 用于早期预警和筛查
- 减少假阴性，提高诊断敏感性
- 适合初筛或需要全面评估风险的场景

**示例：** 只要患者的任一评估工具提示高风险，即判定需要关注。

## 注意事项

1. 呼吸道症候群评估的严重程度等级为英文常量：MILD、MODERATE、SEVERE、CRITICAL
2. 呼吸道症候群严重程度评分范围为0-18分
3. 所有新增字段都是可选的，不会影响现有功能
4. 患者的实验室检查值以字符串形式返回，方便前端显示和处理
5. logicalOperator参数不影响查询过滤，仅影响综合风险判断结果
6. 综合风险判断结果在服务端实时计算，不存储在数据库中

## 后续优化建议

1. 考虑添加缓存机制，减少复杂查询的执行频率
2. 可以添加字段选择功能，允许前端指定需要返回的字段
3. 考虑将复杂的SQL查询改为视图或存储过程
4. 添加导出功能，支持将查询结果导出为Excel或PDF
