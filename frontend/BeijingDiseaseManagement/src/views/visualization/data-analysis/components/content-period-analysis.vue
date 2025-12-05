<template>
  <a-card class="general-card" :header-style="{ paddingBottom: '16px' }">
    <template #title> 特定呼吸道感染性疾病月度新发病例趋势 </template>
    <Chart style="width: 100%; height: 370px" :option="chartOption" />
  </a-card>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import Chart from '@/components/chart/index.vue';

  const xAxis = ref([
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
  ]);
  const rectalCancerData = ref([
    18, 19, 20, 22, 23, 21, 24, 25, 26, 27, 28, 29,
  ]);
  const sigmoidColonCancerData = ref([
    12, 13, 14, 15, 16, 15, 17, 18, 19, 20, 21, 22,
  ]);
  const colonCancerData = ref([10, 11, 12, 13, 14, 13, 15, 16, 17, 18, 19, 20]);

  const chartOption = {
    grid: {
      left: '40',
      right: 0,
      top: '20',
      bottom: '100',
    },
    legend: {
      bottom: 0,
      icon: 'circle',
      textStyle: {
        color: '#4E5969',
      },
    },
    xAxis: {
      type: 'category',
      data: xAxis.value,
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: '#A9AEB8',
        },
      },
      axisTick: {
        show: true,
        alignWithLabel: true,
        lineStyle: {
          color: '#86909C',
        },
        interval(idx: number) {
          if (idx === 0) return false;
          if (idx === xAxis.value.length - 1) return false;
          return true;
        },
      },
      axisLabel: {
        color: '#86909C',
        formatter(value: string, idx: number) {
          if (idx === 0) return '';
          if (idx === xAxis.value.length - 1) return '';
          return value;
        },
      },
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#86909C',
        formatter: '{value}例',
      },
      splitLine: {
        lineStyle: {
          color: '#E5E6EB',
        },
      },
    },
    tooltip: {
      show: true,
      trigger: 'axis',
      formatter(params: any) {
        let html = `<div><p class='tooltip-title'>${params[0].axisValueLabel}</p>`;
        params.forEach((el: any) => {
          html += `<div class='content-panel'><span style='background-color:${el.color}' class='tooltip-item-icon'></span><span>${el.seriesName}</span><span class='tooltip-value'>${el.value}例</span></div>`;
        });
        html += '</div>';
        return html;
      },
      className: 'echarts-tooltip-diy',
    },
    series: [
      {
        name: '直肠恶性肿瘤',
        data: rectalCancerData.value,
        type: 'line',
        smooth: true,
        showSymbol: false,
        color: '#246EFF',
        symbol: 'circle',
        symbolSize: 10,
        emphasis: {
          focus: 'series',
          itemStyle: {
            borderWidth: 2,
            borderColor: '#E0E3FF',
          },
        },
      },
      {
        name: '乙状结肠恶性肿瘤',
        data: sigmoidColonCancerData.value,
        type: 'line',
        smooth: true,
        showSymbol: false,
        color: '#00B2FF',
        symbol: 'circle',
        symbolSize: 10,
        emphasis: {
          focus: 'series',
          itemStyle: {
            borderWidth: 2,
            borderColor: '#E2F2FF',
          },
        },
      },
      {
        name: '结肠恶性肿瘤',
        data: colonCancerData.value,
        type: 'line',
        smooth: true,
        showSymbol: false,
        color: '#81E2FF',
        symbol: 'circle',
        symbolSize: 10,
        emphasis: {
          focus: 'series',
          itemStyle: {
            borderWidth: 2,
            borderColor: '#D9F6FF',
          },
        },
      },
    ],
    dataZoom: [
      {
        bottom: 40,
        type: 'slider',
        left: 40,
        right: 14,
        height: 14,
        borderColor: 'transparent',
        handleSize: '20',
        handleStyle: {
          shadowColor: 'rgba(0, 0, 0, 0.2)',
          shadowBlur: 4,
        },
        brushSelect: false,
        backgroundColor: '#F2F3F5',
      },
      {
        type: 'inside',
        start: 0,
        end: 100,
        zoomOnMouseWheel: false,
      },
    ],
  };
</script>

<style scoped lang="less">
  .chart-box {
    width: 100%;
    height: 230px;
  }
</style>
