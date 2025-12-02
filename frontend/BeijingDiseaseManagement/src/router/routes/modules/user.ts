import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const USER_CENTER: AppRouteRecordRaw = {
  path: '/user',
  name: 'User',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.user',
    icon: 'icon-user',
    requiresAuth: true,
    order: 7,
  },
  children: [
    {
      path: 'info',
      name: 'Info',
      component: () => import('@/views/user/info/index.vue'),
      meta: {
        locale: 'menu.user.info',
        requiresAuth: true,
        roles: ['*'],
      },
    },
    {
      path: 'setting',
      name: 'Setting',
      component: () => import('@/views/user/setting/index.vue'),
      meta: {
        locale: 'menu.user.setting',
        requiresAuth: true,
        roles: ['*'],
      },
    },
  ],
};

const USER_MANAGE: AppRouteRecordRaw = {
  path: '/manage',
  name: 'UserManage',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.user.manage',
    icon: 'icon-user',
    requiresAuth: true,
    order: 8,
  },
  children: [
    {
      path: '',
      name: 'ManageHome',
      component: () => import('@/views/user/manage.vue'),
      meta: {
        locale: 'menu.user.manage',
        requiresAuth: true,
        roles: ['*'],
      },
    },
  ],
};

export default [USER_CENTER, USER_MANAGE];
