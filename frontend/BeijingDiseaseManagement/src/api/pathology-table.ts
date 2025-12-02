import request from '@/utils/request';

export interface PatientPathologyInfo {
  id?: number; // 主键ID，新增时可不传，更新时必传
  patientId: number; // 患者ID
  pathologyNo: string; // 病理号
  pathologyType: string; // 病理类型（如活检、手术等）
  pathologyDate: string; // 病理日期
  specimenType: string; // 标本类型
  grossDescription: string; // 大体描述
  microscopicDescription: string; // 镜下描述
  immunohistochemistry: string; // 免疫组化
  molecularTesting: string; // 分子检测
  diagnosis: string; // 病理诊断
  pathologist: string; // 病理医生
  reportUrl: string; // 病理报告URL
  remark: string; // 备注
}

/**
 * 获取病理分析信息列表
 * @param params 查询参数，如分页、筛选等
 */
function getPathologyInfo(
  params: { page?: number; pageSize?: number; [key: string]: any } = {}
) {
  return request.post('/patient/pathology-table', params);
}

/**
 * 新增病理分析信息
 * @param data 病理分析信息对象
 */
function addPathologyInfo(data: PatientPathologyInfo) {
  return request.post('/patient/pathology-table/add', data);
}

/**
 * 更新病理分析信息
 * @param data 包含id的病理分析信息对象
 */
function updatePathologyInfo(data: PatientPathologyInfo) {
  return request.post('/patient/pathology-table/update', data);
}

/**
 * 删除病理分析信息
 * @param id 病理分析信息id（number）
 */
function deletePathologyInfo(id: number) {
  return request.post('/patient/pathology-table/delete', { id });
}

export default {
  getPathologyInfo,
  addPathologyInfo,
  updatePathologyInfo,
  deletePathologyInfo,
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
//         "pathologyNo": "PATH20240501001", // 病理号
//         "pathologyType": "手术", // 病理类型
//         "pathologyDate": "2024-05-01", // 病理日期
//         "specimenType": "肝组织", // 标本类型
//         "grossDescription": "肝脏表面光滑，切面灰白", // 大体描述
//         "microscopicDescription": "肝细胞排列紊乱，部分区域见肿瘤细胞", // 镜下描述
//         "immunohistochemistry": "CK(+), HepPar-1(+)", // 免疫组化
//         "molecularTesting": "未见突变", // 分子检测
//         "diagnosis": "肝细胞癌", // 病理诊断
//         "pathologist": "张医生", // 病理医生
//         "reportUrl": "https://example.com/pathology1.pdf", // 病理报告URL
//         "remark": "建议随访" // 备注
//       }
//       // ...更多病理分析信息
//     ],
//     "total": 100, // 总条数
//     "page": 1, // 当前页码
//     "pageSize": 10 // 每页条数
//   }
// }
