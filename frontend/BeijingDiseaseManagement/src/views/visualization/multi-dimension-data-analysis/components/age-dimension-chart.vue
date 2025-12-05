<template>
  <div ref="chartRef" style="width: 100%; height: 400px"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLDivElement | null>(null);
  let chart: echarts.ECharts | null = null;

  const ageData = [
    { value: 40, name: '0-18岁' },
    { value: 60, name: '19-30岁' },
    { value: 80, name: '31-45岁' },
    { value: 50, name: '46-60岁' },
    { value: 30, name: '61岁以上' },
  ];

  onMounted(() => {
    if (chartRef.value) {
      chart = echarts.init(chartRef.value);
      chart.setOption({
        title: {
          text: '年龄分布玫瑰图',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}<br/>人数: {c} ({d}%)',
        },
        legend: {
          bottom: 10,
          left: 'center',
        },
        series: [
          {
            name: '年龄分布',
            type: 'pie',
            radius: ['30%', '70%'],
            center: ['50%', '50%'],
            roseType: 'area',
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2,
            },
            label: {
              show: true,
              fontWeight: 'bold',
            },
            color: [
              'rgba(0,191,255,0.8)',
              'rgba(0,255,127,0.8)',
              'rgba(255,215,0,0.8)',
              'rgba(255,99,71,0.8)',
              'rgba(138,43,226,0.8)',
            ],
            data: ageData,
          },
        ],
      });
    }
  });

  onBeforeUnmount(() => {
    if (chart) {
      chart.dispose();
    }
  });
</script>
