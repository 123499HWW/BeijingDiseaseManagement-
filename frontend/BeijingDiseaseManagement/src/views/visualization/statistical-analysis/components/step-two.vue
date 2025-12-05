<template>
  <a-card title="步骤2：数据概览" :bordered="false">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-statistic
          title="选中患者数"
          :value="dataOverview?.selectedPatients || 0"
          :precision="0"
        />
      </a-col>
      <a-col :span="6">
        <a-statistic
          title="分析指标数"
          :value="dataOverview?.selectedIndicators || 0"
          :precision="0"
        />
      </a-col>
      <a-col :span="6">
        <a-statistic
          title="数据完整性"
          :value="dataOverview?.dataCompleteness || 0"
          :precision="1"
          suffix="%"
        />
      </a-col>
      <a-col :span="6">
        <a-statistic
          title="数据质量"
          :value="dataOverview?.dataQuality || 0"
          :precision="1"
          suffix="%"
        />
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 24px">
      <a-col :span="24">
        <a-card title="统计结果表格" :bordered="false">
          <a-table
            :columns="statisticalColumns"
            :data="statisticalTable"
            :pagination="false"
            size="small"
            bordered
            :scroll="{ x: 1500 }"
          />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-form-item>
          <a-space>
            <a-button @click="$emit('prevStep')">上一步</a-button>
            <a-button type="primary" @click="$emit('nextStep')"
              >下一步</a-button
            >
          </a-space>
        </a-form-item>
      </a-col>
    </a-row>
  </a-card>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';

  // Props
  interface Props {
    dataOverview: any;
    selectedIndicators: string[];
  }

  // Emits
  interface Emits {
    (e: 'prevStep'): void;
    (e: 'nextStep'): void;
  }

  const props = defineProps<Props>();
  const emit = defineEmits<Emits>();

  // 统计表格列定义
  const statisticalColumns = computed(() => {
    const baseColumns = [
      { title: '组别', dataIndex: 'group', key: 'group', fixed: 'left' },
      { title: '例数', dataIndex: 'count', key: 'count', fixed: 'left' },
      {
        title: '阳性数',
        dataIndex: 'positiveCount',
        key: 'positiveCount',
        fixed: 'left',
      },
    ];

    const indicatorColumns = (props.selectedIndicators || []).map(
      (indicator) => ({
        title: `${indicator} (%)`,
        dataIndex: indicator,
        key: indicator,
      })
    );

    const resultColumns = [
      {
        title: '阳性率 (%)',
        dataIndex: 'positiveRate',
        key: 'positiveRate',
        fixed: 'right',
      },
    ];

    return [...baseColumns, ...indicatorColumns, ...resultColumns];
  });

  // 生成统计表格数据
  const statisticalTable = computed(() => {
    if (!props.selectedIndicators || props.selectedIndicators.length === 0) {
      return [];
    }

    const groups = [
      '结肠癌',
      '直肠癌',
      '新闻学',
      '腺癌',
      '黏液腺癌',
      '印戒细胞癌',
    ];
    const tableData: any[] = [];

    groups.forEach((group) => {
      const count = Math.floor(Math.random() * 50) + 10;
      const positiveCount = Math.floor(count * (Math.random() * 0.4 + 0.3));
      const row: any = {
        key: group,
        group,
        count,
        positiveCount,
      };

      props.selectedIndicators.forEach((indicator) => {
        row[indicator] = (Math.random() * 80).toFixed(1);
      });

      row.positiveRate = ((positiveCount / count) * 100).toFixed(1);
      tableData.push(row);
    });

    return tableData;
  });
</script>
