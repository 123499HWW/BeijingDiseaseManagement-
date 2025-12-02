import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const PATIENT: AppRouteRecordRaw = {
  path: '/patient',
  name: 'patient',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.patient',
    requiresAuth: true,
    icon: 'icon-user',
    order: 4,
  },
  children: [
    {
      path: 'his-table',
      name: 'HisTable',
      component: () => import('@/views/patient/his-table.vue'),
      meta: {
        locale: 'menu.patient.hisTable',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'detail',
      name: 'PatientDetail',
      component: () => import('@/views/patient/PathologyInfoTable.vue'),
      meta: {
        locale: 'menu.patient.detail',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'profile',
      name: 'PatientProfile',
      component: () => import('@/views/patient/profile.vue'),
      meta: {
        locale: 'menu.patient.profile',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'profileNew',
      name: 'PatientProfileNew',
      component: () => import('@/views/patient/profileNew.vue'),
      meta: {
        locale: 'menu.patient.profileNew',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'data-analysis',
      name: 'PatientDataAnalysis',
      component: () => import('@/views/patient/PatientDataAnalysis.vue'),
      meta: {
        locale: 'menu.patient.dataAnalysis',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'llm-analysis',
      name: 'LLMAnalysis',
      component: () => import('@/views/patient/LLMAnalysis/index.vue'),
      meta: {
        locale: 'menu.patient.llmAnalysis',
        requiresAuth: true,
        roles: ['*'],
        icon: 'icon-file-search',
      },
    },
    {
      path: 'inspection-table',
      name: 'PatientInspectionTable',
      component: () => import('@/views/patient/PatientInspectionTable.vue'),
      meta: {
        locale: 'menu.patient.inspectionTable',
        requiresAuth: true,
        roles: ['*'],
      },
    },
  ],
};

export default PATIENT;
