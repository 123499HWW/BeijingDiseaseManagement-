<template>
  <div>
    <a-row :gutter="16">
      <a-col :span="6">
        <ChainItem
          title="新增患者趋势"
          quota="newPatientsTrend"
          chart-type="line"
        />
      </a-col>
      <a-col :span="6">
        <ChainItem
          title="治疗完成趋势"
          quota="treatmentCompletedTrend"
          chart-type="line"
        />
      </a-col>
      <a-col :span="6">
        <ChainItem
          title="随访人数趋势"
          quota="followUpTrend"
          chart-type="line"
        />
      </a-col>
      <a-col :span="6">
        <ChainItem
          title="复发率趋势"
          quota="recurrenceRateTrend"
          chart-type="line"
        />
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
  import ChainItem from './chain-item.vue';
</script>
