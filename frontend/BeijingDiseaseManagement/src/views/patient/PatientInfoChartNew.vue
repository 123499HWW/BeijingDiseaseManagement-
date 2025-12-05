<template>
  <div style="padding: 24px; background: #fff; border-radius: 8px">
    <div
      style="
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 16px;
      "
    >
      <h3 style="margin: 0">异常指标监控</h3>
      <a-tag color="red" size="large">
        异常指标数量: {{ abnormalIndicators.length }}
      </a-tag>
    </div>

    <Chart
      ref="chartRef"
      style="width: 100%; height: 400px"
      :option="chartOption"
    />

    <!-- 异常指标详情 -->
    <div style="margin-top: 16px">
      <h4 style="margin-bottom: 12px">异常指标详情</h4>
      <a-table
        v-if="abnormalIndicators && abnormalIndicators.length > 0"
        :data="abnormalIndicators"
        :pagination="false"
        size="small"
        :bordered="false"
        stripe
      >
        <a-table-column title="指标名称" data-index="name" />
        <a-table-column title="当前值" data-index="currentValue">
          <template #default="scope">
            <span
              v-if="scope && scope.record"
              :style="{
                color: scope.record.isAbnormal ? '#f5222d' : '#52c41a',
                fontWeight: 'bold',
              }"
            >
              {{ scope.record.currentValue }}
            </span>
          </template>
        </a-table-column>
        <a-table-column title="参考范围" data-index="referenceRange" />
        <a-table-column title="异常程度" data-index="severity">
          <template #default="scope">
            <a-tag
              v-if="scope && scope.record"
              :color="getSeverityColor(scope.record.severity)"
            >
              {{ getSeverityText(scope.record.severity) }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="趋势" data-index="trend">
          <template #default="scope">
            <span
              v-if="scope && scope.record"
              :style="{
                color: scope.record.trend === 'up' ? '#f5222d' : '#52c41a',
              }"
            >
              {{ scope.record.trend === 'up' ? '↗' : '↘' }}
              {{ scope.record.trendText }}
            </span>
          </template>
        </a-table-column>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue';
  import Chart from '@/components/chart/index.vue';

  // 异常指标数据
  const abnormalIndicators = ref([
    {
      name: '谷丙转氨酶',
      currentValue: 85,
      referenceRange: '7-40 U/L',
      severity: 'moderate',
      trend: 'up',
      trendText: '持续升高',
      data: [45, 52, 58, 65, 72, 78, 85],
      isAbnormal: true,
    },
    {
      name: '谷草转氨酶',
      currentValue: 78,
      referenceRange: '13-35 U/L',
      severity: 'moderate',
      trend: 'up',
      trendText: '持续升高',
      data: [38, 42, 48, 55, 62, 70, 78],
      isAbnormal: true,
    },
    {
      name: '总胆红素',
      currentValue: 32,
      referenceRange: '3.4-20.5 μmol/L',
      severity: 'mild',
      trend: 'up',
      trendText: '轻度升高',
      data: [18, 20, 22, 25, 28, 30, 32],
      isAbnormal: true,
    },
    {
      name: '肌酐',
      currentValue: 156,
      referenceRange: '59-104 μmol/L',
      severity: 'severe',
      trend: 'up',
      trendText: '显著升高',
      data: [98, 105, 115, 125, 135, 145, 156],
      isAbnormal: true,
    },
    {
      name: '尿素',
      currentValue: 12.5,
      referenceRange: '2.9-8.2 mmol/L',
      severity: 'moderate',
      trend: 'up',
      trendText: '持续升高',
      data: [7.2, 8.1, 9.0, 10.2, 11.1, 11.8, 12.5],
      isAbnormal: true,
    },
    {
      name: '白细胞',
      currentValue: 3.2,
      referenceRange: '3.5-9.5 ×10^9/L',
      severity: 'mild',
      trend: 'down',
      trendText: '轻度降低',
      data: [4.8, 4.5, 4.2, 3.9, 3.6, 3.4, 3.2],
      isAbnormal: true,
    },
  ]);

  const chartRef = ref();

  // 获取异常程度颜色
  const getSeverityColor = (severity: string) => {
    const colors: Record<string, string> = {
      mild: 'orange',
      moderate: 'red',
      severe: 'purple',
    };
    return colors[severity] || 'blue';
  };

  // 获取异常程度文本
  const getSeverityText = (severity: string) => {
    const texts: Record<string, string> = {
      mild: '轻度异常',
      moderate: '中度异常',
      severe: '重度异常',
    };
    return texts[severity] || '正常';
  };

  // 时间轴数据
  const timeAxis = [
    '2023-07',
    '2023-08',
    '2023-09',
    '2023-10',
    '2023-11',
    '2023-12',
    '2024-01',
  ];

  const colors = [
    '#f5222d',
    '#fa8c16',
    '#faad14',
    '#52c41a',
    '#1890ff',
    '#722ed1',
  ];

  const chartOption = computed(() => {
    const series = abnormalIndicators.value.map((indicator, index) => {
      return {
        name: indicator.name,
        type: 'line',
        data: indicator.data,
        smooth: true,
        showSymbol: true,
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: colors[index % colors.length],
        },
        itemStyle: {
          color: colors[index % colors.length],
        },
      };
    });

    return {
      title: {
        text: '异常指标趋势监控',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#E5E6EB',
        textStyle: { color: '#333' },
      },
      legend: {
        top: 30,
        itemWidth: 12,
        itemHeight: 8,
        textStyle: { fontSize: 12 },
      },
      grid: {
        left: 60,
        right: 40,
        top: 80,
        bottom: 60,
      },
      xAxis: {
        type: 'category',
        data: timeAxis,
        boundaryGap: false,
        axisLabel: {
          fontSize: 12,
          rotate: 45,
        },
        axisLine: { lineStyle: { color: '#E5E6EB' } },
        axisTick: { lineStyle: { color: '#E5E6EB' } },
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}',
          fontSize: 12,
        },
        splitLine: { lineStyle: { color: '#E5E6EB' } },
        axisLine: { lineStyle: { color: '#E5E6EB' } },
        axisTick: { lineStyle: { color: '#E5E6EB' } },
      },
      dataZoom: [
        {
          type: 'slider',
          show: true,
          xAxisIndex: 0,
          start: 0,
          end: 100,
          height: 18,
          bottom: 10,
        },
        {
          type: 'inside',
          xAxisIndex: 0,
          start: 0,
          end: 100,
        },
      ],
      series,
    };
  });
</script>
