import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const SYSTEM: AppRouteRecordRaw = {
  path: '/system',
  name: 'system',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.system',
    icon: 'icon-settings',
    requiresAuth: true,
    order: 10,
  },
  children: [
    // {
    //   path: 'user-setting',
    //   name: 'UserSetting',
    //   component: () => import('@/views/user/setting/index.vue'),
    //   meta: {
    //     locale: 'menu.system.userSetting',
    //     requiresAuth: true,
    //     roles: ['*'],
    //   },
    // },
    {
      path: 'user-manage',
      name: 'UserManage',
      component: () => import('@/views/user/manage.vue'),
      meta: {
        locale: 'menu.system.userManage',
        requiresAuth: true,
        roles: ['*'],
      },
    },
  ],
};

export default SYSTEM;
