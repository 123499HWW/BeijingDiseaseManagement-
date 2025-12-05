# qSOFA评分系统实现文档

## 概述
qSOFA (Quick Sequential Organ Failure Assessment) 是一个快速评估工具，用于识别脓毒症高风险患者。该系统实现了自动化的qSOFA评分功能。

## qSOFA评分标准

每项指标满足得1分，总分0-3分：

| 评分项 | 标准 | 分值 |
|-------|------|------|
| 意识障碍 | 存在意识改变 | 1分 |
| 呼吸频率 | ≥22次/分 | 1分 |
| 收缩压 | ≤90mmHg | 1分 |

## 风险分级

- **总分<2分**：低风险，脓毒症风险较低
- **总分≥2分**：高风险，提示脓毒症高风险，需要进一步评估和治疗

## 数据来源映射

| 评分指标 | 数据来源 | 具体字段 |
|---------|---------|---------|
| 意识障碍 | patient_info表 | chief_complaint, present_illness（文本分析） |
| 呼吸频率 | physical_examination_detail表 | respiration |
| 收缩压 | physical_examination_detail表 | systolic_bp |

## 数据库设计

### 1. qsofa_assessment表
存储qSOFA评分结果，包括：
- 各项评分标准的判断结果
- 实际测量值记录
- 总分和风险等级
- 评估结论和建议措施

### 2. qsofa_patient_relation表
管理评分结果与患者的关联关系

## 系统架构

### 核心类
- **QsofaAssessment**：评分结果实体类
- **QsofaPatientRelation**：患者关联实体类
- **QsofaAssessmentMapper**：评分结果数据访问层
- **QsofaPatientRelationMapper**：关联关系数据访问层
- **QsofaAssessmentService**：评分服务实现
- **QsofaAssessmentController**：REST API控制器

### API接口

#### 1. 单个患者评分
```
POST /api/qsofa/assess/{patientId}
```
对指定患者进行qSOFA评分

#### 2. 批量评分
```
POST /api/qsofa/assess/all
```
对所有患者进行qSOFA评分

#### 3. 健康检查
```
GET /api/qsofa/health
```
检查服务运行状态

## 使用说明

### 1. 执行数据库脚本
```sql
-- 执行 sql/qsofa_tables.sql 创建必要的表
```

### 2. 重启应用
重启Spring Boot应用使新代码生效

### 3. 调用评分接口
- 单个患者：`/api/qsofa/assess/{patientId}`
- 所有患者：`/api/qsofa/assess/all`

### 4. 查看评分结果
- 结果存储在qsofa_assessment表中
- 通过qsofa_patient_relation表关联患者信息

## 评分逻辑说明

### 意识障碍判断
通过分析患者主诉和现病史文本，查找以下关键词：
- "意识障碍"、"意识模糊"、"意识不清"
- "嗜睡"、"昏迷"、"谵妄"
- "精神萎靡"、"精神差"

### 呼吸频率判断
直接从体检记录中获取respiration字段值，判断是否≥22次/分

### 收缩压判断
从体检记录中获取systolic_bp字段值，判断是否≤90mmHg

## 临床意义

### qSOFA评分的应用场景
1. **急诊科筛查**：快速识别可能存在脓毒症的患者
2. **病房监测**：用于早期发现病情恶化的患者
3. **ICU外评估**：在非重症监护环境中的快速评估工具

### 评分≥2分的处理建议
1. 立即进行进一步评估
2. 检查乳酸水平
3. 获取血培养
4. 考虑启动脓毒症集束化治疗
5. 密切监测生命体征

## 注意事项

1. **数据完整性**：评分准确性依赖于患者信息的完整性
2. **意识评估局限**：意识障碍依赖文本分析，可能存在误判
3. **定期重评**：病情可能动态变化，建议定期重新评估
4. **不能替代SOFA**：qSOFA是快速筛查工具，不能替代完整的SOFA评分
5. **重复评分**：系统会跳过已有评分记录的患者

## 扩展建议

1. 增加时序评分功能，追踪患者qSOFA评分变化趋势
2. 整合其他生命体征监测数据
3. 增加与SOFA评分的联合评估
4. 支持评分结果的可视化展示
5. 增加评分提醒和预警功能
6. 支持导出评分报告
