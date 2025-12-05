import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const PATIENT_FEATURE_ANALYSIS: AppRouteRecordRaw = {
  path: '/patient-feature-analysis',
  name: 'patientFeatureAnalysis',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.patientFeatureAnalysis',
    requiresAuth: true,
    icon: 'icon-pie-chart',
    order: 7,
  },
  children: [
    {
      path: 'data-analysis',
      name: 'PatientDataAnalysisVisualization',
      component: () => import('@/views/visualization/data-analysis/index.vue'),
      meta: {
        locale: 'menu.patientFeatureAnalysis.dataAnalysis',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'multi-dimension-data-analysis',
      name: 'PatientMultiDimensionDataAnalysis',
      component: () =>
        import('@/views/visualization/multi-dimension-data-analysis/index.vue'),
      meta: {
        locale: 'menu.patientFeatureAnalysis.multiDimensionDataAnalysis',
        requiresAuth: true,
        roles: ['*'],
      },
    },

  ],
};

export default PATIENT_FEATURE_ANALYSIS;
