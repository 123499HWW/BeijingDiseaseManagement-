<template>
  <div style="width: 100%">
    <div style="margin-bottom: 12px; font-weight: bold; font-size: 16px">
      时间趋势与肿瘤关系
    </div>
    <Chart :option="chartOption" style="width: 100%; height: 340px" />
  </div>
</template>

<script lang="ts" setup>
  import useChartOption from '@/hooks/chart-option';

  const { chartOption } = useChartOption(() => {
    return {
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: [
          '2023-07',
          '2023-08',
          '2023-09',
          '2023-10',
          '2023-11',
          '2023-12',
          '2024-01',
          '2024-02',
          '2024-03',
          '2024-04',
          '2024-05',
          '2024-06',
        ],
      },
      yAxis: { type: 'value', name: '新增患者数' },
      series: [
        {
          name: '新增患者数',
          type: 'line',
          data: [5, 8, 12, 15, 20, 32, 28, 25, 18, 10, 7, 6],
          smooth: true,
          itemStyle: { color: '#F77234' },
        },
      ],
    };
  });
</script>
