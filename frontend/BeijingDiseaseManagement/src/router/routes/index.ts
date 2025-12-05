import type { RouteRecordRaw } from 'vue-router';

import patientImport from './modules/patient-import';
import patientAnalysis from './modules/patient-analysis';
import aiAnalysis from './modules/ai-analysis';
import patientFeatureAnalysis from './modules/patient-feature-analysis';
import statisticalAnalysis from './modules/statistical-analysis';
import descriptiveAnalysis from './modules/descriptive-analysis';
// import dashboard from './modules/dashboard';
import system from './modules/system';
// import patient from './modules/patient';
// import profile from './modules/profile';
// import result from './modules/result';
// import user from './modules/user';
// import list from './modules/list';
// import map from './modules/map';
// import visualization from './modules/visualization';
// import form from './modules/form';
// import exception from './modules/exception';
// import knowledge from './modules/knowledge';
// import know from './modules/know';

export const appRoutes: RouteRecordRaw[] = [
  patientImport,
  patientAnalysis,
  aiAnalysis,
  patientFeatureAnalysis,
  statisticalAnalysis,
  descriptiveAnalysis,
  // list,
  // dashboard,
  system,
  // knowledge,
];

// 保留外部模块自动加载
const externalModules = import.meta.glob('./externalModules/*.ts', {
  eager: true,
});

function formatModules(_modules: any, result: RouteRecordRaw[]) {
  Object.keys(_modules).forEach((key) => {
    const defaultModule = _modules[key].default;
    if (!defaultModule) return;
    const moduleList = Array.isArray(defaultModule)
      ? [...defaultModule]
      : [defaultModule];
    result.push(...moduleList);
  });
  return result;
}

export const appExternalRoutes: RouteRecordRaw[] = formatModules(
  externalModules,
  []
);
