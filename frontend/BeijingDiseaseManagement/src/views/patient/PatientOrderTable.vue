<template>
  <a-table
    :columns="columns"
    :data="data"
    :pagination="false"
    :bordered="true"
    size="small"
    row-key="id"
  />
</template>

<script setup lang="ts">
  import { ref } from 'vue';

  const columns = [
    { title: '住院流水号', dataIndex: 'hospitalNo' },
    { title: '项目名称', dataIndex: 'itemName' },
    { title: '医嘱时间', dataIndex: 'orderTime' },
    { title: '每次量', dataIndex: 'dose' },
    { title: '每次量单位', dataIndex: 'doseUnit' },
  ];

  const data = ref([
    {
      id: 1,
      hospitalNo: 'ZY010001391277',
      itemName: '0.9%氯化钠注射液X(软袋)',
      orderTime: '18:00.0',
      dose: 100,
      doseUnit: 'ml',
    },
    {
      id: 2,
      hospitalNo: 'ZY010001391277',
      itemName: '（国采四）盐酸氨溴索注射液Y',
      orderTime: '18:00.0',
      dose: 30,
      doseUnit: 'mg',
    },
    {
      id: 3,
      hospitalNo: 'ZY010001391277',
      itemName: '0.9%氯化钠注射液（10ml）',
      orderTime: '18:00.0',
      dose: 90,
      doseUnit: 'mg',
    },
    {
      id: 4,
      hospitalNo: 'ZY010001391277',
      itemName: '（国采五）吸入用布地奈德混悬液(S)',
      orderTime: '18:00.0',
      dose: 1,
      doseUnit: 'mg',
    },
    {
      id: 5,
      hospitalNo: 'ZY010001391277',
      itemName: '0.9%氯化钠注射液X(软袋)',
      orderTime: '34:45.0',
      dose: 100,
      doseUnit: 'ml',
    },
    {
      id: 6,
      hospitalNo: 'ZY010001391277',
      itemName: '（国采四）盐酸氨溴索注射液Y',
      orderTime: '34:45.0',
      dose: 30,
      doseUnit: 'mg',
    },
  ]);
</script>
