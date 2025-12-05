<template>
  <div class="statistical-analysis">
    <a-page-header title="数据分析" subtitle="选择分析指标和分组变量" />

    <!-- 数据选择配置 - 折叠面板 -->
    <a-card title="数据选择配置" :bordered="false" style="margin-bottom: 24px">
      <a-collapse v-model:active-key="activeCollapseKeys" :bordered="false">
        <a-collapse-item key="data-selection" header="数据选择配置">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="分析指标">
                <a-radio-group
                  v-model:value="selectedIndicator"
                  @change="onIndicatorChange"
                >
                  <a-space direction="vertical">
                    <a-radio value="blood_pressure">血压</a-radio>
                    <a-radio value="blood_glucose">血糖</a-radio>
                    <a-radio value="white_blood_cell">白细胞计数</a-radio>
                    <a-radio value="hemoglobin">血红蛋白</a-radio>
                    <a-radio value="liver_function">肝功能指标</a-radio>
                    <a-radio value="kidney_function">肾功能指标</a-radio>
                    <a-radio value="blood_lipid">血脂指标</a-radio>
                  </a-space>
                </a-radio-group>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="分组变量">
                <a-radio-group
                  v-model:value="selectedGroupVariable"
                  @change="onGroupVariableChange"
                >
                  <a-space direction="vertical">
                    <a-radio value="gender">性别 (男/女)</a-radio>
                    <a-radio value="age_group">年龄组 (青年/中年/老年)</a-radio>
                    <a-radio value="disease_type"
                      >疾病类型 (A型/B型/C型)</a-radio
                    >
                    <a-radio value="treatment_plan"
                      >治疗方案 (方案1/方案2/方案3)</a-radio
                    >
                    <a-radio value="blood_type">血型 (A/B/O/AB)</a-radio>
                    <a-radio value="region">地域 (省份/城市)</a-radio>
                  </a-space>
                </a-radio-group>
              </a-form-item>
            </a-col>
          </a-row>
        </a-collapse-item>
      </a-collapse>
    </a-card>

    <!-- 数据筛选条件 -->
    <a-card title="数据筛选条件" :bordered="false" style="margin-bottom: 24px">
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="时间范围">
            <a-range-picker v-model:value="dateRange" format="YYYY-MM-DD" />
          </a-form-item>
        </a-col>

        <a-col :span="6">
          <a-form-item label="最小样本量">
            <a-input-number
              v-model:value="minSampleSize"
              :min="10"
              :max="1000"
            />
            <span style="margin-left: 8px">每组</span>
          </a-form-item>
        </a-col>

        <a-col :span="6">
          <a-form-item label="最大缺失率">
            <a-input-number v-model:value="maxMissingRate" :min="0" :max="50" />
            <span style="margin-left: 8px">%</span>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="24">
          <a-form-item label="数据质量选项">
            <a-checkbox-group v-model:value="dataQualityOptions">
              <a-space direction="horizontal">
                <a-checkbox value="exclude_missing">排除缺失值</a-checkbox>
                <a-checkbox value="exclude_outliers">排除异常值</a-checkbox>
                <a-checkbox value="only_complete_cases"
                  >仅包含完整病例</a-checkbox
                >
                <a-checkbox value="only_adults">仅包含成年患者</a-checkbox>
              </a-space>
            </a-checkbox-group>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="24">
          <a-form-item label="数据概览">
            <a-descriptions :column="3" bordered>
              <a-descriptions-item label="总样本数">{{
                dataOverview.totalSamples
              }}</a-descriptions-item>
              <a-descriptions-item label="有效样本数">{{
                dataOverview.validSamples
              }}</a-descriptions-item>
              <a-descriptions-item label="缺失率"
                >{{ dataOverview.missingRate }}%</a-descriptions-item
              >
            </a-descriptions>
          </a-form-item>
        </a-col>
      </a-row>

      <div class="action-buttons">
        <a-space>
          <a-button type="primary" :loading="isLoading" @click="startAnalysis">
            开始分析
          </a-button>
          <a-button @click="resetForm">重置</a-button>
          <a-button @click="previewData">预览数据</a-button>
        </a-space>
      </div>
    </a-card>

    <!-- 数据可视化展示 -->
    <a-card title="数据可视化" :bordered="false" style="margin-bottom: 24px">
      <div class="chart-container">
        <div ref="chartRef" class="chart" style="height: 400px"></div>
      </div>
    </a-card>

    <!-- 分析结果 -->
    <a-card v-if="analysisResult" title="分析结果" :bordered="false">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="分析方法">{{
          analysisResult.method
        }}</a-descriptions-item>
        <a-descriptions-item label="P值">{{
          analysisResult.pValue
        }}</a-descriptions-item>
        <a-descriptions-item label="统计量">{{
          analysisResult.statistic
        }}</a-descriptions-item>
        <a-descriptions-item label="效应量">{{
          analysisResult.effectSize
        }}</a-descriptions-item>
        <a-descriptions-item label="置信区间">{{
          analysisResult.confidenceInterval
        }}</a-descriptions-item>
        <a-descriptions-item label="描述性统计">{{
          analysisResult.descriptiveStats
        }}</a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <a-alert type="info" title="结果解释" :closable="false">
        <p><strong>主要发现：</strong>{{ analysisResult.conclusion }}</p>
        <p><strong>统计解释：</strong>{{ analysisResult.interpretation }}</p>
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
  const selectedGroupVariable = ref<string>('gender');
  const dateRange = ref<[string, string]>(['2024-01-01', '2024-12-31']);
  const minSampleSize = ref<number>(30);
  const maxMissingRate = ref<number>(10);
  const dataQualityOptions = ref<string[]>([
    'exclude_missing',
    'exclude_outliers',
    'only_complete_cases',
    'only_adults',
  ]);
  const activeCollapseKeys = ref<string[]>(['data-selection']);

  const dataOverview = reactive({
    totalSamples: 1250,
    validSamples: 1180,
    missingRate: 5.6,
  });

  const isLoading = ref(false);
  const analysisResult = ref<any>(null);
  const chartRef = ref<HTMLElement>();

  // 事件处理
  // 图表相关
  let chartInstance: echarts.ECharts | null = null;

  const updateChart = () => {
    if (!chartInstance) return;

    const months = [
      '1月',
      '2月',
      '3月',
      '4月',
      '5月',
      '6月',
      '7月',
      '8月',
      '9月',
      '10月',
      '11月',
      '12月',
    ];

    const indicatorNames: { [key: string]: string } = {
      blood_pressure: '血压',
      blood_glucose: '血糖',
      white_blood_cell: '白细胞计数',
      hemoglobin: '血红蛋白',
      liver_function: '肝功能指标',
      kidney_function: '肾功能指标',
      blood_lipid: '血脂指标',
    };

    const groupNames: { [key: string]: string[] } = {
      gender: ['男性', '女性'],
      age_group: ['青年', '中年', '老年'],
      disease_type: ['A型', 'B型', 'C型'],
      treatment_plan: ['方案1', '方案2', '方案3'],
      blood_type: ['A型', 'B型', 'O型', 'AB型'],
      region: ['省份A', '省份B', '省份C'],
    };

    const groups = groupNames[selectedGroupVariable.value] || ['组1', '组2'];

    const series = groups.map((group) => ({
      name: `${indicatorNames[selectedIndicator.value]} - ${group}`,
      type: 'line',
      data: months.map(() => Math.floor(Math.random() * 100) + 50),
      smooth: true,
    }));

    const option = {
      title: {
        text: '指标趋势分析',
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: series.map((s) => s.name),
        type: 'scroll',
        bottom: 10,
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: months,
      },
      yAxis: {
        type: 'value',
      },
      series,
    };

    chartInstance.setOption(option);
  };

  const onIndicatorChange = (value: string) => {
    selectedIndicator.value = value;
    updateChart();
  };

  const onGroupVariableChange = (value: string) => {
    selectedGroupVariable.value = value;
    updateChart();
  };

  const startAnalysis = async () => {
    if (!selectedIndicator.value) {
      Message.error('请选择一个分析指标');
      return;
    }

    if (!selectedGroupVariable.value) {
      Message.error('请选择分组变量');
      return;
    }

    isLoading.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 2000);
      });

      // 根据选择的指标和分组变量生成更专业的分析结果
      const indicatorNames: { [key: string]: string } = {
        blood_pressure: '收缩压',
        blood_glucose: '血糖',
        white_blood_cell: '白细胞计数',
        hemoglobin: '血红蛋白',
        liver_function: '肝功能指标',
        kidney_function: '肾功能指标',
        blood_lipid: '血脂指标',
      };

      const groupNames: { [key: string]: string[] } = {
        gender: ['男性', '女性'],
        age_group: ['青年', '中年', '老年'],
        disease_type: ['A型', 'B型', 'C型'],
        treatment_plan: ['方案1', '方案2', '方案3'],
        blood_type: ['A型', 'B型', 'O型', 'AB型'],
        region: ['省份A', '省份B', '省份C'],
      };

      const indicator = indicatorNames[selectedIndicator.value];
      const groups = groupNames[selectedGroupVariable.value];
      const group1 = groups[0];
      const group2 = groups[1];

      // 生成模拟的统计数据
      const mean1 = Math.floor(Math.random() * 50) + 100;
      const mean2 = Math.floor(Math.random() * 50) + 80;
      const sd1 = Math.floor(Math.random() * 10) + 8;
      const sd2 = Math.floor(Math.random() * 10) + 8;
      const tValue = (Math.random() * 3 - 1.5).toFixed(2);
      const pValue = (Math.random() * 0.05 + 0.001).toFixed(3);
      const df = Math.floor(Math.random() * 50) + 30;
      const meanDiff = (mean1 - mean2).toFixed(1);
      const ciLower = (mean1 - mean2 - 8).toFixed(1);
      const ciUpper = (mean1 - mean2 + 8).toFixed(1);

      analysisResult.value = {
        method: '独立样本t检验',
        pValue,
        statistic: `t=${tValue}, df=${df}`,
        effectSize: `Cohen's d = ${Math.abs(
          parseFloat(tValue) / Math.sqrt(df)
        ).toFixed(3)}`,
        conclusion: `${group1}${indicator}（M=${mean1}, SD=${sd1}）${
          parseFloat(pValue) < 0.05 ? '显著' : '不显著'
        }${
          parseFloat(tValue) > 0 ? '高于' : '低于'
        }${group2}（M=${mean2}, SD=${sd2}），t(${df})=${tValue}, p=${pValue}`,
        confidenceInterval: `[${ciLower}, ${ciUpper}]`,
        descriptiveStats: `${group1}: M=${mean1}, SD=${sd1}; ${group2}: M=${mean2}, SD=${sd2}`,
        interpretation:
          parseFloat(pValue) < 0.05
            ? '存在统计学显著差异，效应量中等'
            : '无统计学显著差异',
      };

      Message.success('分析完成！');
    } catch (error) {
      Message.error('分析失败，请重试');
    } finally {
      isLoading.value = false;
    }
  };

  const resetForm = () => {
    selectedIndicator.value = 'blood_pressure';
    selectedGroupVariable.value = 'gender';
    dateRange.value = ['2024-01-01', '2024-12-31'];
    minSampleSize.value = 30;
    maxMissingRate.value = 10;
    dataQualityOptions.value = [
      'exclude_missing',
      'exclude_outliers',
      'only_complete_cases',
      'only_adults',
    ];
    analysisResult.value = null;
    updateChart();
    Message.success('表单已重置');
  };

  const previewData = () => {
    Message.info('预览数据功能待实现');
  };

  const initChart = () => {
    if (chartRef.value) {
      chartInstance = echarts.init(chartRef.value);
      updateChart();
    }
  };

  onMounted(() => {
    nextTick(() => {
      initChart();
    });
  });
</script>

<style scoped lang="less">
  .statistical-analysis {
    padding: 24px;

    .action-buttons {
      margin-top: 24px;
      text-align: center;
    }

    .chart-container {
      .chart {
        width: 100%;
      }
    }
  }
</style>
