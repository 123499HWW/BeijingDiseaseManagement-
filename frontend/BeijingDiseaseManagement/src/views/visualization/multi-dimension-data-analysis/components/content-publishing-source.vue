<template>
  <a-spin :loading="loading" style="width: 100%">
    <a-card
      class="general-card"
      :title="$t('multiDAnalysis.card.title.contentPublishingSource')"
    >
      <Chart style="width: 100%; height: 300px" :option="chartOption" />
    </a-card>
  </a-spin>
</template>

<script lang="ts" setup>
  import useLoading from '@/hooks/loading';
  import useChartOption from '@/hooks/chart-option';

  const { chartOption } = useChartOption((isDark) => {
    const graphicElementStyle = {
      textAlign: 'center',
      fill: isDark ? 'rgba(255,255,255,0.7)' : '#4E5969',
      fontSize: 14,
      lineWidth: 10,
      fontWeight: 'bold',
    };
    return {
      legend: {
        left: 'center',
        data: [
          '本院',
          '外院转诊',
          '体检发现',
          '随访发现',
          '肠镜',
          'CT',
          'MRI',
          '病理',
        ],
        bottom: 0,
        icon: 'circle',
        itemWidth: 8,
        textStyle: {
          color: isDark ? 'rgba(255,255,255,0.7)' : '#4E5969',
        },
        itemStyle: {
          borderWidth: 0,
        },
      },
      tooltip: {
        show: true,
        trigger: 'item',
      },
      graphic: {
        elements: [
          {
            type: 'text',
            left: '20%',
            top: 'center',
            style: {
              text: '患者来源',
              ...graphicElementStyle,
            },
          },
          {
            type: 'text',
            left: '70%',
            top: 'center',
            style: {
              text: '检查类型',
              ...graphicElementStyle,
            },
          },
        ],
      },
      series: [
        {
          type: 'pie',
          radius: ['50%', '70%'],
          center: ['25%', '50%'],
          label: {
            formatter: '{d}% ',
            color: isDark ? 'rgba(255, 255, 255, 0.7)' : '#4E5969',
          },
          itemStyle: {
            borderColor: isDark ? '#000' : '#fff',
            borderWidth: 1,
          },
          data: [
            { value: 520, name: '本院', itemStyle: { color: '#249EFF' } },
            { value: 180, name: '外院转诊', itemStyle: { color: '#846BCE' } },
            { value: 90, name: '体检发现', itemStyle: { color: '#21CCFF' } },
            { value: 60, name: '随访发现', itemStyle: { color: '#0E42D2' } },
          ],
        },
        {
          type: 'pie',
          radius: ['50%', '70%'],
          center: ['75%', '50%'],
          label: {
            formatter: '{d}% ',
            color: isDark ? 'rgba(255, 255, 255, 0.7)' : '#4E5969',
          },
          itemStyle: {
            borderColor: isDark ? '#000' : '#fff',
            borderWidth: 1,
          },
          data: [
            { value: 320, name: '肠镜', itemStyle: { color: '#249EFF' } },
            { value: 210, name: 'CT', itemStyle: { color: '#846BCE' } },
            { value: 180, name: 'MRI', itemStyle: { color: '#21CCFF' } },
            { value: 150, name: '病理', itemStyle: { color: '#0E42D2' } },
          ],
        },
      ],
    };
  });
  const { loading } = useLoading(false);
</script>

<style scoped lang="less"></style>
