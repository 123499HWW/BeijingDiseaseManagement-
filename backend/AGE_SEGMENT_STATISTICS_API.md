# 年龄分段综合评估统计API文档

## 接口概述

基于综合评估数据（`/api/comprehensive-assessment/page`）进行年龄分段统计，统计不同年龄段患者的各项评估指标分布情况。

## 接口信息

### 请求URL
```
GET /api/comprehensive-assessment/age-segment-statistics
```

### 请求方式
GET

### 请求参数
无

### 返回格式
JSON

## 年龄分段定义

系统预定义了6个年龄段：

| 年龄段 | 范围 | 说明 |
|--------|------|------|
| 0-17 | 0-17岁 | 未成年人 |
| 18-29 | 18-29岁 | 青年 |
| 30-44 | 30-44岁 | 中青年 |
| 45-59 | 45-59岁 | 中年 |
| 60-74 | 60-74岁 | 老年 |
| 75+ | 75岁及以上 | 高龄老年 |

## 统计维度

每个年龄段统计以下5个维度的数据：

### 1. COVID-19严重程度分布 (covid19SeverityDistribution)
- **非重型**：未达到重型标准
- **重型**：满足1项重型标准
- **重型（中危）**：满足2项重型标准
- **重型（高危）**：满足3项及以上重型标准
- **无数据**：未进行评估

### 2. COVID-19危重程度分布 (covid19CriticalDistribution)
- **非危重型**：未达到危重型标准
- **危重型**：满足1项危重型标准
- **危重型（高危）**：满足2项危重型标准
- **危重型（极危重）**：满足3项及以上危重型标准
- **无数据**：未进行评估

### 3. 呼吸道症候群严重程度分布 (respiratorySyndromeDistribution)
- **轻度**：0-2分 (MILD)
- **中度**：3-5分 (MODERATE)
- **重度**：6-7分 (SEVERE)
- **危重**：≥8分 (CRITICAL)
- **无数据**：未进行评估

### 4. 社区获得性肺炎风险分布 (communityPneumoniaRiskDistribution)
- **低风险**：各项评估均未达到高风险标准
- **中风险**：部分指标提示中等风险
- **高风险**：多项指标提示高风险
- **极高风险**：重症肺炎或多项高危指标
- **无数据**：未进行评估

### 5. 脓毒症风险分布 (sepsisRiskDistribution)
- **低风险**：qSOFA和SOFA均为低风险
- **中风险**：部分指标提示中等风险
- **高风险**：qSOFA或SOFA提示高风险
- **极高风险**：多项指标同时提示高风险
- **无数据**：未进行评估

## 返回数据结构

### 成功响应
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "ageRange": "0-17",
            "totalPatients": 15,
            "covid19SeverityDistribution": {
                "非重型": 10,
                "重型": 3,
                "重型（中危）": 1,
                "重型（高危）": 1,
                "无数据": 0
            },
            "covid19CriticalDistribution": {
                "非危重型": 12,
                "危重型": 2,
                "危重型（高危）": 1,
                "危重型（极危重）": 0,
                "无数据": 0
            },
            "respiratorySyndromeDistribution": {
                "轻度": 8,
                "中度": 5,
                "重度": 1,
                "危重": 1,
                "无数据": 0
            },
            "communityPneumoniaRiskDistribution": {
                "低风险": 10,
                "中风险": 3,
                "高风险": 2,
                "极高风险": 0,
                "无数据": 0
            },
            "sepsisRiskDistribution": {
                "低风险": 11,
                "中风险": 2,
                "高风险": 2,
                "极高风险": 0,
                "无数据": 0
            }
        },
        {
            "ageRange": "18-29",
            "totalPatients": 28,
            "covid19SeverityDistribution": {
                "非重型": 20,
                "重型": 5,
                "重型（中危）": 2,
                "重型（高危）": 1,
                "无数据": 0
            },
            // ... 其他分布数据
        },
        // ... 其他年龄段
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
| ageRange | String | 年龄段名称 |
| totalPatients | Integer | 该年龄段患者总数 |
| covid19SeverityDistribution | Map<String, Integer> | COVID-19严重程度分布 |
| covid19CriticalDistribution | Map<String, Integer> | COVID-19危重程度分布 |
| respiratorySyndromeDistribution | Map<String, Integer> | 呼吸道症候群严重程度分布 |
| communityPneumoniaRiskDistribution | Map<String, Integer> | 社区获得性肺炎风险分布 |
| sepsisRiskDistribution | Map<String, Integer> | 脓毒症风险分布 |

## 使用示例

### JavaScript/Axios
```javascript
// 获取年龄分段统计数据
axios.get('/api/comprehensive-assessment/age-segment-statistics')
    .then(response => {
        const statistics = response.data.data;
        
        // 遍历每个年龄段
        statistics.forEach(segment => {
            console.log(`年龄段：${segment.ageRange}`);
            console.log(`总人数：${segment.totalPatients}`);
            
            // 统计COVID-19重型患者
            const covid19Severe = segment.covid19SeverityDistribution['重型'] || 0;
            const covid19SevereHigh = segment.covid19SeverityDistribution['重型（高危）'] || 0;
            console.log(`COVID-19重型患者：${covid19Severe + covid19SevereHigh}人`);
            
            // 统计呼吸道症候群危重患者
            const critical = segment.respiratorySyndromeDistribution['危重'] || 0;
            console.log(`呼吸道症候群危重患者：${critical}人`);
        });
    })
    .catch(error => {
        console.error('获取统计数据失败', error);
    });
```

### 图表展示建议

#### 1. 堆叠柱状图
适合展示每个年龄段的各级别分布，如COVID-19严重程度在各年龄段的分布。

#### 2. 饼图
适合展示单个年龄段内各级别的占比，如60-74岁患者的呼吸道症候群严重程度分布。

#### 3. 热力图
适合展示所有年龄段和所有评估维度的综合视图，颜色深浅表示人数多少。

#### 4. 折线图
适合展示不同严重程度在各年龄段的变化趋势，如高风险患者比例随年龄的变化。

## 性能说明

1. **数据量**：该接口会查询所有患者的综合评估数据，数据量较大时可能响应较慢
2. **建议缓存**：统计结果建议进行缓存，缓存时间可设置为1小时
3. **异步处理**：对于大量数据，建议使用异步方式处理，前端显示加载状态
4. **分页查询**：内部实现使用分页查询（每页10000条）以避免内存溢出

## 注意事项

1. **数据完整性**：统计基于已完成评估的患者数据，未评估的项目会统计为"无数据"
2. **实时性**：数据为实时统计，反映当前数据库中的最新状态
3. **权限控制**：该接口应设置适当的访问权限，避免敏感数据泄露
4. **性能优化**：
   - 建议在非高峰期进行统计
   - 可考虑添加定时任务预先计算统计结果
   - 对于大数据量，可考虑使用数据库层面的统计功能

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 404 | 未找到统计数据 | 检查是否有患者数据 |
| 500 | 服务器内部错误 | 查看服务器日志，联系管理员 |
| 503 | 服务暂时不可用 | 稍后重试 |

## 更新日志

- **2024-12-04**：初始版本发布，支持6个年龄段的5个维度统计
