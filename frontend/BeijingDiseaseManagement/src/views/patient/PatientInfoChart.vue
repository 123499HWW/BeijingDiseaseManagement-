<template>
  <div>
    <a-space style="margin-bottom: 12px">
      <a-button
        v-for="(group, idx) in paramGroups"
        :key="group.name"
        type="primary"
        size="mini"
        :status="activeGroupIdx === idx ? 'success' : 'normal'"
        @click="activeGroupIdx = idx"
      >
        {{ group.name }}
      </a-button>
      <a-button type="outline" size="mini" @click="customVisible = true"
        >自定义组合</a-button
      >
    </a-space>
    <Chart
      ref="chartRef"
      style="width: 100%; height: 320px"
      :option="chartOption"
    />
    <!-- <a-space style="margin: 16px 0 0">
      <a-button
        v-for="event in medicationEvents"
        :key="event.time"
        type="outline"
        size="small"
        style="color: #faad14; border-color: #faad14"
        @click="showMedication(event)"
      >
        💊{{ event.time }}用药
      </a-button>
    </a-space> -->
    <a-modal
      v-model:visible="modalVisible"
      title="用药详情"
      :footer="false"
      width="600px"
    >
      <div style="margin-bottom: 8px; font-weight: 600">
        用药时间点：{{ selectedMedication?.time || '--' }}
        <span style="margin-left: 16px; color: #888; font-weight: 400">
          共{{ selectedMedication?.drugs?.length || 0 }}种药品
        </span>
      </div>
      <a-table
        :data="drugSummary"
        :pagination="false"
        size="small"
        :bordered="false"
        stripe
      >
        <a-table-column title="药品名称" data-index="name" />
        <a-table-column title="医嘱时间" data-index="time" />
        <a-table-column title="每次量" data-index="dose" />
        <a-table-column title="单位" data-index="unit" />
      </a-table>
    </a-modal>
    <a-modal v-model:visible="customVisible" title="自定义组合" width="400px">
      <a-input
        v-model="searchText"
        placeholder="搜索指标"
        style="margin-bottom: 12px"
        allow-clear
      />
      <a-checkbox-group
        v-model="customSelected"
        :options="filteredIndicators"
        style="max-height: 300px; overflow-y: auto"
      />
      <div style="margin-top: 16px">
        <div>已选择：</div>
        <ul>
          <li v-for="item in customSelected" :key="item">{{ item }}</li>
        </ul>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue';
  import Chart from '@/components/chart/index.vue';

  const paramGroups = [
    {
      name: '组合1',
      series: [
        {
          name: '心肌标志物',
          data: [5.2, 5.5, 5.8, 6.1, 6.5, 7.0, 7.8, 8.2, 8.7, 9.0, 9.3, 9.5],
        },
        {
          name: '心肌酶',
          data: [45, 47, 50, 53, 56, 60, 63, 66, 69, 71, 73, 75],
        },
        {
          name: '纤溶3项',
          data: [23, 24, 25, 27, 29, 31, 33, 35, 36, 37, 39, 40],
        },
        {
          name: '凝血4项',
          data: [3.2, 3.3, 3.5, 3.7, 3.9, 4.1, 4.3, 4.5, 4.6, 4.7, 4.9, 5.1],
        },
      ],
      xAxis: [
        '2023-01',
        '2023-02',
        '2023-03',
        '2023-04',
        '2023-05',
        '2023-06',
        '2023-07',
        '2023-08',
        '2023-09',
        '2023-10',
        '2023-11',
        '2023-12',
      ],
    },
    {
      name: '组合2',
      series: [
        {
          name: '白细胞',
          data: [
            8.5, 8.7, 8.9, 9.2, 9.5, 9.8, 10.1, 10.3, 10.5, 10.7, 10.9, 11.1,
          ],
        },
        {
          name: '红细胞',
          data: [4.2, 4.2, 4.1, 4.0, 3.9, 3.8, 3.7, 3.7, 3.6, 3.6, 3.5, 3.5],
        },
        {
          name: '血小板',
          data: [180, 178, 175, 172, 170, 168, 165, 163, 160, 158, 155, 152],
        },
        {
          name: '血红蛋白',
          data: [135, 134, 132, 130, 128, 126, 124, 122, 120, 119, 118, 117],
        },
      ],
      xAxis: [
        '2023-01',
        '2023-02',
        '2023-03',
        '2023-04',
        '2023-05',
        '2023-06',
        '2023-07',
        '2023-08',
        '2023-09',
        '2023-10',
        '2023-11',
        '2023-12',
      ],
    },
  ];

  // 用药事件数据
  const medicationEvents = [
    {
      time: '2023-03',
      drugs: [
        {
          name: '0.9%氯化钠注射液X(软袋)',
          time: '2023-03-15',
          dose: 100,
          unit: 'ml',
        },
        {
          name: '（国采四）盐酸氨溴索注射液Y',
          time: '2023-03-15',
          dose: 30,
          unit: 'mg',
        },
      ],
    },
    {
      time: '2023-06',
      drugs: [
        {
          name: '5%葡萄糖注射液NJ(直立聚丙烯)',
          time: '2023-06-10',
          dose: 100,
          unit: 'ml',
        },
        {
          name: '氨茶碱注射液2ml（基）',
          time: '2023-06-10',
          dose: 0.5,
          unit: 'g',
        },
      ],
    },
    {
      time: '2023-09',
      drugs: [
        {
          name: '5%葡萄糖注射液NJ(直立聚丙烯)',
          time: '2023-09-20',
          dose: 100,
          unit: 'ml',
        },
        {
          name: '氨茶碱注射液2ml（基）',
          time: '2023-09-20',
          dose: 0.5,
          unit: 'g',
        },
      ],
    },
  ];

  const activeGroupIdx = ref(0);
  const modalVisible = ref(false);
  const selectedMedication = ref<any>(null);
  const chartRef = ref();

  // 全部医学指标（图片提取）
  const allIndicators = [
    '活化部分凝血活酶时间',
    '凝血酶时间',
    '凝血酶原时间',
    '纤维蛋白原',
    '凝血酶原时间比值',
    '凝血酶原活度',
    '白细胞',
    '淋巴细胞比率',
    '中性粒细胞绝对值',
    '平均血小板体积',
    '红细胞蛋白',
    '碱性粒细胞比率',
    '单核细胞比率',
    '淋巴细胞绝对值',
    '平均血红蛋白量',
    '碱性粒细胞绝对值',
    '单核细胞绝对值',
    '红细胞体积分布宽度',
    '红细胞体积分布系数',
    '中性粒细胞绝对值',
    '血小板压积',
    '血小板',
    '红细胞平均体积',
    '红细胞',
    '红细胞压积',
    '平均血红蛋白浓度',
    '中性细胞绝对值',
    '中性细胞比率',
    '大型血小板比率',
    '血红蛋白',
    '血小板分布宽度',
    '血清反T3',
    '甲状腺球蛋白',
    '抗甲状腺过氧化酶抗体',
    '甲状腺素',
    '血清促甲状腺激素',
    '游离三碘甲状腺原氨酸',
    '游离甲状腺素',
    '三碘甲状腺原氨酸',
    '抗甲状腺球蛋白抗体',
    '总胆固醇',
    '载脂蛋白-B',
    '甘油三酯',
    '低密度脂蛋白胆固醇',
    '载脂蛋白-A1',
    '高密度脂蛋白胆固醇',
    'α-羟丁酸脱氢酶',
    '肌酸激酶同工酶',
    '肌酸激酶',
    '乳酸脱氢酶',
    '钙',
    '腺苷脱氨酶',
    '总胆红素',
    '间接胆红素',
    '谷草/谷丙',
    '谷草转氨酶',
    '胆碱脂酶',
    '白蛋白',
    '前白蛋白',
    '血清酶',
    '尿素',
    '白蛋白/球蛋白',
    '二氧化碳',
    '碱性磷酸酶',
    '钠',
    '总胆汁酸',
    '谷丙转氨酶',
    '血清铁',
    '铁蛋白',
    '转铁蛋白',
    '铜',
    '锌',
    '镁',
    '磷',
    '肌酐',
    '尿酸',
    '血清钾',
    '血清钙',
    '血清钠',
    '血清氯',
    '血清磷',
    '血清镁',
    '血清铜',
    '血清锌',
    '血清铁',
    '血清转铁蛋白',
    '血清铁蛋白',
    '血清总蛋白',
    '血清白蛋白',
    '血清球蛋白',
    '血清前白蛋白',
    '血清总胆固醇',
    '血清甘油三酯',
    '血清高密度脂蛋白胆固醇',
    '血清低密度脂蛋白胆固醇',
    '血清载脂蛋白A1',
    '血清载脂蛋白B',
    '血清脂蛋白a',
    '血清肌酸激酶',
    '血清乳酸脱氢酶',
    '血清肌酸激酶同工酶',
    '血清α-羟丁酸脱氢酶',
    '血清腺苷脱氨酶',
    '血清总胆红素',
    '血清间接胆红素',
    '血清直接胆红素',
    '血清谷草转氨酶',
    '血清谷丙转氨酶',
    '血清碱性磷酸酶',
    '血清γ-谷氨酰转肽酶',
    '血清胆碱脂酶',
    '血清总胆汁酸',
    '血清葡萄糖',
    '血清胰岛素',
    '血清C-肽',
    '血清胱抑素C',
    '血清β2-微球蛋白',
    '血清同型半胱氨酸',
    '血清叶酸',
    '血清维生素B12',
    '血清25-羟维生素D',
    '血清甲状腺球蛋白',
    '血清抗甲状腺球蛋白抗体',
    '血清抗甲状腺过氧化物酶抗体',
    '血清促甲状腺激素',
    '血清三碘甲状腺原氨酸',
    '血清游离三碘甲状腺原氨酸',
    '血清甲状腺素',
    '血清游离甲状腺素',
    '血清降钙素',
    '血清甲状旁腺激素',
    '血清胰高血糖素',
    '血清胰岛素样生长因子1',
    '血清胰岛素样生长因子结合蛋白3',
    '血清皮质醇',
    '血清促肾上腺皮质激素',
    '血清雌二醇',
    '血清孕酮',
    '血清睾酮',
    '血清泌乳素',
    '血清促卵泡激素',
    '血清促黄体生成素',
    '血清生长激素',
    '血清胰岛素样生长因子',
    '血清胰岛素样生长因子结合蛋白',
  ];
  const searchText = ref('');
  const filteredIndicators = computed(() =>
    allIndicators
      .filter((i) => i.includes(searchText.value))
      .map((i) => ({ label: i, value: i }))
  );
  const customVisible = ref(false);
  const customSelected = ref<string[]>([]);
  watch(activeGroupIdx, () => {
    customSelected.value = [];
  });

  watch(selectedMedication, (val) => {
    // eslint-disable-next-line no-console
    console.log('selectedMedication:', val);
  });

  function showMedication(event: any) {
    selectedMedication.value = event;
    modalVisible.value = true;
  }

  const drugSummary = [
    {
      name: '0.9%氯化钠注射液X(软袋)',
      time: '2023-03-15',
      dose: 100,
      unit: 'ml',
    },
    {
      name: '（国采四）盐酸氨溴索注射液Y',
      time: '2023-03-15',
      dose: 30,
      unit: 'mg',
    },
  ];

  const chartOption = computed(() => {
    const group = paramGroups[activeGroupIdx.value];
    // 只在第一个series加markPoint
    return {
      grid: { left: 40, right: 20, top: 40, bottom: 50 },
      legend: {
        top: 5,
        itemWidth: 12,
        itemHeight: 8,
        textStyle: { fontSize: 12 },
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#E5E6EB',
        textStyle: { color: '#333' },
      },
      xAxis: {
        type: 'category',
        data: group.xAxis,
        boundaryGap: false,
        axisLabel: {
          fontSize: 12,
          rotate: 45, // 旋转45度，防止标签重叠
          hideOverlap: true,
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
          end: 30, // 默认显示前30%
          height: 18,
          bottom: 10,
        },
        {
          type: 'inside',
          xAxisIndex: 0,
          start: 0,
          end: 30,
        },
      ],
      series: group.series.map((s, index) => {
        const base: any = {
          name: s.name,
          data: s.data,
          type: 'line',
          smooth: true,
          showSymbol: true,
          symbolSize: 6,
          lineStyle: { width: 2 },
          itemStyle: {
            color: ['#1765AD', '#52C41A', '#FA8C16', '#F5222D'][index % 4],
          },
          markLine: {
            symbol: ['none', 'none'],
            lineStyle: {
              type: 'dashed',
              color: '#faad14',
              width: 2,
            },
            label: {
              show: true,
              position: 'end',
              formatter: '💊用药',
              color: '#faad14',
              fontSize: 12,
            },
            data: medicationEvents.map((ev) => ({ xAxis: ev.time })),
            emphasis: {
              lineStyle: { width: 3 },
            },
          },
        };
        // 只在第一个series加markPoint
        if (index === 0) {
          base.markPoint = {
            symbol: 'pin',
            symbolSize: 40,
            label: {
              show: true,
              formatter: '💊用药',
              color: '#faad14',
              fontSize: 14,
              fontWeight: 'bold',
            },
            data: medicationEvents.map((ev) => ({
              xAxis: ev.time,
              y: 'max',
              name: ev.time,
            })),
          };
        }
        return base;
      }),
    };
  });
</script>
