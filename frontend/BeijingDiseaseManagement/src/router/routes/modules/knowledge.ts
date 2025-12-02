import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const KNOWLEDGE: AppRouteRecordRaw = {
  path: '/knowledge',
  name: 'knowledge',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.knowledge',
    requiresAuth: true,
    icon: 'icon-book',
    order: 10,
  },
  children: [
    {
      path: 'list',
      name: 'KnowledgeList',
      component: () => import('@/views/knowledge/list/index.vue'),
      meta: {
        locale: 'menu.knowledge.list',
        requiresAuth: true,
      },
    },
    {
      path: 'add',
      name: 'KnowledgeAdd',
      component: () => import('@/views/knowledge/add/index.vue'),
      meta: {
        locale: 'menu.knowledge.add',
        requiresAuth: true,
        hideInMenu: true,
      },
    },
    {
      path: 'edit/:id',
      name: 'KnowledgeEdit',
      component: () => import('@/views/knowledge/edit/index.vue'),
      meta: {
        locale: 'menu.knowledge.edit',
        requiresAuth: true,
        hideInMenu: true,
      },
    },
    {
      path: 'detail/:id',
      name: 'KnowledgeDetail',
      component: () => import('@/views/knowledge/detail/index.vue'),
      meta: {
        locale: 'menu.knowledge.detail',
        requiresAuth: true,
        hideInMenu: true,
      },
    },
  ],
};
