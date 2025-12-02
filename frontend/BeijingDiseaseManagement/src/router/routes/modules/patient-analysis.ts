import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const PATIENT_ANALYSIS: AppRouteRecordRaw = {
  path: '/patient-analysis',
  name: 'patientAnalysis',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.patientAnalysis',
    requiresAuth: true,
    icon: 'icon-bar-chart',
    order: 5,
  },
  children: [
    {
      path: 'profile',
      name: 'PatientProfile',
      component: () => import('@/views/patient/profile.vue'),
      meta: {
        locale: 'menu.patientAnalysis.profile',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    // {
    //   path: 'profileNew',
    //   name: 'PatientProfileNew',
    //   component: () => import('@/views/patient/profileNew.vue'),
    //   meta: {
    //     locale: 'menu.patientAnalysis.profileNew',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
  ],
};

export default PATIENT_ANALYSIS;
