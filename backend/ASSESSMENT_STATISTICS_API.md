# 评估统计分析API文档

## 概述
评估统计分析API提供各个评分系统的统计数据，包括每个风险等级/严重程度的患者数量统计。

## API接口列表

### 1. 获取所有评分系统的统计数据

#### 接口地址
```
GET /api/assessment-statistics/all
```

#### 返回示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "curbStatistics": {
      "低风险": 45,
      "中风险": 23,
      "高风险": 12
    },
    "psiStatistics": {
      "Ⅰ级": 20,
      "Ⅱ级": 18,
      "Ⅲ级": 22,
      "Ⅳ级": 15,
      "Ⅴ级": 5
    },
    "cpisStatistics": {
      "低风险": 70,
      "高风险": 10
    },
    "qsofaStatistics": {
      "低风险": 65,
      "高风险": 15
    },
    "sofaStatistics": {
      "正常或轻微": 40,
      "轻度": 20,
      "中度": 12,
      "重度": 6,
      "极重度": 2
    },
    "severePneumoniaStatistics": {
      "重症肺炎": 18,
      "非重症肺炎": 62
    },
    "covid19Statistics": {
      "非重型": 50,
      "重型": 15,
      "重型（中危）": 10,
      "重型（高危）": 5
    },
    "covid19CriticalStatistics": {
      "非危重型": 68,
      "危重型": 7,
      "危重型（高危）": 3,
      "危重型（极危重）": 2
    },
    "respiratorySyndromeStatistics": {
      "轻度": 35,
      "中度": 25,
      "重度": 15,
      "危重": 5
    },
    "totalPatients": 100,
    "assessedPatients": 80,
    "unassessedPatients": 20,
    "highRiskPatients": 25,
    "statisticsTimestamp": 1701234567890
  }
}
```

### 2. 获取指定评分系统的统计数据

#### 接口地址
```
GET /api/assessment-statistics/by-type/{type}
```

#### 参数说明
| 参数 | 类型 | 说明 |
|------|------|------|
| type | String | 评分类型：CURB65、PSI、CPIS、QSOFA、SOFA、SEVERE_PNEUMONIA、COVID19、COVID19_CRITICAL、RESPIRATORY_SYNDROME |

#### 请求示例
```
GET /api/assessment-statistics/by-type/CURB65
```

#### 返回示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "低风险": 45,
    "中风险": 23,
    "高风险": 12
  }
}
```

### 3. 获取评估覆盖率统计

#### 接口地址
```
GET /api/assessment-statistics/coverage
```

#### 返回示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPatients": 100,
    "assessedPatients": 80,
    "unassessedPatients": 20,
    "highRiskPatients": 25,
    "coverageRate": "80.00%",
    "highRiskRate": "31.25%"
  }
}
```

### 4. 获取高风险患者统计

#### 接口地址
```
GET /api/assessment-statistics/high-risk
```

#### 返回示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "curb65HighRisk": 12,
    "psiHighRisk": 20,
    "cpisHighRisk": 10,
    "qsofaHighRisk": 15,
    "sofaHighRisk": 8,
    "severePneumonia": 18,
    "covid19HighRisk": 15,
    "covid19CriticalHighRisk": 12,
    "respiratorySyndromeHighRisk": 20,
    "totalHighRiskPatients": 25
  }
}
```

### 5. 获取评分系统汇总统计（用于图表展示）

#### 接口地址
```
GET /api/assessment-statistics/summary
```

#### 返回示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPatients": 100,
    "assessedPatients": 80,
    "unassessedPatients": 20,
    "highRiskPatients": 25,
    "curb65": {
      "低风险": 45,
      "中风险": 23,
      "高风险": 12
    },
    "psi": {
      "Ⅰ级": 20,
      "Ⅱ级": 18,
      "Ⅲ级": 22,
      "Ⅳ级": 15,
      "Ⅴ级": 5
    },
    "cpis": {
      "低风险": 70,
      "高风险": 10
    },
    "qsofa": {
      "低风险": 65,
      "高风险": 15
    },
    "sofa": {
      "正常或轻微": 40,
      "轻度": 20,
      "中度": 12,
      "重度": 6,
      "极重度": 2
    },
    "severePneumonia": {
      "重症肺炎": 18,
      "非重症肺炎": 62
    },
    "covid19": {
      "非重型": 50,
      "重型": 15,
      "重型（中危）": 10,
      "重型（高危）": 5
    },
    "covid19Critical": {
      "非危重型": 68,
      "危重型": 7,
      "危重型（高危）": 3,
      "危重型（极危重）": 2
    },
    "respiratorySyndrome": {
      "轻度": 35,
      "中度": 25,
      "重度": 15,
      "危重": 5
    },
    "timestamp": 1701234567890
  }
}
```

## 统计指标说明

### 风险等级分类

#### CURB-65评分
- **低风险**：0-1分，门诊治疗
- **中风险**：2分，考虑住院
- **高风险**：3-5分，住院治疗

#### PSI评分
- **Ⅰ级**：≤50分，极低风险
- **Ⅱ级**：51-70分，低风险
- **Ⅲ级**：71-90分，低风险
- **Ⅳ级**：91-130分，中风险
- **Ⅴ级**：>130分，高风险

#### CPIS评分
- **低风险**：≤6分
- **高风险**：>6分

#### qSOFA评分
- **低风险**：<2分
- **高风险**：≥2分

#### SOFA评分
- **正常或轻微**：0-3分
- **轻度**：4-7分
- **中度**：8-11分
- **重度**：12-14分
- **极重度**：≥15分

#### 重症肺炎诊断
- **重症肺炎**：满足诊断标准
- **非重症肺炎**：不满足诊断标准

#### COVID-19重型诊断
- **非重型**：0项标准
- **重型**：1项标准
- **重型（中危）**：2项标准
- **重型（高危）**：≥3项标准

#### COVID-19危重型诊断
- **非危重型**：0项标准
- **危重型**：1项标准
- **危重型（高危）**：2项标准
- **危重型（极危重）**：≥3项标准

#### 呼吸道症候群评估
- **轻度**：0-2分
- **中度**：3-5分
- **重度**：6-7分
- **危重**：≥8分

## 使用示例

### JavaScript/Axios
```javascript
// 获取所有统计数据
axios.get('/api/assessment-statistics/all')
  .then(response => {
    const statistics = response.data.data;
    console.log('总患者数：', statistics.totalPatients);
    console.log('高风险患者数：', statistics.highRiskPatients);
    
    // 绘制CURB-65饼图
    drawPieChart('curb65Chart', statistics.curbStatistics);
    
    // 绘制PSI柱状图
    drawBarChart('psiChart', statistics.psiStatistics);
  });

// 获取特定评分系统统计
axios.get('/api/assessment-statistics/by-type/SOFA')
  .then(response => {
    const sofaData = response.data.data;
    console.log('SOFA评分分布：', sofaData);
  });

// 获取覆盖率统计
axios.get('/api/assessment-statistics/coverage')
  .then(response => {
    const coverage = response.data.data;
    console.log(`评估覆盖率：${coverage.coverageRate}`);
    console.log(`高风险率：${coverage.highRiskRate}`);
  });
```

### 图表展示建议

#### 1. 饼图适用项目
- CURB-65风险分布
- CPIS风险分布
- qSOFA风险分布
- 重症肺炎诊断分布
- 评估覆盖率

#### 2. 柱状图适用项目
- PSI分级分布
- SOFA严重程度分布
- COVID-19重型分级
- COVID-19危重型分级
- 各评分系统高风险患者对比

#### 3. 雷达图适用项目
- 综合风险评估（多维度）

#### 4. 折线图适用项目
- 时间序列统计（需要定期采集数据）
- 风险趋势分析

## 注意事项

1. **数据准确性**：统计数据基于最新的评估结果，每个患者只统计最新一次评估
2. **性能考虑**：统计大量数据时可能需要较长时间，建议使用缓存机制
3. **实时性**：数据为查询时的实时统计，不是历史快照
4. **去重逻辑**：同一患者多次评估只计算最新结果
5. **高风险判定**：不同评分系统的高风险标准不同，详见风险等级分类说明

## 错误处理

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 500 | 服务器内部错误 | 检查日志，联系管理员 |
| 404 | 未找到指定类型的评分数据 | 检查评分类型参数是否正确 |
| 400 | 参数错误 | 检查请求参数格式 |
