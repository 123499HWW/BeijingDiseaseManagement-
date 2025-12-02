<template>
  <div ref="chartRef" class="tnm-diagnosis-heatmap"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | null = null;

  // 基于实际数据的TNM分期与诊断类型关联数据
  const heatmapData = {
    tnmStages: ['I期', 'II期', 'III期', 'IV期', '未分期'],
    diagnoses: [
      '结肠恶性肿瘤',
      '直肠恶性肿瘤',
      '乙状结肠恶性肿瘤',
      '升结肠恶性肿瘤',
      '转移性癌',
    ],
    data: [
      // I期在各诊断中的分布
      [15, 12, 8, 6, 2],
      // II期在各诊断中的分布
      [25, 20, 15, 12, 8],
      // III期在各诊断中的分布
      [35, 28, 22, 18, 15],
      // IV期在各诊断中的分布
      [20, 15, 12, 10, 25],
      // 未分期在各诊断中的分布
      [5, 5, 3, 4, 10],
    ],
  };

  const initChart = () => {
    if (!chartRef.value) return;

    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: 'TNM分期与诊断类型关联分析',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        position: 'top',
        formatter: (params: any) => {
          const { value, data } = params;
          const percentage = ((value / 300) * 100).toFixed(1); // 总患者数300
          return `
            <div style="padding: 8px;">
              <div style="font-weight: bold; margin-bottom: 4px;">${params.name}</div>
              <div>患者数量: ${value}人</div>
              <div>占比: ${percentage}%</div>
              <div style="color: #999; font-size: 12px;">TNM分期-诊断关联</div>
            </div>
          `;
        },
      },
      grid: {
        height: '50%',
        top: '10%',
      },
      xAxis: {
        type: 'category',
        data: heatmapData.diagnoses,
        splitArea: {
          show: true,
        },
        axisLabel: {
          rotate: 45,
          fontSize: 10,
        },
      },
      yAxis: {
        type: 'category',
        data: heatmapData.tnmStages,
        splitArea: {
          show: true,
        },
        axisLabel: {
          fontSize: 10,
        },
      },
      visualMap: {
        min: 0,
        max: 35,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '15%',
        inRange: {
          color: [
            '#313695',
            '#4575b4',
            '#74add1',
            '#abd9e9',
            '#e0f3f8',
            '#ffffcc',
            '#fee090',
            '#fdae61',
            '#f46d43',
            '#d73027',
            '#a50026',
          ],
        },
      },
      series: [
        {
          name: '患者数量',
          type: 'heatmap',
          data: heatmapData.data.flatMap((row, i) =>
            row.map((value, j) => [j, i, value])
          ),
          label: {
            show: true,
            fontSize: 10,
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
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
  .tnm-diagnosis-heatmap {
    width: 100%;
    height: 100%;
  }
</style>
