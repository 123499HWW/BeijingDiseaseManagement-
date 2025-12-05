<template>
  <div class="knowledge-edit">
    <a-card title="编辑知识库" :bordered="false">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
        @submit="handleSubmit"
      >
        <a-form-item field="title" label="标题" required>
          <a-input
            v-model="formData.title"
            placeholder="请输入知识库标题"
            allow-clear
          />
        </a-form-item>
        <a-form-item field="category" label="分类" required>
          <a-select
            v-model="formData.category"
            placeholder="请选择分类"
            allow-clear
          >
            <a-option value="技术">技术</a-option>
            <a-option value="产品">产品</a-option>
            <a-option value="运营">运营</a-option>
            <a-option value="其他">其他</a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="content" label="内容" required>
          <a-textarea
            v-model="formData.content"
            placeholder="请输入知识库内容"
            :auto-size="{ minRows: 4, maxRows: 8 }"
            allow-clear
          />
        </a-form-item>
        <a-form-item field="tags" label="标签">
          <a-input-tag
            v-model="formData.tags"
            placeholder="请输入标签，按回车确认"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" :loading="loading">
              保存
            </a-button>
            <a-button @click="handleCancel">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { Message } from '@arco-design/web-vue';

  const router = useRouter();
  const route = useRoute();
  const formRef = ref();
  const loading = ref(false);

  const formData = reactive({
    title: '',
    category: '',
    content: '',
    tags: [],
  });

  const rules = {
    title: [
      { required: true, message: '请输入标题' },
      { minLength: 2, maxLength: 50, message: '标题长度在 2-50 个字符' },
    ],
    category: [{ required: true, message: '请选择分类' }],
    content: [
      { required: true, message: '请输入内容' },
      { minLength: 10, message: '内容至少 10 个字符' },
    ],
  };

  // 模拟获取数据
  const fetchData = async (id: string) => {
    // 这里应该调用 API 获取数据
    // 现在用模拟数据
    const mockData = {
      title: `知识库标题 ${id}`,
      category: '技术',
      content: '这是知识库的内容，包含详细的技术文档和说明...',
      tags: ['Vue', 'TypeScript', '前端'],
    };

    Object.assign(formData, mockData);
  };

  onMounted(() => {
    const id = route.params.id as string;
    if (id) {
      fetchData(id);
    }
  });

  const handleSubmit = async () => {
    try {
      await formRef.value.validate();
      loading.value = true;

      // 这里可以调用 API 保存数据
      console.log('提交数据:', formData);

      Message.success('保存成功');
      router.push('/knowledge/list');
    } catch (error) {
      console.error('表单验证失败:', error);
    } finally {
      loading.value = false;
    }
  };

  const handleCancel = () => {
    router.back();
  };
</script>

<style scoped>
  .knowledge-edit {
    padding: 20px;
  }
</style>
