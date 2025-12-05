import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const STATISTICAL_ANALYSIS: AppRouteRecordRaw = {
  path: '/statistical-analysis',
  name: 'statisticalAnalysis',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.statisticalAnalysis',
    requiresAuth: true,
    icon: 'icon-bar-chart',
    order: 8,
  },
  children: [
    {
      path: 'two-group-comparison',
      name: 'TwoGroupComparison',
      component: () =>
        import('@/views/visualization/statistical-analysis/index.vue'),
      meta: {
        locale: 'menu.statisticalAnalysis.twoGroupComparison',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'paired-comparison',
      name: 'PairedComparison',
      component: () =>
        import(
          '@/views/visualization/statistical-analysis/paired-comparison.vue'
        ),
      meta: {
        locale: 'menu.statisticalAnalysis.pairedComparison',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'paired-data-analysis',
      name: 'PairedDataAnalysis',
      component: () =>
        import(
          '@/views/visualization/statistical-analysis/paired-data-analysis.vue'
        ),
      meta: {
        locale: 'menu.statisticalAnalysis.pairedDataAnalysis',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'multi-group-comparison',
      name: 'MultiGroupComparison',
      component: () =>
        import(
          '@/views/visualization/statistical-analysis/multi-group-comparison.vue'
        ),
      meta: {
        locale: 'menu.statisticalAnalysis.multiGroupComparison',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'correlation-analysis',
      name: 'CorrelationAnalysis',
      component: () =>
        import(
          '@/views/visualization/statistical-analysis/correlation-analysis.vue'
        ),
      meta: {
        locale: 'menu.statisticalAnalysis.correlationAnalysis',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'regression-analysis',
      name: 'RegressionAnalysis',
      component: () =>
        import(
          '@/views/visualization/statistical-analysis/regression-analysis.vue'
        ),
      meta: {
        locale: 'menu.statisticalAnalysis.regressionAnalysis',
        requiresAuth: true,
        roles: ['*'],
      },
    },
  ],
};

export default STATISTICAL_ANALYSIS;
