<template>
  <div>
    <LLMConfig v-if="!reportGenerated" @generate="onGenerate" />
    <LLMReport v-else :data="reportData" @back="onBack" />
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import LLMConfig from './Config.vue';
  import LLMReport from './Report.vue';

  const reportGenerated = ref(false);
  const reportData = ref<any>(null);

  function onGenerate(data: any) {
    // 这里可以调用后端生成报告，当前用mock
    reportData.value = {
      ...data,
      patient: {
        name: '张三',
        age: 56,
        gender: '男',
        admissionDate: '2023-07-01',
        department: '内科',
        diagnosis: '高血压, 2型糖尿病',
      },
      lab: [
        { name: '白细胞', value: '6.5×10^9/L', ref: '3.5-9.5' },
        { name: '红细胞', value: '135g/L', ref: '130-175' },
        { name: '血小板', value: '180×10^9/L', ref: '125-350' },
        { name: '空腹血糖', value: '8.7mmol/L', ref: '3.9-6.1' },
        { name: '糖化血红蛋白', value: '7.8%', ref: '4.0-6.0' },
        { name: '总胆固醇', value: '5.8mmol/L', ref: '2.9-5.2' },
      ],
      abnormal: [
        '空腹血糖(8.7mmol/L)及糖化血红蛋白(7.8%)均高于正常范围，提示血糖控制不佳。',
        '总胆固醇(5.8mmol/L)轻度升高，存在血脂异常风险。',
      ],
    };
    reportGenerated.value = true;
  }

  function onBack() {
    reportGenerated.value = false;
  }
</script>
