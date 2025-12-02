<template>
  <a-card title="步骤3：分析结果" :bordered="false">
    <a-descriptions :column="2" bordered>
      <a-descriptions-item label="统计方法">{{
        analysisResult?.method || '-'
      }}</a-descriptions-item>
      <a-descriptions-item label="显著性水平">{{
        analysisResult?.significanceLevel || '-'
      }}</a-descriptions-item>
      <a-descriptions-item label="统计量">{{
        analysisResult?.statistic || '-'
      }}</a-descriptions-item>
      <a-descriptions-item label="P值">{{
        analysisResult?.pValue || '-'
      }}</a-descriptions-item>
      <a-descriptions-item label="自由度">{{
        analysisResult?.df || '-'
      }}</a-descriptions-item>
      <a-descriptions-item label="效应量">{{
        analysisResult?.effectSize || '-'
      }}</a-descriptions-item>
    </a-descriptions>

    <a-divider />

    <a-alert type="info" title="统计结论" :closable="false">
      <p
        ><strong>显著性判断：</strong
        >{{ analysisResult?.significance || '-' }}</p
      >
      <p
        ><strong>效应大小：</strong
        >{{ analysisResult?.effectInterpretation || '-' }}</p
      >
      <p
        ><strong>实际意义：</strong
        >{{ analysisResult?.practicalSignificance || '-' }}</p
      >
      <p><strong>结论：</strong>{{ analysisResult?.conclusion || '-' }}</p>
    </a-alert>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-form-item>
          <a-space>
            <a-button @click="$emit('prevStep')">上一步</a-button>
            <a-button type="primary" @click="finishAnalysis">完成分析</a-button>
            <a-button @click="$emit('resetAll')">重新开始</a-button>
          </a-space>
        </a-form-item>
      </a-col>
    </a-row>
  </a-card>
</template>

<script lang="ts" setup>
  import { Message } from '@arco-design/web-vue';

  // Props
  interface Props {
    analysisResult: any;
  }

  // Emits
  interface Emits {
    (e: 'prevStep'): void;
    (e: 'resetAll'): void;
  }

  const props = defineProps<Props>();
  const emit = defineEmits<Emits>();

  const finishAnalysis = () => {
    Message.success('分析完成！');
  };
</script>
