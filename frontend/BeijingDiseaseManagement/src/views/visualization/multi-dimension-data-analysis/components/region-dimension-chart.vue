<template>
  <div ref="chartRef" style="width: 100%; height: 100%"></div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref<HTMLDivElement | null>(null);
  let chart: echarts.ECharts | null = null;

  // 云南省下属市区数据（name需与GeoJSON一致）
  const cityData = [
    { name: '昆明市', value: 80 },
    { name: '大理白族自治州', value: 50 },
    { name: '玉溪市', value: 30 },
    { name: '曲靖市', value: 60 },
    { name: '红河哈尼族彝族自治州', value: 40 },
    { name: '昭通市', value: 20 },
    { name: '普洱市', value: 10 },
    { name: '保山市', value: 25 },
    { name: '丽江市', value: 35 },
    { name: '临沧市', value: 15 },
    { name: '楚雄彝族自治州', value: 22 },
    { name: '文山壮族苗族自治州', value: 18 },
    { name: '西双版纳傣族自治州', value: 12 },
    { name: '德宏傣族景颇族自治州', value: 28 },
    { name: '怒江傈僳族自治州', value: 8 },
    { name: '迪庆藏族自治州', value: 5 },
  ];

  onMounted(async () => {
    if (chartRef.value) {
      chart = echarts.init(chartRef.value);
      chart.showLoading();
      // 加载 public/assets/云南省 (2).json
      const res = await fetch('/assets/云南省 (2).json');
      const geoJson = await res.json();
      chart.hideLoading();
      echarts.registerMap('yunnan', geoJson);

      chart.setOption({
        title: {
          text: '云南省各市肿瘤指标分布',
          subtext: '数据为示例',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}<br/>肿瘤指标: {c}',
        },
        visualMap: {
          min: 0,
          max: 80,
          text: ['高', '低'],
          realtime: false,
          calculable: true,
          inRange: {
            color: ['#e0f7fa', '#80deea', '#26c6da', '#00838f', '#d32f2f'],
          },
        },
        series: [
          {
            name: '肿瘤指标',
            type: 'map',
            map: 'yunnan',
            layoutCenter: ['50%', '50%'],
            layoutSize: '90%',
            label: {
              show: true,
              color: '#333',
              fontSize: 12,
            },
            data: cityData,
            emphasis: {
              itemStyle: { areaColor: '#c6e2ff' },
            },
          },
        ],
      });
    }
  });

  onBeforeUnmount(() => {
    if (chart) {
      chart.dispose();
    }
  });
</script>
