<template>
  <a-table
    :columns="columns"
    :data="data"
    :pagination="pagination"
    :bordered="true"
    size="small"
    row-key="reportTime"
    @page-change="onPageChange"
  />
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import getInspectionData from './mock';

  const columns = [
    { title: '检验项目', dataIndex: 'inspectionItem' },
    { title: '检验结果', dataIndex: 'result' },
    { title: '单位', dataIndex: 'unit' },
    { title: '参考值', dataIndex: 'referenceValue' },
    { title: '报告时间', dataIndex: 'reportTime' },
  ];

  const pageSize = 10;
  const pagination = ref({
    current: 1,
    pageSize,
    total: 0,
    showTotal: true,
  });

  interface PathologyRecord {
    reportTime: string;
    inspectionItem: string;
    result: string;
    unit: string;
    referenceValue: string;
    id: number;
  }
  const data = ref<PathologyRecord[]>([]);

  function loadData(page = 1) {
    const { list, total } = getInspectionData(page, pageSize);
    data.value = list;
    pagination.value.total = total;
    pagination.value.current = page;
  }

  function onPageChange(page: number) {
    loadData(page);
  }

  loadData(1);
</script>
