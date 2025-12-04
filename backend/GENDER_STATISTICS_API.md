# 性别分类综合评估统计API文档

## 接口概述

基于综合评估数据进行性别分类统计，对比男性和女性患者的各项评估指标分布情况，并提供年龄相关的统计信息。

## 接口信息

### 请求URL
```
GET /api/comprehensive-assessment/gender-statistics
```

### 请求方式
GET

### 请求参数
无

### 返回格式
JSON

## 统计维度

每个性别分组统计以下维度的数据：

### 1. 基础统计信息
- **totalPatients**：该性别的患者总数
- **averageAge**：平均年龄
- **minAge**：最小年龄
- **maxAge**：最大年龄

### 2. COVID-19严重程度分布 (covid19SeverityDistribution)
- **非重型**：未达到重型标准
- **重型**：满足1项重型标准
- **重型（中危）**：满足2项重型标准
- **重型（高危）**：满足3项及以上重型标准
- **无数据**：未进行评估

### 3. COVID-19危重程度分布 (covid19CriticalDistribution)
- **非危重型**：未达到危重型标准
- **危重型**：满足1项危重型标准
- **危重型（高危）**：满足2项危重型标准
- **危重型（极危重）**：满足3项及以上危重型标准
- **无数据**：未进行评估

### 4. 呼吸道症候群严重程度分布 (respiratorySyndromeDistribution)
- **轻度**：0-2分
- **中度**：3-5分
- **重度**：6-7分
- **危重**：≥8分
- **无数据**：未进行评估

### 5. 社区获得性肺炎风险分布 (communityPneumoniaRiskDistribution)
- **低风险**：各项评估均未达到高风险标准
- **中风险**：部分指标提示中等风险
- **高风险**：多项指标提示高风险
- **极高风险**：重症肺炎或多项高危指标
- **无数据**：未进行评估

### 6. 脓毒症风险分布 (sepsisRiskDistribution)
- **低风险**：qSOFA和SOFA均为低风险
- **中风险**：部分指标提示中等风险
- **高风险**：qSOFA或SOFA提示高风险
- **极高风险**：多项指标同时提示高风险
- **无数据**：未进行评估

### 7. 年龄段分布 (ageGroupDistribution)
- **0-17**：未成年人
- **18-29**：青年
- **30-44**：中青年
- **45-59**：中年
- **60-74**：老年
- **75+**：高龄老年

## 返回数据结构

### 成功响应
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "gender": "男",
            "totalPatients": 85,
            "averageAge": 56.4,
            "minAge": 23,
            "maxAge": 88,
            "covid19SeverityDistribution": {
                "非重型": 45,
                "重型": 25,
                "重型（中危）": 10,
                "重型（高危）": 5,
                "无数据": 0
            },
            "covid19CriticalDistribution": {
                "非危重型": 60,
                "危重型": 15,
                "危重型（高危）": 7,
                "危重型（极危重）": 3,
                "无数据": 0
            },
            "respiratorySyndromeDistribution": {
                "轻度": 30,
                "中度": 25,
                "重度": 20,
                "危重": 10,
                "无数据": 0
            },
            "communityPneumoniaRiskDistribution": {
                "低风险": 40,
                "中风险": 25,
                "高风险": 15,
                "极高风险": 5,
                "无数据": 0
            },
            "sepsisRiskDistribution": {
                "低风险": 50,
                "中风险": 20,
                "高风险": 10,
                "极高风险": 5,
                "无数据": 0
            },
            "ageGroupDistribution": {
                "0-17": 2,
                "18-29": 8,
                "30-44": 15,
                "45-59": 25,
                "60-74": 25,
                "75+": 10
            }
        },
        {
            "gender": "女",
            "totalPatients": 65,
            "averageAge": 52.3,
            "minAge": 18,
            "maxAge": 82,
            "covid19SeverityDistribution": {
                "非重型": 40,
                "重型": 15,
                "重型（中危）": 7,
                "重型（高危）": 3,
                "无数据": 0
            },
            // ... 其他分布数据
        }
    ]
}
```

### 失败响应
```json
{
    "code": 500,
    "message": "统计失败: 错误信息",
    "data": null
}
```

## 返回字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| gender | String | 性别（男/女） |
| totalPatients | Integer | 该性别患者总数 |
| averageAge | Double | 平均年龄 |
| minAge | Integer | 最小年龄 |
| maxAge | Integer | 最大年龄 |
| covid19SeverityDistribution | Map<String, Integer> | COVID-19严重程度分布 |
| covid19CriticalDistribution | Map<String, Integer> | COVID-19危重程度分布 |
| respiratorySyndromeDistribution | Map<String, Integer> | 呼吸道症候群严重程度分布 |
| communityPneumoniaRiskDistribution | Map<String, Integer> | 社区获得性肺炎风险分布 |
| sepsisRiskDistribution | Map<String, Integer> | 脓毒症风险分布 |
| ageGroupDistribution | Map<String, Integer> | 年龄段分布 |

## 使用示例

### JavaScript/Axios
```javascript
// 获取性别分类统计数据
axios.get('/api/comprehensive-assessment/gender-statistics')
    .then(response => {
        const statistics = response.data.data;
        
        // 遍历每个性别的统计
        statistics.forEach(stat => {
            console.log(`性别：${stat.gender}`);
            console.log(`总人数：${stat.totalPatients}`);
            console.log(`平均年龄：${stat.averageAge?.toFixed(1)}岁`);
            console.log(`年龄范围：${stat.minAge}-${stat.maxAge}岁`);
            
            // 计算高风险患者比例
            const highRisk = (stat.covid19SeverityDistribution['重型（高危）'] || 0) +
                           (stat.covid19CriticalDistribution['危重型（极危重）'] || 0);
            const highRiskRate = (highRisk / stat.totalPatients * 100).toFixed(2);
            console.log(`高风险患者比例：${highRiskRate}%`);
        });
        
        // 性别对比
        const male = statistics.find(s => s.gender === '男');
        const female = statistics.find(s => s.gender === '女');
        
        if (male && female) {
            const maleRatio = (male.totalPatients / (male.totalPatients + female.totalPatients) * 100).toFixed(1);
            console.log(`男性占比：${maleRatio}%`);
        }
    })
    .catch(error => {
        console.error('获取统计数据失败', error);
    });
```

### 图表展示建议

#### 1. 对比柱状图
适合展示男女两组在各项评估指标上的对比，如COVID-19严重程度分布对比。

#### 2. 雷达图
适合展示男女两组在多个维度上的综合对比，如各风险等级的占比。

#### 3. 饼图组
适合展示每个性别内部的分布情况，如男性患者的年龄段分布。

#### 4. 箱线图
适合展示男女两组的年龄分布差异，包括中位数、四分位数等。

## 数据分析建议

### 1. 性别差异分析
- 比较男女在疾病严重程度上的差异
- 分析是否存在性别相关的风险因素

### 2. 年龄因素分析
- 结合年龄段分布，分析性别和年龄的交互影响
- 识别高风险人群特征

### 3. 综合风险评估
- 综合多个评估维度，识别性别特异性的风险模式
- 为个性化治疗方案提供依据

## 性能说明

1. **数据量**：该接口会查询所有患者的综合评估数据，分别统计男性和女性
2. **建议缓存**：统计结果建议进行缓存，缓存时间可设置为1小时
3. **异步处理**：对于大量数据，建议使用异步方式处理
4. **优化建议**：可考虑使用数据库层面的GROUP BY进行统计优化

## 注意事项

1. **数据完整性**：统计基于已完成评估的患者数据
2. **样本偏差**：注意样本中男女比例可能不均衡
3. **隐私保护**：统计数据为汇总信息，不包含个人识别信息
4. **实时性**：数据为实时统计，反映当前数据库中的最新状态

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 404 | 未找到统计数据 | 检查是否有患者数据 |
| 500 | 服务器内部错误 | 查看服务器日志，联系管理员 |
| 503 | 服务暂时不可用 | 稍后重试 |

## 更新日志

- **2024-12-04**：初始版本发布，支持男女性别的综合评估统计和年龄分布分析
