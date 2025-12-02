import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const DESCRIPTIVE_ANALYSIS: AppRouteRecordRaw = {
  path: '/descriptive-analysis',
  name: 'descriptiveAnalysis',
  component: DEFAULT_LAYOUT,
  meta: {
    // locale: 'menu.descriptiveAnalysis',
    // requiresAuth: true,
    // icon: 'icon-pie-chart',
    // order: 9,
  },
  children: [
    {
      path: 'index',
      name: 'DescriptiveAnalysisIndex',
      component: () =>
        import('@/views/visualization/descriptive-analysis/index.vue'),
      meta: {
        locale: 'menu.descriptiveAnalysis.index',
        requiresAuth: true,
        roles: ['*'],
      },
    },
  ],
};

export default DESCRIPTIVE_ANALYSIS;
