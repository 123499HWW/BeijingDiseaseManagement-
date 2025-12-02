<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patient.inspectionTable']" />
    <a-card class="general-card" :title="$t('menu.patient.inspectionTable')">
      <!-- 搜索表单 -->
      <a-row>
        <a-col :flex="1">
          <a-form
            :model="formModel"
            :label-col-props="{ span: 6 }"
            :wrapper-col-props="{ span: 18 }"
            label-align="left"
          >
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item field="hospitalNo" label="患者编号">
                  <a-input
                    v-model="formModel.hospitalNo"
                    placeholder="请输入患者编号"
                  />
                </a-form-item>
              </a-col>
              <!-- <a-col :span="8">
                <a-form-item field="name" label="姓名">
                  <a-input v-model="formModel.name" placeholder="请输入姓名" />
                </a-form-item>
              </a-col> -->
              <a-col :span="8">
                <a-form-item field="inspectionType" label="专利类型">
                  <a-input
                    v-model="formModel.inspectionType"
                    placeholder="请输入专利类型"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="inspectionDate" label="申请日期">
                  <a-range-picker
                    v-model="formModel.inspectionDate"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="inspectionItem" label="专利名称">
                  <a-input
                    v-model="formModel.inspectionItem"
                    placeholder="请输入专利名称"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="result" label="授权状态">
                  <a-input
                    v-model="formModel.result"
                    placeholder="请输入授权状态"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-col>
        <a-divider style="height: 84px" direction="vertical" />
        <a-col :flex="'86px'" style="text-align: right">
          <a-space direction="vertical" :size="18">
            <a-button type="primary" @click="search">
              <template #icon>
                <icon-search />
              </template>
              查询
            </a-button>
            <a-button @click="reset">
              <template #icon>
                <icon-refresh />
              </template>
              重置
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-divider style="margin-top: 0" />

      <!-- 工具栏 -->
      <a-row style="margin-bottom: 16px">
        <a-col :span="12">
          <a-space>
            <a-button type="primary">
              <template #icon>
                <icon-plus />
              </template>
              新增
            </a-button>
          </a-space>
        </a-col>
        <a-col
          :span="12"
          style="display: flex; align-items: center; justify-content: end"
        >
          <a-space>
            <a-button type="primary" @click="importPatient">
              <template #icon><icon-upload /></template>
              导入专利
            </a-button>
            <a-button type="outline" @click="showDetailImport = true">
              <template #icon><icon-upload /></template>
              导入专利详情
            </a-button>
            <a-button type="dashed" @click="showRecord = true">
              <template #icon><icon-file /></template>
              导入记录
            </a-button>
            <a-button type="outline">
              <template #icon><icon-download /></template>
              导出
            </a-button>
          </a-space>
          <input
            id="import-patient-input"
            type="file"
            style="display: none"
            multiple
            @change="handlePatientFile"
          />
          <input
            id="import-detail-input"
            type="file"
            style="display: none"
            multiple
            @change="handleDetailFile"
          />
          <a-tooltip content="刷新">
            <div class="action-icon" @click="search">
              <icon-refresh size="18" />
            </div>
          </a-tooltip>
          <a-dropdown @select="handleSelectDensity">
            <a-tooltip content="密度">
              <div class="action-icon"><icon-line-height size="18" /></div>
            </a-tooltip>
            <template #content>
              <a-doption
                v-for="item in densityList"
                :key="item.value"
                :value="item.value"
                :class="{ active: item.value === size }"
              >
                <span>{{ item.name }}</span>
              </a-doption>
            </template>
          </a-dropdown>
          <a-tooltip content="列设置">
            <a-popover
              trigger="click"
              position="bl"
              @popup-visible-change="popupVisibleChange"
            >
              <div class="action-icon"><icon-settings size="18" /></div>
              <template #content>
                <div id="tableSetting">
                  <div
                    v-for="(item, index) in showColumns"
                    :key="item.dataIndex"
                    class="setting"
                  >
                    <div style="margin-right: 4px; cursor: move">
                      <icon-drag-arrow />
                    </div>
                    <div>
                      <a-checkbox
                        v-model="item.checked"
                        @change="
                          handleChange($event, item as TableColumnData, index)
                        "
                      ></a-checkbox>
                    </div>
                    <div class="title">
                      {{ item.title === '#' ? '序列号' : item.title }}
                    </div>
                  </div>
                </div>
              </template>
            </a-popover>
          </a-tooltip>
        </a-col>
      </a-row>

      <!-- 表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :pagination="pagination"
        :columns="(cloneColumns as TableColumnData[])"
        :data="renderData"
        :bordered="false"
        :size="size"
        class="ellipsis-table"
        @page-change="onPageChange"
      >
        <template #operations="{ record }">
          <a-space>
            <a-button type="text" size="small" @click="editRecord(record)">
              编辑
            </a-button>
            <a-popconfirm
              content="确定要删除这条记录吗？"
              @ok="deleteRecord(record)"
            >
              <a-button type="text" size="small" status="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-card>
    <!-- 导入记录弹窗 -->
    <a-modal
      v-model:visible="showRecord"
      title="导入记录"
      width="900px"
      :footer="false"
      class="import-record-modal"
      @cancel="showRecord = false"
    >
      <div
        style="
          text-align: center;
          margin-bottom: 16px;
          font-size: 16px;
          font-weight: bold;
        "
        >批量导入记录示例</div
      >
      <!-- 静态示例表格 -->
      <table
        style="
          width: 100%;
          margin-bottom: 16px;
          border-collapse: collapse;
          font-size: 14px;
          background: #f6f8fa;
        "
      >
        <thead>
          <tr style="background: #f0f2f5">
            <th style="border: 1px solid #e5e6eb; padding: 6px">数据名称</th>
            <th style="border: 1px solid #e5e6eb; padding: 6px">数据分类</th>
            <th style="border: 1px solid #e5e6eb; padding: 6px">流水号</th>
            <th style="border: 1px solid #e5e6eb; padding: 6px">导入日期</th>
            <th style="border: 1px solid #e5e6eb; padding: 6px">更新日期</th>
            <th style="border: 1px solid #e5e6eb; padding: 6px">更新人</th>
            <th style="border: 1px solid #e5e6eb; padding: 6px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style="border: 1px solid #e5e6eb; padding: 6px"
              >patent_0601.xlsx</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px">专利信息</td>
            <td style="border: 1px solid #e5e6eb; padding: 6px"
              >LSH20240601001</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px"
              >2024-06-01 10:00</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px"
              >2024-06-01 12:00</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px">张三</td>
            <td style="border: 1px solid #e5e6eb; padding: 6px">下载 删除</td>
          </tr>
        </tbody>
      </table>
      <!-- 静态示例表格结束 -->
      <a-table
        :data="importRecords"
        :pagination="false"
        size="small"
        :bordered="true"
      >
        <a-table-column title="数据名称" data-index="name" />
        <a-table-column title="数据分类" data-index="category" />
        <a-table-column title="流水号" data-index="serial" />
        <a-table-column title="导入日期" data-index="importDate" />
        <a-table-column title="更新日期" data-index="updateDate" />
        <a-table-column title="更新人" data-index="updateUser" />
        <a-table-column title="操作">
          <template #default="slotProps">
            <a-space>
              <a-button type="text" @click="download(slotProps?.record)">
                <icon-download /> 下载
              </a-button>
              <a-button
                type="text"
                status="danger"
                @click="remove(slotProps?.record)"
              >
                <icon-delete /> 删除
              </a-button>
            </a-space>
          </template>
        </a-table-column>
      </a-table>
    </a-modal>
    <!-- 导入专利详情弹窗 -->
    <a-modal
      v-model:visible="showDetailImport"
      title="批量导入专利详情"
      :footer="false"
      @cancel="showDetailImport = false"
    >
      <a-input
        v-model="searchNo"
        placeholder="搜索患者流水号"
        allow-clear
        style="margin-bottom: 12px"
      />
      <a-select
        v-model="importType"
        :options="importTypeOptions"
        placeholder="选择导入类型"
        style="width: 100%; margin-bottom: 12px"
      />
      <a-button type="primary" style="width: 100%" @click="handleDetailImport">
        <template #icon><icon-upload /></template>
        上传文件
      </a-button>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, reactive, watch, nextTick, onUnmounted } from 'vue';
  import useLoading from '@/hooks/loading';
  import { Pagination } from '@/types/global';
  import type { TableColumnData } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';
  import { Message } from '@arco-design/web-vue';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  // 定义专利数据类型
  interface InspectionRecord {
    id: number;
    hospitalNo: string;
    name: string;
    inspectionType: string;
    inspectionDate: string;
    inspectionItem: string;
    result: string;
    unit: string;
    referenceRange: string;
    status: string;
    department: string;
    doctor: string;
    reportDate: string;
  }

  const generateFormModel = () => {
    return {
      hospitalNo: '',
      name: '',
      inspectionType: '',
      inspectionDate: [],
      inspectionItem: '',
      result: '',
    };
  };

  const { loading, setLoading } = useLoading(true);

  const renderData = ref<InspectionRecord[]>([]);
  const formModel = ref(generateFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const size = ref<SizeProps>('medium');

  const basePagination: Pagination = {
    current: 1,
    pageSize: 10,
  };
  const pagination = reactive({
    ...basePagination,
  });

  const densityList = computed(() => [
    { name: '迷你', value: 'mini' },
    { name: '偏小', value: 'small' },
    { name: '中等', value: 'medium' },
    { name: '偏大', value: 'large' },
  ]);

  const columns = computed<TableColumnData[]>(() => [
    {
      title: '患者编号',
      dataIndex: 'hospitalNo',
      width: 120,
      align: 'center',
    },
    // { title: '姓名', dataIndex: 'name', width: 100, align: 'center' },
    {
      title: '专利类型',
      dataIndex: 'inspectionType',
      width: 100,
      align: 'center',
    },
    {
      title: '申请日期',
      dataIndex: 'inspectionDate',
      width: 120,
      align: 'center',
    },
    {
      title: '专利名称',
      dataIndex: 'inspectionItem',
      width: 160,
      align: 'center',
    },
    { title: '授权状态', dataIndex: 'result', width: 100, align: 'center' },
    { title: '专利号', dataIndex: 'unit', width: 140, align: 'center' },
    {
      title: '所属领域',
      dataIndex: 'referenceRange',
      width: 140,
      align: 'center',
    },
    { title: '当前进度', dataIndex: 'status', width: 100, align: 'center' },
    { title: '牵头单位', dataIndex: 'department', width: 140, align: 'center' },
    { title: '负责人', dataIndex: 'doctor', width: 100, align: 'center' },
    { title: '更新时间', dataIndex: 'reportDate', width: 140, align: 'center' },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      width: 100,
      fixed: 'right',
      align: 'center',
    },
  ]);

  // Mock 数据
  const mockData = [
    {
      id: 1,
      hospitalNo: 'SCH2024001',
      name: '李文博',
      inspectionType: '发明专利',
      inspectionDate: '2024-03-12',
      inspectionItem: '多模态数据新闻生成引擎',
      result: '已授权',
      unit: 'CN11580011A',
      referenceRange: '数据采集与可视化',
      status: '已公告',
      department: '清华大学新闻与传播学院',
      doctor: '李文博',
      reportDate: '2024-06-10 10:00',
    },
    {
      id: 2,
      hospitalNo: 'SCH2024003',
      name: '周启航',
      inspectionType: '发明专利',
      inspectionDate: '2024-02-28',
      inspectionItem: '面向媒体的算力调度方法',
      result: '受理中',
      unit: 'CN202410223344.X',
      referenceRange: '算力网络',
      status: '实质审查',
      department: '南方科技大学新闻与传播学院',
      doctor: '周启航',
      reportDate: '2024-06-08 09:30',
    },
    {
      id: 3,
      hospitalNo: 'SCH2024005',
      name: '陈晓宁',
      inspectionType: '实用新型',
      inspectionDate: '2024-01-16',
      inspectionItem: '校园科普内容协同平台',
      result: '已授权',
      unit: 'CN215678901U',
      referenceRange: '教育传播',
      status: '证书下发',
      department: '浙江大学传媒与国际文化学院',
      doctor: '陈晓宁',
      reportDate: '2024-05-30 14:20',
    },
    {
      id: 4,
      hospitalNo: 'SCH2024006',
      name: '刘思涵',
      inspectionType: '软件著作权',
      inspectionDate: '2024-04-02',
      inspectionItem: 'AI 审校红线评估系统 V3.0',
      result: '登记完成',
      unit: '软著登字第150233号',
      referenceRange: '算法治理',
      status: '已入库',
      department: '电子科技大学文化与传媒学院',
      doctor: '刘思涵',
      reportDate: '2024-06-12 11:15',
    },
    {
      id: 5,
      hospitalNo: 'SCH2024007',
      name: '黄柏松',
      inspectionType: '发明专利',
      inspectionDate: '2024-03-05',
      inspectionItem: '多语种科普自动校摘方法',
      result: '已授权',
      unit: 'CN11590022B',
      referenceRange: '国际传播',
      status: '授权公告',
      department: '暨南大学新闻与传播学院',
      doctor: '黄柏松',
      reportDate: '2024-06-05 16:40',
    },
    {
      id: 6,
      hospitalNo: 'SCH2024008',
      name: '王雪萌',
      inspectionType: '实用新型',
      inspectionDate: '2024-02-10',
      inspectionItem: '城市媒体算力调度终端',
      result: '受理中',
      unit: 'CN202420556677.2',
      referenceRange: '媒体工程',
      status: '初审中',
      department: '长江日报技术中心',
      doctor: '王雪萌',
      reportDate: '2024-06-01 08:50',
    },
    {
      id: 7,
      hospitalNo: 'SCH2024009',
      name: '宋佳怡',
      inspectionType: '软件著作权',
      inspectionDate: '2024-04-20',
      inspectionItem: '科研新闻影响力评价系统',
      result: '登记完成',
      unit: '软著登字第150556号',
      referenceRange: '评价指标',
      status: '已入库',
      department: '西安交通大学新闻与新媒体学院',
      doctor: '宋佳怡',
      reportDate: '2024-06-11 15:05',
    },
    {
      id: 8,
      hospitalNo: 'SCH2024010',
      name: '赵一航',
      inspectionType: '发明专利',
      inspectionDate: '2024-01-08',
      inspectionItem: '科创金融舆情监测方法',
      result: '已授权',
      unit: 'CN11575588A',
      referenceRange: '金融传播',
      status: '证书下发',
      department: '中国科学技术大学新闻传播系',
      doctor: '赵一航',
      reportDate: '2024-05-26 13:40',
    },
    {
      id: 9,
      hospitalNo: 'SCH2024002',
      name: '张语琪',
      inspectionType: '外观设计',
      inspectionDate: '2023-12-28',
      inspectionItem: '卫星遥感可视化交互界面',
      result: '已授权',
      unit: 'CN308800122S',
      referenceRange: '遥感解译',
      status: '授权公告',
      department: '复旦大学信息学院',
      doctor: '张语琪',
      reportDate: '2024-05-18 09:05',
    },
    {
      id: 10,
      hospitalNo: 'SCH2024004',
      name: '郭凌霄',
      inspectionType: '发明专利',
      inspectionDate: '2024-03-30',
      inspectionItem: '新闻知识图谱增量更新方法',
      result: '受理中',
      unit: 'CN202410445566.7',
      referenceRange: '知识工程',
      status: '文件补正',
      department: '南京大学新闻与传播学院',
      doctor: '郭凌霄',
      reportDate: '2024-06-09 17:10',
    },
  ];

  let sortableInstance: Sortable | null = null;

  const fetchData = async (params: any = { current: 1, pageSize: 20 }) => {
    setLoading(true);
    try {
      // 模拟异步请求
      setTimeout(() => {
        const start = (params.current - 1) * params.pageSize;
        const end = start + params.pageSize;
        const list = mockData.slice(start, end);

        renderData.value = list;
        pagination.current = params.current;
        pagination.total = mockData.length;
        setLoading(false);
      }, 300);
    } catch (err) {
      setLoading(false);
    }
  };

  const search = () => {
    fetchData({
      ...basePagination,
      ...formModel.value,
    });
  };

  const onPageChange = (current: number) => {
    fetchData({ ...basePagination, current });
  };

  const reset = () => {
    formModel.value = generateFormModel();
  };

  const handleSelectDensity = (
    val: string | number | Record<string, any> | undefined
  ) => {
    size.value = val as SizeProps;
  };

  const handleChange = (
    checked: boolean | (string | boolean | number)[],
    column: Column,
    index: number
  ) => {
    if (!checked) {
      cloneColumns.value = showColumns.value.filter(
        (item) => item.dataIndex !== column.dataIndex
      );
    } else {
      cloneColumns.value.splice(index, 0, column);
    }
  };

  const exchangeArray = <T extends Array<any>>(
    array: T,
    beforeIdx: number,
    newIdx: number,
    isDeep = false
  ): T => {
    const newArray = isDeep ? cloneDeep(array) : array;
    if (beforeIdx > -1 && newIdx > -1) {
      newArray.splice(
        beforeIdx,
        1,
        newArray.splice(newIdx, 1, newArray[beforeIdx]).pop()
      );
    }
    return newArray;
  };

  const popupVisibleChange = (val: boolean) => {
    if (val) {
      nextTick(() => {
        const el = document.getElementById('tableSetting') as HTMLElement;
        if (el && !sortableInstance) {
          sortableInstance = new Sortable(el, {
            onEnd(e: any) {
              const { oldIndex, newIndex } = e;
              exchangeArray(cloneColumns.value, oldIndex, newIndex);
              exchangeArray(showColumns.value, oldIndex, newIndex);
            },
          });
        }
      });
    } else if (sortableInstance) {
      sortableInstance.destroy();
      sortableInstance = null;
    }
  };

  const editRecord = (record: InspectionRecord) => {
    Message.info(`编辑专利: ${record.name}`);
  };

  const deleteRecord = (record: InspectionRecord) => {
    Message.success(`删除专利: ${record.name}`);
    // 这里可以调用删除API
  };

  // 组件卸载时清理 Sortable 实例
  onUnmounted(() => {
    if (sortableInstance) {
      sortableInstance.destroy();
      sortableInstance = null;
    }
  });

  watch(
    () => columns.value,
    (val) => {
      cloneColumns.value = cloneDeep(val);
      cloneColumns.value.forEach((item) => {
        item.checked = true;
      });
      showColumns.value = cloneDeep(cloneColumns.value);
    },
    { deep: true, immediate: true }
  );

  fetchData();

  const showDetailImport = ref(false);
  const showRecord = ref(false);
  const searchNo = ref('');
  const importType = ref('');
  const importTypeOptions = [
    { label: '发明专利', value: 'invention' },
    { label: '实用新型', value: 'utility' },
    { label: '外观设计', value: 'design' },
    { label: '软件著作权', value: 'software' },
  ];

  function importPatient() {
    document.getElementById('import-patient-input')?.click();
  }
  function handlePatientFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入专利文件', files);
    }
  }
  function handleDetailImport() {
    document.getElementById('import-detail-input')?.click();
    showDetailImport.value = false;
  }
  function handleDetailFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入专利详情文件', files, searchNo.value, importType.value);
    }
  }

  const importRecords = ref([
    {
      name: 'patent_0601.xlsx',
      category: '专利信息',
      serial: 'LSH20240601001',
      importDate: '2024-06-01 10:00',
      updateDate: '2024-06-01 12:00',
      updateUser: '张三',
    },
    {
      name: 'patent_0602.xlsx',
      category: '专利信息',
      serial: 'LSH20240602001',
      importDate: '2024-06-02 14:30',
      updateDate: '2024-06-02 15:00',
      updateUser: '李四',
    },
    {
      name: 'patent_0603.xlsx',
      category: '专利信息',
      serial: 'LSH20240603001',
      importDate: '2024-06-03 09:15',
      updateDate: '2024-06-03 10:00',
      updateUser: '王五',
    },
  ]);
  function download({ name }: { name: string }) {
    // 这里可以实现下载逻辑
    // 示例：console.log('下载', name);
  }
  function remove({ name }: { name: string }) {
    importRecords.value = importRecords.value.filter((r) => r.name !== name);
  }
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
  }

  .general-card {
    min-height: 300px;
  }

  .ellipsis-table {
    :deep(.arco-table-td) {
      .arco-table-cell {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .ellipsis-cell {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 180px;
    display: inline-block;
  }

  .action-icon {
    margin-left: 12px;
    cursor: pointer;
  }

  .active {
    color: #0960bd;
    background-color: #e3f4fc;
  }

  .setting {
    display: flex;
    align-items: center;
    width: 200px;

    .title {
      margin-left: 12px;
      cursor: pointer;
    }
  }
  .import-record-modal {
    :deep(.arco-modal-content) {
      border-radius: 10px;
      padding: 24px 32px 32px 32px;
      background: #f8fafb;
    }
    :deep(.arco-table) {
      border-radius: 8px;
      background: #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    }
    :deep(.arco-table-th) {
      background: #f0f2f5;
      font-weight: bold;
    }
    :deep(.arco-table-td) {
      font-size: 15px;
    }
  }
</style>
