import request from '@/utils/request';

export interface PatientBasicInfo {
  id?: number; // 主键ID，新增时可不传，更新时必传
  hospitalNo: string; // 住院号
  name: string; // 姓名
  idCard: string; // 身份证号
  number: string; // 患者编号
  job: string; // 职业
  bloodType: string; // 血型
  t: string; // T分期
  n: string; // N分期
  m: string; // M分期
  tnmStage: string; // TNM分期
  clinicalStage: string; // 临床分期
  address: string; // 地址
  phone: string; // 电话
  workplace: string; // 工作单位
  diagnosis: string; // 诊断
  hospitalDiagnosis: string; // 入院诊断
  pathologyDiagnosis: string; // 病理诊断
  personalHistory: string; // 个人史
  maritalHistory: string; // 婚育史
  menstrualHistory: string; // 月经史
  fertilityHistory: string; // 生育史
  pathologyNo: string; // 病理号
  reportDate: string; // 报告日期
  grossDescription: string; // 大体描述
}

/**
 * 获取患者基本信息
 * @param params 查询参数，如分页、筛选等
 */
function getPatientBasicInfo(
  params: { page?: number; pageSize?: number; [key: string]: any } = {}
) {
  return request.post('/patient/his-table', params);
}

/**
 * 新增患者基本信息
 * @param data 患者基本信息对象
 */
function addPatientBasicInfo(data: PatientBasicInfo) {
  return request.post('/patient/his-table/add', data);
}

/**
 * 更新患者基本信息
 * @param data 包含id的患者基本信息对象
 */
function updatePatientBasicInfo(data: PatientBasicInfo) {
  return request.post('/patient/his-table/update', data);
}

/**
 * 删除患者基本信息
 * @param id 患者id（number）
 */
function deletePatientBasicInfo(id: number) {
  return request.post('/patient/his-table/delete', { id });
}

export default {
  getPatientBasicInfo,
  addPatientBasicInfo,
  updatePatientBasicInfo,
  deletePatientBasicInfo,
};

// 响应示例：
// {
//   "success": true,
//   "message": "操作成功",
//   "data": { ... }
// }

// 返回响应示例：
// {
//   "success": true,
//   "message": "查询成功",
//   "data": {
//     "list": [
//       {
//         "id": 1,
//         "hospitalNo": "20230715001", // 住院号
//         "name": "张三", // 姓名
//         "idCard": "110101199001011234", // 身份证号
//         "number": "P20240501001", // 患者编号
//         "job": "工程师", // 职业
//         "bloodType": "A型", // 血型
//         "t": "T2", // T分期
//         "n": "N1", // N分期
//         "m": "M0", // M分期
//         "tnmStage": "II期", // TNM分期
//         "clinicalStage": "中期", // 临床分期
//         "address": "北京市朝阳区建国路88号", // 地址
//         "phone": "13800138000", // 电话
//         "workplace": "北京科技有限公司", // 工作单位
//         "diagnosis": "高血压、糖尿病", // 诊断
//         "hospitalDiagnosis": "高血压", // 入院诊断
//         "pathologyDiagnosis": "肝细胞癌", // 病理诊断
//         "personalHistory": "无吸烟史，无饮酒史", // 个人史
//         "maritalHistory": "已婚，2子女", // 婚育史
//         "menstrualHistory": "规律，28天/次", // 月经史
//         "fertilityHistory": "2次足月顺产", // 生育史
//         "pathologyNo": "PATH20240501001", // 病理号
//         "reportDate": "2024-05-01", // 报告日期
//         "grossDescription": "肝脏表面光滑，切面灰白" // 大体描述
//       }
//       // ...更多患者
//     ],
//     "total": 100, // 总条数
//     "page": 1, // 当前页码
//     "pageSize": 10 // 每页条数
//   }
// }
