// 影像数据 mock
function getImagingData(page = 1, pageSize = 10) {
  const allData = [
    {
      reportTime: '2024-05-01',
      imagingType: '教材',
      imagingNo: 'BOOK-202405-001',
      diagnosis: '数据新闻方法论',
      description:
        '覆盖数据获取、清洗、建模到叙事的完整流程，配套 120 个案例脚本与代码仓库。',
      conclusion: '被教育部新闻传播教学指导委员会收录为示范教材。',
      radiologist: '编审：王晓',
      hospitalNo: 'SCH2024001',
      name: '李文博',
    },
    {
      reportTime: '2024-05-05',
      imagingType: '专著',
      imagingNo: 'BOOK-202405-002',
      diagnosis: '算力驱动的新闻科学科',
      description:
        '提出“新闻算力中心”框架，详细拆解数据要素、模型治理、算法伦理三大模块。',
      conclusion: '入选新华社“新型主流媒体研究”书单。',
      radiologist: '编审：刘程',
      hospitalNo: 'SCH2024003',
      name: '周启航',
    },
    {
      reportTime: '2024-05-08',
      imagingType: '译著',
      imagingNo: 'BOOK-202405-003',
      diagnosis: '全球科学议题监测手册',
      description:
        '引进 MIT Comparative Media 项目的科学议题监测方法，新增中文案例 24 例。',
      conclusion: '中国外文局重点翻译项目，面向全国媒体发行。',
      radiologist: '编审：陈露',
      hospitalNo: 'SCH2024007',
      name: '黄柏松',
    },
    {
      reportTime: '2024-05-10',
      imagingType: '教材',
      imagingNo: 'BOOK-202405-004',
      diagnosis: '科普叙事实践',
      description:
        '聚焦校园媒体与地方科协协同模式，拆解课堂、实训、项目的全链条。',
      conclusion: '浙江省一流课程共建教材。',
      radiologist: '编审：吴彤',
      hospitalNo: 'SCH2024005',
      name: '陈晓宁',
    },
    {
      reportTime: '2024-05-15',
      imagingType: '工具书',
      imagingNo: 'BOOK-202405-005',
      diagnosis: '科研新闻知识图谱手册',
      description:
        '提供 3.4 万条科研实体与新闻节点的标准化描述，内置 60 套问答模版。',
      conclusion: '国家科技传播中心推荐使用。',
      radiologist: '编审：赵越',
      hospitalNo: 'SCH2024004',
      name: '郭凌霄',
    },
    {
      reportTime: '2024-05-18',
      imagingType: '专著',
      imagingNo: 'BOOK-202405-006',
      diagnosis: '城市媒体算力调度实践',
      description:
        '总结武汉新闻算力中台建设，包含 12 套算子、6 种资源编排模式。',
      conclusion: '荣获中国新闻技术工作者协会优秀成果奖。',
      radiologist: '编审：何峻',
      hospitalNo: 'SCH2024008',
      name: '王雪萌',
    },
    {
      reportTime: '2024-05-22',
      imagingType: '案例集',
      imagingNo: 'BOOK-202405-007',
      diagnosis: '科创金融传播案例',
      description:
        '沉淀 48 个科创企业传播案例及可复用的舆情监测指标体系。',
      conclusion: '被多地金融监管局采用为培训教材。',
      radiologist: '编审：孙琦',
      hospitalNo: 'SCH2024010',
      name: '赵一航',
    },
    {
      reportTime: '2024-05-26',
      imagingType: '专著',
      imagingNo: 'BOOK-202405-008',
      diagnosis: 'AI 审校与媒体伦理',
      description:
        '提出“AI 审校红线”评估模型，提供 300+ 条语料对比实验结果。',
      conclusion: '列入新华智云内训教材。',
      radiologist: '编审：段琰',
      hospitalNo: 'SCH2024006',
      name: '刘思涵',
    },
    {
      reportTime: '2024-05-28',
      imagingType: '译著',
      imagingNo: 'BOOK-202405-009',
      diagnosis: '多语种科学传播设计',
      description:
        '系统介绍跨语种知识迁移流程，并结合大湾区案例补充中文注释。',
      conclusion: '入选粤港澳大湾区国际传播教材库。',
      radiologist: '编审：胡蓝',
      hospitalNo: 'SCH2024007',
      name: '黄柏松',
    },
    {
      reportTime: '2024-05-30',
      imagingType: '工具书',
      imagingNo: 'BOOK-202405-010',
      diagnosis: '科研新闻影响力指标库',
      description:
        '整理 52 个评价指标及算法公式，附带可直接导入的仪表盘模板。',
      conclusion: '陕西省科研新闻评价平台指定参考书。',
      radiologist: '编审：许玥',
      hospitalNo: 'SCH2024009',
      name: '宋佳怡',
    },
  ];
  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  return {
    list: allData.slice(start, end).map((item, idx) => ({ ...item })),
    total: allData.length,
  };
}

export default getImagingData;
