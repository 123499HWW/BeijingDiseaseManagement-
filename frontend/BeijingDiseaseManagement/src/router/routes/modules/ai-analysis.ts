import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const AI_ANALYSIS: AppRouteRecordRaw = {
  path: '/ai-analysis',
  name: 'aiAnalysis',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.aiAnalysis',
    requiresAuth: true,
    icon: 'icon-robot',
    order: 6,
  },
  children: [
    {
      path: 'ml',
      name: 'MLAnalysis',
      component: () => import('@/views/patient/PatientDataAnalysis.vue'),
      meta: {
        locale: 'menu.aiAnalysis.ml',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    // {
    //   path: 'llm',
    //   name: 'LLMAnalysis',
    //   component: () => import('@/views/patient/LLMAnalysis/index.vue'),
    //   meta: {
    //     locale: 'menu.aiAnalysis.llm',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
  ],
};

export default AI_ANALYSIS;
