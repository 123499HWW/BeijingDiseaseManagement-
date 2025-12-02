<template>
  <a-card class="general-card">
    <a-table
      :columns="inspectionColumns"
      :data="inspectionData"
      :pagination="inspectionPagination"
      :loading="inspectionLoading"
      :bordered="false"
      size="small"
      row-key="id"
      @page-change="onInspectionPageChange"
      @row-click="onRowClick"
    />
  </a-card>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue';
  import getInspectionData from './mock';

  const emit = defineEmits(['indicatorClick']);

  const inspectionColumns = [
    { title: '报告时间', dataIndex: 'reportTime' },
    { title: '检验项目', dataIndex: 'inspectionItem' },
    { title: '检验结果', dataIndex: 'result' },
    { title: '单位', dataIndex: 'unit' },
    { title: '参考值', dataIndex: 'referenceValue' },
  ];

  const inspectionPagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const inspectionLoading = ref(false);
  interface InspectionItem {
    reportTime: string;
    inspectionItem: string;
    result: string;
    unit: string;
    referenceValue: string;
    id: number;
  }
  const inspectionData = ref<InspectionItem[]>([]);

  function fetchInspectionData(params = { current: 1, pageSize: 10 }) {
    inspectionLoading.value = true;
    setTimeout(() => {
      const { list, total } = getInspectionData(
        params.current,
        params.pageSize
      );
      inspectionData.value = list;
      inspectionPagination.total = total;
      inspectionLoading.value = false;
    }, 300);
  }

  function onInspectionPageChange(page: number) {
    inspectionPagination.current = page;
    fetchInspectionData({
      current: page,
      pageSize: inspectionPagination.pageSize,
    });
  }

  function onRowClick(record: InspectionItem) {
    emit('indicatorClick', record);
  }

  onMounted(() => {
    fetchInspectionData({
      current: inspectionPagination.current,
      pageSize: inspectionPagination.pageSize,
    });
  });
</script>

<style scoped lang="less">
  .general-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    margin-bottom: 0;
  }
</style>
