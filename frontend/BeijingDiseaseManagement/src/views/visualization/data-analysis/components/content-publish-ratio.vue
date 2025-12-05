<template>
  <a-card class="general-card" :header-style="{ paddingBottom: '14px' }">
    <template #title> 各肿瘤类型年度发患者数分布 </template>
    <Chart style="width: 100%; height: 347px" :option="chartOption" />
  </a-card>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import Chart from '@/components/chart/index.vue';

  const xAxis = ref(['2018', '2019', '2020', '2021', '2022']);
  const rectalCancerData = ref([120, 130, 140, 150, 160]);
  const sigmoidColonCancerData = ref([80, 85, 90, 95, 100]);
  const colonCancerData = ref([60, 65, 70, 75, 80]);

  const chartOption = {
    grid: {
      left: '4%',
      right: 0,
      top: '20',
      bottom: '60',
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
      },
      axisLabel: {
        color: '#86909C',
      },
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#86909C',
        formatter(value: number) {
          return value;
        },
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
          html += `<div class='content-panel'><span style='background-color:${el.color}' class='tooltip-item-icon'></span><span>${el.seriesName}</span><span class='tooltip-value'>${el.value}</span></div>`;
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
        stack: 'one',
        type: 'bar',
        barWidth: 16,
        color: '#246EFF',
      },
      {
        name: '乙状结肠恶性肿瘤',
        data: sigmoidColonCancerData.value,
        stack: 'one',
        type: 'bar',
        color: '#00B2FF',
      },
      {
        name: '结肠恶性肿瘤',
        data: colonCancerData.value,
        stack: 'one',
        type: 'bar',
        color: '#81E2FF',
      },
    ],
  };
</script>

<style scoped lang="less"></style>
