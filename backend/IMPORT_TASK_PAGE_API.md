# 导入任务分页查询API文档

## 1. 接口概述

将原有的`GET /api/import/list`接口升级为`POST /api/import/page`分页查询接口，支持更丰富的查询条件和分页功能。

## 2. 接口变更说明

| 项目 | 原接口 | 新接口 |
|------|--------|--------|
| 路径 | /api/import/list | /api/import/page |
| 方法 | GET | POST |
| 参数方式 | Query参数 | JSON Body |
| 返回内容 | List<ImportTask> | 分页对象 |
| 查询条件 | 2个（createdBy, taskStatus） | 10+个条件 |

## 3. API接口详情

### 3.1 分页查询导入任务

**接口地址:** `POST /api/import/page`

**请求参数:**
```json
{
  "pageNum": 1,              // 页码，默认1
  "pageSize": 10,            // 每页大小，默认10，最大100
  "taskStatus": "COMPLETED",  // 可选：PENDING/PROCESSING/COMPLETED/FAILED/CANCELLED
  "importType": "PATIENT",    // 可选：PATIENT/EXAMINATION/LAB_RESULT
  "createdBy": "admin",       // 可选：创建人
  "fileName": "患者数据",     // 可选：文件名（模糊查询）
  "startTimeBegin": "2024-12-01 00:00:00",  // 可选：开始时间起
  "startTimeEnd": "2024-12-31 23:59:59",    // 可选：开始时间止
  "endTimeBegin": "2024-12-01 00:00:00",    // 可选：结束时间起
  "endTimeEnd": "2024-12-31 23:59:59",      // 可选：结束时间止
  "minSuccessCount": 0,      // 可选：最小成功数
  "maxSuccessCount": 1000,   // 可选：最大成功数
  "minFailureCount": 0,      // 可选：最小失败数
  "maxFailureCount": 100     // 可选：最大失败数
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "taskId": 1,
        "taskNumber": "TASK20241204001",
        "taskName": "Excel数据导入 - 患者数据.xlsx",
        "importType": "PATIENT",
        "importTypeDesc": "患者数据",
        "fileName": "患者数据.xlsx",
        "filePath": "uploads/import/20241204143000_患者数据.xlsx",
        "fileSize": 102400,
        "totalCount": 100,
        "successCount": 95,
        "failureCount": 5,
        "skipCount": 0,
        "taskStatus": "COMPLETED",
        "taskStatusDesc": "已完成",
        "progressPercent": 95,
        "startTime": "2024-12-04 14:30:00",
        "endTime": "2024-12-04 14:31:30",
        "durationSeconds": 90,
        "errorMessage": null,
        "createdAt": "2024-12-04 14:29:50",
        "createdBy": "admin",
        "updatedAt": "2024-12-04 14:31:30",
        "updatedBy": "admin"
      }
    ],
    "total": 100,        // 总记录数
    "size": 10,          // 每页大小
    "current": 1,        // 当前页码
    "pages": 10          // 总页数
  }
}
```

### 3.2 原有接口（保持兼容）

**接口地址:** `GET /api/import/list`

**请求参数:**
- `createdBy` - 创建人（可选）
- `taskStatus` - 任务状态（可选）

**说明:** 此接口保留以维持向后兼容，建议使用新的分页查询接口。

## 4. 查询条件说明

### 4.1 任务状态（taskStatus）

| 值 | 描述 | 说明 |
|----|------|------|
| PENDING | 待处理 | 任务已创建，等待执行 |
| PROCESSING | 处理中 | 任务正在执行 |
| COMPLETED | 已完成 | 任务执行成功 |
| FAILED | 失败 | 任务执行失败 |
| CANCELLED | 已取消 | 任务被取消 |

### 4.2 导入类型（importType）

| 值 | 描述 | 说明 |
|----|------|------|
| PATIENT | 患者数据 | 患者基本信息导入 |
| PATIENT_DATA | 患者数据 | 患者数据导入（兼容） |
| EXAMINATION | 体检数据 | 体检信息导入 |
| LAB_RESULT | 检验结果 | 检验结果导入 |

## 5. 新增字段说明

### 5.1 计算字段

| 字段 | 类型 | 说明 |
|------|------|------|
| progressPercent | Integer | 导入进度百分比（successCount/totalCount * 100） |
| durationSeconds | Long | 任务执行耗时（秒） |
| taskStatusDesc | String | 任务状态中文描述 |
| importTypeDesc | String | 导入类型中文描述 |

## 6. 使用建议

1. **分页参数**: 建议每页10-20条，最大不超过100条
2. **时间查询**: 使用时间范围查询时，注意时区问题
3. **文件名查询**: 支持模糊匹配，输入部分文件名即可
4. **状态筛选**: 可用于监控任务执行情况
5. **成功/失败数筛选**: 用于质量监控和异常处理

## 7. 迁移指南

### 从旧接口迁移到新接口

```javascript
// 旧接口调用
GET /api/import/list?createdBy=admin&taskStatus=COMPLETED

// 新接口调用
POST /api/import/page
{
  "pageNum": 1,
  "pageSize": 20,
  "createdBy": "admin",
  "taskStatus": "COMPLETED"
}
```

## 8. 注意事项

1. 新接口使用POST方法，请求参数通过JSON Body传递
2. 分页从1开始，不是从0开始
3. 查询结果按创建时间倒序排列
4. 所有时间字段支持LocalDateTime格式
5. 逻辑删除的记录（is_deleted=1）不会出现在查询结果中
