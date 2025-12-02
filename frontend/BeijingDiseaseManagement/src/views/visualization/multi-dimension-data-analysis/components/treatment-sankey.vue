<template>
  <a-card class="general-card" title="分期-药品组合流向分析">
    <div class="sankey-block">
      <select v-model="selectedStage" style="width: 120px; margin-bottom: 12px">
        <option value="">全部分期</option>
        <option v-for="stage in stages" :key="stage" :value="stage">
          {{ stage }}
        </option>
      </select>
      <div class="sankey-layout">
        <div class="sankey-chart">
          <div ref="sankeyRef" style="width: 100%; height: 400px"></div>
        </div>
        <div class="combo-table">
          <table>
            <thead>
              <tr>
                <th>组合名称</th>
                <th>药品明细</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="combo in combos" :key="combo">
                <td>{{ combo }}</td>
                <td>{{ comboDetail[combo] }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
  import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
  import * as echarts from 'echarts';

  const sankeyRef = ref<HTMLDivElement | null>(null);
  let chart: echarts.ECharts | null = null;

  const stages = ['I期', 'II期', 'III期', 'IV期'];
  const combos = ['组合1', '组合2', '组合3', '组合4', '组合5'];
  const comboDetail: Record<string, string> = {
    组合1: '奥沙利铂 + 卡培他滨',
    组合2: '贝伐珠单抗 + 伊立替康',
    组合3: '氟尿嘧啶 + 奥沙利铂 + 西妥昔单抗',
    组合4: '紫杉醇 + 顺铂',
    组合5: '吉西他滨 + 替吉奥',
  };

  const nodes = [
    ...stages.map((name) => ({ name })),
    ...combos.map((name) => ({ name })),
  ];

  type SankeyLink = { source: string; target: string; value: number };
  function getRandom(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
  const allLinks: SankeyLink[] = [];
  stages.forEach((stage) => {
    combos.forEach((combo) => {
      if (Math.random() < 0.8) {
        allLinks.push({
          source: stage,
          target: combo,
          value: getRandom(5, 30),
        });
      }
    });
  });

  const selectedStage = ref('');
  const getLinks = () => {
    if (!selectedStage.value) return allLinks;
    return allLinks.filter((link) => link.source === selectedStage.value);
  };

  function renderChart() {
    if (sankeyRef.value) {
      if (!chart) chart = echarts.init(sankeyRef.value);
      chart.setOption({
        tooltip: {
          trigger: 'item',
          triggerOn: 'mousemove',
          formatter: (params: any) => {
            if (comboDetail[params.name]) {
              return `${params.name}<br/>${comboDetail[params.name]}`;
            }
            return params.name;
          },
        },
        series: [
          {
            type: 'sankey',
            data: nodes,
            links: getLinks(),
            emphasis: { focus: 'adjacency' },
            nodeAlign: 'left',
            lineStyle: { color: 'gradient', curveness: 0.5 },
            label: { fontWeight: 'bold' },
            draggable: true,
            zoom: 1,
          },
        ],
      });
    }
  }

  onMounted(renderChart);
  onBeforeUnmount(() => {
    if (chart) chart.dispose();
  });
  watch(selectedStage, renderChart);
</script>

<style scoped>
  .general-card {
    margin-bottom: 32px;
  }

  .sankey-block {
    width: 100%;
    text-align: left;
  }

  .sankey-layout {
    display: flex;
    flex-direction: row;
    gap: 32px;
    align-items: flex-start;
  }

  .sankey-chart {
    flex: 2;
    min-width: 0;
  }

  .combo-table {
    flex: 1;
    min-width: 180px;
    height: 400px;
    overflow: auto;
  }

  .combo-table table {
    width: 100%;
    font-size: 14px;
    table-layout: fixed;
    background: #fafbfc;
    border-collapse: collapse;
  }

  .combo-table th,
  .combo-table td {
    padding: 6px 10px;
    text-align: left;
    word-break: break-all;
    border: 1px solid #e5e6eb;
  }

  .combo-table th {
    font-weight: bold;
    background: #f2f3f5;
  }
</style>
