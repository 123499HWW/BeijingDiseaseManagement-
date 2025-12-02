<template>
  <div class="knowledge-detail">
    <a-card title="知识库详情" :bordered="false">
      <a-descriptions
        :data="descriptionsData"
        layout="vertical"
        bordered
        :column="1"
      />

      <div class="content-section">
        <h3>内容</h3>
        <div class="content-text">{{ knowledgeData.content }}</div>
      </div>

      <div class="action-section">
        <a-space>
          <a-button type="primary" @click="handleEdit"> 编辑 </a-button>
          <a-button status="danger" @click="handleDelete"> 删除 </a-button>
          <a-button @click="handleBack"> 返回 </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { Message, Modal } from '@arco-design/web-vue';

  const router = useRouter();
  const route = useRoute();

  const knowledgeData = reactive({
    id: '',
    title: '',
    category: '',
    content: '',
    tags: [],
    createTime: '',
    updateTime: '',
  });

  // 模拟获取数据
  const fetchData = async (id: string) => {
    // 这里应该调用 API 获取数据
    // 现在用模拟数据
    const mockData = {
      id,
      title: `知识库标题 ${id}`,
      category: '技术',
      content:
        '这是知识库的详细内容，包含完整的技术文档、使用说明、最佳实践等信息。这里可以包含很长的内容，用于展示知识库的完整信息。',
      tags: ['Vue', 'TypeScript', '前端', '开发'],
      createTime: '2024-01-15 10:30:00',
      updateTime: '2024-01-20 14:20:00',
    };

    Object.assign(knowledgeData, mockData);
  };

  const descriptionsData = computed(() => [
    {
      label: '标题',
      value: knowledgeData.title,
    },
    {
      label: '分类',
      value: knowledgeData.category,
    },
    {
      label: '标签',
      value: knowledgeData.tags.join(', '),
    },
    {
      label: '创建时间',
      value: knowledgeData.createTime,
    },
    {
      label: '更新时间',
      value: knowledgeData.updateTime,
    },
  ]);

  onMounted(() => {
    const id = route.params.id as string;
    if (id) {
      fetchData(id);
    }
  });

  const handleEdit = () => {
    router.push(`/knowledge/edit/${knowledgeData.id}`);
  };

  const handleDelete = () => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个知识库吗？删除后无法恢复。',
      onOk: () => {
        // 这里调用删除 API
        Message.success('删除成功');
        router.push('/knowledge/list');
      },
    });
  };

  const handleBack = () => {
    router.back();
  };
</script>

<style scoped>
  .knowledge-detail {
    padding: 20px;
  }

  .content-section {
    margin-top: 24px;
  }

  .content-section h3 {
    margin-bottom: 16px;
    color: #1d2129;
  }

  .content-text {
    padding: 16px;
    color: #4e5969;
    line-height: 1.6;
    background-color: #f7f8fa;
    border-radius: 6px;
  }

  .action-section {
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid #e5e6eb;
  }
</style>
