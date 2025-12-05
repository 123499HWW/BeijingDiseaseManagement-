<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patient.orderTable']" />
    <a-card class="general-card" :title="$t('menu.patient.orderTable')">
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
                <a-form-item field="orderType" label="案例类型">
                  <a-input
                    v-model="formModel.orderType"
                    placeholder="请输入案例类型"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="orderDate" label="收录日期">
                  <a-range-picker
                    v-model="formModel.orderDate"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="orderNo" label="案例编号">
                  <a-input
                    v-model="formModel.orderNo"
                    placeholder="请输入案例编号"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="status" label="推进状态">
                  <a-input
                    v-model="formModel.status"
                    placeholder="请输入推进状态"
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
              导入案例
            </a-button>
            <a-button type="outline" @click="showDetailImport = true">
              <template #icon><icon-upload /></template>
              导入案例详情
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
        <template #index="{ rowIndex }">
          {{ rowIndex + 1 + (pagination.current - 1) * pagination.pageSize }}
        </template>
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
              >case_0601.xlsx</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px">案例信息</td>
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
    <!-- 导入案例详情弹窗 -->
    <a-modal
      v-model:visible="showDetailImport"
      title="批量导入案例详情"
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

  // 定义案例数据类型
  interface OrderRecord {
    id: number;
    hospitalNo: string;
    name: string;
    orderType: string;
    orderDate: string;
    orderNo: string;
    status: string;
    content: string;
    dosage: string;
    frequency: string;
    duration: string;
    department: string;
    doctor: string;
    nurse: string;
    startDate: string;
    stopDate: string;
  }

  const generateFormModel = () => {
    return {
      hospitalNo: '',
      name: '',
      orderType: '',
      orderDate: [],
      orderNo: '',
      status: '',
    };
  };

  const { loading, setLoading } = useLoading(true);

  const renderData = ref<OrderRecord[]>([]);
  const formModel = ref(generateFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const size = ref<SizeProps>('medium');

  const basePagination: Pagination = {
    current: 1,
    pageSize: 20,
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
    { title: '案例类型', dataIndex: 'orderType', width: 100, align: 'center' },
    { title: '收录日期', dataIndex: 'orderDate', width: 120, align: 'center' },
    { title: '案例编号', dataIndex: 'orderNo', width: 140, align: 'center' },
    { title: '推进状态', dataIndex: 'status', width: 100, align: 'center' },
    { title: '案例概述', dataIndex: 'content', width: 220, align: 'center' },
    { title: '合作级别', dataIndex: 'dosage', width: 100, align: 'center' },
    { title: '参与方', dataIndex: 'frequency', width: 120, align: 'center' },
    { title: '落地成果', dataIndex: 'duration', width: 140, align: 'center' },
    { title: '负责单位', dataIndex: 'department', width: 140, align: 'center' },
    { title: '牵头人', dataIndex: 'doctor', width: 100, align: 'center' },
    { title: '支持人', dataIndex: 'nurse', width: 100, align: 'center' },
    { title: '开始时间', dataIndex: 'startDate', width: 120, align: 'center' },
    { title: '结束时间', dataIndex: 'stopDate', width: 120, align: 'center' },
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
      orderType: '示范案例',
      orderDate: '2024/5/10 09:30',
      orderNo: 'CASE20240510001',
      status: '推进中',
      content: '建设智慧媒体实验室并制定数据治理规范',
      dosage: '国家级',
      frequency: '清华大学×新华社',
      duration: '发布行业标准3项',
      department: '清华大学新闻与传播学院',
      doctor: '李文博',
      nurse: '团队秘书处',
      startDate: '2024/5/01',
      stopDate: '',
    },
    {
      id: 2,
      hospitalNo: 'SCH2024003',
      name: '周启航',
      orderType: '合作案例',
      orderDate: '2024/4/28 11:05',
      orderNo: 'CASE20240428002',
      status: '已落地',
      content: '媒体算力调度中心与南方多城算力共享',
      dosage: '省部级',
      frequency: '南科大×南方日报',
      duration: '上线算力调度平台',
      department: '南方科技大学新闻与传播学院',
      doctor: '周启航',
      nurse: '魏露',
      startDate: '2024/3/15',
      stopDate: '2024/5/30',
    },
    {
      id: 3,
      hospitalNo: 'SCH2024005',
      name: '陈晓宁',
      orderType: '教学实践案例',
      orderDate: '2024/5/06 14:20',
      orderNo: 'CASE20240506003',
      status: '推进中',
      content: '“科普+新闻”混合式课程服务浙江科协',
      dosage: '省级',
      frequency: '浙江大学×浙江省科协',
      duration: '形成10门实践课程',
      department: '浙江大学传媒与国际文化学院',
      doctor: '陈晓宁',
      nurse: '教学运营组',
      startDate: '2024/4/01',
      stopDate: '',
    },
    {
      id: 4,
      hospitalNo: 'SCH2024006',
      name: '刘思涵',
      orderType: '示范案例',
      orderDate: '2024/4/18 10:00',
      orderNo: 'CASE20240418004',
      status: '推进中',
      content: 'AI 审校红线评估模型在新华社试点',
      dosage: '中央媒体',
      frequency: '电子科大×新华智云',
      duration: '形成评估工具与培训包',
      department: '电子科技大学文化与传媒学院',
      doctor: '刘思涵',
      nurse: '算法安全组',
      startDate: '2024/3/20',
      stopDate: '',
    },
    {
      id: 5,
      hospitalNo: 'SCH2024007',
      name: '黄柏松',
      orderType: '国际合作案例',
      orderDate: '2024/5/15 16:40',
      orderNo: 'CASE20240515005',
      status: '归档',
      content: '全球科学议题监测平台支撑多语种传播',
      dosage: '国家级',
      frequency: '暨南大学×中国外文局',
      duration: '多语种栏目上线',
      department: '暨南大学新闻与传播学院',
      doctor: '黄柏松',
      nurse: '国际业务部',
      startDate: '2024/2/10',
      stopDate: '2024/5/12',
    },
    {
      id: 6,
      hospitalNo: 'SCH2024008',
      name: '王雪萌',
      orderType: '产媒融合案例',
      orderDate: '2024/4/05 08:50',
      orderNo: 'CASE20240405006',
      status: '推进中',
      content: '武汉新闻算力中台对接城市算力资源',
      dosage: '市级',
      frequency: '长江日报×武汉算力中心',
      duration: '算力调度6个场景',
      department: '长江日报技术中心',
      doctor: '王雪萌',
      nurse: '城市应用组',
      startDate: '2024/1/15',
      stopDate: '',
    },
    {
      id: 7,
      hospitalNo: 'SCH2024009',
      name: '宋佳怡',
      orderType: '示范案例',
      orderDate: '2024/5/20 15:05',
      orderNo: 'CASE20240520007',
      status: '推进中',
      content: '科研新闻影响力评价体系落地陕西',
      dosage: '省级',
      frequency: '西安交大×陕西省科技厅',
      duration: '上线评价仪表盘',
      department: '西安交通大学新闻与新媒体学院',
      doctor: '宋佳怡',
      nurse: '指标研究组',
      startDate: '2024/4/12',
      stopDate: '',
    },
    {
      id: 8,
      hospitalNo: 'SCH2024010',
      name: '赵一航',
      orderType: '产业化案例',
      orderDate: '2024/5/26 13:40',
      orderNo: 'CASE20240526008',
      status: '推进中',
      content: '科创金融案例库支撑政策宣传',
      dosage: '部委合作',
      frequency: '中科大×科创板公司',
      duration: '沉淀48个案例',
      department: '中国科学技术大学新闻传播系',
      doctor: '赵一航',
      nurse: '案例编辑组',
      startDate: '2024/3/01',
      stopDate: '',
    },
    {
      id: 9,
      hospitalNo: 'SCH2024002',
      name: '张语琪',
      orderType: '国际合作案例',
      orderDate: '2024/4/12 09:20',
      orderNo: 'CASE20240412009',
      status: '已落地',
      content: '遥感数据+新闻联合发布支撑应急科普',
      dosage: '省级',
      frequency: '复旦大学×新华社卫星新闻实验室',
      duration: '形成双语数据播报',
      department: '复旦大学信息学院',
      doctor: '张语琪',
      nurse: '可视化团队',
      startDate: '2024/2/05',
      stopDate: '2024/4/30',
    },
    {
      id: 10,
      hospitalNo: 'SCH2024004',
      name: '郭凌霄',
      orderType: '示范案例',
      orderDate: '2024/5/08 17:10',
      orderNo: 'CASE20240508010',
      status: '推进中',
      content: '新闻知识工程与问答系统联动',
      dosage: '国家重点实验室',
      frequency: '南京大学×中央级媒体',
      duration: '知识库覆盖3.4万实体',
      department: '南京大学新闻与传播学院',
      doctor: '郭凌霄',
      nurse: '知识管理组',
      startDate: '2024/3/18',
      stopDate: '',
    },
  ];
  let sortableInstance: Sortable | null = null;

  const fetchData = async (params: any = { current: 1, pageSize: 10 }) => {
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

  const editRecord = (record: OrderRecord) => {
    Message.info(`编辑案例: ${record.name}`);
  };

  const deleteRecord = (record: OrderRecord) => {
    Message.success(`删除案例: ${record.name}`);
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
    { label: '示范案例', value: 'benchmark' },
    { label: '合作案例', value: 'cooperation' },
    { label: '产业化案例', value: 'industrialization' },
    { label: '教学实践案例', value: 'teaching' },
  ];

  function importPatient() {
    document.getElementById('import-patient-input')?.click();
  }
  function handlePatientFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入案例文件', files);
    }
  }
  function handleDetailImport() {
    document.getElementById('import-detail-input')?.click();
    showDetailImport.value = false;
  }
  function handleDetailFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入案例详情文件', files, searchNo.value, importType.value);
    }
  }

  const importRecords = ref([
    {
      name: 'case_0601.xlsx',
      category: '案例信息',
      serial: 'LSH20240601001',
      importDate: '2024-06-01 10:00',
      updateDate: '2024-06-01 12:00',
      updateUser: '张三',
    },
    {
      name: 'case_0602.xlsx',
      category: '案例信息',
      serial: 'LSH20240602001',
      importDate: '2024-06-02 14:30',
      updateDate: '2024-06-02 15:00',
      updateUser: '李四',
    },
    {
      name: 'case_0603.xlsx',
      category: '案例信息',
      serial: 'LSH20240603001',
      importDate: '2024-06-03 09:15',
      updateDate: '2024-06-03 10:00',
      updateUser: '王五',
    },
  ]);
  function download({ name }: { name: string }) {
    Message.info(`下载案例：${name}`);
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
