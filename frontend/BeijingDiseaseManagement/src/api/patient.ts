import request from '@/utils/request';

export interface Patient {
  id: number;
  admissionNumber: string;
  name: string;
  idCard: string;
  occupation: string;
  bloodType: string;
  t: string;
  n: string;
  m: string;
  tnmStage: string;
  clinicalStage: string;
  address: string;
  phone: string;
  workUnit: string;
  diagnosis: string;
  hospitalDiagnosis: string;
  pathologyDiagnosis: string;
  personalHistory: string;
  marriageHistory: string;
  menstruationHistory: string;
  fertilityHistory: string;
}

export interface PatientListParams {
  page?: number;
  pageSize?: number;
  search?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface PatientListResponse {
  list: Patient[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface PatientStats {
  total: number;
  byOccupation: Record<string, number>;
  byBloodType: Record<string, number>;
  byTnmStage: Record<string, number>;
  byDiagnosis: Record<string, number>;
}

// 获取患者列表
export function getPatients(params?: PatientListParams) {
  return request({
    url: '/api/patients',
    method: 'GET',
    params,
  });
}

// 获取患者详情
export function getPatient(id: number) {
  return request({
    url: `/api/patients/${id}`,
    method: 'GET',
  });
}

// 获取患者统计信息
export function getPatientStats() {
  return request({
    url: '/api/patients/stats',
    method: 'GET',
  });
}

// 搜索患者
export function searchPatients(keyword: string) {
  return request({
    url: '/api/patients/search',
    method: 'GET',
    params: { keyword },
  });
}
