<template>
  <div style="width: 100%">
    <div style=" margin-bottom: 12px;font-weight: bold; font-size: 16px">
      职业分布与直肠癌类型关系
    </div>
    <Chart :option="chartOption" style=" width: 100%;height: 340px" />
  </div>
</template>

<script lang="ts" setup>
  import useChartOption from '@/hooks/chart-option';

  const { chartOption } = useChartOption(() => {
    return {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: {
        data: ['腺癌', '印戒细胞癌', '转移性癌', '转移性腺癌', '其他'],
      },
      grid: { left: 80, right: 20, bottom: 40, top: 40 },
      yAxis: {
        type: 'category',
        data: ['职业A', '职业B', '职业C', '职业D', '职业E'],
      },
      xAxis: { type: 'value', name: '人数' },
      series: [
        { name: '腺癌', type: 'bar', stack: '类型', data: [10, 8, 6, 5, 2] },
        {
          name: '印戒细胞癌',
          type: 'bar',
          stack: '类型',
          data: [2, 3, 1, 2, 1],
        },
        { name: '转移性癌', type: 'bar', stack: '类型', data: [5, 4, 2, 3, 1] },
        {
          name: '转移性腺癌',
          type: 'bar',
          stack: '类型',
          data: [1, 2, 1, 1, 0],
        },
        { name: '其他', type: 'bar', stack: '类型', data: [1, 1, 0, 0, 1] },
      ],
    };
  });
</script>
