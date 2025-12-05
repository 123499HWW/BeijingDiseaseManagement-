<template>
  <div class="multi-group-comparison">
    <a-page-header
      title="多组定量数据比较分析"
      subtitle="多种治疗方法对比分析"
    />

    <!-- 数据配置 -->
    <a-card title="数据配置" :bordered="false" style="margin-bottom: 24px">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="分析指标">
            <a-select
              v-model:value="selectedIndicator"
              placeholder="选择分析指标"
              @change="onIndicatorChange"
            >
              <a-option value="blood_pressure">收缩压</a-option>
              <a-option value="blood_glucose">血糖</a-option>
              <a-option value="weight">体重</a-option>
              <a-option value="cholesterol">胆固醇</a-option>
              <a-option value="triglyceride">甘油三酯</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="分组变量">
            <a-select
              v-model:value="selectedGroupVariable"
              placeholder="选择分组变量"
              @change="onGroupVariableChange"
            >
              <a-option value="treatment">治疗方案</a-option>
              <a-option value="department">科室分组</a-option>
              <a-option value="age_group">年龄分组</a-option>
              <a-option value="gender">性别分组</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="数据质量">
            <a-checkbox-group v-model:value="dataQualityOptions">
              <a-space direction="vertical">
                <a-checkbox value="exclude_missing">排除缺失值</a-checkbox>
                <a-checkbox value="exclude_outliers">排除异常值</a-checkbox>
                <a-checkbox value="normality_test">正态性检验</a-checkbox>
                <a-checkbox value="variance_test">方差齐性检验</a-checkbox>
              </a-space>
            </a-checkbox-group>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16" style="margin-top: 16px">
        <a-col :span="24">
          <a-form-item label="操作">
            <a-space>
              <a-button
                type="primary"
                :loading="isAnalyzing"
                @click="startAnalysis"
              >
                开始分析
              </a-button>
              <a-button @click="resetForm">重置</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-card>

    <!-- 数据概览 -->
    <a-card title="数据概览" :bordered="false" style="margin-bottom: 24px">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic
            title="总样本数"
            :value="dataOverview.totalSamples"
            :precision="0"
          />
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="分组数"
            :value="dataOverview.groupCount"
            :precision="0"
          />
        </a-col>
        <a-col :span="6">
          <a-statistic title="均值" :value="dataOverview.mean" :precision="1" />
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="标准差"
            :value="dataOverview.stdDev"
            :precision="1"
          />
        </a-col>
      </a-row>
    </a-card>

    <!-- 数据可视化 -->
    <a-row :gutter="16" style="margin-bottom: 24px">
      <a-col :span="12">
        <a-card title="箱线图" :bordered="false">
          <div class="chart-container">
            <div ref="boxplotRef" class="chart" style="height: 300px"></div>
          </div>
        </a-card>
      </a-col>

      <a-col :span="12">
        <a-card title="条形图" :bordered="false">
          <div class="chart-container">
            <div ref="barChartRef" class="chart" style="height: 300px"></div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 检验结果 -->
    <a-card title="检验结果" :bordered="false" style="margin-bottom: 24px">
      <a-tabs v-model:active-key="activeTab">
        <a-tab-pane key="normality" title="正态性检验">
          <a-table
            :columns="normalityColumns"
            :data="normalityResults"
            :pagination="false"
            size="small"
          />
        </a-tab-pane>

        <a-tab-pane key="variance" title="方差齐性检验">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="检验方法">
              {{ varianceTest.method }}
            </a-descriptions-item>
            <a-descriptions-item label="统计量">
              {{ varianceTest.statistic }}
            </a-descriptions-item>
            <a-descriptions-item label="P值">
              {{ varianceTest.pValue }}
            </a-descriptions-item>
            <a-descriptions-item label="结论">
              {{ varianceTest.conclusion }}
            </a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="anova" title="方差分析">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="检验方法">
              {{ anovaResult.method }}
            </a-descriptions-item>
            <a-descriptions-item label="F统计量">
              {{ anovaResult.fStatistic }}
            </a-descriptions-item>
            <a-descriptions-item label="自由度">
              {{ anovaResult.df }}
            </a-descriptions-item>
            <a-descriptions-item label="P值">
              {{ anovaResult.pValue }}
            </a-descriptions-item>
            <a-descriptions-item label="效应量">
              {{ anovaResult.effectSize }}
            </a-descriptions-item>
            <a-descriptions-item label="结论">
              {{ anovaResult.conclusion }}
            </a-descriptions-item>
          </a-descriptions>

          <a-divider />

          <a-card title="事后检验结果" size="small">
            <a-table
              :columns="postHocColumns"
              :data="postHocResults"
              :pagination="false"
              size="small"
            />
          </a-card>
        </a-tab-pane>

        <a-tab-pane key="kruskal" title="Kruskal-Wallis检验">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="检验方法">
              {{ kruskalResult.method }}
            </a-descriptions-item>
            <a-descriptions-item label="H统计量">
              {{ kruskalResult.hStatistic }}
            </a-descriptions-item>
            <a-descriptions-item label="自由度">
              {{ kruskalResult.df }}
            </a-descriptions-item>
            <a-descriptions-item label="P值">
              {{ kruskalResult.pValue }}
            </a-descriptions-item>
            <a-descriptions-item label="效应量">
              {{ kruskalResult.effectSize }}
            </a-descriptions-item>
            <a-descriptions-item label="结论">
              {{ kruskalResult.conclusion }}
            </a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 分析结论 -->
    <a-card v-if="analysisConclusion" title="分析结论" :bordered="false">
      <a-alert type="info" :closable="false">
        <p><strong>主要发现：</strong>{{ analysisConclusion.mainFinding }}</p>
        <p
          ><strong>统计解释：</strong>{{ analysisConclusion.interpretation }}</p
        >
        <p
          ><strong>临床意义：</strong
          >{{ analysisConclusion.clinicalSignificance }}</p
        >
      </a-alert>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted, nextTick } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import * as echarts from 'echarts';

  // 响应式数据
  const selectedIndicator = ref<string>('blood_pressure');
  const selectedGroupVariable = ref<string>('treatment');
  const dataQualityOptions = ref<string[]>([
    'exclude_missing',
    'normality_test',
    'variance_test',
  ]);
  const activeTab = ref<string>('normality');
  const isAnalyzing = ref(false);

  // 图表引用
  const boxplotRef = ref<HTMLElement>();
  const barChartRef = ref<HTMLElement>();

  // 图表实例
  let boxplotInstance: echarts.ECharts | null = null;
  let barChartInstance: echarts.ECharts | null = null;

  // 数据概览
  const dataOverview = reactive({
    totalSamples: 90,
    groupCount: 3,
    mean: 135.2,
    stdDev: 12.8,
  });

  // 正态性检验结果
  const normalityColumns = [
    { title: '分组', dataIndex: 'group', key: 'group' },
    { title: '样本数', dataIndex: 'sampleSize', key: 'sampleSize' },
    { title: 'W统计量', dataIndex: 'wStatistic', key: 'wStatistic' },
    { title: 'P值', dataIndex: 'pValue', key: 'pValue' },
    { title: '结论', dataIndex: 'conclusion', key: 'conclusion' },
  ];

  const normalityResults = ref([
    {
      group: '方案A',
      sampleSize: 30,
      wStatistic: 0.956,
      pValue: 0.234,
      conclusion: '正态分布',
    },
    {
      group: '方案B',
      sampleSize: 30,
      wStatistic: 0.943,
      pValue: 0.156,
      conclusion: '正态分布',
    },
    {
      group: '方案C',
      sampleSize: 30,
      wStatistic: 0.967,
      pValue: 0.445,
      conclusion: '正态分布',
    },
  ]);

  // 方差齐性检验结果
  const varianceTest = reactive({
    method: 'Levene检验',
    statistic: 'F = 1.234',
    pValue: '0.298',
    conclusion: '方差齐性',
  });

  // 方差分析结果
  const anovaResult = reactive({
    method: '单因素方差分析',
    fStatistic: 'F(2,87) = 6.42',
    df: 'df = 2, 87',
    pValue: '0.003',
    effectSize: 'η² = 0.129',
    conclusion: '组间差异显著',
  });

  // 事后检验结果
  const postHocColumns = [
    { title: '比较组', dataIndex: 'comparison', key: 'comparison' },
    { title: '均值差', dataIndex: 'meanDiff', key: 'meanDiff' },
    { title: '标准误', dataIndex: 'stdError', key: 'stdError' },
    { title: 'P值', dataIndex: 'pValue', key: 'pValue' },
    { title: '显著性', dataIndex: 'significance', key: 'significance' },
  ];

  const postHocResults = ref([
    {
      comparison: 'A vs B',
      meanDiff: 5.2,
      stdError: 2.1,
      pValue: 0.089,
      significance: '不显著',
    },
    {
      comparison: 'A vs C',
      meanDiff: 12.8,
      stdError: 2.1,
      pValue: 0.002,
      significance: '显著',
    },
    {
      comparison: 'B vs C',
      meanDiff: 7.6,
      stdError: 2.1,
      pValue: 0.015,
      significance: '显著',
    },
  ]);

  // Kruskal-Wallis检验结果
  const kruskalResult = reactive({
    method: 'Kruskal-Wallis H检验',
    hStatistic: 'H = 9.81',
    df: 'df = 2',
    pValue: '0.007',
    effectSize: 'ε² = 0.108',
    conclusion: '组间差异显著',
  });

  // 分析结论
  const analysisConclusion = ref<any>(null);

  // 获取指标名称
  const getIndicatorName = (indicator: string) => {
    const names: { [key: string]: string } = {
      blood_pressure: '收缩压',
      blood_glucose: '血糖',
      weight: '体重',
      cholesterol: '胆固醇',
      triglyceride: '甘油三酯',
    };
    return names[indicator] || indicator;
  };

  // 更新箱线图
  const updateBoxplot = () => {
    if (!boxplotInstance) return;

    const option = {
      title: {
        text: `${getIndicatorName(selectedIndicator.value)}分组箱线图`,
        left: 'center',
      },
      tooltip: {
        trigger: 'item',
      },
      grid: {
        left: '10%',
        right: '10%',
        bottom: '15%',
      },
      xAxis: {
        type: 'category',
        data: ['方案A', '方案B', '方案C'],
      },
      yAxis: {
        type: 'value',
        name: getIndicatorName(selectedIndicator.value),
      },
      series: [
        {
          name: getIndicatorName(selectedIndicator.value),
          type: 'boxplot',
          data: [
            [120, 125, 130, 135, 140],
            [115, 120, 125, 130, 135],
            [110, 115, 120, 125, 130],
          ],
          itemStyle: {
            color: '#5470c6',
          },
        },
      ],
    };

    boxplotInstance.setOption(option);
  };

  // 更新条形图
  const updateBarChart = () => {
    if (!barChartInstance) return;

    const option = {
      title: {
        text: `${getIndicatorName(selectedIndicator.value)}分组均值对比`,
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
      },
      grid: {
        left: '10%',
        right: '10%',
        bottom: '15%',
      },
      xAxis: {
        type: 'category',
        data: ['方案A', '方案B', '方案C'],
      },
      yAxis: {
        type: 'value',
        name: getIndicatorName(selectedIndicator.value),
      },
      series: [
        {
          name: '均值',
          type: 'bar',
          data: [135.2, 130.0, 122.4],
          itemStyle: {
            color: '#91cc75',
          },
        },
      ],
    };

    barChartInstance.setOption(option);
  };

  // 更新图表
  const updateCharts = () => {
    updateBoxplot();
    updateBarChart();
  };

  // 事件处理
  const onIndicatorChange = (value: string) => {
    selectedIndicator.value = value;
    updateCharts();
  };

  const onGroupVariableChange = (value: string) => {
    selectedGroupVariable.value = value;
    updateCharts();
  };

  // 生成分析结论
  const generateAnalysisConclusion = () => {
    analysisConclusion.value = {
      mainFinding: `三组${getIndicatorName(
        selectedIndicator.value
      )}差异显著，F(2,87)=6.42, p=0.003；Tukey检验显示方案A显著高于方案C（p=0.002）。`,
      interpretation:
        '方差分析结果显示组间存在统计学显著差异，说明不同治疗方案对患者指标的影响存在差异。',
      clinicalSignificance:
        '方案A在改善患者指标方面效果最佳，建议在临床实践中优先考虑该方案。',
    };
  };

  // 开始分析
  const startAnalysis = async () => {
    if (!selectedIndicator.value) {
      Message.error('请选择分析指标');
      return;
    }

    isAnalyzing.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 2000);
      });

      // 生成分析结论
      generateAnalysisConclusion();

      Message.success('分析完成！');
    } catch (error) {
      Message.error('分析失败，请重试');
    } finally {
      isAnalyzing.value = false;
    }
  };

  // 重置表单
  const resetForm = () => {
    selectedIndicator.value = 'blood_pressure';
    selectedGroupVariable.value = 'treatment';
    dataQualityOptions.value = [
      'exclude_missing',
      'normality_test',
      'variance_test',
    ];
    analysisConclusion.value = null;
    Message.success('表单已重置');
  };

  // 初始化图表
  const initCharts = () => {
    if (boxplotRef.value) {
      boxplotInstance = echarts.init(boxplotRef.value);
      updateBoxplot();
    }
    if (barChartRef.value) {
      barChartInstance = echarts.init(barChartRef.value);
      updateBarChart();
    }
  };

  onMounted(() => {
    nextTick(() => {
      initCharts();
    });
  });
</script>

<style scoped lang="less">
  .multi-group-comparison {
    padding: 24px;

    .chart-container {
      .chart {
        width: 100%;
      }
    }
  }
</style>
