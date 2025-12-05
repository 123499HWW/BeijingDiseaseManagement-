import request from '@/utils/request';

export interface PatientImagingInfo {
  id?: number; // 主键ID，新增时可不传，更新时必传
  patientId: number; // 患者ID
  imagingType: string; // 影像类型（如CT、MRI等）
  imagingDate: string; // 检查日期
  imagingResult: string; // 影像结果描述
  imagingInstitution: string; // 检查机构
  imagingDoctor: string; // 检查医生
  imagingImageUrl: string; // 影像图片URL
  imagingReportUrl: string; // 影像报告URL
  remark: string; // 备注
}

/**
 * 获取影像信息列表
 * @param params 查询参数，如分页、筛选等
 */
function getImagingInfo(
  params: { page?: number; pageSize?: number; [key: string]: any } = {}
) {
  return request.post('/patient/imaging-table', params);
}

/**
 * 新增影像信息
 * @param data 影像信息对象
 */
function addImagingInfo(data: PatientImagingInfo) {
  return request.post('/patient/imaging-table/add', data);
}

/**
 * 更新影像信息
 * @param data 包含id的影像信息对象
 */
function updateImagingInfo(data: PatientImagingInfo) {
  return request.post('/patient/imaging-table/update', data);
}

/**
 * 删除影像信息
 * @param id 影像信息id（number）
 */
function deleteImagingInfo(id: number) {
  return request.post('/patient/imaging-table/delete', { id });
}

export default {
  getImagingInfo,
  addImagingInfo,
  updateImagingInfo,
  deleteImagingInfo,
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
//         "imagingType": "CT", // 影像类型
//         "imagingDate": "2024-05-01", // 检查日期
//         "imagingResult": "肝脏未见明显异常", // 影像结果描述
//         "imagingInstitution": "北京协和医院", // 检查机构
//         "imagingDoctor": "李医生", // 检查医生
//         "imagingImageUrl": "https://example.com/image1.jpg", // 影像图片URL
//         "imagingReportUrl": "https://example.com/report1.pdf", // 影像报告URL
//         "remark": "建议定期复查" // 备注
//       }
//       // ...更多影像信息
//     ],
//     "total": 100, // 总条数
//     "page": 1, // 当前页码
//     "pageSize": 10 // 每页条数
//   }
// }
