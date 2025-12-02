<template>
  <a-card class="general-card" :body-style="{ padding: '24px 24px' }">
    <!-- 搜索区域 -->
    <div
      style="margin-bottom: 16px; display: flex; align-items: center; gap: 12px"
    >
      <span style="font-weight: 500; color: #1d2129">住院流水号搜索：</span>
      <a-input
        v-model="searchValue"
        placeholder="请输入住院流水号"
        style="width: 200px"
        allow-clear
        @press-enter="handleSearch"
      />
      <a-button type="primary" @click="handleSearch">搜索</a-button>
      <a-button @click="handleReset">重置</a-button>
    </div>

    <a-descriptions-item label="住院流水号">
      <span style="display: inline-block; width: 130px">ZY110001456658</span>
    </a-descriptions-item>
    <a-descriptions-item label="姓名">
      <span style="display: inline-block; width: 100px"> 李*华</span>
    </a-descriptions-item>
    <a-descriptions-item label="诊断">
      <span style="display: inline-block; width: 180px"> 乙状结肠恶性肿瘤</span>
    </a-descriptions-item>
    <!-- 图表横向贯穿 -->
    <div style="margin-top: 24px; width: 100%">
      <PatientInfoChartNew />
    </div>
  </a-card>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { Message } from '@arco-design/web-vue';
  import PatientInfoChartNew from './PatientInfoChartNew.vue';

  const router = useRouter();
  const searchValue = ref('');

  function handleSearch() {
    if (!searchValue.value.trim()) {
      Message.warning('请输入住院流水号');
      return;
    }
    // 这里可以添加实际的搜索逻辑
    Message.success(`正在搜索住院流水号: ${searchValue.value}`);
    console.log('搜索住院流水号:', searchValue.value);
  }

  function handleReset() {
    searchValue.value = '';
    Message.info('已重置搜索条件');
  }

  function goToProfileNew() {
    router.push({ path: '/patient/profileNew' });
  }
</script>

<style scoped lang="less">
  .general-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    margin-bottom: 0;
  }
</style>
