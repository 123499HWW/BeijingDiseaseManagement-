<template>
  <div class="container">
    <a-tabs v-model="activeTab" @change="onTabChange">
      <a-tab-pane key="age" title="年龄维度">
        <div class="chart-fixed"><AgeDimensionChart /></div>
        <div class="table-fixed"><InteractionTable type="age" /></div>
      </a-tab-pane>
      <a-tab-pane key="region" title="地域维度">
        <div class="chart-fixed"><RegionDimensionChart /></div>
        <div class="table-fixed"><InteractionTable type="region" /></div>
      </a-tab-pane>
      <a-tab-pane key="job" title="职业维度">
        <div class="chart-fixed"><JobDimensionChart /></div>
        <div class="table-fixed"><InteractionTable type="job" /></div>
      </a-tab-pane>
      <a-tab-pane key="time" title="时间维度">
        <div class="chart-fixed"><TimeDimensionChart /></div>
        <div class="table-fixed"><InteractionTable type="time" /></div>
      </a-tab-pane>
      <a-tab-pane key="diagnosis" title="诊断类型">
        <div class="chart-fixed"><DiagnosisSunburst /></div>
        <div class="table-fixed"><InteractionTable type="diagnosis" /></div>
      </a-tab-pane>
      <a-tab-pane key="diagnosis-trend" title="诊断趋势">
        <div class="chart-fixed"><DiagnosisTrendChart /></div>
        <div class="table-fixed"
          ><InteractionTable type="diagnosis-trend"
        /></div>
      </a-tab-pane>
      <a-tab-pane key="blood-disease" title="血型分析">
        <div class="chart-fixed"><BloodDiseaseAnalysis /></div>
        <div class="table-fixed"><InteractionTable type="blood-disease" /></div>
      </a-tab-pane>
      <a-tab-pane key="tnm-heatmap" title="TNM分期">
        <div class="chart-fixed"><TnmDiagnosisHeatmap /></div>
        <div class="table-fixed"><InteractionTable type="tnm-heatmap" /></div>
      </a-tab-pane>
      <a-tab-pane key="region-occupation" title="地域职业">
        <div class="chart-fixed"><RegionOccupationDisease /></div>
        <div class="table-fixed"
          ><InteractionTable type="region-occupation"
        /></div>
      </a-tab-pane>
      <a-tab-pane key="patient-profile" title="患者画像">
        <div class="chart-fixed"><PatientProfileRadar /></div>
        <div class="table-fixed"
          ><InteractionTable type="patient-profile"
        /></div>
      </a-tab-pane>
      <a-tab-pane key="treatment" title="分期-药品组合">
        <div class="chart-fixed"><TreatmentSankey /></div>
        <div class="table-fixed"><InteractionTable type="treatment" /></div>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script lang="ts" setup>
  import { ref, nextTick } from 'vue';
  import AgeDimensionChart from './components/age-dimension-chart.vue';
  import JobDimensionChart from './components/job-dimension-chart.vue';
  import RegionDimensionChart from './components/region-dimension-chart.vue';
  import TimeDimensionChart from './components/time-dimension-chart.vue';
  import TreatmentSankey from './components/treatment-sankey.vue';
  import DiagnosisSunburst from './components/diagnosis-sunburst.vue';
  import DiagnosisTrendChart from './components/diagnosis-trend-chart.vue';
  import BloodDiseaseAnalysis from './components/blood-disease-analysis.vue';
  import TnmDiagnosisHeatmap from './components/tnm-diagnosis-heatmap.vue';
  import RegionOccupationDisease from './components/region-occupation-disease.vue';
  import PatientProfileRadar from './components/patient-profile-radar.vue';
  import InteractionTable from './components/interaction-table.vue';

  const activeTab = ref('age');

  // Tab切换时触发resize
  function onTabChange() {
    nextTick(() => {
      // 触发所有ECharts实例resize（假设每个子组件都暴露了resize方法）
      const chartComponents = document.querySelectorAll('.chart-fixed div');
      chartComponents.forEach((el) => {
        // eslint-disable-next-line no-underscore-dangle
        const echartsInstance = (el as any).__echarts__;
        if (echartsInstance && typeof echartsInstance.resize === 'function') {
          echartsInstance.resize();
        }
      });
    });
  }
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
  }
  .chart-fixed {
    width: 100%;
    height: 400px;
    background: #fff;
    border-radius: 8px;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .table-fixed {
    height: 480px;
    background: #fff;
    border-radius: 8px;
    overflow: auto;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  }
</style>
