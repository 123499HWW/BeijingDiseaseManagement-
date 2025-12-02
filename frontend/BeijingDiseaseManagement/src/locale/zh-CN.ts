import localeMessageBox from '@/components/message-box/locale/zh-CN';
import localeLogin from '@/views/login/locale/zh-CN';

import localeWorkplace from '@/views/dashboard/workplace/locale/zh-CN';
/** simple */
import localeMonitor from '@/views/dashboard/monitor/locale/zh-CN';

import localeSearchTable from '@/views/list/search-table/locale/zh-CN';
import localeCardList from '@/views/list/card/locale/zh-CN';

import localeStepForm from '@/views/form/step/locale/zh-CN';
import localeGroupForm from '@/views/form/group/locale/zh-CN';

import localeBasicProfile from '@/views/profile/basic/locale/zh-CN';

import localeDataAnalysis from '@/views/visualization/data-analysis/locale/zh-CN';
import localeMultiDAnalysis from '@/views/visualization/multi-dimension-data-analysis/locale/zh-CN';

import localeSuccess from '@/views/result/success/locale/zh-CN';
import localeError from '@/views/result/error/locale/zh-CN';

import locale403 from '@/views/exception/403/locale/zh-CN';
import locale404 from '@/views/exception/404/locale/zh-CN';
import locale500 from '@/views/exception/500/locale/zh-CN';

import localeUserInfo from '@/views/user/info/locale/zh-CN';
import localeUserSetting from '@/views/user/setting/locale/zh-CN';
/** simple end */
import localeSettings from './zh-CN/settings';

export default {
  'menu.dashboard': '仪表盘',
  'menu.server.dashboard': '仪表盘-服务端',
  'menu.server.workplace': '工作台-服务端',
  'menu.server.monitor': '实时监控-服务端',
  'menu.knowledge': '知识库',
  'menu.knowledge.list': '知识库列表',
  'menu.knowledge.add': '知识库新增',
  'menu.knowledge.edit': '知识库编辑',
  'menu.knowledge.detail': '知识库详情',
  'menu.knowledge.delete': '知识库删除',
  'menu.knowledge.search': '知识库搜索',
  'menu.knowledge.export': '知识库导出',
  'menu.map': '地图',
  'menu.map.index': '地图首页',
  'menu.map.marker': '地图标记',
  'menu.list': '列表页',
  'menu.result': '结果页',
  'menu.exception': '异常页',
  'menu.form': '表单页',
  'menu.profile': '详情页',
  'menu.visualization': '数据可视化',
  'menu.user': '个人中心',
  'menu.arcoWebsite': 'Arco Design',
  'menu.faq': '常见问题',
  'navbar.docs': '文档中心',
  'navbar.action.locale': '切换为中文',
  'menu.patient': '患者管理',
  'menu.patient.detail': '患者信息',
  'menu.patient.profile': '患者详情信息',
  'menu.patient.profileNew': '患者详情图表',
  'menu.patientAnalysis': '患者特定疾病评估',
  'menu.patientAnalysis.profile': '特定疾病评估指标表',
  'menu.patientAnalysis.profile.detail': '特定疾病评估指标表详情',
  'menu.patientAnalysis.profileNew': '患者详情图表',
  'menu.patient.llmAnalysis': 'LLM分析',
  'menu.patientImport': '患者信息录入',
  'menu.patientImport.hisTable': '患者管理',
  'menu.patientImport.detail': '论文管理',
  'menu.patientImport.imagingTable': '疾病评估指标表',
  'menu.patientImport.inspectionTable': '专利管理',
  'menu.patientImport.orderTable': '案例管理',
  'menu.aiAnalysis': '特定疾病评估模型',
  'menu.aiAnalysis.ml': '疾病评估模型',
  'menu.aiAnalysis.llm': '大模型分析',
  'menu.patientFeatureAnalysis': '患者特征分析',
  'menu.patientFeatureAnalysis.dataAnalysis': '数据分析',
  'menu.patientFeatureAnalysis.multiDimensionDataAnalysis': '多维度数据分析',
  'menu.system': '系统设置',
  'menu.system.userSetting': '用户设置',
  'menu.system.userManage': '用户管理',
  'menu.patient.orderTable': '患者医嘱信息',
  'menu.patient.inspectionTable': '指标信息',
  'menu.patient.imagingTable': '影像信息',
  'menu.patient.pathologyTable': '病理信息',
  'menu.patient.pathologyTable.detail': '患者基本信息',
  'menu.statisticalAnalysis': '统计分析',
  'menu.statisticalAnalysis.twoGroupComparison': '两组定量数据比较',
  'menu.statisticalAnalysis.pairedComparison': '配对数据比较',
  'menu.statisticalAnalysis.pairedDataAnalysis': '配对数据分析',
  'menu.statisticalAnalysis.multiGroupComparison': '多组定量数据比较',
  'menu.statisticalAnalysis.correlationAnalysis': '相关分析',
  'menu.statisticalAnalysis.regressionAnalysis': '回归分析',
  'menu.descriptiveAnalysis': '描述性统计分析',
  'menu.descriptiveAnalysis.index': '描述性统计分析',

  ...localeSettings,
  ...localeMessageBox,
  ...localeLogin,
  ...localeWorkplace,
  /** simple */
  ...localeMonitor,
  ...localeSearchTable,
  ...localeCardList,
  ...localeStepForm,
  ...localeGroupForm,
  ...localeBasicProfile,
  ...localeDataAnalysis,
  ...localeMultiDAnalysis,
  ...localeSuccess,
  ...localeError,
  ...locale403,
  ...locale404,
  ...locale500,
  ...localeUserInfo,
  ...localeUserSetting,
  /** simple end */
};
