<template>
  <a-card
    class="general-card"
    :title="$t('workplace.announcement')"
    :header-style="{ paddingBottom: '0' }"
    :body-style="{ padding: '15px 20px 13px 20px' }"
  >
    <template #extra>
      <a-link>{{ $t('workplace.viewMore') }}</a-link>
    </template>
    <div>
      <div v-for="(item, idx) in list" :key="idx" class="item">
        <a-tag :color="item.type" size="small">{{ item.label }}</a-tag>
        <span class="item-content">
          {{ item.content }}
        </span>
      </div>
    </div>
  </a-card>
</template>

<script lang="ts" setup>
  const list = [
    {
      type: 'orangered',
      label: '活动',
      content: '医院健康体检活动开始报名',
    },
    {
      type: 'cyan',
      label: '消息',
      content: '新增患者信息尚未审核，详情请点击查看。',
    },
    {
      type: 'blue',
      label: '通知',
      content: '医疗系统维护通知，今晚22:00-24:00进行系统升级。',
    },
    {
      type: 'blue',
      label: '通知',
      content: '1月医疗设备更新计划通知',
    },
    {
      type: 'cyan',
      label: '消息',
      content: '新增诊疗记录已经通过审核，详情请点击查看。',
    },
  ];
</script>

<style scoped lang="less">
  .item {
    display: flex;
    align-items: center;
    width: 100%;
    height: 24px;
    margin-bottom: 4px;
    .item-content {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-left: 4px;
      color: var(--color-text-2);
      text-decoration: none;
      font-size: 13px;
      cursor: pointer;
    }
  }
</style>
