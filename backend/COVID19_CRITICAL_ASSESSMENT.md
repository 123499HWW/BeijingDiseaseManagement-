# COVID-19危重型诊断系统实现文档

## 概述
本系统实现了COVID-19危重型诊断功能，根据国家卫健委发布的诊断标准，自动评估患者是否为COVID-19危重型病例。危重型是COVID-19最严重的临床分型，需要重点关注和积极救治。

## 危重型诊断标准

满足以下任何一条即可诊断为危重型：

| 诊断指标 | 判断标准 | 临床意义 |
|---------|---------|----------|
| 呼吸衰竭 | 氧合指数<150mmHg且需要机械通气 | 提示严重呼吸功能障碍 |
| 休克 | 收缩压<90mmHg或舒张压<60mmHg或需要血管活性药物 | 提示循环功能衰竭 |
| 其他器官功能衰竭 | 合并器官功能衰竭且需要ICU监护治疗 | 提示多器官功能障碍 |

## 数据来源映射

| 诊断指标 | 数据来源 | 具体字段 |
|---------|---------|---------|
| 氧合指数 | patient_info表 | arterial_oxygenation_index |
| 机械通气 | patient_info表 | ventilator_used |
| 血压 | physical_examination_detail表 | systolic_bp, diastolic_bp |
| 血管活性药物 | patient_info表 | dopamine_used, dobutamine_used, norepinephrine_used |
| ICU监护 | patient_info表 | intensive_care_unit |
| 肾功能 | patient_info表 | serum_creatinine |
| 肝功能 | patient_info表 | total_bilirubin |
| 凝血功能 | patient_info表 | platelet_count |

## 数据库设计

### 1. covid19_critical_assessment表
存储COVID-19危重型诊断结果，包括：
- 呼吸衰竭相关指标
- 休克相关指标
- ICU监护相关指标
- 器官功能衰竭相关指标
- 诊断结果和建议措施

### 2. covid19_critical_patient_relation表
管理诊断结果与患者的关联关系

## 系统架构

### 核心类
- **Covid19CriticalAssessment**：诊断结果实体类
- **Covid19CriticalPatientRelation**：患者关联实体类
- **Covid19CriticalAssessmentMapper**：诊断结果数据访问层
- **Covid19CriticalPatientRelationMapper**：关联关系数据访问层
- **Covid19CriticalAssessmentService**：诊断服务实现
- **Covid19CriticalAssessmentController**：REST API控制器

### API接口

#### 1. 单个患者诊断
```
POST /api/covid19-critical/assess/{patientId}
```
对指定患者进行COVID-19危重型诊断

#### 2. 批量诊断
```
POST /api/covid19-critical/assess/all
```
对所有患者进行COVID-19危重型诊断

#### 3. 健康检查
```
GET /api/covid19-critical/health
```
检查服务运行状态

## 使用说明

### 1. 执行数据库脚本
```sql
-- 执行 sql/covid19_critical_tables.sql 创建必要的表
```

### 2. 重启应用
重启Spring Boot应用使新代码生效

### 3. 调用诊断接口
- 单个患者：`/api/covid19-critical/assess/{patientId}`
- 所有患者：`/api/covid19-critical/assess/all`

### 4. 查看诊断结果
- 结果存储在covid19_critical_assessment表中
- 通过covid19_critical_patient_relation表关联患者信息

## 严重程度分级

根据满足的标准数量，系统将患者分为以下级别：

| 满足标准数 | 严重程度 | 临床建议 |
|-----------|---------|----------|
| 0项 | 非危重型 | 密切监测，预防病情恶化 |
| 1项 | 危重型 | ICU治疗，生命支持 |
| 2项 | 危重型（高危） | ICU密切监护，升级生命支持 |
| ≥3项 | 危重型（极危重） | 全面生命支持，ECMO等 |

## 诊断逻辑说明

### 呼吸衰竭判断
- 氧合指数<150mmHg提示呼吸衰竭
- 同时使用呼吸机表明需要机械通气

### 休克判断
- 收缩压<90mmHg或舒张压<60mmHg
- 或使用血管活性药物（多巴胺、多巴酚丁胺、去甲肾上腺素等）

### 器官功能衰竭判断
- 肾功能衰竭：肌酐>176.8 μmol/L
- 肝功能衰竭：胆红素>34.2 μmol/L
- 凝血功能障碍：血小板<100×10^9/L
- 循环系统衰竭：已在休克中判断

## 临床意义

### 危重型患者的管理要点

#### 1. 生命支持治疗
- **呼吸支持**：有创机械通气、ECMO
- **循环支持**：血管活性药物、液体复苏
- **肾脏支持**：CRRT（连续性肾脏替代治疗）
- **营养支持**：肠内或肠外营养

#### 2. 器官功能保护
- 肺保护性通气策略
- 维持组织灌注
- 保护肾功能
- 维持肝功能

#### 3. 并发症防治
- 预防呼吸机相关性肺炎
- 预防深静脉血栓
- 预防应激性溃疡
- 预防导管相关感染

#### 4. 综合治疗
- 抗病毒治疗
- 免疫调节治疗
- 抗凝治疗
- 中医药治疗

## 注意事项

1. **数据完整性**：诊断准确性依赖于数据的完整性
2. **动态评估**：危重型病情变化快，需要持续监测
3. **综合判断**：系统诊断仅供参考，需结合临床实际
4. **及时干预**：一旦诊断为危重型，需要立即采取积极措施
5. **重复诊断**：系统会跳过已有诊断记录的患者

## 监测重点

### 生命体征监测
- 心率、血压、呼吸频率
- 血氧饱和度
- 体温
- 尿量

### 实验室指标监测
- 血气分析
- 血常规、凝血功能
- 肝肾功能
- 心肌标志物
- 炎症指标（CRP、PCT、IL-6等）

### 影像学监测
- 胸部CT动态变化
- 肺部超声
- 心脏超声

## 预后评估

危重型患者预后较差，需要重点关注以下因素：
- 年龄>65岁
- 合并基础疾病
- 多器官功能衰竭
- 持续低氧血症
- 严重炎症反应

## 扩展建议

1. 增加更多器官功能评估指标
2. 整合APACHE II、SOFA等评分系统
3. 增加预后评估模型
4. 支持治疗效果评估
5. 增加并发症监测
6. 支持个性化治疗方案推荐
7. 增加康复评估功能
8. 支持远程监护数据接入
