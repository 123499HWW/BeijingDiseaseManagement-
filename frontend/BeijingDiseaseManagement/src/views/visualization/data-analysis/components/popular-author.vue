<template>
  <a-card class="general-card" :header-style="{ paddingBottom: '14px' }">
    <template #title> 高发肿瘤类型统计 </template>
    <a-table
      :data="tableData"
      :pagination="false"
      :bordered="false"
      style="margin-bottom: 20px"
      :scroll="{ x: '100%', y: '350px' }"
    >
      <template #columns>
        <a-table-column title="排名" data-index="ranking" />
        <a-table-column title="肿瘤类型" data-index="cancerType" />
        <a-table-column
          title="发患者数"
          data-index="caseCount"
          :sortable="{ sortDirections: ['ascend', 'descend'] }"
        />
        <a-table-column title="占比" data-index="ratio" />
      </template>
    </a-table>
  </a-card>
</template>

<script lang="ts" setup>
  const tableData = [
    { ranking: 1, cancerType: '呼吸道症候群', caseCount: 320, ratio: '34.5%' },
    {
      ranking: 2,
      cancerType: '脓毒症',
      caseCount: 210,
      ratio: '22.1%',
    },
    {
      ranking: 3,
      cancerType: '社区获得性肺炎',
      caseCount: 180,
      ratio: '19.4%',
    },
    {
      ranking: 4,
      cancerType: '横结肠恶性肿瘤',
      caseCount: 120,
      ratio: '13.2%',
    },
    { ranking: 5, cancerType: '社区获得性肺炎', caseCount: 90, ratio: '10.8%' },
  ];
</script>

<style scoped lang="less">
  .general-card {
    max-height: 425px;
  }
</style>
