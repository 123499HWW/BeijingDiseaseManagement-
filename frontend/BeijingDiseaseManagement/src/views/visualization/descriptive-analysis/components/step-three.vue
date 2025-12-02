<template>
  <a-card title="描述性统计分析" :bordered="false">
    <!-- 统计表格 -->
    <a-card title="统计信息汇总" :bordered="false" style="margin-bottom: 24px">
      <!-- 搜索栏 -->
      <div style="margin-bottom: 16px">
        <a-input
          v-model="searchKeyword"
          placeholder="搜索指标名称"
          style="width: 300px"
          allow-clear
        >
          <template #prefix>
            <icon-search />
          </template>
        </a-input>
      </div>

      <a-table
        :columns="statColumns"
        :data="filteredStatData"
        :pagination="pagination"
        :bordered="true"
        size="small"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      />
    </a-card>

    <!-- 图表展示区域 -->
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card
          title="直方图 (分布情况)"
          :bordered="false"
          style="margin-bottom: 16px"
        >
          <div
            ref="histogramRef"
            class="chart"
            style="height: 350px; width: 100%"
          ></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card
          title="箱线图 (离散程度)"
          :bordered="false"
          style="margin-bottom: 16px"
        >
          <div
            ref="boxplotRef"
            class="chart"
            style="height: 350px; width: 100%"
          ></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card
          title="条形图 (频数分布)"
          :bordered="false"
          style="margin-bottom: 16px"
        >
          <div
            ref="barChartRef"
            class="chart"
            style="height: 350px; width: 100%"
          ></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card
          title="饼图 (构成比)"
          :bordered="false"
          style="margin-bottom: 16px"
        >
          <div
            ref="pieChartRef"
            class="chart"
            style="height: 350px; width: 100%"
          ></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-form-item>
          <a-space>
            <a-button @click="$emit('prevStep')">上一步</a-button>
            <a-button type="primary" @click="finishAnalysis">完成分析</a-button>
            <a-button @click="$emit('resetAll')">重新开始</a-button>
          </a-space>
        </a-form-item>
      </a-col>
    </a-row>
  </a-card>
</template>

<script lang="ts" setup>
  import {
    ref,
    reactive,
    computed,
    watch,
    onMounted,
    nextTick,
    onUnmounted,
  } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import * as echarts from 'echarts';

  // Emits
  interface Emits {
    (e: 'prevStep'): void;
    (e: 'resetAll'): void;
  }

  defineEmits<Emits>();

  // 搜索和分页相关
  const searchKeyword = ref('');
  const pagination = ref({
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: true,
    pageSizeOptions: ['5', '10', '20', '50'],
  });

  // 统计表格列定义
  const statColumns = [
    { title: '指标名称', dataIndex: 'indicator', key: 'indicator', width: 120 },
    { title: '集中趋势', dataIndex: 'centralTendency', key: 'centralTendency' },
    { title: '离散程度', dataIndex: 'dispersion', key: 'dispersion' },
    { title: '分布情况', dataIndex: 'distribution', key: 'distribution' },
  ];

  // 统计表格数据
  const statData = reactive([
    {
      indicator: '年龄',
      centralTendency: '均值: 65.2, 中位数: 64.5, 众数: 62',
      dispersion: '标准差: 12.3, 四分位间距: 18.5, 极差: 45, 变异系数: 18.9%',
      distribution: '频数: 156, 频率: 78%, 构成比: 15.6%, 累积频率: 78%',
    },
    {
      indicator: 'CA199',
      centralTendency: '均值: 45.8, 中位数: 38.2, 众数: 35',
      dispersion: '标准差: 28.6, 四分位间距: 32.1, 极差: 156, 变异系数: 62.4%',
      distribution: '频数: 142, 频率: 71%, 构成比: 14.2%, 累积频率: 71%',
    },
    {
      indicator: 'CEA',
      centralTendency: '均值: 12.3, 中位数: 10.8, 众数: 9.5',
      dispersion: '标准差: 8.9, 四分位间距: 11.2, 极差: 67, 变异系数: 72.4%',
      distribution: '频数: 138, 频率: 69%, 构成比: 13.8%, 累积频率: 69%',
    },
    {
      indicator: '血糖',
      centralTendency: '均值: 6.8, 中位数: 6.5, 众数: 6.2',
      dispersion: '标准差: 1.2, 四分位间距: 1.8, 极差: 8.5, 变异系数: 17.6%',
      distribution: '频数: 165, 频率: 82.5%, 构成比: 16.5%, 累积频率: 82.5%',
    },
    {
      indicator: '血压(收缩压)',
      centralTendency: '均值: 128.5, 中位数: 126.0, 众数: 125',
      dispersion: '标准差: 15.8, 四分位间距: 22.3, 极差: 65, 变异系数: 12.3%',
      distribution: '频数: 178, 频率: 89%, 构成比: 17.8%, 累积频率: 89%',
    },
  ]);

  // 过滤后的统计数据
  const filteredStatData = computed(() => {
    if (!searchKeyword.value) {
      return statData;
    }
    return statData.filter((item) =>
      item.indicator.toLowerCase().includes(searchKeyword.value.toLowerCase())
    );
  });

  // 监听过滤后的数据变化，更新分页
  watch(filteredStatData, (newFilteredList) => {
    pagination.value.total = newFilteredList.length;
    pagination.value.current = 1; // 重置到第一页
  });

  // 分页处理函数
  const onPageChange = (current: number) => {
    pagination.value.current = current;
  };

  const onPageSizeChange = (pageSize: number) => {
    pagination.value.pageSize = pageSize;
    pagination.value.current = 1; // 重置到第一页
  };

  // 图表引用
  const histogramRef = ref<HTMLElement>();
  const boxplotRef = ref<HTMLElement>();
  const barChartRef = ref<HTMLElement>();
  const pieChartRef = ref<HTMLElement>();

  // 图表实例
  let histogramChart: echarts.ECharts | null = null;
  let boxplotChart: echarts.ECharts | null = null;
  let barChart: echarts.ECharts | null = null;
  let pieChart: echarts.ECharts | null = null;

  // 生成模拟统计数据
  const generateMockStatistics = () => {
    // 为每个指标生成随机统计数据
    statData.forEach((item, index) => {
      const baseValue = Math.floor(Math.random() * 100) + 50;
      const stdDev = Math.floor(Math.random() * 20) + 5;
      const median = baseValue + Math.random() * 5 - 2.5;
      const mode = baseValue + Math.random() * 3 - 1.5;
      const iqr = Math.random() * 15 + 8;
      const range = Math.random() * 50 + 20;
      const cv = (stdDev / baseValue) * 100;
      const frequency = Math.floor(Math.random() * 200) + 100;
      const totalSample = 1000;
      const percentage = (frequency / totalSample) * 100;
      const cumulative = Math.min(percentage + Math.random() * 20, 100);

      item.centralTendency = `均值: ${baseValue.toFixed(
        1
      )}, 中位数: ${median.toFixed(1)}, 众数: ${mode.toFixed(0)}`;
      item.dispersion = `标准差: ${stdDev.toFixed(
        1
      )}, 四分位间距: ${iqr.toFixed(1)}, 极差: ${range.toFixed(
        0
      )}, 变异系数: ${cv.toFixed(1)}%`;
      item.distribution = `频数: ${frequency}, 频率: ${percentage.toFixed(
        1
      )}%, 构成比: ${(percentage / 10).toFixed(
        1
      )}%, 累积频率: ${cumulative.toFixed(1)}%`;
    });
  };

  // 图表更新函数
  const updateHistogram = () => {
    if (!histogramChart) return;

    const option = {
      title: {
        text: '数据分布直方图',
        left: 'center',
        textStyle: {
          fontSize: 14,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: ['40-50', '50-60', '60-70', '70-80', '80-90', '90-100'],
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          fontSize: 12,
        },
      },
      series: [
        {
          name: '频数',
          type: 'bar',
          data: [15, 25, 30, 20, 8, 2],
          itemStyle: {
            color: '#5470c6',
            borderRadius: [4, 4, 0, 0],
          },
          barWidth: '60%',
        },
      ],
    };

    histogramChart.setOption(option);
  };

  const updateBoxplot = () => {
    if (!boxplotChart) return;

    const option = {
      title: {
        text: '箱线图',
        left: 'center',
        textStyle: {
          fontSize: 14,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'item',
        formatter(params: any) {
          return `${params.name}<br/>最小值: ${params.data[0]}<br/>Q1: ${params.data[1]}<br/>中位数: ${params.data[2]}<br/>Q3: ${params.data[3]}<br/>最大值: ${params.data[4]}`;
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: ['血压', '血糖', '白细胞'],
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          fontSize: 12,
        },
      },
      series: [
        {
          name: '数值',
          type: 'boxplot',
          data: [
            [50, 70, 80, 90, 100],
            [60, 75, 85, 95, 110],
            [40, 60, 70, 80, 90],
          ],
          itemStyle: {
            color: '#91cc75',
            borderColor: '#5470c6',
          },
        },
      ],
    };

    boxplotChart.setOption(option);
  };

  const updateBarChart = () => {
    if (!barChart) return;

    const option = {
      title: {
        text: '频数分布',
        left: 'center',
        textStyle: {
          fontSize: 14,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: ['正常', '轻度异常', '中度异常', '重度异常'],
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          fontSize: 12,
        },
      },
      series: [
        {
          name: '患者数',
          type: 'bar',
          data: [120, 45, 25, 10],
          itemStyle: {
            color: '#91cc75',
            borderRadius: [4, 4, 0, 0],
          },
          barWidth: '60%',
        },
      ],
    };

    barChart.setOption(option);
  };

  const updatePieChart = () => {
    if (!pieChart) return;

    const option = {
      title: {
        text: '构成比',
        left: 'center',
        textStyle: {
          fontSize: 14,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        textStyle: {
          fontSize: 12,
        },
      },
      series: [
        {
          name: '患者分布',
          type: 'pie',
          radius: '50%',
          center: ['50%', '50%'],
          data: [
            { value: 120, name: '正常', itemStyle: { color: '#91cc75' } },
            { value: 45, name: '轻度异常', itemStyle: { color: '#fac858' } },
            { value: 25, name: '中度异常', itemStyle: { color: '#ee6666' } },
            { value: 10, name: '重度异常', itemStyle: { color: '#73c0de' } },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
          label: {
            fontSize: 12,
            formatter: '{b}: {c} ({d}%)',
          },
        },
      ],
    };

    pieChart.setOption(option);
  };

  const updateCharts = () => {
    updateHistogram();
    updateBoxplot();
    updateBarChart();
    updatePieChart();
  };

  const finishAnalysis = () => {
    Message.success('分析完成！');
  };

  // 图表初始化
  const initCharts = () => {
    if (histogramRef.value) {
      histogramChart = echarts.init(histogramRef.value);
    }
    if (boxplotRef.value) {
      boxplotChart = echarts.init(boxplotRef.value);
    }
    if (barChartRef.value) {
      barChart = echarts.init(barChartRef.value);
    }
    if (pieChartRef.value) {
      pieChart = echarts.init(pieChartRef.value);
    }
    updateCharts();
  };

  // 窗口大小变化时重绘图表
  const handleResize = () => {
    if (histogramChart) {
      histogramChart.resize();
    }
    if (boxplotChart) {
      boxplotChart.resize();
    }
    if (barChart) {
      barChart.resize();
    }
    if (pieChart) {
      pieChart.resize();
    }
  };

  onMounted(() => {
    nextTick(() => {
      generateMockStatistics();
      initCharts();
      // 添加窗口大小变化监听
      window.addEventListener('resize', handleResize);
    });
  });

  onUnmounted(() => {
    // 移除窗口大小变化监听
    window.removeEventListener('resize', handleResize);
    // 销毁图表实例
    if (histogramChart) {
      histogramChart.dispose();
    }
    if (boxplotChart) {
      boxplotChart.dispose();
    }
    if (barChart) {
      barChart.dispose();
    }
    if (pieChart) {
      pieChart.dispose();
    }
  });
</script>

<style scoped lang="less">
  .chart {
    width: 100%;
  }
</style>
