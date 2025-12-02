<template>
  <a-spin :loading="loading" style="width: 100%">
    <a-card
      class="general-card"
      :title="$t('multiDAnalysis.card.title.dataOverview')"
    >
      <a-row justify="space-between">
        <a-col v-for="(item, idx) in renderData" :key="idx" :span="6">
          <a-statistic
            :title="item.title"
            :value="item.value"
            show-group-separator
            :value-from="0"
            animation
          >
            <template #prefix>
              <span
                class="statistic-prefix"
                :style="{ background: item.prefix.background }"
              >
                <component
                  :is="item.prefix.icon"
                  :style="{ color: item.prefix.iconColor }"
                />
              </span>
            </template>
          </a-statistic>
        </a-col>
      </a-row>
      <Chart style="height: 328px; margin-top: 20px" :option="chartOption" />
      <!-- 近一周异常指标患者表格 -->
      <div style="margin-top: 24px; font-weight: bold; font-size: 16px"
        >近一周异常指标患者统计</div
      >
      <a-table
        style="margin-top: 8px"
        :columns="tableColumns"
        :data="tableData"
        :pagination="false"
        bordered
        size="small"
      />
    </a-card>
  </a-spin>
</template>

<script lang="ts" setup>
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { LineSeriesOption } from 'echarts';
  import { queryDataOverview } from '@/api/visualization';
  import useLoading from '@/hooks/loading';
  import { ToolTipFormatterParams } from '@/types/echarts';
  import useThemes from '@/hooks/themes';
  import useChartOption from '@/hooks/chart-option';

  const tooltipItemsHtmlString = (items: ToolTipFormatterParams[]) => {
    return items
      .map(
        (el) => `<div class="content-panel">
        <p>
          <span style="background-color: ${
            el.color
          }" class="tooltip-item-icon"></span><span>${el.seriesName}</span>
        </p>
        <span class="tooltip-value">${
          (el.value ?? '').toLocaleString?.() ?? ''
        }</span>
      </div>`
      )
      .reverse()
      .join('');
  };

  const generateSeries = (
    name: string,
    lineColor: string,
    itemBorderColor: string,
    data: number[]
  ): LineSeriesOption => {
    return {
      name,
      data,
      stack: 'Total',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 10,
      itemStyle: {
        color: lineColor,
      },
      emphasis: {
        focus: 'series',
        itemStyle: {
          color: lineColor,
          borderWidth: 2,
          borderColor: itemBorderColor,
        },
      },
      lineStyle: {
        width: 2,
        color: lineColor,
      },
      showSymbol: false,
      areaStyle: {
        opacity: 0.1,
        color: lineColor,
      },
    };
  };
  const { t } = useI18n();
  const { loading, setLoading } = useLoading(true);
  const { isDark } = useThemes();
  const renderData = computed(() => [
    {
      title: '直肠癌患者总数',
      value: 1200,
      prefix: {
        icon: 'icon-user',
        background: isDark.value ? '#3F385E' : '#F5E8FF',
        iconColor: isDark.value ? '#8558D3' : '#722ED1',
      },
    },
    {
      title: '本月新增患者',
      value: 32,
      prefix: {
        icon: 'icon-plus',
        background: isDark.value ? '#3D5A62' : '#E8FFFB',
        iconColor: isDark.value ? '#6ED1CE' : '#33D1C9',
      },
    },
    {
      title: '治疗完成人数',
      value: 860,
      prefix: {
        icon: 'icon-check-circle',
        background: isDark.value ? '#354276' : '#E8F3FF',
        iconColor: isDark.value ? '#4A7FF7' : '#165DFF',
      },
    },
    {
      title: '住院中人数',
      value: 210,
      prefix: {
        icon: 'icon-clock-circle',
        background: isDark.value ? '#593E2F' : '#FFE4BA',
        iconColor: isDark.value ? '#F29A43' : '#F77234',
      },
    },
  ]);
  // 图表数据改为新增患者趋势
  const xAxis = ref<string[]>([
    '1月',
    '2月',
    '3月',
    '4月',
    '5月',
    '6月',
    '7月',
    '8月',
    '9月',
    '10月',
    '11月',
    '12月',
  ]);
  const newPatientsData = ref<number[]>([
    5, 8, 12, 15, 20, 32, 28, 25, 18, 10, 7, 6,
  ]);
  const { chartOption } = useChartOption((dark) => {
    return {
      grid: {
        left: '2.6%',
        right: '4',
        top: '40',
        bottom: '40',
      },
      xAxis: {
        type: 'category',
        offset: 2,
        data: xAxis.value,
        boundaryGap: false,
        axisLabel: {
          color: '#4E5969',
          formatter(value: number, idx: number) {
            return `${value}`;
          },
        },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false },
        axisPointer: { show: true, lineStyle: { color: '#23ADFF', width: 2 } },
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisLabel: {
          formatter(value: number) {
            return String(value);
          },
        },
        splitLine: { lineStyle: { color: dark ? '#2E2E30' : '#F2F3F5' } },
      },
      tooltip: {
        trigger: 'axis',
        formatter(params) {
          const [firstElement] = params as ToolTipFormatterParams[];
          return `<div><p class="tooltip-title">${
            firstElement.axisValueLabel
          }</p>${tooltipItemsHtmlString(
            params as ToolTipFormatterParams[]
          )}</div>`;
        },
        className: 'echarts-tooltip-diy',
      },
      series: [
        generateSeries('新增患者', '#3469FF', '#E8F3FF', newPatientsData.value),
      ],
    };
  });
  // 表格字段和数据
  const tableColumns = [
    { title: '姓名', dataIndex: 'name', align: 'center' },
    { title: '年龄', dataIndex: 'age', align: 'center' },
    { title: '性别', dataIndex: 'gender', align: 'center' },
    { title: '分期', dataIndex: 'stage', align: 'center' },
    { title: '诊断', dataIndex: 'diagnosis', align: 'center' },
    { title: '异常指标', dataIndex: 'abnormalIndex', align: 'center' },
    { title: '指标参数', dataIndex: 'indexValue', align: 'center' },
    { title: '指标单位', dataIndex: 'indexUnit', align: 'center' },
    { title: '检查日期', dataIndex: 'checkDate', align: 'center' },
  ];
  const tableData = [
    {
      name: '李*华',
      age: 56,
      gender: '女',
      stage: 'IV期',
      diagnosis: '乙状结肠恶性肿瘤',
      abnormalIndex: 'CA199',
      indexValue: 120,
      indexUnit: 'U/ml',
      checkDate: '2024-06-01',
    },
    {
      name: '母松义',
      age: 62,
      gender: '男',
      stage: 'IV期',
      diagnosis: '结肠恶性肿瘤',
      abnormalIndex: 'CEA',
      indexValue: 15.2,
      indexUnit: 'ng/ml',
      checkDate: '2024-06-02',
    },
    {
      name: '杨爱萍',
      age: 48,
      gender: '女',
      stage: 'IV期',
      diagnosis: '乙状结肠恶性肿瘤',
      abnormalIndex: 'CA125',
      indexValue: 80,
      indexUnit: 'U/ml',
      checkDate: '2024-06-03',
    },
    {
      name: '陈正静',
      age: 70,
      gender: '女',
      stage: 'IV期',
      diagnosis: '乙状结肠恶性肿瘤',
      abnormalIndex: 'CA724',
      indexValue: 9.8,
      indexUnit: 'U/ml',
      checkDate: '2024-06-04',
    },
    {
      name: '王胜明',
      age: 53,
      gender: '男',
      stage: 'IVC期',
      diagnosis: '多发性结肠息肉',
      abnormalIndex: 'CA199',
      indexValue: 110,
      indexUnit: 'U/ml',
      checkDate: '2024-06-05',
    },
  ];
  const fetchData = async () => {
    setLoading(true);
    try {
      const { data } = await queryDataOverview();
      xAxis.value = data.xAxis;
      // 已不再需要处理el.name的分支，直接忽略
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  };
  fetchData();
</script>

<style scoped lang="less">
  :deep(.arco-statistic) {
    .arco-statistic-title {
      color: rgb(var(--gray-10));
      font-weight: bold;
    }
    .arco-statistic-value {
      display: flex;
      align-items: center;
    }
  }
  .statistic-prefix {
    display: inline-block;
    width: 32px;
    height: 32px;
    margin-right: 8px;
    color: var(--color-white);
    font-size: 16px;
    line-height: 32px;
    text-align: center;
    vertical-align: middle;
    border-radius: 6px;
  }
</style>
