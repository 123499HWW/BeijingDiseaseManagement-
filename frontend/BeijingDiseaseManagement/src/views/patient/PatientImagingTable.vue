<template>
  <a-card class="general-card">
    <a-table
      :columns="imagingColumns"
      :data="imagingData"
      :pagination="false"
      :bordered="true"
      size="small"
      row-key="id"
    />
  </a-card>
</template>

<script setup lang="ts">
  import { ref } from 'vue';

  const imagingColumns = [
    { title: '检查时间', dataIndex: 'checkTime' },
    { title: '影像所见', dataIndex: 'imagingFindings' },
    { title: '影像结论', dataIndex: 'imagingConclusion' },
    { title: '检查流水号', dataIndex: 'checkNo' },
  ];

  const imagingData = ref([
    {
      id: 1,
      checkTime: '2023-07-10 09:30',
      imagingFindings: '胸部X线片显示心影增大',
      imagingConclusion: '心影增大，建议进一步检查',
      checkNo: 'IMG20230710001',
    },
    {
      id: 2,
      checkTime: '2023-07-12 14:20',
      imagingFindings: '腹部CT显示肝脏正常',
      imagingConclusion: '肝脏形态正常，未见异常',
      checkNo: 'IMG20230712001',
    },
  ]);
</script>

<style scoped lang="less">
  .general-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    margin-bottom: 0;
  }
</style>
