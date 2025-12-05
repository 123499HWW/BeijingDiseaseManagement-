# src/views/patient 目录结构说明

<<<<<<< HEAD
本目录为"患者管理"相关页面模块，包含患者详情页、数据分析、HIS 表、LLM 分析等功能页面。
=======
本目录为"患者管理"相关页面模块，包含患者详情页、数据分析、HIS 表、LLM 分析等功能页面。

> > > > > > > b4c776b32009d269391e3c74f016f6e371dc87aa

```
patient/
├── detail/
│   └── index.vue                    # 表格型患者详情页，参考search-table风格，含表格、搜索、分页等
├── profile.vue                      # 非表格型患者详情页，分组卡片+患者指标信息，含分页mock数据
├── PatientDataAnalysis.vue          # 患者数据分析页面，包含特征归因分析、预测模型等图表
├── LLMAnalysis/
│   └── Report.vue                   # LLM分析报告页面，展示AI分析结果
├── his-table.vue                    # HIS表页面，医院信息系统数据展示，含搜索、分页、列设置等功能
├── PatientImagingTable.vue          # 患者影像表格组件
├── PatientIndicators.vue            # 患者指标组件
├── PatientBasicInfo.vue             # 患者基本信息组件
├── PatientMedicalHistory.vue        # 患者病史组件
├── PatientTreatmentPlan.vue         # 患者治疗方案组件
├── PatientMedicationList.vue        # 患者用药清单组件
├── mock.ts                          # 患者相关mock数据，主要为检验指标分页数据
└── README.md                        # 本说明文档
```

## 功能说明

### 核心页面

- **`detail/index.vue`**：表格型详情页，适合展示患者列表、可搜索、可分页，支持内容溢出隐藏和 tooltip 显示
- **`profile.vue`**：分组卡片式详情页，适合展示单个患者详细信息及检验/影像指标，指标区支持分页
- **`PatientDataAnalysis.vue`**：患者数据分析页面，包含特征归因分析、预测模型等数据可视化图表
- **`LLMAnalysis/Report.vue`**：LLM 分析报告页面，展示 AI 对患者数据的智能分析结果
- **`his-table.vue`**：HIS 表页面，医院信息系统数据展示，包含完整的表格功能：
  - 搜索表单（住院流水号、姓名、身份证号、诊断、报告日期、TNM 分期）
  - 表格展示（19 个字段：id、住院流水号、姓名、身份证号、编号、职业、血型、TNM 分期、临床分期、住址、电话、工作单位、诊断、住院诊断、病理诊断、病理号、报告日期、肉眼所见、操作）
    > > > > > > > b4c776b32009d269391e3c74f016f6e371dc87aa
  - 分页功能
  - 密度调节（迷你、偏小、中等、偏大）
  - 列设置（可拖拽排序、显示/隐藏列）
  - 内容溢出处理（ellipsis + tooltip）
  - 操作列（查看、编辑、删除）

### 组件模块

- **`PatientImagingTable.vue`**：患者影像表格组件
- **`PatientIndicators.vue`**：患者指标组件
- **`PatientBasicInfo.vue`**：患者基本信息组件
- **`PatientMedicalHistory.vue`**：患者病史组件
- **`PatientTreatmentPlan.vue`**：患者治疗方案组件
- **`PatientMedicationList.vue`**：患者用药清单组件

### 数据支持

- **`mock.ts`**：本地 mock 数据，供各页面调用，支持大数据量分页

## 技术特性

### 表格功

- **响应式设计**：支持不同屏幕尺寸
- **搜索过滤**：多条件组合搜索
- **分页处理**：大数据量分页展示
- **列管理**：可拖拽排序、显示/隐藏列
- **密度调节**：4 种表格密度可选
- **内容处理**：长文本自动省略，鼠标悬停显示完整内容

### 数据可视化

- **ECharts 图表**：特征归因分析、预测模型等
- **AI 分析**：LLM 智能分析报告
- **实时数据**：动态数据更新

### 用户体验

- **加载状态**：数据加载时显示 loading

- **操作反馈**：增删改操作的消息提示
- **国际化**：支持中英文切换
- **主题适配**：支持明暗主题切换

## 扩展说明

如需扩展功能，可在本目录下新增文件：

- 新增页面：在根目录创建 `.vue` 文件
- 新增组件：创建组件文件并在页面中引用
- 新增数据：在 `mock.ts` 中添加 mock 数据

- 新增路由：在 `router/routes/modules/patient.ts` 中配置路由
- 新增国际化：在 `locale/zh-CN.ts` 和 `locale/en-US.ts` 中添加文本

## 路由配置

患者管理模块的路由配置位于 `src/router/routes/modules/patient.ts`，包含以下路由：

- `/patient/detail` - 患者详情页
- `/patient/profile` - 患者信息页
- `/patient/data-analysis` - 患者数据分析
- `/patient/llm-analysis` - LLM 分析
- `/patient/his-table` - HIS 表
