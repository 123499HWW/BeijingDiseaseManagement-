# 月度综合评估统计API文档

## 接口概述

基于患者入院日期（admission_date）按月统计各种疾病的患者分布情况，提供每月患者的全面评估统计数据。

## 接口信息

### 请求URL
```
GET /api/comprehensive-assessment/monthly-statistics
```

### 请求方式
GET

### 请求参数
无

### 返回格式
JSON

## 统计维度

每个月份统计以下维度的数据：

### 1. 基础信息
- **month**：月份（格式：YYYY-MM）
- **totalPatients**：该月患者总数
- **newAdmissions**：该月新入院患者数

### 2. 疾病评估分布（11个评估系统）

#### COVID-19相关
- **covid19SeverityDistribution**：COVID-19严重程度分布
  - 非重型、重型、重型（中危）、重型（高危）、无数据
- **covid19CriticalDistribution**：COVID-19危重程度分布
  - 非危重型、危重型、危重型（高危）、危重型（极危重）、无数据

#### 呼吸道评估
- **respiratorySyndromeDistribution**：呼吸道症候群严重程度分布
  - 轻度、中度、重度、危重、无数据

#### 肺炎评估
- **communityPneumoniaRiskDistribution**：社区获得性肺炎风险分布
  - 低风险、中风险、高风险、极高风险、无数据
- **severePneumoniaDiagnosisDistribution**：重症肺炎诊断分布
  - 重症肺炎、非重症肺炎、无数据

#### 脓毒症评估
- **sepsisRiskDistribution**：脓毒症风险分布
  - 低风险、中风险、高风险、极高风险、无数据

#### 评分系统
- **curbRiskDistribution**：CURB-65风险等级分布
  - 低风险、中风险、高风险、无数据
- **psiRiskDistribution**：PSI风险等级分布
  - I级、II级、III级、IV级、V级、无数据
- **cpisRiskDistribution**：CPIS风险等级分布
  - 低风险、高风险、无数据
- **qsofaRiskDistribution**：qSOFA风险等级分布
  - 低风险、高风险、无数据
- **sofaSeverityDistribution**：SOFA严重程度分布
  - 轻度、中度、重度、极重度、无数据

### 3. 人口统计学分布
- **genderDistribution**：性别分布（男、女、未知）
- **ageGroupDistribution**：年龄段分布（0-17、18-29、30-44、45-59、60-74、75+）

## 返回数据结构

### 成功响应
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "month": "2024-01",
            "totalPatients": 120,
            "newAdmissions": 120,
            "covid19SeverityDistribution": {
                "非重型": 65,
                "重型": 35,
                "重型（中危）": 15,
                "重型（高危）": 5,
                "无数据": 0
            },
            "covid19CriticalDistribution": {
                "非危重型": 85,
                "危重型": 20,
                "危重型（高危）": 10,
                "危重型（极危重）": 5,
                "无数据": 0
            },
            "respiratorySyndromeDistribution": {
                "轻度": 40,
                "中度": 35,
                "重度": 25,
                "危重": 20,
                "无数据": 0
            },
            "communityPneumoniaRiskDistribution": {
                "低风险": 50,
                "中风险": 40,
                "高风险": 25,
                "极高风险": 5,
                "无数据": 0
            },
            "sepsisRiskDistribution": {
                "低风险": 60,
                "中风险": 35,
                "高风险": 20,
                "极高风险": 5,
                "无数据": 0
            },
            "severePneumoniaDiagnosisDistribution": {
                "重症肺炎": 30,
                "非重症肺炎": 90,
                "无数据": 0
            },
            "curbRiskDistribution": {
                "低风险": 45,
                "中风险": 50,
                "高风险": 25,
                "无数据": 0
            },
            "psiRiskDistribution": {
                "I级": 20,
                "II级": 25,
                "III级": 30,
                "IV级": 25,
                "V级": 20,
                "无数据": 0
            },
            "cpisRiskDistribution": {
                "低风险": 70,
                "高风险": 50,
                "无数据": 0
            },
            "qsofaRiskDistribution": {
                "低风险": 75,
                "高风险": 45,
                "无数据": 0
            },
            "sofaSeverityDistribution": {
                "轻度": 35,
                "中度": 40,
                "重度": 30,
                "极重度": 15,
                "无数据": 0
            },
            "genderDistribution": {
                "男": 70,
                "女": 50,
                "未知": 0
            },
            "ageGroupDistribution": {
                "0-17": 5,
                "18-29": 10,
                "30-44": 20,
                "45-59": 35,
                "60-74": 35,
                "75+": 15
            }
        },
        {
            "month": "2024-02",
            // ... 2024年2月数据
        },
        // ... 其他月份
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

## 使用示例

### JavaScript/Axios
```javascript
// 获取月度统计数据
axios.get('/api/comprehensive-assessment/monthly-statistics')
    .then(response => {
        const statistics = response.data.data;
        
        // 按月份排序（如果需要）
        statistics.sort((a, b) => a.month.localeCompare(b.month));
        
        // 遍历每个月份的统计
        statistics.forEach(monthStat => {
            console.log(`月份：${monthStat.month}`);
            console.log(`总患者数：${monthStat.totalPatients}`);
            console.log(`新入院：${monthStat.newAdmissions}`);
            
            // 计算重症比例
            const severe = monthStat.covid19SeverityDistribution['重型（高危）'] || 0;
            const critical = monthStat.covid19CriticalDistribution['危重型（极危重）'] || 0;
            const severeRate = ((severe + critical) / monthStat.totalPatients * 100).toFixed(2);
            console.log(`重症患者比例：${severeRate}%`);
        });
        
        // 生成月度趋势数据
        const trendData = statistics.map(stat => ({
            month: stat.month,
            total: stat.totalPatients,
            severe: stat.covid19SeverityDistribution['重型'] || 0,
            critical: stat.covid19CriticalDistribution['危重型'] || 0
        }));
        
        console.log('月度趋势数据：', trendData);
    })
    .catch(error => {
        console.error('获取统计数据失败', error);
    });
```

### 图表展示建议

#### 1. 时间序列折线图
展示患者总数、重症患者数、危重患者数的月度变化趋势。

#### 2. 堆叠面积图
展示不同严重程度患者的月度分布变化。

#### 3. 热力图
展示各评估系统在不同月份的风险等级分布。

#### 4. 雷达图
展示特定月份在多个评估维度上的综合表现。

#### 5. 柱状图
对比不同月份的患者总数和新入院人数。

## 数据分析应用

### 1. 疫情趋势分析
- 监测疾病流行的时间趋势
- 识别季节性高发期
- 预测未来发病趋势

### 2. 医疗资源规划
- 根据月度患者量预测医疗资源需求
- 合理安排医护人员排班
- 优化床位和设备配置

### 3. 治疗效果评估
- 分析不同时期的治疗效果
- 评估防控措施的有效性
- 优化临床路径

### 4. 早期预警
- 识别患者数量异常增长
- 发现重症比例上升趋势
- 及时启动应急响应

## 性能说明

1. **数据量**：该接口会查询所有患者的综合评估数据并按月份分组
2. **建议缓存**：统计结果建议进行缓存，缓存时间可设置为1小时
3. **异步处理**：对于历史数据较多的情况，建议使用异步方式处理
4. **优化建议**：
   - 可考虑限制查询的时间范围（如最近12个月）
   - 使用数据库层面的GROUP BY优化
   - 预计算历史月份数据

## 注意事项

1. **日期处理**：
   - 月份格式统一为"YYYY-MM"
   - 未知日期的患者归类为"未知月份"
   - 跨年数据会按时间顺序排列

2. **数据完整性**：
   - 统计基于已完成评估的患者数据
   - 未评估的项目统计为"无数据"

3. **时区考虑**：
   - 入院日期按本地时区处理
   - 建议统一使用UTC时间存储

4. **数据更新**：
   - 实时统计反映当前数据库状态
   - 历史数据不会自动更新

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 404 | 未找到统计数据 | 检查是否有患者数据 |
| 500 | 服务器内部错误 | 查看服务器日志，联系管理员 |
| 503 | 服务暂时不可用 | 稍后重试 |

## 扩展功能建议

1. **自定义时间范围**：支持指定起止月份
2. **数据导出**：支持导出Excel或CSV格式
3. **对比分析**：支持同比、环比分析
4. **预测模型**：基于历史数据预测未来趋势

## 更新日志

- **2024-12-04**：初始版本发布，支持按月统计11个评估系统的患者分布
