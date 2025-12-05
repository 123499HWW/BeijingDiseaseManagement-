import request from '@/utils/request';

export interface PatientInspectionInfo {
  id?: number; // 主键ID，新增时可不传，更新时必传
  patientId: number; // 患者ID
  inspectionType: string; // 检查类型（如血常规、生化等）
  inspectionDate: string; // 检查日期
  inspectionItem: string; // 检查项目
  inspectionResult: string; // 检查结果
  unit: string; // 单位
  referenceRange: string; // 参考范围
  abnormalFlag: string; // 异常标志（如↑、↓、正常）
  inspectionInstitution: string; // 检查机构
  inspectionDoctor: string; // 检查医生
  remark: string; // 备注
}

/**
 * 获取指标信息列表
 * @param params 查询参数，如分页、筛选等
 */
function getInspectionInfo(
  params: { page?: number; pageSize?: number; [key: string]: any } = {}
) {
  return request.post('/patient/inspection-table', params);
}

/**
 * 新增指标信息
 * @param data 指标信息对象
 */
function addInspectionInfo(data: PatientInspectionInfo) {
  return request.post('/patient/inspection-table/add', data);
}

/**
 * 更新指标信息
 * @param data 包含id的指标信息对象
 */
function updateInspectionInfo(data: PatientInspectionInfo) {
  return request.post('/patient/inspection-table/update', data);
}

/**
 * 删除指标信息
 * @param id 指标信息id（number）
 */
function deleteInspectionInfo(id: number) {
  return request.post('/patient/inspection-table/delete', { id });
}

/**
 * 根据 inspectionItem 查询该项目的历史日期及对应值
 * @param params { inspectionItem: string; patientId?: number; startDate?: string; endDate?: string }
 * inspectionItem 检查项目名称，patientId 可选，startDate/endDate 可选时间范围
 */
function getInspectionItemHistory(params: {
  inspectionItem: string;
  patientId?: number;
  startDate?: string;
  endDate?: string;
}) {
  return request.post('/patient/inspection-table/history', params);
}

/**
 * 批量查询多个 inspectionItem 的历史日期及对应值
 * @param params { inspectionItems: string[]; patientId?: number; startDate?: string; endDate?: string }
 * inspectionItems 检查项目名称数组，patientId 可选，startDate/endDate 可选时间范围
 */
function getMultipleInspectionItemsHistory(params: {
  inspectionItems: string[];
  patientId?: number;
  startDate?: string;
  endDate?: string;
}) {
  return request.post('/patient/inspection-table/history/batch', params);
}

export default {
  getInspectionInfo,
  addInspectionInfo,
  updateInspectionInfo,
  deleteInspectionInfo,
  getInspectionItemHistory,
  getMultipleInspectionItemsHistory,
};

// 返回响应示例：
// {
//   "success": true,
//   "message": "查询成功",
//   "data": {
//     "list": [
//       {
//         "id": 1,
//         "patientId": 1, // 患者ID
//         "inspectionType": "血常规", // 检查类型
//         "inspectionDate": "2024-05-01", // 检查日期
//         "inspectionItem": "白细胞计数", // 检查项目
//         "inspectionResult": "6.5", // 检查结果
//         "unit": "10^9/L", // 单位
//         "referenceRange": "4.0-10.0", // 参考范围
//         "abnormalFlag": "正常", // 异常标志
//         "inspectionInstitution": "北京协和医院", // 检查机构
//         "inspectionDoctor": "王医生", // 检查医生
//         "remark": "无异常" // 备注
//       }
//       // ...更多指标信息
//     ],
//     "total": 100, // 总条数
//     "page": 1, // 当前页码
//     "pageSize": 10 // 每页条数
//   }
// }

// getInspectionItemHistory 响应示例：
// {
//   "success": true,
//   "message": "查询成功",
//   "data": {
//     "inspectionItem": "血红蛋白",
//     "patientId": 1,
//     "history": [
//       { "date": "2024-04-01", "value": "120" },
//       { "date": "2024-04-15", "value": "125" },
//       { "date": "2024-05-01", "value": "130" }
//     ]
//   }
// }

// getMultipleInspectionItemsHistory 响应示例：
// {
//   "success": true,
//   "message": "查询成功",
//   "data": [
//     {
//       "inspectionItem": "血红蛋白",
//       "patientId": 1,
//       "history": [
//         { "date": "2024-04-01", "value": "120" },
//         { "date": "2024-04-15", "value": "125" }
//       ]
//     },
//     {
//       "inspectionItem": "白细胞",
//       "patientId": 1,
//       "history": [
//         { "date": "2024-04-01", "value": "5.2" },
//         { "date": "2024-04-15", "value": "5.8" }
//       ]
//     }
//   ]
// }
