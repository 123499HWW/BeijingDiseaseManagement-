# 疾病分类统计API文档

## 接口概述
本接口用于统计各种疾病的患者数量分布，包括社区获得性肺炎风险、脓毒症风险、COVID-19重型/危重型以及呼吸道症候群的评估情况。

## 接口信息

### 基本信息
- **接口路径**: `/api/comprehensive-assessment/disease-statistics`
- **请求方法**: GET
- **Content-Type**: application/json
- **响应格式**: JSON

### 接口说明
该接口会遍历所有患者的综合评估数据，统计各种疾病的分布情况。

## 请求参数
无需请求参数，直接调用即可。

## 响应参数

### 响应结构
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "statisticsTime": "2024-12-04 21:30:00",
        "totalPatients": 1500,
        "communityPneumoniaRiskStats": {
            "高风险": 320,
            "低风险": 680,
            "无数据": 500
        },
        "communityPneumoniaHighRiskCount": 320,
        "communityPneumoniaLowRiskCount": 680,
        "sepsisRiskStats": {
            "高风险": 280,
            "低风险": 720,
            "无数据": 500
        },
        "sepsisHighRiskCount": 280,
        "sepsisLowRiskCount": 720,
        "covid19Stats": {
            "重型": 150,
            "非重型": 850,
            "未评估": 500
        },
        "covid19SevereCount": 150,
        "covid19NonSevereCount": 850,
        "covid19CriticalStats": {
            "危重型": 80,
            "非危重型": 920,
            "未评估": 500
        },
        "covid19CriticalCount": 80,
        "covid19NonCriticalCount": 920,
        "respiratorySyndromeStats": {
            "已评估": 1000,
            "未评估": 500
        },
        "respiratorySyndromeLevelStats": {
            "轻度": 400,
            "中度": 300,
            "重度": 300,
            "无数据": 500
        },
        "assessedPatients": 1000,
        "unassessedPatients": 500
    }
}
```

### 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| statisticsTime | String | 统计时间 |
| totalPatients | Integer | 总患者数 |
| communityPneumoniaRiskStats | Map | 社区获得性肺炎风险分布 |
| communityPneumoniaHighRiskCount | Integer | 社区获得性肺炎高风险患者数 |
| communityPneumoniaLowRiskCount | Integer | 社区获得性肺炎低风险患者数 |
| sepsisRiskStats | Map | 脓毒症风险分布 |
| sepsisHighRiskCount | Integer | 脓毒症高风险患者数 |
| sepsisLowRiskCount | Integer | 脓毒症低风险患者数 |
| covid19Stats | Map | COVID-19重型评估分布 |
| covid19SevereCount | Integer | COVID-19重型患者数 |
| covid19NonSevereCount | Integer | COVID-19非重型患者数 |
| covid19CriticalStats | Map | COVID-19危重型评估分布 |
| covid19CriticalCount | Integer | COVID-19危重型患者数 |
| covid19NonCriticalCount | Integer | COVID-19非危重型患者数 |
| respiratorySyndromeStats | Map | 呼吸道症候群评估状态 |
| respiratorySyndromeLevelStats | Map | 呼吸道症候群严重程度分布 |
| assessedPatients | Integer | 已评估患者总数 |
| unassessedPatients | Integer | 未评估患者总数 |

## 统计维度说明

### 1. 社区获得性肺炎风险
- **高风险**: 满足高风险条件的患者
- **低风险**: 不满足高风险条件的患者
- **无数据**: 未进行评估或数据缺失

### 2. 脓毒症风险
- **高风险**: 满足脓毒症高风险条件
- **低风险**: 不满足高风险条件
- **无数据**: 未进行评估或数据缺失

### 3. COVID-19重型
- **重型**: 被诊断为COVID-19重型
- **非重型**: 未被诊断为重型
- **未评估**: 未进行COVID-19重型评估

### 4. COVID-19危重型
- **危重型**: 被诊断为COVID-19危重型
- **非危重型**: 未被诊断为危重型
- **未评估**: 未进行COVID-19危重型评估

### 5. 呼吸道症候群
- **严重程度分级**:
  - 轻度 (MILD)
  - 中度 (MODERATE)
  - 重度 (SEVERE)
  - 无数据: 未评估

## 使用示例

### CURL请求示例
```bash
curl -X GET http://localhost:8080/api/comprehensive-assessment/disease-statistics \
  -H "Content-Type: application/json"
```

### JavaScript/Axios示例
```javascript
// 获取疾病分类统计
async function getDiseaseStatistics() {
    try {
        const response = await axios.get('/api/comprehensive-assessment/disease-statistics');
        const statistics = response.data.data;
        
        console.log('总患者数:', statistics.totalPatients);
        console.log('社区获得性肺炎高风险:', statistics.communityPneumoniaHighRiskCount);
        console.log('脓毒症高风险:', statistics.sepsisHighRiskCount);
        console.log('COVID-19重型:', statistics.covid19SevereCount);
        console.log('COVID-19危重型:', statistics.covid19CriticalCount);
        
        // 处理统计数据...
        displayStatistics(statistics);
    } catch (error) {
        console.error('获取疾病统计失败:', error);
    }
}
```

### 前端展示建议

#### 1. 饼图展示
适合展示各疾病的风险分布：
```javascript
// ECharts配置示例
const pieOption = {
    title: { text: '社区获得性肺炎风险分布' },
    series: [{
        type: 'pie',
        data: [
            { value: statistics.communityPneumoniaHighRiskCount, name: '高风险' },
            { value: statistics.communityPneumoniaLowRiskCount, name: '低风险' }
        ]
    }]
};
```

#### 2. 柱状图对比
适合对比不同疾病的高风险患者数：
```javascript
const barOption = {
    xAxis: {
        data: ['社区获得性肺炎', '脓毒症', 'COVID-19重型', 'COVID-19危重型']
    },
    yAxis: {},
    series: [{
        type: 'bar',
        data: [
            statistics.communityPneumoniaHighRiskCount,
            statistics.sepsisHighRiskCount,
            statistics.covid19SevereCount,
            statistics.covid19CriticalCount
        ]
    }]
};
```

#### 3. 数据表格
```javascript
const tableData = [
    {
        disease: '社区获得性肺炎',
        highRisk: statistics.communityPneumoniaHighRiskCount,
        lowRisk: statistics.communityPneumoniaLowRiskCount,
        total: statistics.totalPatients
    },
    {
        disease: '脓毒症',
        highRisk: statistics.sepsisHighRiskCount,
        lowRisk: statistics.sepsisLowRiskCount,
        total: statistics.totalPatients
    }
    // ...
];
```

## 性能说明
- 该接口会遍历所有患者数据进行统计，数据量大时可能需要较长时间
- 建议在非高峰期调用或使用定时任务预先计算
- 内部使用分页查询（每页10000条）以优化内存使用

## 注意事项
1. 统计数据基于当前时间点的数据快照
2. 呼吸道症候群严重程度会自动从英文转换为中文
3. 统计过程中会记录详细日志，便于追踪和调试
4. 最多处理100页数据（100万条记录）以防止超时

## 错误码
| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 500 | 服务器内部错误 | 检查日志，可能是数据库连接或查询问题 |
| 504 | 请求超时 | 数据量过大，考虑优化查询或分批处理 |
