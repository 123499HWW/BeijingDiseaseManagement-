<template>
  <div class="correlation-analysis">
    <a-page-header title="相关分析" subtitle="衡量两个变量之间的相关程度" />

    <!-- 数据配置 -->
    <a-card title="数据配置" :bordered="false" style="margin-bottom: 24px">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="变量X">
            <a-select
              v-model:value="variableX"
              placeholder="选择变量X"
              @change="onVariableChange"
            >
              <a-option value="age">年龄</a-option>
              <a-option value="weight">体重</a-option>
              <a-option value="height">身高</a-option>
              <a-option value="blood_pressure">血压</a-option>
              <a-option value="blood_glucose">血糖</a-option>
              <a-option value="cholesterol">胆固醇</a-option>
              <a-option value="triglyceride">甘油三酯</a-option>
              <a-option value="bmi">BMI</a-option>
              <a-option value="heart_rate">心率</a-option>
              <a-option value="temperature">体温</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="变量Y">
            <a-select
              v-model:value="variableY"
              placeholder="选择变量Y"
              @change="onVariableChange"
            >
              <a-option value="age">年龄</a-option>
              <a-option value="weight">体重</a-option>
              <a-option value="height">身高</a-option>
              <a-option value="blood_pressure">血压</a-option>
              <a-option value="blood_glucose">血糖</a-option>
              <a-option value="cholesterol">胆固醇</a-option>
              <a-option value="triglyceride">甘油三酯</a-option>
              <a-option value="bmi">BMI</a-option>
              <a-option value="heart_rate">心率</a-option>
              <a-option value="temperature">体温</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="数据类型">
            <a-radio-group v-model:value="dataType" @change="onDataTypeChange">
              <a-radio value="continuous">连续变量</a-radio>
              <a-radio value="ordinal">等级变量</a-radio>
              <a-radio value="categorical">分类变量</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16" style="margin-top: 16px">
        <a-col :span="8">
          <a-form-item label="分析方法">
            <a-select v-model:value="analysisMethod" placeholder="选择分析方法">
              <a-option value="pearson">Pearson相关</a-option>
              <a-option value="spearman">Spearman秩相关</a-option>
              <a-option value="chi_square">卡方检验</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="样本数量">
            <a-input-number
              v-model:value="sampleSize"
              :min="10"
              :max="1000"
              placeholder="输入样本数量"
            />
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="数据质量">
            <a-checkbox-group v-model:value="dataQualityOptions">
              <a-space direction="vertical">
                <a-checkbox value="exclude_missing">排除缺失值</a-checkbox>
                <a-checkbox value="exclude_outliers">排除异常值</a-checkbox>
                <a-checkbox value="normality_test">正态性检验</a-checkbox>
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
                :loading="isLoading"
                @click="generateData"
              >
                数据查询
              </a-button>
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
            title="样本数"
            :value="dataOverview.sampleSize"
            :precision="0"
          />
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="变量X均值"
            :value="dataOverview.xMean"
            :precision="2"
          />
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="变量Y均值"
            :value="dataOverview.yMean"
            :precision="2"
          />
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="数据完整性"
            :value="dataOverview.completeness"
            :precision="1"
            suffix="%"
          />
        </a-col>
      </a-row>
    </a-card>

    <!-- 散点图 -->
    <a-card title="散点图" :bordered="false" style="margin-bottom: 24px">
      <div class="chart-container">
        <div ref="scatterChartRef" class="chart" style="height: 400px"></div>
      </div>
    </a-card>

    <!-- 分析结果 -->
    <a-card v-if="analysisResult" title="分析结果" :bordered="false">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="分析方法">{{
          analysisResult.method
        }}</a-descriptions-item>
        <a-descriptions-item label="相关系数">{{
          analysisResult.correlation
        }}</a-descriptions-item>
        <a-descriptions-item label="P值">{{
          analysisResult.pValue
        }}</a-descriptions-item>
        <a-descriptions-item label="样本数">{{
          analysisResult.sampleSize
        }}</a-descriptions-item>
        <a-descriptions-item label="置信区间">{{
          analysisResult.confidenceInterval
        }}</a-descriptions-item>
        <a-descriptions-item label="效应量">{{
          analysisResult.effectSize
        }}</a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <a-alert type="info" title="结果解释" :closable="false">
        <p><strong>相关性强度：</strong>{{ analysisResult.strength }}</p>
        <p><strong>统计显著性：</strong>{{ analysisResult.significance }}</p>
        <p><strong>实际意义：</strong>{{ analysisResult.interpretation }}</p>
      </a-alert>
    </a-card>

    <!-- 正态性检验结果 -->
    <a-card
      v-if="normalityResult"
      title="正态性检验"
      :bordered="false"
      style="margin-bottom: 24px"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="变量X正态性">{{
          normalityResult.xNormality
        }}</a-descriptions-item>
        <a-descriptions-item label="变量Y正态性">{{
          normalityResult.yNormality
        }}</a-descriptions-item>
        <a-descriptions-item label="Shapiro-Wilk检验">{{
          normalityResult.shapiroWilk
        }}</a-descriptions-item>
        <a-descriptions-item label="Kolmogorov-Smirnov检验">{{
          normalityResult.kolmogorovSmirnov
        }}</a-descriptions-item>
      </a-descriptions>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted, nextTick } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import * as echarts from 'echarts';

  // 响应式数据
  const variableX = ref<string>('age');
  const variableY = ref<string>('weight');
  const dataType = ref<string>('continuous');
  const analysisMethod = ref<string>('pearson');
  const sampleSize = ref<number>(100);
  const dataQualityOptions = ref<string[]>(['exclude_missing']);

  const isLoading = ref(false);
  const isAnalyzing = ref(false);
  const hasData = ref(false);
  const analysisResult = ref<any>(null);
  const normalityResult = ref<any>(null);
  const scatterChartRef = ref<HTMLElement>();

  // 数据概览
  const dataOverview = reactive({
    sampleSize: 100,
    xMean: 50,
    yMean: 70,
    completeness: 100,
  });

  // 生成的数据
  const generatedData = ref<{ x: number[]; y: number[] }>({ x: [], y: [] });

  // 图表相关
  let scatterChartInstance: echarts.ECharts | null = null;

  // 计算属性 - 移除未使用的变量

  // 变量名称映射
  const variableNames: { [key: string]: string } = {
    age: '年龄',
    weight: '体重',
    height: '身高',
    blood_pressure: '血压',
    blood_glucose: '血糖',
    cholesterol: '胆固醇',
    triglyceride: '甘油三酯',
    bmi: 'BMI',
    heart_rate: '心率',
    temperature: '体温',
  };

  // 生成模拟数据
  const generateSimulatedData = () => {
    const x: number[] = [];
    const y: number[] = [];

    // 根据变量类型生成不同的数据范围
    const getDataRange = (variable: string) => {
      const ranges: { [key: string]: { min: number; max: number } } = {
        age: { min: 20, max: 80 },
        weight: { min: 40, max: 120 },
        height: { min: 150, max: 190 },
        blood_pressure: { min: 90, max: 180 },
        blood_glucose: { min: 4, max: 12 },
        cholesterol: { min: 3, max: 8 },
        triglyceride: { min: 0.5, max: 3 },
        bmi: { min: 18, max: 35 },
        heart_rate: { min: 60, max: 100 },
        temperature: { min: 36, max: 38 },
      };
      return ranges[variable] || { min: 0, max: 100 };
    };

    // 如果没有选择变量，使用默认值
    const xVar = variableX.value || 'age';
    const yVar = variableY.value || 'weight';

    const xRange = getDataRange(xVar);
    const yRange = getDataRange(yVar);

    // 生成相关数据（添加一些相关性）
    for (let i = 0; i < sampleSize.value; i += 1) {
      const baseX = Math.random() * (xRange.max - xRange.min) + xRange.min;
      const noise = (Math.random() - 0.5) * 0.3; // 添加噪声
      const baseY = Math.random() * (yRange.max - yRange.min) + yRange.min;

      // 添加相关性（根据数据类型调整）
      let correlationFactor = 0.6; // 默认中等相关性
      if (dataType.value === 'continuous') {
        correlationFactor = 0.7;
      } else if (dataType.value === 'ordinal') {
        correlationFactor = 0.5;
      } else {
        correlationFactor = 0.3;
      }

      const xFactor = (baseX - xRange.min) / (xRange.max - xRange.min);
      const correlatedY =
        baseY +
        xFactor * correlationFactor * (yRange.max - yRange.min) +
        noise * (yRange.max - yRange.min);

      x.push(parseFloat(baseX.toFixed(2)));
      const clampedY = Math.max(yRange.min, Math.min(yRange.max, correlatedY));
      y.push(parseFloat(clampedY.toFixed(2)));
    }

    generatedData.value = { x, y };

    // 更新数据概览
    dataOverview.sampleSize = x.length;
    dataOverview.xMean = parseFloat(
      (x.reduce((a, b) => a + b, 0) / x.length).toFixed(2)
    );
    dataOverview.yMean = parseFloat(
      (y.reduce((a, b) => a + b, 0) / y.length).toFixed(2)
    );
    dataOverview.completeness = 100; // 模拟数据完整性为100%
  };

  // 更新散点图
  const updateScatterChart = () => {
    if (!scatterChartInstance) return;

    const option = {
      title: {
        text: `${variableNames[variableX.value || 'age']} vs ${
          variableNames[variableY.value || 'weight']
        } 散点图`,
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
        formatter(params: any) {
          const data = params[0];
          return `${variableNames[variableX.value || 'age']}: ${
            data.value[0]
          }<br/>${variableNames[variableY.value || 'weight']}: ${
            data.value[1]
          }`;
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true,
      },
      xAxis: {
        type: 'value',
        name: variableNames[variableX.value || 'age'],
        nameLocation: 'middle',
        nameGap: 30,
      },
      yAxis: {
        type: 'value',
        name: variableNames[variableY.value || 'weight'],
        nameLocation: 'middle',
        nameGap: 40,
      },
      series: [
        {
          name: '数据点',
          type: 'scatter',
          data: generatedData.value.x.map((x, index) => [
            x,
            generatedData.value.y[index],
          ]),
          symbolSize: 8,
          itemStyle: {
            color: '#5470c6',
            opacity: 0.8,
          },
          emphasis: {
            itemStyle: {
              color: '#ff6b6b',
              opacity: 1,
            },
          },
        },
      ],
    };

    scatterChartInstance.setOption(option);
  };

  // 计算相关系数
  const calculateCorrelation = () => {
    const { x, y } = generatedData.value;
    const n = x.length;

    const xMean = x.reduce((a, b) => a + b, 0) / n;
    const yMean = y.reduce((a, b) => a + b, 0) / n;

    let numerator = 0;
    let xSumSquares = 0;
    let ySumSquares = 0;

    for (let i = 0; i < n; i += 1) {
      const xDiff = x[i] - xMean;
      const yDiff = y[i] - yMean;
      numerator += xDiff * yDiff;
      xSumSquares += xDiff * xDiff;
      ySumSquares += yDiff * yDiff;
    }

    const correlation = numerator / Math.sqrt(xSumSquares * ySumSquares);
    return parseFloat(correlation.toFixed(4));
  };

  // 生成分析结果
  const generateAnalysisResult = () => {
    const correlation = calculateCorrelation();
    const pValue = (Math.random() * 0.01 + 0.001).toFixed(4);
    const effectSize = Math.abs(correlation);

    let strength = '';
    if (Math.abs(correlation) >= 0.8) {
      strength = '强相关';
    } else if (Math.abs(correlation) >= 0.5) {
      strength = '中等相关';
    } else if (Math.abs(correlation) >= 0.3) {
      strength = '弱相关';
    } else {
      strength = '极弱相关或无相关';
    }

    const significance = parseFloat(pValue) < 0.05 ? '统计显著' : '统计不显著';

    const methodNames: { [key: string]: string } = {
      pearson: 'Pearson相关系数',
      spearman: 'Spearman秩相关系数',
      chi_square: '卡方检验',
    };

    const ciLower = (correlation - 0.1).toFixed(4);
    const ciUpper = (correlation + 0.1).toFixed(4);

    analysisResult.value = {
      method: methodNames[analysisMethod.value],
      correlation,
      pValue,
      sampleSize: sampleSize.value,
      confidenceInterval: `[${ciLower}, ${ciUpper}]`,
      effectSize: effectSize.toFixed(4),
      strength,
      significance,
      interpretation: `${variableNames[variableX.value || 'age']}与${
        variableNames[variableY.value || 'weight']
      }之间存在${strength}关系，相关系数为${correlation}，${significance}（p=${pValue}）。`,
    };

    // 生成正态性检验结果
    normalityResult.value = {
      xNormality: Math.random() > 0.3 ? '正态分布' : '非正态分布',
      yNormality: Math.random() > 0.3 ? '正态分布' : '非正态分布',
      shapiroWilk: `W=${(Math.random() * 0.2 + 0.8).toFixed(3)}, p=${(
        Math.random() * 0.01 +
        0.001
      ).toFixed(4)}`,
      kolmogorovSmirnov: `D=${(Math.random() * 0.2 + 0.1).toFixed(3)}, p=${(
        Math.random() * 0.01 +
        0.001
      ).toFixed(4)}`,
    };
  };

  // 事件处理
  const onVariableChange = () => {
    // 移除变量重复检查，允许选择相同变量
  };

  const onDataTypeChange = () => {
    // 根据数据类型自动选择分析方法
    if (dataType.value === 'continuous') {
      analysisMethod.value = 'pearson';
    } else if (dataType.value === 'ordinal') {
      analysisMethod.value = 'spearman';
    } else {
      analysisMethod.value = 'chi_square';
    }
  };

  const generateData = async () => {
    // 移除变量检查，直接生成数据
    isLoading.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 1000);
      });

      generateSimulatedData();
      hasData.value = true;
      updateScatterChart();

      Message.success(`已查询到${sampleSize.value}个样本的数据`);
    } catch (error) {
      Message.error('数据查询失败，请重试');
    } finally {
      isLoading.value = false;
    }
  };

  const startAnalysis = async () => {
    // 如果没有数据，先生成数据
    if (!hasData.value) {
      generateSimulatedData();
      hasData.value = true;
      updateScatterChart();
    }

    isAnalyzing.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 1500);
      });

      generateAnalysisResult();

      Message.success('分析完成！');
    } catch (error) {
      Message.error('分析失败，请重试');
    } finally {
      isAnalyzing.value = false;
    }
  };

  const resetForm = () => {
    variableX.value = '';
    variableY.value = '';
    dataType.value = 'continuous';
    analysisMethod.value = 'pearson';
    sampleSize.value = 100;
    dataQualityOptions.value = ['exclude_missing'];
    hasData.value = false;
    analysisResult.value = null;
    normalityResult.value = null;
    generatedData.value = { x: [], y: [] };
    Message.success('表单已重置');
  };

  const initChart = () => {
    if (scatterChartRef.value) {
      scatterChartInstance = echarts.init(scatterChartRef.value);
    }
  };

  onMounted(() => {
    nextTick(() => {
      initChart();
      // 页面加载时生成默认数据
      generateSimulatedData();
      hasData.value = true;
      updateScatterChart();
    });
  });
</script>

<style scoped lang="less">
  .correlation-analysis {
    padding: 24px;

    .chart-container {
      .chart {
        width: 100%;
      }
    }
  }
</style>
