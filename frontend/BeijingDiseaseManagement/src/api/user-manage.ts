import request from '@/utils/request';

export interface UserInfo {
  id?: number; // 主键ID，新增时可不传，更新时必传
  username: string; // 用户名
  realName: string; // 真实姓名
  gender: string; // 性别
  age: number; // 年龄
  phone: string; // 手机号
  email: string; // 邮箱
  role: string; // 角色（如管理员、医生、护士等）
  status: string; // 状态（如启用、禁用）
  department: string; // 所属科室
  createTime: string; // 创建时间
  updateTime: string; // 更新时间
  remark: string; // 备注
}

/**
 * 获取用户信息列表
 * @param params 查询参数，如分页、筛选等
 */
function getUserInfo(
  params: { page?: number; pageSize?: number; [key: string]: any } = {}
) {
  return request.post('/user/manage', params);
}

/**
 * 新增用户信息
 * @param data 用户信息对象
 */
function addUserInfo(data: UserInfo) {
  return request.post('/user/manage/add', data);
}

/**
 * 更新用户信息
 * @param data 包含id的用户信息对象
 */
function updateUserInfo(data: UserInfo) {
  return request.post('/user/manage/update', data);
}

/**
 * 删除用户信息
 * @param id 用户id（number）
 */
function deleteUserInfo(id: number) {
  return request.post('/user/manage/delete', { id });
}

export default {
  getUserInfo,
  addUserInfo,
  updateUserInfo,
  deleteUserInfo,
};

// 返回响应示例：
// {
//   "success": true,
//   "message": "查询成功",
//   "data": {
//     "list": [
//       {
//         "id": 1,
//         "username": "admin", // 用户名
//         "realName": "超级管理员", // 真实姓名
//         "gender": "男", // 性别
//         "age": 35, // 年龄
//         "phone": "13800138000", // 手机号
//         "email": "admin@example.com", // 邮箱
//         "role": "管理员", // 角色
//         "status": "启用", // 状态
//         "department": "信息科", // 所属科室
//         "createTime": "2024-05-01 10:00:00", // 创建时间
//         "updateTime": "2024-05-10 09:00:00", // 更新时间
//         "remark": "系统内置账号" // 备注
//       }
//       // ...更多用户
//     ],
//     "total": 100, // 总条数
//     "page": 1, // 当前页码
//     "pageSize": 10 // 每页条数
//   }
// }
