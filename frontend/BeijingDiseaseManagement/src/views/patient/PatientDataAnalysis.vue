<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patient.pathologyTable']" />
    <a-card class="general-card" :title="$t('menu.patient.pathologyTable')">
      <template #extra>
        <a-button type="primary" size="small" @click="updateTasks"
          >更新评估任务</a-button
        >
      </template>
      <div class="analysis-page">
        <div class="cards">
          <div
            v-for="item in cards"
            :key="item.key"
            class="data-card"
            :style="{ '--accent': item.color }"
          >
            <div class="card-header">
              <span class="card-icon">i</span>
              <span class="card-title">{{ item.title }}</span>
            </div>
            <div class="card-body">
              <div class="metric">
                <div class="metric-label">处理数量</div>
                <div class="metric-value">{{
                  formatNumber(item.processed)
                }}</div>
              </div>
              <div class="metric">
                <div class="metric-label">待处理</div>
                <div class="metric-value">{{ formatNumber(item.pending) }}</div>
              </div>
            </div>
            <div class="card-footer">
              <a-button size="small" type="text" @click="onAction('说明', item)"
                >说明</a-button
              >
            </div>
          </div>
        </div>
        <a-modal
          v-model:visible="showIntro"
          :title="introTitle"
          width="80vw"
          :footer="false"
          :align-center="false"
          :modal-style="{ top: '2vh' }"
          :body-style="{ maxHeight: '88vh', overflowY: 'auto' }"
        >
          <div class="intro-wrap">
            <template
              v-if="selectedCard && selectedCard.title.includes('脓毒症')"
            >
              <a-tabs size="small" type="card" default-active-key="qSOFA">
                <a-tab-pane key="qSOFA" title="（1）qSOFA">
                  <a-table
                    :data="sepsisIntro.qSOFA"
                    :columns="introColumns"
                    :pagination="false"
                    size="small"
                    :bordered="true"
                    :stripe="true"
                    :scroll="{ x: 900 }"
                  />
                </a-tab-pane>
                <a-tab-pane key="SOFA" title="（2）SOFA">
                  <a-table
                    :data="sepsisIntro.SOFA"
                    :columns="introColumns"
                    :pagination="false"
                    size="small"
                    :bordered="true"
                    :stripe="true"
                    :scroll="{ x: 1000 }"
                  />
                </a-tab-pane>
              </a-tabs>
            </template>
            <template v-else>
              <a-tabs size="small" type="card" default-active-key="CURB65">
                <a-tab-pane key="CURB65" title="（1）CURB-65评分">
                  <a-table
                    :data="capIntro.CURB65"
                    :columns="introColumns"
                    :pagination="false"
                    size="small"
                    :bordered="true"
                    :stripe="true"
                    :scroll="{ x: 900 }"
                  />
                </a-tab-pane>
                <a-tab-pane key="PSI" title="（2）PSI评分">
                  <a-table
                    :data="capIntro.PSI"
                    :columns="introColumns"
                    :pagination="false"
                    size="small"
                    :bordered="true"
                    :stripe="true"
                    :scroll="{ x: 1000 }"
                  />
                </a-tab-pane>
                <a-tab-pane key="CPIS" title="（3）CPIS评分">
                  <a-table
                    :data="capIntro.CPIS"
                    :columns="introColumns"
                    :pagination="false"
                    size="small"
                    :bordered="true"
                    :stripe="true"
                    :scroll="{ x: 900 }"
                  />
                </a-tab-pane>
                <a-tab-pane key="SEVERE" title="（4）重症肺炎诊断标准">
                  <a-table
                    :data="capIntro.SEVERE"
                    :columns="introColumns"
                    :pagination="false"
                    size="small"
                    :bordered="true"
                    :stripe="true"
                    :scroll="{ x: 900 }"
                  />
                </a-tab-pane>
              </a-tabs>
            </template>
          </div>
        </a-modal>
      </div>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';

  interface CardItem {
    key: string;
    title: string;
    processed: number;
    pending: number;
    color: string; // 卡片强调色
  }

  const cards = ref<CardItem[]>([
    {
      key: 'chief-complaint',
      title: '社区获得性肺炎模型',
      processed: 276659,
      pending: 7522,
      color: '#1677ff', // 蓝色
    },
    {
      key: 'drug-mapping',
      title: '脓毒症模型',
      processed: 0,
      pending: 0,
      color: '#f53f3f', // 红色
    },
    {
      key: 'icd-mapping',
      title: '3.COVID-19模型',
      processed: 0,
      pending: 0,
      color: '#f7ba1e', // 黄色
    },
    {
      key: 'icd-mapping',
      title: '呼吸道症候群模型',
      processed: 0,
      pending: 0,
      color: '#00b42a', // 绿色
    },
  ]);

  // 说明弹窗数据（社区获得性肺炎 CAP）
  const showIntro = ref(false);
  const introTitle = ref('社区获得性肺炎映射模型 - 说明');
  const activeIntroTab = ref('CURB65');
  const selectedCard = ref<CardItem | null>(null);
  const capIntro = {
    CURB65: [
      {
        item: '年龄≥65岁',
        table: '患者基本信息表',
        field: '出生日期',
        valueSet: '',
      },
      {
        item: '意识障碍',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '主诉/现病史/体格检查；住院病程',
        valueSet: '',
      },
      {
        item: '尿素氮＞7mmol/L',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '呼吸频率≥30次/分',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
      {
        item: 'SBP＜90mmHg或DBP≤60mmHg',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
    ],
    PSI: [
      { item: '女性', table: '患者基本信息表', field: '性别', valueSet: '' },
      {
        item: '基础疾病：肿瘤/肝病/心衰/脑血管病/肾病',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录',
        field: '既往史',
        valueSet: '',
      },
      {
        item: '精神状态改变',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '主诉/现病史/体格检查；住院病程',
        valueSet: '',
      },
      {
        item: 'HR＞125、RR＞30、SBP＜90、T＜35℃或＞40℃',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
      {
        item: '动脉血pH＜7.35',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '血尿素氮＞30mg/dl',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '血钠＜130mmol/L',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '血糖＞14mmol/L',
        table: '检验报告项目',
        field: '检验定量结果/计量单位',
        valueSet: '血糖-C011',
      },
      {
        item: 'HCT＜30%',
        table: '检验报告项目',
        field: '检验定量结果/计量单位',
        valueSet: '红细胞压积-C023',
      },
      {
        item: 'PaO2＜60mmHg',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '胸腔积液',
        table: '检查报告',
        field: '结果-主观提示/客观所见',
        valueSet: '胸片-1004；CT-1005',
      },
    ],
    CPIS: [
      {
        item: '体温',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
      {
        item: '血白细胞',
        table: '检验报告项目',
        field: '检验定量结果/计量单位',
        valueSet: '白细胞计数-C005',
      },
      {
        item: '分泌物（量/性状）',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '主诉/现病史；住院病程',
        valueSet: '',
      },
      {
        item: '氧合指数',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '胸片浸润影',
        table: '检查报告',
        field: '结果-主观提示',
        valueSet: '胸片-1004',
      },
      {
        item: '气管吸取物培养或痰培养',
        table: '检验报告',
        field: '主观提示/客观所见',
        valueSet: '',
      },
    ],
    SEVERE: [
      {
        item: '（有创）机械通气',
        table: '生命体征护理记录单',
        field: '是否使用呼吸机',
        valueSet: '',
      },
      {
        item: '需要血管活性药物',
        table: '医嘱处方条目',
        field: '药品名称（间羟胺/多巴胺/多巴酚丁胺/去甲肾上腺素）',
        valueSet: '不在药品代码中',
      },
      {
        item: '呼吸频率≥30次/分',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
      {
        item: 'PaO2/FiO2≤250mmHg',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '多肺叶浸润',
        table: '检查报告',
        field: '结果-主观提示/客观所见',
        valueSet: 'CT-1005',
      },
      {
        item: '意识/定向障碍',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '主诉/现病史/体格检查；住院病程',
        valueSet: '',
      },
      {
        item: '血尿素氮≥7mmol/L',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '低血压（需积极液体复苏）',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
    ],
  } as const;

  // 统一列定义，供 Tab 内各表复用
  const introColumns = [
    { title: '评分内容', dataIndex: 'item' },
    { title: '采集数据表', dataIndex: 'table' },
    { title: '数据项中文名称', dataIndex: 'field' },
    { title: '相关值域表', dataIndex: 'valueSet' },
  ];

  // 脓毒症说明数据
  const sepsisIntro = {
    qSOFA: [
      {
        item: '意识障碍',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '主诉/现病史/体格检查；住院病程',
        valueSet: '',
      },
      {
        item: '呼吸频率≥22次/分',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
      {
        item: 'SBP≤90mmHg',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
    ],
    SOFA: [
      {
        item: '氧合指数',
        table: '-',
        field: '-',
        valueSet: '不在实验室临床生化检验采集范围',
      },
      {
        item: '血小板计数',
        table: '检验报告项目',
        field: '检验定量结果/计量单位',
        valueSet: '血小板计数-C007',
      },
      {
        item: '平均动脉压（1/3×收缩压 + 2/3×舒张压）',
        table: '门（急）诊病历/门（急）诊留观记录/入院记录；住院日常病程记录',
        field: '体格检查；住院病程',
        valueSet: '',
      },
      {
        item: '胆红素',
        table: '检验报告项目',
        field: '检验定量结果/计量单位',
        valueSet: '总胆红素测定-C012',
      },
      {
        item: '应用血管活性药物',
        table: '-',
        field: '-',
        valueSet: '不在药品代码中（间羟胺/多巴胺/多巴酚丁胺/去甲肾上腺素）',
      },
      {
        item: '格拉斯哥评分',
        table: '-',
        field: '-',
        valueSet: '',
      },
      {
        item: '肌酐',
        table: '检验报告项目',
        field: '检验定量结果/计量单位',
        valueSet: '肌酐测定-C008',
      },
      {
        item: '尿量',
        table: '-',
        field: '-',
        valueSet: '不在采集范围',
      },
    ],
  } as const;

  function formatNumber(n: number) {
    return new Intl.NumberFormat('zh-CN').format(n);
  }

  function onAction(action: string, item: CardItem) {
    if (action === '说明') {
      selectedCard.value = item;
      introTitle.value = `${item.title} - 说明`;
      activeIntroTab.value = item.title.includes('脓毒症') ? 'qSOFA' : 'CURB65';
      showIntro.value = true;
      return;
    }
    Message.info(`${action} - ${item.title}`);
  }

  function updateTasks() {
    Message.success('已触发评估任务更新');
    // TODO: 在此对接后端任务触发接口
  }
</script>

<style scoped>
  .container {
    padding: 0 20px 20px 20px;
  }

  .general-card {
    min-height: 300px;
  }

  .analysis-page {
    padding: 16px;
    color: #1d2129;
  }

  .cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
  }

  .data-card {
    background: #fff;
    color: #1d2129;
    border: none;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08), 0 1px 4px rgba(0, 0, 0, 0.04);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
  }

  .data-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: var(--accent, #1677ff);
    opacity: 0.8;
  }

  .data-card:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12), 0 4px 8px rgba(0, 0, 0, 0.08);
    transform: translateY(-4px);
  }

  .card-header {
    display: flex;
    align-items: center;
    font-weight: 600;
    font-size: 16px;
    margin-bottom: 12px;
  }

  .card-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    margin-right: 10px;
    font-size: 14px;
    line-height: 1;
    background: var(--accent, #1677ff);
    color: #fff;
  }

  .card-title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .card-body {
    display: flex;
    align-items: stretch;
    gap: 48px;
    padding: 12px 0;
    border-top: 1px solid #f2f3f5;
    border-bottom: 1px solid #f2f3f5;
  }

  .metric-label {
    font-size: 12px;
    color: #86909c;
  }

  .metric-value {
    margin-top: 4px;
    font-size: 26px;
    font-weight: 700;
    color: #1d2129;
  }

  .card-footer {
    display: flex;
    gap: 12px;
    padding-top: 12px;
    justify-content: flex-start;
  }

  .card-footer :deep(.arco-btn-text) {
    color: #1677ff;
    font-weight: 500;
    padding: 4px 12px;
    border-radius: 6px;
    transition: all 0.2s ease;
  }

  .card-footer :deep(.arco-btn-text:hover) {
    background-color: rgba(22, 119, 255, 0.1);
    color: #1677ff;
  }

  .action {
    color: var(--accent, #1677ff);
    text-decoration: none;
  }
  .action:hover {
    text-decoration: underline;
  }
  /* Intro modal styles */
  .intro-wrap {
    padding-right: 8px; /* 给滚动条留一点内边距，避免贴边 */
  }
  .intro-title {
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1d2129;
  }
  .intro-subtitle {
    margin: 12px 0 8px;
    font-size: 14px;
    font-weight: 600;
    color: #1d2129;
  }
  .intro-section {
    margin: 16px 0 8px;
    font-size: 13px;
    font-weight: 600;
    color: #4e5969;
  }
  .intro-wrap :deep(.arco-table) {
    font-size: 12px;
    margin-bottom: 12px;
  }
  .intro-wrap :deep(.arco-table-th) {
    white-space: nowrap;
    background-color: #f5f7fa;
    color: #1d2129;
    font-weight: 600;
  }
  .intro-wrap :deep(.arco-table-td) {
    white-space: normal;
    word-break: break-word;
    line-height: 1.7;
    color: #4e5969;
  }
  .intro-wrap :deep(.arco-table-tr:hover) {
    background-color: #f9fbff;
  }

  /* 更精致的表格外观 */
  .intro-table :deep(.arco-table-container) {
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid #e9edf3;
  }
  .intro-table :deep(.arco-table-td),
  .intro-table :deep(.arco-table-th) {
    padding: 10px 12px;
  }
  .intro-table
    :deep(.arco-table-body .arco-table-tr:nth-child(odd) .arco-table-td) {
    background-color: #fbfcfe;
  }
  .intro-table :deep(.arco-table-border .arco-table-td) {
    border-color: #eef2f6;
  }
</style>
