# 优化版综合评估查询API文档

## 概述

为了确保响应速度控制在2秒内，我们提供了优化版的综合评估查询接口。该接口查询所有评估表的所有字段以及患者基本信息（编号、年龄、性别），通过数据库优化策略大幅提升查询性能。

## 优化策略

### 1. 数据库视图优化
- 创建了9个评估表的最新记录视图
- 避免了复杂的子查询嵌套
- 预先计算每个患者的最新评估记录

### 2. 索引优化
- 为所有关联表添加patient_id索引
- 为is_deleted字段添加索引
- 复合索引优化连接查询

### 3. 查询优化
- 使用视图替代子查询
- 限制每页最大记录数为50条
- 精确匹配替代模糊查询

### 4. 缓存策略
- 提供缓存版接口
- 支持热点数据缓存
- 自动缓存键生成

## 接口列表

### 1. 优化版分页查询

#### 接口URL
```
POST /api/comprehensive-assessment/page-optimized
```

#### 请求参数
```json
{
    "pageNum": 1,           // 页码，默认1
    "pageSize": 20,         // 每页记录数，默认10，最大50
    "patientNumber": "P001", // 患者编号（精确匹配）
    "gender": "男",         // 性别（男/女）
    "minAge": 18,          // 最小年龄
    "maxAge": 65,          // 最大年龄
    "hasAssessment": true   // 是否有评估记录
}
```

#### 返回示例
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [
            {
                "patientId": 1,
                "patientNumber": "P001",
                "gender": "男",
                "age": 45,
                "admissionDate": "2024-01-15",
                "chiefComplaint": "发热、咳嗽",
                
                // CURB-65评分
                "curbId": 101,
                "curbTotalScore": 3,
                "curbRiskLevel": "中风险",
                "curbConfusion": 0,
                "curbBloodUrea": 1,
                "curbRespiratoryRate": 1,
                "curbBloodPressure": 0,
                "curbAgeFactor": 1,
                
                // COVID-19重型
                "covid19AssessmentId": 201,
                "covid19IsSevereType": true,
                "covid19CriteriaCount": 2,
                "covid19SeverityLevel": "重型",
                "covid19AssessmentResult": "满足2项重型标准",
                
                // COVID-19危重型
                "covid19CriticalAssessmentId": 301,
                "covid19IsCriticalType": false,
                "covid19CriticalCriteriaCount": 0,
                "covid19CriticalSeverityLevel": "非危重型",
                "covid19CriticalAssessmentResult": "未满足危重型标准",
                
                // CPIS评分
                "cpisId": 401,
                "cpisTotalScore": 4,
                "cpisRiskLevel": "低风险",
                "cpisTemperatureScore": 1,
                "cpisWbcScore": 1,
                "cpisSecretionScore": 1,
                "cpisOxygenationScore": 1,
                "cpisXrayScore": 0,
                
                // PSI评分
                "psiId": 501,
                "psiTotalScore": 85,
                "psiRiskClass": "III级",
                "psiDemographicScore": 45,
                "psiComorbidityScore": 20,
                "psiPhysicalScore": 10,
                "psiLaboratoryScore": 10,
                
                // qSOFA评分
                "qsofaAssessmentId": 601,
                "qsofaTotalScore": 2,
                "qsofaRiskLevel": "高风险",
                "qsofaAlteredMentation": 1,
                "qsofaSystolicBp": 0,
                "qsofaRespiratoryRate": 1,
                
                // 重症肺炎诊断
                "severePneumoniaId": 701,
                "isSeverePneumonia": false,
                "majorCriteriaCount": 0,
                "minorCriteriaCount": 2,
                "severePneumoniaConclusion": "非重症肺炎",
                
                // SOFA评分
                "sofaAssessmentId": 801,
                "sofaTotalScore": 6,
                "sofaSeverityLevel": "中度",
                "sofaRespiratoryScore": 2,
                "sofaCoagulationScore": 1,
                "sofaLiverScore": 0,
                "sofaCardiovascularScore": 1,
                "sofaNeurologicalScore": 1,
                "sofaRenalScore": 1,
                
                // 呼吸道症候群评估
                "respiratorySyndromeId": 901,
                "respiratorySyndromeSeverityScore": 5,
                "respiratorySyndromeSeverityLevel": "中度",
                "respiratoryRiskFactorsCount": 3,
                
                // 综合风险评级
                "communityAcquiredPneumoniaRisk": "中风险",
                "sepsisRisk": "中风险"
            }
        ],
        "total": 150,
        "size": 20,
        "current": 1,
        "pages": 8
    }
}
```

### 2. 缓存版分页查询

#### 接口URL
```
POST /api/comprehensive-assessment/page-cached?useCache=true
```

#### 请求参数
与优化版相同，额外支持`useCache`查询参数：
- `useCache=true`：使用缓存（默认）
- `useCache=false`：跳过缓存，直接查询数据库

## 性能指标

### 响应时间要求
- **优秀**: < 500ms
- **良好**: 500ms - 1000ms
- **可接受**: 1000ms - 2000ms
- **需优化**: > 2000ms

### 性能监控
接口会在日志中记录以下性能指标：
- 执行时间（executionTime）
- 总记录数（totalRecords）
- 当前页记录数（currentPageRecords）
- 性能评级（performance）

## 数据库准备

### 1. 创建视图
执行 `database/create_assessment_views.sql` 文件中的SQL语句创建必要的数据库视图。

```sql
-- 示例：创建CURB-65最新记录视图
CREATE OR REPLACE VIEW curb_latest_view AS 
SELECT pcr.patient_id, car.* 
FROM curb_assessment_result car 
INNER JOIN patient_curb_relation pcr ON car.curb_id = pcr.curb_id 
-- ... 详见SQL文件
```

### 2. 添加索引
确保以下索引已创建：
```sql
CREATE INDEX idx_patient_info_patient_number ON patient_info(patient_number);
CREATE INDEX idx_patient_info_gender_age ON patient_info(gender, age);
-- ... 更多索引详见SQL文件
```

## 使用示例

### JavaScript/Axios
```javascript
// 优化版查询
const queryData = {
    pageNum: 1,
    pageSize: 20,
    gender: '男',
    minAge: 30,
    maxAge: 60,
    hasAssessment: true
};

axios.post('/api/comprehensive-assessment/page-optimized', queryData)
    .then(response => {
        const data = response.data.data;
        console.log(`查询成功，共${data.total}条记录`);
        console.log(`当前页有${data.records.length}条数据`);
        
        // 处理数据
        data.records.forEach(record => {
            console.log(`患者${record.patientNumber}的评估结果：`);
            console.log(`- CURB-65: ${record.curbTotalScore}分 (${record.curbRiskLevel})`);
            console.log(`- PSI: ${record.psiTotalScore}分 (${record.psiRiskClass})`);
            console.log(`- SOFA: ${record.sofaTotalScore}分 (${record.sofaSeverityLevel})`);
        });
    })
    .catch(error => {
        console.error('查询失败', error);
    });

// 使用缓存版
axios.post('/api/comprehensive-assessment/page-cached?useCache=true', queryData)
    .then(response => {
        // 处理缓存数据
    });
```

### Java调用示例
```java
// 构建查询条件
ComprehensiveAssessmentQueryDTO queryDTO = new ComprehensiveAssessmentQueryDTO();
queryDTO.setPageNum(1);
queryDTO.setPageSize(20);
queryDTO.setGender("男");
queryDTO.setMinAge(30);
queryDTO.setMaxAge(60);
queryDTO.setHasAssessment(true);

// 调用优化版查询
IPage<ComprehensiveAssessmentVO> result = comprehensiveAssessmentService
    .queryComprehensivePageOptimized(queryDTO);

// 处理结果
System.out.println("总记录数：" + result.getTotal());
System.out.println("当前页记录数：" + result.getRecords().size());
```

## 注意事项

1. **分页限制**：每页最大50条记录，超过会自动限制为50条
2. **查询优化**：患者编号使用精确匹配而非模糊查询
3. **视图依赖**：必须先创建数据库视图才能使用优化版接口
4. **缓存更新**：数据更新后，缓存会自动失效（TODO：实现缓存失效机制）
5. **并发控制**：高并发场景下建议使用缓存版接口

## 故障排查

### 响应时间超过2秒
1. 检查数据库视图是否创建成功
2. 验证索引是否正确添加
3. 分析慢查询日志
4. 考虑减少每页记录数

### 查询结果不准确
1. 检查视图定义是否正确
2. 验证is_deleted逻辑
3. 确认最新记录获取逻辑

### 缓存问题
1. 检查缓存配置
2. 验证缓存键生成逻辑
3. 确认缓存失效策略

## 性能优化建议

1. **定期维护**：定期ANALYZE表和视图
2. **分区表**：考虑对大表进行分区
3. **读写分离**：配置主从数据库
4. **异步加载**：前端实现懒加载和虚拟滚动
5. **CDN加速**：静态资源使用CDN

## 更新日志

- **2024-12-04**：初始版本，实现优化查询和缓存机制
