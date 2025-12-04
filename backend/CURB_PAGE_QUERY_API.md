# CURB-65评分分页查询接口文档

## 接口概述
本接口用于分页查询CURB-65评分结果，支持根据总分和风险等级进行条件筛选，返回包含评分详情和患者编号的完整信息。

## 接口信息

### 基本信息
- **接口地址**: `/api/assessment/curb/page`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求体参数

请求体为JSON格式，包含以下字段：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| totalScore | Integer | 否 | - | 总分（0-5分） |
| riskLevel | String | 否 | - | 风险等级（低风险/中风险/高风险） |
| gender | String | 否 | - | 性别（男/女） |
| minAge | Integer | 否 | - | 最小年龄 |
| maxAge | Integer | 否 | - | 最大年龄 |
| current | Integer | 否 | 1 | 当前页码 |
| size | Integer | 否 | 10 | 每页记录数 |

### 请求体示例
```json
{
  "totalScore": 2,
  "riskLevel": "中风险",
  "gender": "男",
  "minAge": 50,
  "maxAge": 70,
  "current": 1,
  "size": 10
}
```

### 参数说明

#### totalScore（总分）
- 取值范围：0-5
- 0分：无任何指标异常
- 1分：1项指标异常
- 2分：2项指标异常
- 3分：3项指标异常
- 4分：4项指标异常
- 5分：5项指标全部异常

#### riskLevel（风险等级）
- 低风险：总分0-1分
- 中风险：总分2分
- 高风险：总分3-5分

#### gender（性别）
- 男：男性患者
- 女：女性患者

#### minAge 和 maxAge（年龄范围）
- minAge：查询年龄大于等于此值的患者
- maxAge：查询年龄小于等于此值的患者
- 可以单独使用或组合使用
- 取值必须大于等于0
- minAge不能大于maxAge

### 响应数据

#### 响应结构
```json
{
  "success": true,
  "message": "success",
  "data": {
    "records": [
      {
        // CURB评分结果字段
        "curbId": 1,
        "ageResult": true,
        "confusionResult": false,
        "ureaResult": true,
        "respirationResult": false,
        "bloodPressureResult": false,
        "totalScore": 2,
        "riskLevel": "中风险",
        "remark": "患者年龄≥65岁，尿素氮>7mmol/L",
        
        // 患者信息
        "patientNumber": "P2024001",
        "gender": "男",
        "age": 68,
        "patientId": 100,
        
        // 评估信息
        "assessmentDate": "2024-01-15 10:30:00",
        "assessmentType": "ADMISSION",
        
        // 系统字段
        "createdAt": "2024-01-15T10:30:00",
        "createdBy": "doctor01",
        "updatedAt": "2024-01-15T10:30:00",
        "updatedBy": "doctor01",
        "isDeleted": 0
      }
    ],
    "total": 100,           // 总记录数
    "size": 10,             // 每页记录数
    "current": 1,           // 当前页码
    "pages": 10             // 总页数
  }
}
```

#### 字段说明

##### CURB评分结果字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| curbId | Long | CURB评分记录ID |
| ageResult | Boolean | 年龄≥65岁 |
| confusionResult | Boolean | 存在意识障碍 |
| ureaResult | Boolean | 尿素氮>7mmol/L |
| respirationResult | Boolean | 呼吸频率≥30次/分 |
| bloodPressureResult | Boolean | 血压异常(SBP<90或DBP≤60) |
| totalScore | Integer | 总分(0-5) |
| riskLevel | String | 风险等级 |
| remark | String | 备注信息 |

##### 患者关联字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| patientNumber | String | 患者编号 |
| gender | String | 性别(男/女) |
| age | Integer | 年龄 |
| patientId | Long | 患者ID |
| assessmentDate | String | 评估时间 |
| assessmentType | String | 评估类型(ADMISSION/FOLLOW_UP/DISCHARGE) |

## 使用示例

### 1. 查询所有评分结果（默认分页）
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{}
```

### 2. 查询总分为2的评分结果
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "totalScore": 2
}
```

### 3. 查询高风险患者
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "riskLevel": "高风险"
}
```

### 4. 组合查询（高风险且总分为3）
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "totalScore": 3,
  "riskLevel": "高风险"
}
```

### 5. 自定义分页（第2页，每页20条）
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "current": 2,
  "size": 20
}
```

### 6. 查询男性患者
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "gender": "男"
}
```

### 7. 查询年龄60岁以上患者
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "minAge": 60
}
```

### 8. 查询年龄40-60岁之间的患者
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "minAge": 40,
  "maxAge": 60
}
```

### 9. 查询65岁以上男性高风险患者
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "gender": "男",
  "minAge": 65,
  "riskLevel": "高风险"
}
```

### 10. 完整组合查询
```bash
POST /api/assessment/curb/page
Content-Type: application/json

{
  "totalScore": 2,
  "riskLevel": "中风险",
  "gender": "女",
  "minAge": 50,
  "maxAge": 70,
  "current": 1,
  "size": 15
}
```

## 错误处理

### 错误响应格式
```json
{
  "success": false,
  "message": "错误信息",
  "data": null
}
```

### 常见错误
1. **总分超出范围**
   - 错误信息：总分必须在0-5之间
   - 原因：totalScore参数不在0-5范围内

2. **风险等级错误**
   - 错误信息：风险等级必须是：低风险、中风险或高风险
   - 原因：riskLevel参数值不正确

3. **性别参数错误**
   - 错误信息：性别必须是：男或女
   - 原因：gender参数值不正确

4. **年龄参数错误**
   - 错误信息：最小年龄不能小于0 / 最大年龄不能小于0
   - 原因：年龄参数为负数

5. **年龄范围错误**
   - 错误信息：最小年龄不能大于最大年龄
   - 原因：minAge > maxAge

6. **系统异常**
   - 错误信息：查询失败: [具体错误]
   - 原因：数据库连接失败或其他系统异常

## 注意事项

1. **分页限制**
   - 建议每页记录数不超过100条
   - 页码从1开始，不是从0开始

2. **查询性能**
   - 涉及三表关联查询，数据量大时可能影响性能
   - 建议添加适当的查询条件缩小范围

3. **数据完整性**
   - 返回的患者编号可能为空（如果患者信息被删除）
   - 评估时间和类型来自关联表

4. **权限控制**
   - 需要登录认证
   - 根据用户权限可能限制查询范围

## 相关接口

- POST `/api/assessment/curb/assess-all` - 批量评估所有患者
- POST `/api/assessment/curb/assess/{patientId}` - 评估单个患者
- GET `/api/assessment/curb/result/{patientId}` - 查询指定患者的评分结果
- GET `/api/assessment/curb/statistics` - 获取统计信息
