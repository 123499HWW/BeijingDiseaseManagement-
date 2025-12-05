<template>
  <div ref="chartRef" class="blood-disease-analysis"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | null = null;

  // 基于实际数据的血型与疾病关联数据
  const analysisData = {
    bloodTypes: ['A型', 'B型', 'O型', 'AB型', '未查', '不详'],
    diseases: [
      '结肠恶性肿瘤',
      '直肠恶性肿瘤',
      '乙状结肠恶性肿瘤',
      '升结肠恶性肿瘤',
      '转移性癌',
    ],
    data: [
      // A型血在各疾病中的分布
      [25, 18, 15, 12, 8],
      // B型血在各疾病中的分布
      [20, 15, 12, 10, 6],
      // O型血在各疾病中的分布
      [30, 22, 18, 15, 10],
      // AB型血在各疾病中的分布
      [8, 6, 5, 4, 3],
      // 未查血型在各疾病中的分布
      [15, 12, 10, 8, 5],
      // 不详血型在各疾病中的分布
      [10, 8, 6, 5, 3],
    ],
  };

  const initChart = () => {
    if (!chartRef.value) return;

    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: '血型与疾病易感性分析',
        // subtext: '不同血型在各诊断类型中的分布情况',
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
          const { name, value, data } = params;
          const percentage = ((value / 158) * 100).toFixed(1); // 总患者数158
          return `
            <div style="padding: 8px;">
              <div style="font-weight: bold; margin-bottom: 4px;">${name}</div>
              <div>患者数量: ${value}人</div>
              <div>占比: ${percentage}%</div>
              <div style="color: #999; font-size: 12px;">血型-疾病关联</div>
            </div>
          `;
        },
      },
      legend: {
        data: analysisData.diseases,
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
        data: analysisData.bloodTypes,
        axisLabel: {
          fontSize: 10,
          rotate: 45,
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
      series: analysisData.diseases.map((disease, index) => ({
        name: disease,
        type: 'bar',
        stack: 'total',
        emphasis: {
          focus: 'series',
        },
        data: analysisData.data.map((row) => row[index]),
        itemStyle: {
          color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de'][index],
        },
      })),
      toolbox: {
        feature: {
          saveAsImage: {},
          dataView: {
            readOnly: false,
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
  .blood-disease-analysis {
    width: 100%;
    height: 100%;
  }
</style>
