<template>
  <a-card class="general-card">
    <a-tabs v-model:active-key="activeTab">
      <a-tab-pane key="imaging" title="影像信息">
        <PatientImagingTable />
      </a-tab-pane>
      <a-tab-pane key="inspection" title="检验指标">
        <PatientInspectionTable />
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import PatientImagingTable from './PatientImagingTable.vue';
  import PatientInspectionTable from './PatientInspectionTable.vue';

  const activeTab = ref('inspection');
</script>
