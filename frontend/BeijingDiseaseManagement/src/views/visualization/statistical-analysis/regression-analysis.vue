<template>
  <div class="regression-analysis">
    <a-page-header
      title="回归分析"
      subtitle="用于预测和判断因变量与自变量之间的关系"
    />

    <!-- 数据配置 -->
    <a-card title="数据配置" :bordered="false" style="margin-bottom: 24px">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="回归类型">
            <a-radio-group
              v-model:value="regressionType"
              @change="onRegressionTypeChange"
            >
              <a-radio value="linear">线性回归</a-radio>
              <a-radio value="logistic">Logistic回归</a-radio>
              <a-radio value="cox">Cox回归</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="因变量">
            <a-select
              v-model:value="dependentVariable"
              placeholder="选择因变量"
            >
              <a-option value="blood_glucose">血糖水平</a-option>
              <a-option value="disease_risk">发病风险</a-option>
              <a-option value="survival_time">生存时间</a-option>
              <a-option value="blood_pressure">血压</a-option>
              <a-option value="cholesterol">胆固醇</a-option>
              <a-option value="bmi">BMI</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="自变量">
            <a-select
              v-model:value="independentVariables"
              mode="multiple"
              placeholder="选择自变量"
            >
              <a-option value="age">年龄</a-option>
              <a-option value="weight">体重</a-option>
              <a-option value="height">身高</a-option>
              <a-option value="gender">性别</a-option>
              <a-option value="smoking">吸烟史</a-option>
              <a-option value="diabetes">糖尿病史</a-option>
              <a-option value="hypertension">高血压史</a-option>
              <a-option value="bmi">BMI</a-option>
              <a-option value="blood_pressure">血压</a-option>
              <a-option value="cholesterol">胆固醇</a-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16" style="margin-top: 16px">
        <a-col :span="8">
          <a-form-item label="样本数量">
            <a-input-number
              v-model:value="sampleSize"
              :min="50"
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

        <a-col :span="8">
          <a-form-item label="模型评估">
            <a-checkbox-group v-model:value="modelEvaluationOptions">
              <a-space direction="vertical">
                <a-checkbox value="r_squared">R²</a-checkbox>
                <a-checkbox value="adjusted_r_squared">调整R²</a-checkbox>
                <a-checkbox value="aic">AIC</a-checkbox>
                <a-checkbox value="bic">BIC</a-checkbox>
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
            title="自变量数"
            :value="dataOverview.independentCount"
            :precision="0"
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
        <a-col :span="6">
          <a-statistic title="模型类型" :value="dataOverview.modelType" />
        </a-col>
      </a-row>
    </a-card>

    <!-- 回归结果图表 -->
    <a-card title="回归结果图表" :bordered="false" style="margin-bottom: 24px">
      <div class="chart-container">
        <div ref="regressionChartRef" class="chart" style="height: 400px"></div>
      </div>
    </a-card>

    <!-- 分析结果 -->
    <a-card v-if="analysisResult" title="分析结果" :bordered="false">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="回归类型">{{
          analysisResult.regressionType
        }}</a-descriptions-item>
        <a-descriptions-item label="样本数">{{
          analysisResult.sampleSize
        }}</a-descriptions-item>
        <a-descriptions-item label="R²">{{
          analysisResult.rSquared
        }}</a-descriptions-item>
        <a-descriptions-item label="调整R²">{{
          analysisResult.adjustedRSquared
        }}</a-descriptions-item>
        <a-descriptions-item label="F统计量">{{
          analysisResult.fStatistic
        }}</a-descriptions-item>
        <a-descriptions-item label="P值">{{
          analysisResult.pValue
        }}</a-descriptions-item>
        <a-descriptions-item label="AIC">{{
          analysisResult.aic
        }}</a-descriptions-item>
        <a-descriptions-item label="BIC">{{
          analysisResult.bic
        }}</a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <a-alert type="info" title="模型解释" :closable="false">
        <p><strong>模型显著性：</strong>{{ analysisResult.significance }}</p>
        <p><strong>拟合优度：</strong>{{ analysisResult.goodnessOfFit }}</p>
        <p><strong>预测能力：</strong>{{ analysisResult.predictionPower }}</p>
        <p><strong>实际意义：</strong>{{ analysisResult.interpretation }}</p>
      </a-alert>
    </a-card>

    <!-- 系数表 -->
    <a-card
      v-if="coefficientsTable"
      title="回归系数表"
      :bordered="false"
      style="margin-bottom: 24px"
    >
      <a-table
        :columns="coefficientsColumns"
        :data="coefficientsTable"
        :pagination="false"
        size="small"
      />
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted, nextTick } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import * as echarts from 'echarts';

  // 响应式数据
  const regressionType = ref<string>('linear');
  const dependentVariable = ref<string>('blood_glucose');
  const independentVariables = ref<string[]>(['age', 'weight']);
  const sampleSize = ref<number>(200);
  const dataQualityOptions = ref<string[]>(['exclude_missing']);
  const modelEvaluationOptions = ref<string[]>([
    'r_squared',
    'adjusted_r_squared',
  ]);

  const isLoading = ref(false);
  const isAnalyzing = ref(false);
  const hasData = ref(false);
  const analysisResult = ref<any>(null);
  const coefficientsTable = ref<any[]>([]);
  const regressionChartRef = ref<HTMLElement>();

  // 数据概览
  const dataOverview = reactive({
    sampleSize: 200,
    independentCount: 2,
    completeness: 100,
    modelType: '线性回归',
  });

  // 生成的数据
  const generatedData = ref<any>({});

  // 图表相关
  let regressionChartInstance: echarts.ECharts | null = null;

  // 变量名称映射
  const variableNames: { [key: string]: string } = {
    age: '年龄',
    weight: '体重',
    height: '身高',
    gender: '性别',
    smoking: '吸烟史',
    diabetes: '糖尿病史',
    hypertension: '高血压史',
    bmi: 'BMI',
    blood_pressure: '血压',
    cholesterol: '胆固醇',
    blood_glucose: '血糖水平',
    disease_risk: '发病风险',
    survival_time: '生存时间',
  };

  // 回归类型名称映射
  const regressionTypeNames: { [key: string]: string } = {
    linear: '线性回归',
    logistic: 'Logistic回归',
    cox: 'Cox回归',
  };

  // 系数表列定义
  const coefficientsColumns = [
    {
      title: '变量',
      dataIndex: 'variable',
      key: 'variable',
    },
    {
      title: '系数',
      dataIndex: 'coefficient',
      key: 'coefficient',
    },
    {
      title: '标准误',
      dataIndex: 'stdError',
      key: 'stdError',
    },
    {
      title: 't值',
      dataIndex: 'tValue',
      key: 'tValue',
    },
    {
      title: 'P值',
      dataIndex: 'pValue',
      key: 'pValue',
    },
    {
      title: '显著性',
      dataIndex: 'significance',
      key: 'significance',
    },
  ];

  // 生成模拟数据
  const generateSimulatedData = () => {
    const data: any = {
      dependent: [],
      independent: {},
    };

    // 为每个自变量生成数据
    independentVariables.value.forEach((varName) => {
      data.independent[varName] = [];
    });

    // 生成样本数据
    for (let i = 0; i < sampleSize.value; i += 1) {
      // 生成自变量数据
      const age = Math.random() * 50 + 30; // 30-80岁
      const weight = Math.random() * 40 + 50; // 50-90kg
      const height = Math.random() * 30 + 160; // 160-190cm
      const gender = Math.random() > 0.5 ? 1 : 0; // 0-1
      const smoking = Math.random() > 0.7 ? 1 : 0; // 0-1
      const diabetes = Math.random() > 0.8 ? 1 : 0; // 0-1
      const hypertension = Math.random() > 0.75 ? 1 : 0; // 0-1
      const bmi = weight / (height / 100) ** 2;
      const bloodPressure = Math.random() * 40 + 120; // 120-160
      const cholesterol = Math.random() * 3 + 4; // 4-7

      // 根据回归类型生成因变量
      let dependentValue = 0;
      if (regressionType.value === 'linear') {
        // 线性回归：血糖水平
        dependentValue = 5 + 0.02 * age + 0.01 * weight + Math.random() * 2;
      } else if (regressionType.value === 'logistic') {
        // Logistic回归：发病风险
        const risk = 1 / (1 + Math.exp(-(-2 + 0.05 * age + 0.02 * weight)));
        dependentValue = Math.random() < risk ? 1 : 0;
      } else {
        // Cox回归：生存时间
        dependentValue = Math.random() * 60 + 20; // 20-80个月
      }

      // 存储数据
      data.dependent.push(dependentValue);
      if (independentVariables.value.includes('age'))
        data.independent.age.push(age);
      if (independentVariables.value.includes('weight'))
        data.independent.weight.push(weight);
      if (independentVariables.value.includes('height'))
        data.independent.height.push(height);
      if (independentVariables.value.includes('gender'))
        data.independent.gender.push(gender);
      if (independentVariables.value.includes('smoking'))
        data.independent.smoking.push(smoking);
      if (independentVariables.value.includes('diabetes'))
        data.independent.diabetes.push(diabetes);
      if (independentVariables.value.includes('hypertension'))
        data.independent.hypertension.push(hypertension);
      if (independentVariables.value.includes('bmi'))
        data.independent.bmi.push(bmi);
      if (independentVariables.value.includes('blood_pressure'))
        data.independent.blood_pressure.push(bloodPressure);
      if (independentVariables.value.includes('cholesterol'))
        data.independent.cholesterol.push(cholesterol);
    }

    generatedData.value = data;

    // 更新数据概览
    dataOverview.sampleSize = sampleSize.value;
    dataOverview.independentCount = independentVariables.value.length;
    dataOverview.modelType = regressionTypeNames[regressionType.value];
  };

  // 更新回归图表
  const updateRegressionChart = () => {
    if (!regressionChartInstance) return;

    let option;

    if (regressionType.value === 'linear') {
      // 线性回归：散点图 + 回归线
      const xData =
        generatedData.value.independent[independentVariables.value[0]] || [];
      const yData = generatedData.value.dependent;

      // 计算回归线
      const n = xData.length;
      const xMean = xData.reduce((a: number, b: number) => a + b, 0) / n;
      const yMean = yData.reduce((a: number, b: number) => a + b, 0) / n;

      let numerator = 0;
      let denominator = 0;
      for (let i = 0; i < n; i += 1) {
        numerator += (xData[i] - xMean) * (yData[i] - yMean);
        denominator += (xData[i] - xMean) * (xData[i] - xMean);
      }

      const slope = numerator / denominator;
      const intercept = yMean - slope * xMean;

      const regressionLine = xData.map((x: number) => slope * x + intercept);

      option = {
        title: {
          text: `${variableNames[dependentVariable.value]} vs ${
            variableNames[independentVariables.value[0]]
          } 线性回归`,
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
        },
        legend: {
          data: ['实际值', '回归线'],
          bottom: 10,
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true,
        },
        xAxis: {
          type: 'value',
          name: variableNames[independentVariables.value[0]],
        },
        yAxis: {
          type: 'value',
          name: variableNames[dependentVariable.value],
        },
        series: [
          {
            name: '实际值',
            type: 'scatter',
            data: xData.map((x: number, index: number) => [x, yData[index]]),
            symbolSize: 6,
            itemStyle: { color: '#5470c6', opacity: 0.7 },
          },
          {
            name: '回归线',
            type: 'line',
            data: xData.map((x: number, index: number) => [
              x,
              regressionLine[index],
            ]),
            lineStyle: { color: '#ff6b6b', width: 2 },
            symbol: 'none',
          },
        ],
      };
    } else if (regressionType.value === 'logistic') {
      // Logistic回归：ROC曲线
      // 这里可以基于实际数据生成ROC曲线，目前使用模拟数据
      // const predictions = generatedData.value.dependent;
      // const actuals = generatedData.value.dependent;

      option = {
        title: {
          text: 'Logistic回归 - ROC曲线',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true,
        },
        xAxis: {
          type: 'value',
          name: '假阳性率',
          min: 0,
          max: 1,
        },
        yAxis: {
          type: 'value',
          name: '真阳性率',
          min: 0,
          max: 1,
        },
        series: [
          {
            name: 'ROC曲线',
            type: 'line',
            data: [
              [0, 0],
              [0.1, 0.3],
              [0.2, 0.5],
              [0.3, 0.65],
              [0.4, 0.75],
              [0.5, 0.82],
              [0.6, 0.87],
              [0.7, 0.91],
              [0.8, 0.94],
              [0.9, 0.97],
              [1, 1],
            ],
            lineStyle: { color: '#5470c6', width: 2 },
            symbol: 'none',
          },
          {
            name: '随机线',
            type: 'line',
            data: [
              [0, 0],
              [1, 1],
            ],
            lineStyle: { color: '#999', width: 1, type: 'dashed' },
            symbol: 'none',
          },
        ],
      };
    } else {
      // Cox回归：生存曲线
      const timePoints = Array.from({ length: 60 }, (_, i) => i + 1);
      const survivalRates = timePoints.map(
        (t) => Math.exp(-0.02 * t) + Math.random() * 0.1
      );

      option = {
        title: {
          text: 'Cox回归 - 生存曲线',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true,
        },
        xAxis: {
          type: 'value',
          name: '时间（月）',
        },
        yAxis: {
          type: 'value',
          name: '生存率',
          min: 0,
          max: 1,
        },
        series: [
          {
            name: '生存曲线',
            type: 'line',
            data: timePoints.map((t, i) => [t, survivalRates[i]]),
            lineStyle: { color: '#5470c6', width: 2 },
            symbol: 'none',
          },
        ],
      };
    }

    regressionChartInstance.setOption(option);
  };

  // 生成分析结果
  const generateAnalysisResult = () => {
    const rSquared = (Math.random() * 0.3 + 0.6).toFixed(3);
    const adjustedRSquared = (parseFloat(rSquared) - 0.05).toFixed(3);
    const fStatistic = (Math.random() * 20 + 30).toFixed(2);
    const pValue = (Math.random() * 0.001 + 0.0001).toFixed(4);
    const aic = (Math.random() * 50 + 200).toFixed(1);
    const bic = (parseFloat(aic) + Math.random() * 20).toFixed(1);

    const significance = parseFloat(pValue) < 0.05 ? '统计显著' : '统计不显著';
    const goodnessOfFit = parseFloat(rSquared) > 0.7 ? '拟合良好' : '拟合一般';
    const predictionPower =
      parseFloat(rSquared) > 0.8 ? '预测能力强' : '预测能力一般';

    analysisResult.value = {
      regressionType: regressionTypeNames[regressionType.value],
      sampleSize: sampleSize.value,
      rSquared,
      adjustedRSquared,
      fStatistic,
      pValue,
      aic,
      bic,
      significance,
      goodnessOfFit,
      predictionPower,
      interpretation: `${
        regressionTypeNames[regressionType.value]
      }模型${significance}（p=${pValue}），R²=${rSquared}，${goodnessOfFit}，${predictionPower}。`,
    };

    // 生成系数表
    coefficientsTable.value = independentVariables.value.map((varName) => ({
      variable: variableNames[varName],
      coefficient: (Math.random() * 2 - 1).toFixed(3),
      stdError: (Math.random() * 0.5 + 0.1).toFixed(3),
      tValue: (Math.random() * 3 + 2).toFixed(2),
      pValue: (Math.random() * 0.01 + 0.001).toFixed(4),
      significance: Math.random() > 0.3 ? '***' : '**',
    }));
  };

  // 事件处理
  const onRegressionTypeChange = () => {
    // 根据回归类型自动设置因变量
    if (regressionType.value === 'linear') {
      dependentVariable.value = 'blood_glucose';
    } else if (regressionType.value === 'logistic') {
      dependentVariable.value = 'disease_risk';
    } else {
      dependentVariable.value = 'survival_time';
    }
  };

  const generateData = async () => {
    isLoading.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 1000);
      });

      generateSimulatedData();
      hasData.value = true;
      updateRegressionChart();

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
      updateRegressionChart();
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
    regressionType.value = 'linear';
    dependentVariable.value = 'blood_glucose';
    independentVariables.value = ['age', 'weight'];
    sampleSize.value = 200;
    dataQualityOptions.value = ['exclude_missing'];
    modelEvaluationOptions.value = ['r_squared', 'adjusted_r_squared'];
    hasData.value = false;
    analysisResult.value = null;
    coefficientsTable.value = [];
    Message.success('表单已重置');
  };

  const initChart = () => {
    if (regressionChartRef.value) {
      regressionChartInstance = echarts.init(regressionChartRef.value);
    }
  };

  onMounted(() => {
    nextTick(() => {
      initChart();
      // 页面加载时生成默认数据
      generateSimulatedData();
      hasData.value = true;
      updateRegressionChart();
    });
  });
</script>

<style scoped lang="less">
  .regression-analysis {
    padding: 24px;

    .chart-container {
      .chart {
        width: 100%;
      }
    }
  }
</style>
