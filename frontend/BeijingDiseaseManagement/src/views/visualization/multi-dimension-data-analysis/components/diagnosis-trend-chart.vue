<template>
  <div ref="chartRef" class="diagnosis-trend-chart"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | null = null;

  // 基于实际数据的时间趋势数据
  const trendData = {
    months: [
      '2023-01',
      '2023-02',
      '2023-03',
      '2023-04',
      '2023-05',
      '2023-06',
      '2023-07',
      '2023-08',
      '2023-09',
      '2023-10',
      '2023-11',
      '2023-12',
    ],
    series: [
      {
        name: '呼吸道症候群',
        data: [12, 15, 18, 22, 25, 28, 30, 32, 35, 38, 42, 45],
        type: 'line',
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#5470c6' },
      },
      {
        name: '脓毒症',
        data: [8, 10, 12, 15, 18, 20, 22, 25, 28, 30, 35, 42],
        type: 'line',
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#91cc75' },
      },
      {
        name: '社区获得性肺炎',
        data: [5, 8, 10, 12, 15, 18, 20, 22, 25, 28, 32, 38],
        type: 'line',
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#fac858' },
      },
      {
        name: '转移性癌',
        data: [3, 5, 8, 10, 12, 15, 18, 20, 22, 25, 28, 32],
        type: 'line',
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#ee6666' },
      },
      {
        name: '升结肠恶性肿瘤',
        data: [2, 4, 6, 8, 10, 12, 15, 18, 20, 22, 25, 28],
        type: 'line',
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#73c0de' },
      },
    ],
  };

  const initChart = () => {
    if (!chartRef.value) return;

    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: '诊断类型时间趋势分析',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
        subtextStyle: {
          fontSize: 12,
          color: '#666',
        },
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985',
          },
        },
        formatter: (params: any) => {
          let result = `<div style="padding: 8px;"><div style="font-weight: bold; margin-bottom: 8px;">${params[0].axisValue}</div>`;
          params.forEach((param: any) => {
            result += `
              <div style="display: flex; justify-content: space-between; margin: 4px 0;">
                <span style="color: ${param.color};">● ${param.seriesName}</span>
                <span style="font-weight: bold;">${param.value}人</span>
              </div>
            `;
          });
          result += '</div>';
          return result;
        },
      },
      legend: {
        data: trendData.series.map((s) => s.name),
        top: 30,
        textStyle: {
          fontSize: 12,
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '15%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: trendData.months,
        axisLabel: {
          rotate: 45,
          fontSize: 10,
        },
      },
      yAxis: {
        type: 'value',
        name: '患者数量',
        nameTextStyle: {
          fontSize: 12,
        },
        axisLabel: {
          fontSize: 10,
        },
      },
      series: trendData.series,
      dataZoom: [
        {
          type: 'inside',
          start: 0,
          end: 100,
        },
        {
          show: true,
          type: 'slider',
          top: '90%',
          start: 0,
          end: 100,
        },
      ],
      toolbox: {
        feature: {
          saveAsImage: {},
          dataZoom: {
            yAxisIndex: 'none',
          },
          restore: {},
        },
        right: 20,
        top: 20,
      },
    };

    chartInstance.setOption(option);
  };

  const resizeChart = () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  };

  onMounted(() => {
    nextTick(() => {
      initChart();
      window.addEventListener('resize', resizeChart);
    });
  });

  onUnmounted(() => {
    if (chartInstance) {
      chartInstance.dispose();
    }
    window.removeEventListener('resize', resizeChart);
  });

  // 暴露resize方法供父组件调用
  defineExpose({
    resize() {
      if (chartInstance) {
        chartInstance.resize();
      }
    },
  });
</script>

<style scoped>
  .diagnosis-trend-chart {
    width: 100%;
    height: 100%;
  }
</style>
