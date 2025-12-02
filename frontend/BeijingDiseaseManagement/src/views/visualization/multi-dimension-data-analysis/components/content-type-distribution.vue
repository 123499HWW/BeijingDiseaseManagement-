<template>
  <a-card
    class="general-card"
    :title="$t('multiDAnalysis.card.title.contentTypeDistribution')"
    :header-style="{ paddingBottom: 0 }"
  >
    <Chart style="height: 222px" :option="chartOption" />
  </a-card>
</template>

<script lang="ts" setup>
  import useChartOption from '@/hooks/chart-option';

  const { chartOption } = useChartOption((isDark) => {
    return {
      grid: {
        left: 0,
        right: 0,
        top: 0,
        bottom: 20,
      },
      legend: {
        show: true,
        top: 'center',
        right: '0',
        orient: 'vertical',
        icon: 'circle',
        itemWidth: 10,
        itemHeight: 10,
        itemGap: 20,
        textStyle: {
          color: isDark ? '#ffffff' : '#4E5969',
        },
        data: ['分期分布', '治疗方式分布'],
      },
      radar: {
        center: ['40%', '50%'],
        radius: 80,
        indicator: [
          { name: 'I期', max: 300 },
          { name: 'II期', max: 300 },
          { name: 'III期', max: 300 },
          { name: 'IV期', max: 300 },
          { name: '手术', max: 300 },
          { name: '放疗', max: 300 },
          { name: '化疗', max: 300 },
          { name: '靶向', max: 300 },
        ],
        axisName: {
          color: isDark ? '#ffffff' : '#1D2129',
        },
        axisLine: {
          lineStyle: {
            color: isDark ? '#484849' : '#E5E6EB',
          },
        },
        splitLine: {
          lineStyle: {
            color: isDark ? '#484849' : '#E5E6EB',
          },
        },
        splitArea: {
          areaStyle: {
            color: [],
          },
        },
      },
      series: [
        {
          type: 'radar',
          areaStyle: {
            opacity: 0.2,
          },
          data: [
            {
              value: [120, 180, 220, 80, 0, 0, 0, 0],
              name: '分期分布',
              symbol: 'none',
              itemStyle: {
                color: isDark ? '#6CAAF5' : '#249EFF',
              },
            },
            {
              value: [0, 0, 0, 0, 200, 120, 90, 60],
              name: '治疗方式分布',
              symbol: 'none',
              itemStyle: {
                color: isDark ? '#A079DC' : '#313CA9',
              },
            },
          ],
        },
      ],
    };
  });
</script>

<style scoped lang="less"></style>
