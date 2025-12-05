<template>
  <div class="paired-comparison">
    <a-page-header
      title="配对数据比较分析"
      subtitle="患者对照干预前后数据对比分析"
    />

    <!-- 患者搜索配置 -->
    <a-card title="患者搜索配置" :bordered="false" style="margin-bottom: 24px">
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="患者A医院流水号">
            <a-input
              v-model:value="patientA.hospitalSerialNumber"
              placeholder="请输入患者A医院流水号"
              allow-clear
            />
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="患者B医院流水号">
            <a-input
              v-model:value="patientB.hospitalSerialNumber"
              placeholder="请输入患者B医院流水号"
              allow-clear
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16" style="margin-top: 16px">
        <a-col :span="8">
          <a-form-item label="分析指标">
            <a-select
              v-model:value="selectedIndicator"
              placeholder="选择分析指标"
              @change="onIndicatorChange"
            >
              <a-option value="weight">体重</a-option>
              <a-option value="blood_pressure">血压</a-option>
              <a-option value="blood_glucose">血糖</a-option>
              <a-option value="white_blood_cell">白细胞计数</a-option>
              <a-option value="hemoglobin">血红蛋白</a-option>
              <a-option value="liver_function">肝功能指标</a-option>
              <a-option value="kidney_function">肾功能指标</a-option>
              <a-option value="blood_lipid">血脂指标</a-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="8">
          <a-form-item label="干预时间">
            <a-date-picker
              v-model:value="interventionDate"
              format="YYYY-MM-DD"
              placeholder="选择干预时间"
            />
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
                @click="searchPatients"
              >
                搜索患者
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

    <!-- 患者信息展示 -->
    <a-row :gutter="16" style="margin-bottom: 24px">
      <a-col :span="12">
        <a-card v-if="patientA.info" title="患者A信息" :bordered="false">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="医院流水号">{{
              patientA.info.serialNumber
            }}</a-descriptions-item>
            <a-descriptions-item label="姓名">{{
              patientA.info.name
            }}</a-descriptions-item>
            <a-descriptions-item label="性别">{{
              patientA.info.gender
            }}</a-descriptions-item>
            <a-descriptions-item label="年龄"
              >{{ patientA.info.age }}岁</a-descriptions-item
            >
            <a-descriptions-item label="科室">{{
              patientA.info.department
            }}</a-descriptions-item>
            <a-descriptions-item label="诊断">{{
              patientA.info.diagnosis
            }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>

      <a-col :span="12">
        <a-card v-if="patientB.info" title="患者B信息" :bordered="false">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="医院流水号">{{
              patientB.info.serialNumber
            }}</a-descriptions-item>
            <a-descriptions-item label="姓名">{{
              patientB.info.name
            }}</a-descriptions-item>
            <a-descriptions-item label="性别">{{
              patientB.info.gender
            }}</a-descriptions-item>
            <a-descriptions-item label="年龄"
              >{{ patientB.info.age }}岁</a-descriptions-item
            >
            <a-descriptions-item label="科室">{{
              patientB.info.department
            }}</a-descriptions-item>
            <a-descriptions-item label="诊断">{{
              patientB.info.diagnosis
            }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>

    <!-- 数据可视化展示 -->
    <a-card
      title="指标趋势对比图"
      :bordered="false"
      style="margin-bottom: 24px"
    >
      <div class="chart-container">
        <div ref="chartRef" class="chart" style="height: 400px"></div>
      </div>
    </a-card>

    <!-- 分析结果 -->
    <a-card v-if="analysisResult" title="分析结果" :bordered="false">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="配对t检验">{{
          analysisResult.pairedTTest.method
        }}</a-descriptions-item>
        <a-descriptions-item label="配对t检验P值">{{
          analysisResult.pairedTTest.pValue
        }}</a-descriptions-item>
        <a-descriptions-item label="配对t检验统计量">{{
          analysisResult.pairedTTest.statistic
        }}</a-descriptions-item>
        <a-descriptions-item label="Wilcoxon检验">{{
          analysisResult.wilcoxonTest.method
        }}</a-descriptions-item>
        <a-descriptions-item label="Wilcoxon检验P值">{{
          analysisResult.wilcoxonTest.pValue
        }}</a-descriptions-item>
        <a-descriptions-item label="Wilcoxon检验统计量">{{
          analysisResult.wilcoxonTest.statistic
        }}</a-descriptions-item>
        <a-descriptions-item label="样本数">{{
          analysisResult.sampleSize
        }}</a-descriptions-item>
        <a-descriptions-item label="患者A干预前均值">{{
          analysisResult.patientABeforeMean
        }}</a-descriptions-item>
        <a-descriptions-item label="患者A干预后均值">{{
          analysisResult.patientAAfterMean
        }}</a-descriptions-item>
        <a-descriptions-item label="患者B干预前均值">{{
          analysisResult.patientBBeforeMean
        }}</a-descriptions-item>
        <a-descriptions-item label="患者B干预后均值">{{
          analysisResult.patientBAfterMean
        }}</a-descriptions-item>
        <a-descriptions-item label="平均变化">{{
          analysisResult.meanChange
        }}</a-descriptions-item>
        <a-descriptions-item label="置信区间">{{
          analysisResult.confidenceInterval
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
  const selectedIndicator = ref<string>('weight');
  const interventionDate = ref<string>('2024-06-01');

  const isLoading = ref(false);
  const isAnalyzing = ref(false);
  const analysisResult = ref<any>(null);
  const chartRef = ref<HTMLElement>();

  // 患者数据
  const patientA = reactive({
    hospitalSerialNumber: '',
    info: null as any,
    data: [] as number[],
  });

  const patientB = reactive({
    hospitalSerialNumber: '',
    info: null as any,
    data: [] as number[],
  });

  // 图表相关
  let chartInstance: echarts.ECharts | null = null;

  // 图表相关
  const updateChart = () => {
    if (!chartInstance) return;

    const indicatorNames: { [key: string]: string } = {
      weight: '体重',
      blood_pressure: '血压',
      blood_glucose: '血糖',
      white_blood_cell: '白细胞计数',
      hemoglobin: '血红蛋白',
      liver_function: '肝功能指标',
      kidney_function: '肾功能指标',
      blood_lipid: '血脂指标',
    };

    const timePoints = [
      '2024-01-15',
      '2024-02-15',
      '2024-03-15',
      '2024-04-15',
      '2024-05-15',
      '2024-06-15',
      '2024-07-15',
      '2024-08-15',
      '2024-09-15',
      '2024-10-15',
      '2024-11-15',
      '2024-12-15',
    ];

    // 生成患者A和B的完整数据
    const patientAData = timePoints.map(
      () => Math.floor(Math.random() * 10) + 70
    );
    const patientBData = timePoints.map(
      () => Math.floor(Math.random() * 10) + 75
    );

    // 找到干预时间对应的索引（假设6月15日是干预时间）
    const interventionIndex = 5; // 2024-06-15对应索引5

    const option = {
      title: {
        text: `${indicatorNames[selectedIndicator.value]}趋势对比分析`,
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['患者A', '患者B', '干预时间'],
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
        data: timePoints,
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '患者A',
          type: 'line',
          data: patientAData,
          smooth: true,
          itemStyle: { color: '#5470c6' },
        },
        {
          name: '患者B',
          type: 'line',
          data: patientBData,
          smooth: true,
          itemStyle: { color: '#91cc75' },
        },
        {
          name: '干预时间',
          type: 'line',
          data: timePoints.map((_, index) =>
            index === interventionIndex ? 100 : null
          ),
          lineStyle: {
            type: 'dashed',
            color: '#ff6b6b',
            width: 2,
          },
          symbol: 'none',
          markLine: {
            data: [
              {
                xAxis: interventionIndex,
                lineStyle: {
                  type: 'dashed',
                  color: '#ff6b6b',
                },
                label: {
                  formatter: '干预时间',
                  position: 'insideEndTop',
                },
              },
            ],
          },
        },
      ],
    };

    chartInstance.setOption(option);
  };

  const generateAnalysisResult = () => {
    const indicatorNames: { [key: string]: string } = {
      weight: '体重',
      blood_pressure: '血压',
      blood_glucose: '血糖',
      white_blood_cell: '白细胞计数',
      hemoglobin: '血红蛋白',
      liver_function: '肝功能指标',
      kidney_function: '肾功能指标',
      blood_lipid: '血脂指标',
    };

    const indicator = indicatorNames[selectedIndicator.value];

    // 生成模拟数据
    const patientABeforeMean = (Math.random() * 20 + 70).toFixed(1);
    const patientAAfterMean = (
      parseFloat(patientABeforeMean) -
      Math.random() * 5 -
      2
    ).toFixed(1);
    const patientBBeforeMean = (Math.random() * 20 + 75).toFixed(1);
    const patientBAfterMean = (
      parseFloat(patientBBeforeMean) -
      Math.random() * 5 -
      2
    ).toFixed(1);

    const meanChange = (
      (parseFloat(patientAAfterMean) + parseFloat(patientBAfterMean)) / 2 -
      (parseFloat(patientABeforeMean) + parseFloat(patientBBeforeMean)) / 2
    ).toFixed(1);

    // 配对t检验结果
    const tValue = (Math.random() * 3 + 4).toFixed(2);
    const tPValue = (Math.random() * 0.001 + 0.0001).toFixed(4);
    const df = Math.floor(Math.random() * 20 + 20);

    // Wilcoxon符号秩检验结果
    const wilcoxonValue = Math.floor(Math.random() * 200 + 300);
    const wilcoxonPValue = (Math.random() * 0.001 + 0.0001).toFixed(4);

    const sampleSize = Math.floor(Math.random() * 20 + 30);
    const ciLower = (parseFloat(meanChange) - 1.5).toFixed(1);
    const ciUpper = (parseFloat(meanChange) + 1.5).toFixed(1);

    analysisResult.value = {
      pairedTTest: {
        method: '配对t检验',
        statistic: `t=${tValue}, df=${df}`,
        pValue: tPValue,
      },
      wilcoxonTest: {
        method: 'Wilcoxon符号秩检验',
        statistic: `V=${wilcoxonValue}`,
        pValue: wilcoxonPValue,
      },
      sampleSize,
      patientABeforeMean: `${patientABeforeMean} ${
        selectedIndicator.value === 'weight' ? 'kg' : ''
      }`,
      patientAAfterMean: `${patientAAfterMean} ${
        selectedIndicator.value === 'weight' ? 'kg' : ''
      }`,
      patientBBeforeMean: `${patientBBeforeMean} ${
        selectedIndicator.value === 'weight' ? 'kg' : ''
      }`,
      patientBAfterMean: `${patientBAfterMean} ${
        selectedIndicator.value === 'weight' ? 'kg' : ''
      }`,
      meanChange: `${meanChange} ${
        selectedIndicator.value === 'weight' ? 'kg' : ''
      }`,
      confidenceInterval: `[${ciLower}, ${ciUpper}]`,
      conclusion: `干预后${indicator}（患者A: M=${patientAAfterMean}, 患者B: M=${patientBAfterMean}）${
        parseFloat(tPValue) < 0.05 ? '显著' : '不显著'
      }${
        parseFloat(meanChange) < 0 ? '低于' : '高于'
      }干预前（患者A: M=${patientABeforeMean}, 患者B: M=${patientBBeforeMean}），配对t检验: t(${df})=${tValue}, p=${tPValue}；Wilcoxon检验: V=${wilcoxonValue}, p=${wilcoxonPValue}`,
      interpretation:
        parseFloat(tPValue) < 0.05
          ? '存在统计学显著差异，干预效果明显'
          : '无统计学显著差异，干预效果不明显',
    };
  };

  // 事件处理
  const onIndicatorChange = (value: string) => {
    selectedIndicator.value = value;
    updateChart();
  };

  const searchPatients = async () => {
    if (!patientA.hospitalSerialNumber || !patientB.hospitalSerialNumber) {
      Message.error('请输入两个患者的医院流水号');
      return;
    }

    isLoading.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 1500);
      });

      // 模拟患者A信息
      patientA.info = {
        serialNumber: patientA.hospitalSerialNumber,
        name: '张三',
        gender: '男',
        age: 45,
        department: '内分泌科',
        diagnosis: '2型糖尿病',
      };

      // 模拟患者B信息
      patientB.info = {
        serialNumber: patientB.hospitalSerialNumber,
        name: '李四',
        gender: '女',
        age: 52,
        department: '内分泌科',
        diagnosis: '2型糖尿病',
      };

      Message.success(
        `已找到患者: ${patientA.info.name} 和 ${patientB.info.name}`
      );
      updateChart();
    } catch (error) {
      Message.error('搜索患者失败，请重试');
    } finally {
      isLoading.value = false;
    }
  };

  const startAnalysis = async () => {
    if (!selectedIndicator.value) {
      Message.error('请选择分析指标');
      return;
    }

    // 如果没有搜索患者，自动生成假数据
    if (!patientA.info || !patientB.info) {
      patientA.info = {
        serialNumber: 'P001',
        name: '张三',
        gender: '男',
        age: 45,
        department: '内分泌科',
        diagnosis: '2型糖尿病',
      };

      patientB.info = {
        serialNumber: 'P002',
        name: '李四',
        gender: '女',
        age: 52,
        department: '内分泌科',
        diagnosis: '2型糖尿病',
      };

      Message.info('使用默认患者数据进行演示分析');
    }

    isAnalyzing.value = true;

    try {
      await new Promise((resolve) => {
        setTimeout(resolve, 2000);
      });

      // 生成模拟分析结果
      generateAnalysisResult();

      Message.success('分析完成！');
    } catch (error) {
      Message.error('分析失败，请重试');
    } finally {
      isAnalyzing.value = false;
    }
  };

  const resetForm = () => {
    selectedIndicator.value = 'weight';
    interventionDate.value = '2024-06-01';
    patientA.hospitalSerialNumber = '';
    patientA.info = null;
    patientB.hospitalSerialNumber = '';
    patientB.info = null;
    analysisResult.value = null;
    Message.success('表单已重置');
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
  .paired-comparison {
    padding: 24px;

    .chart-container {
      .chart {
        width: 100%;
      }
    }
  }
</style>
