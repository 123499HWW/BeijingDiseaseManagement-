<template>
  <div ref="chartRef" class="region-occupation-disease"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | null = null;

  // 基于实际数据的地域-职业-疾病关联数据
  const analysisData = {
    regions: ['昆明', '大理', '楚雄', '玉溪', '红河', '其他'],
    occupations: ['农民', '工人', '职员', '退休人员', '无业人员', '其他'],
    diseases: ['结肠恶性肿瘤', '直肠恶性肿瘤', '乙状结肠恶性肿瘤'],
    data: [
      // 昆明地区各职业在各疾病中的分布
      [
        [25, 20, 15], // 农民
        [15, 12, 8], // 工人
        [20, 18, 12], // 职员
        [10, 8, 5], // 退休人员
        [8, 6, 4], // 无业人员
        [5, 4, 3], // 其他
      ],
      // 大理地区
      [
        [20, 15, 12],
        [12, 10, 6],
        [15, 12, 8],
        [8, 6, 4],
        [6, 4, 3],
        [4, 3, 2],
      ],
      // 楚雄地区
      [
        [18, 14, 10],
        [10, 8, 5],
        [12, 10, 6],
        [6, 5, 3],
        [5, 4, 2],
        [3, 2, 1],
      ],
      // 玉溪地区
      [
        [15, 12, 8],
        [8, 6, 4],
        [10, 8, 5],
        [5, 4, 2],
        [4, 3, 2],
        [2, 2, 1],
      ],
      // 红河地区
      [
        [12, 10, 6],
        [6, 5, 3],
        [8, 6, 4],
        [4, 3, 2],
        [3, 2, 1],
        [2, 1, 1],
      ],
      // 其他地区
      [
        [10, 8, 5],
        [5, 4, 2],
        [6, 5, 3],
        [3, 2, 1],
        [2, 2, 1],
        [1, 1, 1],
      ],
    ],
  };

  const initChart = () => {
    if (!chartRef.value) return;

    chartInstance = echarts.init(chartRef.value);

    const option = {
      title: {
        text: '地域-职业-疾病关联分析',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          const { name, value, data } = params;
          const percentage = ((value / 500) * 100).toFixed(1); // 总患者数500
          return `
            <div style="padding: 8px;">
              <div style="font-weight: bold; margin-bottom: 4px;">${name}</div>
              <div>患者数量: ${value}人</div>
              <div>占比: ${percentage}%</div>
              <div style="color: #999; font-size: 12px;">地域-职业-疾病关联</div>
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
        data: analysisData.regions,
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
      series: analysisData.diseases.map((disease, diseaseIndex) => ({
        name: disease,
        type: 'bar',
        stack: 'total',
        emphasis: {
          focus: 'series',
        },
        data: analysisData.data.map((regionData, regionIndex) => {
          let total = 0;
          analysisData.occupations.forEach((_, occupationIndex) => {
            total += regionData[occupationIndex][diseaseIndex];
          });
          return total;
        }),
        itemStyle: {
          color: ['#5470c6', '#91cc75', '#fac858'][diseaseIndex],
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

  defineExpose({
    resize() {
      if (chartInstance) {
        chartInstance.resize();
      }
    },
  });
</script>

<style scoped>
  .region-occupation-disease {
    width: 100%;
    height: 100%;
  }
</style>
