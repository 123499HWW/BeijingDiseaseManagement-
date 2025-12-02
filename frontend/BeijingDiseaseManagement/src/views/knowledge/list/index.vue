<template>
  <div class="container">
    <Breadcrumb :items="['menu.knowledge', 'menu.knowledge.list']" />
    <a-row :gutter="20" align="stretch">
      <a-col :span="24">
        <a-card class="general-card" title="知识库列表">
          <a-row justify="space-between">
            <a-col :span="24">
              <!-- 搜索区域 -->
              <div class="search-section">
                <a-form layout="inline" :model="searchForm">
                  <a-form-item field="keyword" label="关键词">
                    <a-input
                      v-model="searchForm.keyword"
                      placeholder="请输入标题或内容关键词"
                      allow-clear
                      style="width: 200px"
                    />
                  </a-form-item>
                  <a-form-item field="category" label="分类">
                    <a-select
                      v-model="searchForm.category"
                      placeholder="请选择分类"
                      allow-clear
                      style="width: 150px"
                    >
                      <a-option value="技术">技术</a-option>
                      <a-option value="产品">产品</a-option>
                      <a-option value="运营">运营</a-option>
                      <a-option value="其他">其他</a-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item field="status" label="状态">
                    <a-select
                      v-model="searchForm.status"
                      placeholder="请选择状态"
                      allow-clear
                      style="width: 120px"
                    >
                      <a-option value="published">已发布</a-option>
                      <a-option value="draft">草稿</a-option>
                      <a-option value="archived">已归档</a-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item>
                    <a-space>
                      <a-button type="primary" @click="handleSearch">
                        <template #icon>
                          <icon-search />
                        </template>
                        搜索
                      </a-button>
                      <a-button @click="handleReset">
                        <template #icon>
                          <icon-refresh />
                        </template>
                        重置
                      </a-button>
                      <a-button @click="toggleAdvancedSearch">
                        {{ showAdvancedSearch ? '收起' : '高级搜索' }}
                        <template #icon>
                          <icon-down v-if="!showAdvancedSearch" />
                          <icon-up v-else />
                        </template>
                      </a-button>
                    </a-space>
                  </a-form-item>
                </a-form>

                <!-- 高级搜索 -->
                <div v-if="showAdvancedSearch" class="advanced-search">
                  <a-divider />
                  <a-form layout="inline" :model="advancedSearchForm">
                    <a-form-item field="tags" label="标签">
                      <a-input-tag
                        v-model="advancedSearchForm.tags"
                        placeholder="请输入标签"
                        allow-clear
                        style="width: 200px"
                      />
                    </a-form-item>
                    <a-form-item field="author" label="作者">
                      <a-input
                        v-model="advancedSearchForm.author"
                        placeholder="请输入作者"
                        allow-clear
                        style="width: 150px"
                      />
                    </a-form-item>
                    <a-form-item field="sortBy" label="排序">
                      <a-select
                        v-model="advancedSearchForm.sortBy"
                        placeholder="请选择排序"
                        style="width: 120px"
                      >
                        <a-option value="createTime">创建时间</a-option>
                        <a-option value="updateTime">更新时间</a-option>
                        <a-option value="title">标题</a-option>
                        <a-option value="viewCount">浏览量</a-option>
                      </a-select>
                    </a-form-item>
                    <a-form-item field="sortOrder" label="排序方式">
                      <a-select
                        v-model="advancedSearchForm.sortOrder"
                        placeholder="排序方式"
                        style="width: 100px"
                      >
                        <a-option value="desc">降序</a-option>
                        <a-option value="asc">升序</a-option>
                      </a-select>
                    </a-form-item>
                  </a-form>
                </div>
              </div>

              <!-- 工具栏 -->
              <div class="toolbar">
                <a-space>
                  <a-button type="primary" @click="handleAdd">
                    <template #icon>
                      <icon-plus />
                    </template>
                    新增
                  </a-button>
                  <a-button @click="handleExport">
                    <template #icon>
                      <icon-download />
                    </template>
                    导出
                  </a-button>
                </a-space>
              </div>

              <!-- 卡片列表 -->
              <div class="list-wrap">
                <a-typography-title class="block-title" :heading="6">
                  知识库列表
                </a-typography-title>
                <a-row class="list-row" :gutter="24">
                  <a-col
                    :xs="12"
                    :sm="12"
                    :md="12"
                    :lg="6"
                    :xl="6"
                    :xxl="6"
                    class="list-col"
                  >
                    <div class="card-wrap empty-wrap">
                      <a-card :bordered="false" hoverable @click="handleAdd">
                        <a-result :status="null" title="新增知识库">
                          <template #icon>
                            <icon-plus style="font-size: 20px" />
                          </template>
                        </a-result>
                      </a-card>
                    </div>
                  </a-col>
                  <a-col
                    v-for="item in tableData"
                    :key="item.id"
                    class="list-col"
                    :xs="12"
                    :sm="12"
                    :md="12"
                    :lg="6"
                    :xl="6"
                    :xxl="6"
                  >
                    <CardWrap
                      :loading="loading"
                      :title="item.title"
                      :description="getDescription(item)"
                      :default-value="false"
                      action-type="button"
                      :icon="getIcon(item.category)"
                      :open-txt="'查看'"
                      :close-txt="'编辑'"
                      :show-tag="true"
                      :tag-text="getStatusText(item.status)"
                      @click="handleView(item)"
                    >
                      <a-descriptions
                        style="margin-top: 16px"
                        :data="getCardData(item)"
                        layout="inline-horizontal"
                        :column="2"
                      />
                      <template #skeleton>
                        <a-skeleton :animation="true">
                          <a-skeleton-line
                            :widths="['50%', '50%', '100%', '40%']"
                            :rows="4"
                          />
                          <a-skeleton-line :widths="['40%']" :rows="1" />
                        </a-skeleton>
                      </template>
                    </CardWrap>
                  </a-col>
                </a-row>
              </div>

              <!-- 分页 -->
              <div class="pagination-wrapper">
                <a-pagination
                  v-model:current="pagination.current"
                  v-model:page-size="pagination.pageSize"
                  :total="pagination.total"
                  :show-total="pagination.showTotal"
                  :show-jumper="pagination.showJumper"
                  :show-page-size="pagination.showPageSize"
                  @change="onPageChange"
                  @page-size-change="onPageSizeChange"
                />
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { Message, Modal } from '@arco-design/web-vue';
  import {
    IconPlus,
    IconSearch,
    IconRefresh,
    IconDown,
    IconUp,
    IconDelete,
    IconDownload,
    IconCopy,
    IconArchive,
    IconBook,
    IconCode,
    IconPalette,
    IconBarChart,
  } from '@arco-design/web-vue/es/icon';
  import CardWrap from '@/views/list/card/components/card-wrap.vue';
  import Breadcrumb from '@/components/breadcrumb/index.vue';

  const router = useRouter();
  const loading = ref(false);
  const showAdvancedSearch = ref(false);

  const searchForm = reactive({
    keyword: '',
    category: '',
    status: '',
    dateRange: [],
  });

  const advancedSearchForm = reactive({
    tags: [],
    author: '',
    sortBy: 'createTime',
    sortOrder: 'desc',
  });

  const tableData = ref<any[]>([]);
  const pagination = reactive({
    total: 0,
    current: 1,
    pageSize: 12,
    showTotal: true,
    showJumper: true,
    showPageSize: true,
  });

  // 模拟数据
  const mockData = [
    {
      id: '1',
      title: 'Vue3 开发指南',
      category: '技术',
      status: 'published',
      tags: ['Vue', '前端', '开发'],
      author: '张三',
      viewCount: 1250,
      createTime: '2024-01-15 10:30:00',
    },
    {
      id: '2',
      title: '产品设计规范',
      category: '产品',
      status: 'published',
      tags: ['设计', '规范', 'UI'],
      author: '李四',
      viewCount: 890,
      createTime: '2024-01-14 14:20:00',
    },
    {
      id: '3',
      title: '运营数据分析',
      category: '运营',
      status: 'draft',
      tags: ['数据', '分析', '运营'],
      author: '王五',
      viewCount: 0,
      createTime: '2024-01-13 09:15:00',
    },
    {
      id: '4',
      title: 'TypeScript 最佳实践',
      category: '技术',
      status: 'published',
      tags: ['TypeScript', '开发', '最佳实践'],
      author: '赵六',
      viewCount: 2100,
      createTime: '2024-01-12 16:45:00',
    },
    {
      id: '5',
      title: '用户增长策略',
      category: '运营',
      status: 'archived',
      tags: ['增长', '策略', '用户'],
      author: '钱七',
      viewCount: 567,
      createTime: '2024-01-11 11:20:00',
    },
  ];

  const getCategoryColor = (category: string) => {
    const colors: Record<string, string> = {
      技术: 'blue',
      产品: 'green',
      运营: 'orange',
      其他: 'gray',
    };
    return colors[category] || 'gray';
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      published: 'green',
      draft: 'orange',
      archived: 'gray',
    };
    return colors[status] || 'gray';
  };

  const getStatusText = (status: string) => {
    const texts: Record<string, string> = {
      published: '已发布',
      draft: '草稿',
      archived: '已归档',
    };
    return texts[status] || status;
  };

  const getIcon = (category: string) => {
    const icons: Record<string, any> = {
      技术: IconCode,
      产品: IconPalette,
      运营: IconBarChart,
      其他: IconBook,
    };
    return icons[category] || IconBook;
  };

  const getDescription = (item: any) => {
    return `分类：${item.category} | 作者：${item.author} | 浏览量：${item.viewCount}`;
  };

  const getCardData = (item: any) => {
    return [
      { label: '分类', value: item.category },
      { label: '作者', value: item.author },
      { label: '浏览量', value: item.viewCount },
      { label: '创建时间', value: item.createTime },
    ];
  };

  const fetchData = async () => {
    loading.value = true;
    try {
      // 这里应该调用 API 获取数据
      // 模拟 API 调用延迟
      await new Promise((resolve) => {
        setTimeout(resolve, 500);
      });

      tableData.value = mockData;
      pagination.total = mockData.length;
    } catch (error) {
      Message.error('获取数据失败');
    } finally {
      loading.value = false;
    }
  };

  const handleSearch = () => {
    pagination.current = 1;
    fetchData();
  };

  const handleReset = () => {
    searchForm.keyword = '';
    searchForm.category = '';
    searchForm.status = '';
    searchForm.dateRange = [];
    advancedSearchForm.tags = [];
    advancedSearchForm.author = '';
    advancedSearchForm.sortBy = 'createTime';
    advancedSearchForm.sortOrder = 'desc';
    pagination.current = 1;
    fetchData();
  };

  const toggleAdvancedSearch = () => {
    showAdvancedSearch.value = !showAdvancedSearch.value;
  };

  const handleAdd = () => {
    router.push('/knowledge/add');
  };

  const handleView = (record: any) => {
    router.push(`/knowledge/detail/${record.id}`);
  };

  const handleEdit = (record: any) => {
    router.push(`/knowledge/edit/${record.id}`);
  };

  const handleDelete = async (_record: any) => {
    try {
      // 这里应该调用删除 API
      Message.success('删除成功');
      fetchData();
    } catch (error) {
      Message.error('删除失败');
    }
  };

  const handleExport = () => {
    // 这里应该调用导出 API
    Message.success('导出成功');
  };

  const handleCopy = (_record: any) => {
    // 这里应该调用复制 API
    Message.success('复制成功');
  };

  const handleArchive = (_record: any) => {
    Modal.confirm({
      title: '确认归档',
      content: '确定要归档这条记录吗？',
      onOk: async () => {
        try {
          // 这里应该调用归档 API
          Message.success('归档成功');
          fetchData();
        } catch (error) {
          Message.error('归档失败');
        }
      },
    });
  };

  const onPageChange = (current: number) => {
    pagination.current = current;
    fetchData();
  };

  const onPageSizeChange = (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.current = 1;
    fetchData();
  };

  onMounted(() => {
    fetchData();
  });
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
    :deep(.arco-list-content) {
      overflow-x: hidden;
    }

    :deep(.arco-card-meta-title) {
      font-size: 14px;
    }
  }

  .search-section {
    margin-bottom: 16px;
    padding: 16px;
    background-color: #f7f8fa;
    border-radius: 6px;
  }

  .advanced-search {
    margin-top: 16px;
  }

  .toolbar {
    margin-bottom: 16px;
  }

  .list-wrap {
    .list-row {
      align-items: stretch;
      .list-col {
        margin-bottom: 16px;
      }
    }
    :deep(.arco-space) {
      width: 100%;
      .arco-space-item {
        &:last-child {
          flex: 1;
        }
      }
    }
  }

  .card-wrap {
    height: 100%;
    transition: all 0.3s;
    border: 1px solid var(--color-neutral-3);
    &:hover {
      transform: translateY(-4px);
    }
    :deep(.arco-card-meta-description) {
      color: rgb(var(--gray-6));
      .arco-descriptions-item-label-inline {
        font-weight: normal;
        font-size: 12px;
        color: rgb(var(--gray-6));
      }
      .arco-descriptions-item-value-inline {
        color: rgb(var(--gray-8));
      }
    }
  }

  .empty-wrap {
    height: 200px;
    border-radius: 4px;
    :deep(.arco-card) {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      .arco-result-title {
        color: rgb(var(--gray-6));
      }
    }
  }

  .block-title {
    margin: 0 0 12px 0;
    font-size: 14px;
  }

  .pagination-wrapper {
    margin-top: 24px;
    text-align: center;
  }
</style>
