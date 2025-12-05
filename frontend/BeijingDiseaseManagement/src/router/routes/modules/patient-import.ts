import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const PATIENT_IMPORT: AppRouteRecordRaw = {
  path: '/patient-import',
  name: 'patientImport',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.patientImport',
    requiresAuth: true,
    icon: 'icon-user',
    order: 4,
  },
  children: [
    {
      path: 'his-table',
      name: 'PatientHisTable',
      component: () => import('@/views/patient/his-table.vue'),
      meta: {
        locale: 'menu.patientImport.hisTable',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    // {
    //   path: 'detail',
    //   name: 'PatientDetail',
    //   component: () => import('@/views/patient/PathologyInfoTable.vue'),
    //   meta: {
    //     locale: 'menu.patientImport.detail',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
    // {
    //   path: 'imaging-table',
    //   name: 'PatientImagingTable',
    //   component: () => import('@/views/patient/imaging-table.vue'),
    //   meta: {
    //     locale: 'menu.patientImport.imagingTable',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
    // {
    //   path: 'inspection-table',
    //   name: 'PatientInspectionTable',
    //   component: () => import('@/views/patient/inspection-table.vue'),
    //   meta: {
    //     locale: 'menu.patientImport.inspectionTable',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
    // {
    //   path: 'order-table',
    //   name: 'PatientOrderTable',
    //   component: () => import('@/views/patient/order-table.vue'),
    //   meta: {
    //     locale: 'menu.patientImport.orderTable',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
  ],
};

export default PATIENT_IMPORT;
