<template>
  <div ref="chartRef" class="patient-profile-radar"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | null = null;

  // 基于实际数据的患者画像数据
  const radarData = {
    indicators: [
      { name: '年龄分布', max: 100 },
      { name: '职业风险', max: 100 },
      { name: '地域分布', max: 100 },
      { name: '血型分布', max: 100 },
      { name: '分期严重程度', max: 100 },
      { name: '诊断类型', max: 100 },
    ],
    series: [
      {
        name: '典型患者画像',
        data: [
          {
            value: [65, 75, 80, 60, 70, 85],
            name: '典型患者特征',
          },
        ],
        type: 'radar',
        areaStyle: {
          opacity: 0.3,
        },
        lineStyle: {
          width: 2,
        },
        itemStyle: {
          color: '#5470c6',
        },
      },
      {
        name: '高风险患者画像',
        data: [
          {
            value: [45, 90, 85, 70, 95, 90],
            name: '高风险特征',
          },
        ],
        type: 'radar',
        areaStyle: {
          opacity: 0.2,
        },
        lineStyle: {
          width: 2,
        },
        itemStyle: {
          color: '#ee6666',
        },
      },
      {
        name: '低风险患者画像',
        data: [
          {
            value: [75, 30, 40, 50, 30, 40],
            name: '低风险特征',
          },
        ],
        type: 'radar',
        areaStyle: {
          opacity: 0.2,
        },
        lineStyle: {
          width: 2,
        },
        itemStyle: {
          color: '#91cc75',
        },
      },
    ],
  };

  const initChart = () => {
    if (!chartRef.value) return;

    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: '患者画像雷达分析',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          const { name, value, seriesName } = params;
          return `
            <div style="padding: 8px;">
              <div style="font-weight: bold; margin-bottom: 4px;">${seriesName}</div>
              <div>特征维度: ${name}</div>
              <div>风险评分: ${value}</div>
              <div style="color: #999; font-size: 12px;">患者画像分析</div>
            </div>
          `;
        },
      },
      legend: {
        data: radarData.series.map((s) => s.name),
        top: 30,
        textStyle: {
          fontSize: 12,
        },
      },
      radar: {
        indicator: radarData.indicators,
        radius: '65%',
        center: ['50%', '55%'],
        splitNumber: 5,
        axisName: {
          color: '#333',
          fontSize: 10,
        },
        splitLine: {
          lineStyle: {
            color: ['#ddd'],
          },
        },
        splitArea: {
          show: false,
        },
        axisLine: {
          lineStyle: {
            color: '#ddd',
          },
        },
      },
      series: radarData.series,
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

  defineExpose({
    resize() {
      if (chartInstance) {
        chartInstance.resize();
      }
    },
  });
</script>

<style scoped>
  .patient-profile-radar {
    width: 100%;
    height: 100%;
  }
</style>
