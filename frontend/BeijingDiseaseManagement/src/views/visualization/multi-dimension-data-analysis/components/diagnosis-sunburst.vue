<template>
  <div ref="chartRef" class="diagnosis-sunburst-chart"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | null = null;

  // 基于实际数据的诊断类型层级结构
  const diagnosisData = [
    {
      name: '结肠相关',
      children: [
        {
          name: '乙状结肠恶性肿瘤',
          value: 45,
          itemStyle: { color: '#5470c6' },
        },
        {
          name: '结肠恶性肿瘤',
          value: 38,
          itemStyle: { color: '#91cc75' },
        },
        {
          name: '升结肠恶性肿瘤',
          value: 32,
          itemStyle: { color: '#fac858' },
        },
        {
          name: '降结肠恶性肿瘤',
          value: 28,
          itemStyle: { color: '#ee6666' },
        },
        {
          name: '盲肠恶性肿瘤',
          value: 15,
          itemStyle: { color: '#73c0de' },
        },
      ],
    },
    {
      name: '直肠相关',
      children: [
        {
          name: '直肠恶性肿瘤',
          value: 42,
          itemStyle: { color: '#3ba272' },
        },
        {
          name: '直肠腺癌',
          value: 25,
          itemStyle: { color: '#fc8452' },
        },
      ],
    },
    {
      name: '其他消化系统',
      children: [
        {
          name: '胆囊恶性肿瘤',
          value: 18,
          itemStyle: { color: '#9a60b4' },
        },
        {
          name: '胃恶性肿瘤',
          value: 12,
          itemStyle: { color: '#ea7ccc' },
        },
      ],
    },
    {
      name: '转移性癌',
      children: [
        {
          name: '转移性腺癌',
          value: 35,
          itemStyle: { color: '#ff7875' },
        },
        {
          name: '转移性癌',
          value: 22,
          itemStyle: { color: '#ffc53d' },
        },
      ],
    },
    {
      name: '良性肿瘤',
      children: [
        {
          name: '多发性结肠良性肿瘤',
          value: 8,
          itemStyle: { color: '#bae637' },
        },
      ],
    },
  ];

  const initChart = () => {
    if (!chartRef.value) return;

    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: '诊断类型分布',
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
        trigger: 'item',
        formatter: (params: any) => {
          const { name, value, path } = params;
          const percentage = ((value / 320) * 100).toFixed(1);
          return `
            <div style="padding: 8px;">
              <div style="font-weight: bold; margin-bottom: 4px;">${name}</div>
              <div>患者数量: ${value}人</div>
              <div>占比: ${percentage}%</div>
              ${
                path.length > 1
                  ? `<div style="color: #999; font-size: 12px;">${path
                      .slice(1)
                      .map((p: any) => p.name)
                      .join(' > ')}</div>`
                  : ''
              }
            </div>
          `;
        },
      },
      series: [
        {
          name: '诊断类型',
          type: 'sunburst',
          data: diagnosisData,
          radius: ['20%', '90%'],
          center: ['50%', '55%'],
          sort: null,
          levels: [
            {
              itemStyle: {
                borderWidth: 2,
                borderColor: '#fff',
              },
              label: {
                rotate: 'tangential',
              },
            },
            {
              itemStyle: {
                borderWidth: 1,
                borderColor: '#fff',
              },
              label: {
                rotate: 'tangential',
              },
            },
            {
              itemStyle: {
                borderWidth: 1,
                borderColor: '#fff',
              },
              label: {
                rotate: 'radial',
              },
            },
          ],
          label: {
            show: true,
            formatter: (params: any) => {
              return params.name;
            },
            fontSize: 12,
            color: '#333',
          },
          itemStyle: {
            borderRadius: 4,
            borderWidth: 2,
          },
          emphasis: {
            focus: 'ancestor',
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.3)',
            },
          },
        },
      ],
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: 10,
        top: 20,
        bottom: 20,
        textStyle: {
          fontSize: 12,
        },
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
  .diagnosis-sunburst-chart {
    width: 100%;
    height: 100%;
  }
</style>
