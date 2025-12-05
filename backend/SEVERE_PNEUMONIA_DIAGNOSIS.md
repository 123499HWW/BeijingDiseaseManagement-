# 重症肺炎诊断系统实现文档

## 概述
本系统实现了重症肺炎的自动化诊断功能，根据2007年美国感染病学会/美国胸科学会（IDSA/ATS）制定的重症肺炎诊断标准进行判断。

## 诊断标准

### 主要标准（满足1项即可诊断为重症肺炎）
1. **需要有创机械通气**
2. **感染性休克需要血管活性药物**

### 次要标准（满足3项即可诊断为重症肺炎）
1. **呼吸频率≥30次/分**
2. **PaO2/FiO2≤250mmHg**
3. **多肺叶浸润**
4. **意识障碍或定向障碍**
5. **血尿素氮≥7mmol/L**
6. **低血压需要液体复苏**（收缩压<90mmHg或舒张压<60mmHg）

## 数据来源映射

| 诊断指标 | 数据来源 | 具体字段 |
|---------|---------|---------|
| 机械通气 | patient_info表 | ventilator_used |
| 血管活性药物 | patient_info表 | dopamine_used, dobutamine_used, norepinephrine_used, vasoactive_drugs_used |
| 呼吸频率 | physical_examination_detail表 | respiratory_rate |
| PaO2/FiO2 | patient_info表 | arterial_oxygenation_index |
| 多肺叶浸润 | physical_examination_detail表 | ct_imaging_findings（文本分析） |
| 意识障碍 | patient_info表 | chief_complaint, present_illness（文本分析） |
| 血尿素氮 | patient_info表 | blood_urea_nitrogen |
| 低血压 | physical_examination_detail表 | blood_pressure（解析血压值） |

## 数据库设计

### 1. severe_pneumonia_diagnosis表
存储重症肺炎诊断结果，包括：
- 各项诊断标准的判断结果
- 实际检测值记录
- 诊断结论和建议措施
- 诊断时间和方法

### 2. severe_pneumonia_patient_relation表
管理诊断结果与患者的关联关系

## 系统架构

### 核心类
- **SeverePneumoniaDiagnosis**：诊断结果实体类
- **SeverePneumoniaPatientRelation**：患者关联实体类
- **SeverePneumoniaDiagnosisMapper**：诊断结果数据访问层
- **SeverePneumoniaPatientRelationMapper**：关联关系数据访问层
- **SeverePneumoniaDiagnosisService**：诊断服务实现
- **SeverePneumoniaDiagnosisController**：REST API控制器

### API接口

#### 1. 单个患者诊断
```
POST /api/severe-pneumonia/diagnose/{patientId}
```
对指定患者进行重症肺炎诊断

#### 2. 批量诊断
```
POST /api/severe-pneumonia/diagnose/all
```
对所有患者进行重症肺炎诊断

#### 3. 健康检查
```
GET /api/severe-pneumonia/health
```
检查服务运行状态

## 使用说明

### 1. 执行数据库脚本
```sql
-- 执行 sql/severe_pneumonia_tables.sql 创建必要的表
```

### 2. 重启应用
重启Spring Boot应用使新代码生效

### 3. 调用诊断接口
- 单个患者：`/api/severe-pneumonia/diagnose/{patientId}`
- 所有患者：`/api/severe-pneumonia/diagnose/all`

### 4. 查看诊断结果
- 结果存储在severe_pneumonia_diagnosis表中
- 通过severe_pneumonia_patient_relation表关联患者信息

## 诊断逻辑说明

### 主要标准判断
1. **机械通气**：检查ventilator_used字段
2. **血管活性药物**：检查多巴胺、多巴酚丁胺、去甲肾上腺素等药物使用情况

### 次要标准判断
1. **呼吸频率**：从体检记录获取respiratory_rate，判断是否≥30次/分
2. **氧合指数**：获取arterial_oxygenation_index，判断是否≤250mmHg
3. **多肺叶浸润**：分析CT报告文本，查找"多发"、"多叶"、"双肺"、"弥漫"等关键词
4. **意识障碍**：分析主诉和现病史，查找"意识障碍"、"嗜睡"、"昏迷"等关键词
5. **血尿素氮**：获取blood_urea_nitrogen值，判断是否≥7mmol/L
6. **低血压**：解析血压值，判断收缩压是否<90mmHg或舒张压<60mmHg

### 诊断结果
- **重症肺炎**：满足≥1项主要标准 或 ≥3项次要标准
- **非重症肺炎**：不满足上述条件

## 注意事项

1. **数据完整性**：诊断准确性依赖于患者信息的完整性
2. **文本分析局限**：意识障碍和肺叶浸润依赖文本分析，可能存在误判
3. **血压解析**：需要血压值格式为"收缩压/舒张压"（如"120/80"）
4. **氧合指数**：直接使用arterial_oxygenation_index字段，假设已计算好
5. **重复诊断**：系统会跳过已有诊断记录的患者

## 扩展建议

1. 增加更多诊断指标的自动获取
2. 改进文本分析算法，提高判断准确性
3. 支持诊断结果的修订和复查
4. 增加诊断结果的可视化展示
5. 支持导出诊断报告
6. 增加诊断标准的自定义配置
