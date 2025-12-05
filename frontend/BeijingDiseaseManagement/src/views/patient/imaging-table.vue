<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patient.imagingTable']" />
    <a-card class="general-card" :title="$t('menu.patient.imagingTable')">
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
              <a-col :span="8">
                <a-form-item field="imagingType" label="著作类型">
                  <a-input
                    v-model="formModel.imagingType"
                    placeholder="请输入著作类型"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="imagingDate" label="出版日期">
                  <a-range-picker
                    v-model="formModel.imagingDate"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="imagingNo" label="著作编号">
                  <a-input
                    v-model="formModel.imagingNo"
                    placeholder="请输入著作编号"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="diagnosis" label="主题方向">
                  <a-input
                    v-model="formModel.diagnosis"
                    placeholder="请输入主题方向"
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
              更新评估指标表
            </a-button>
            <a-button type="outline" @click="showDetailImport = true">
              <template #icon><icon-upload /></template>
              导入著作详情
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
              >monograph_0601.xlsx</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px">著作信息</td>
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
    <!-- 导入著作详情弹窗 -->
    <a-modal
      v-model:visible="showDetailImport"
      title="批量导入著作详情"
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
  import getImagingData from './imaging-mock';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  // 定义著作数据类型
  interface ImagingRecord {
    id: number;
    hospitalNo: string;
    name: string;
    imagingType: string;
    imagingDate: string;
    imagingNo: string;
    diagnosis: string;
    description: string;
    conclusion: string;
    radiologist: string;
    reportDate: string;
  }

  const generateFormModel = () => {
    return {
      hospitalNo: '',
      name: '',
      imagingType: '',
      imagingDate: [],
      imagingNo: '',
      diagnosis: '',
    };
  };

  const { loading, setLoading } = useLoading(true);

  const renderData = ref<ImagingRecord[]>([]);
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
      title: '著作类型',
      dataIndex: 'imagingType',
      width: 90,
      align: 'center',
    },
    {
      title: '出版日期',
      dataIndex: 'imagingDate',
      width: 120,
      align: 'center',
    },
    { title: '著作编号', dataIndex: 'imagingNo', width: 120, align: 'center' },
    { title: '主题方向', dataIndex: 'diagnosis', width: 150, align: 'center' },
    {
      title: '内容简介',
      dataIndex: 'description',
      width: 180,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '收录情况',
      dataIndex: 'conclusion',
      width: 180,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '编审负责人',
      dataIndex: 'radiologist',
      width: 120,
      align: 'center',
    },
    { title: '最近更新', dataIndex: 'reportDate', width: 120, align: 'center' },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      width: 100,
      fixed: 'right',
      align: 'center',
    },
  ]);

  let sortableInstance: Sortable | null = null;

  const fetchData = async (params: any = { current: 1, pageSize: 10 }) => {
    setLoading(true);
    try {
      // 使用 imaging-mock.ts 的数据
      setTimeout(() => {
        const { list, total } = getImagingData(params.current, params.pageSize);

        // 转换数据格式以匹配原有结构
        const convertedList = list.map((item) => ({
          id: item.id,
          hospitalNo: item.hospitalNo,
          name: item.name,
          imagingType: item.imagingType,
          imagingDate: item.reportTime,
          imagingNo: item.imagingNo,
          diagnosis: item.diagnosis,
          description: item.description,
          conclusion: item.conclusion,
          radiologist: item.radiologist,
          reportDate: item.reportTime,
        }));

        renderData.value = convertedList;
        pagination.current = params.current;
        pagination.total = total;
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

  const editRecord = (record: ImagingRecord) => {
    Message.info(`编辑著作: ${record.name}`);
  };

  const deleteRecord = (record: ImagingRecord) => {
    Message.success(`删除著作: ${record.name}`);
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
    { label: '教材类', value: 'textbook' },
    { label: '学术专著', value: 'monograph' },
    { label: '译著', value: 'translation' },
    { label: '工具书', value: 'reference' },
  ];

  function importPatient() {
    document.getElementById('import-patient-input')?.click();
  }
  function handlePatientFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入著作文件', files);
    }
  }
  function handleDetailImport() {
    document.getElementById('import-detail-input')?.click();
    showDetailImport.value = false;
  }
  function handleDetailFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入著作详情文件', files, searchNo.value, importType.value);
    }
  }

  const importRecords = ref([
    {
      name: 'monograph_0601.xlsx',
      category: '著作信息',
      serial: 'LSH20240601001',
      importDate: '2024-06-01 10:00',
      updateDate: '2024-06-01 12:00',
      updateUser: '张三',
    },
    {
      name: 'monograph_0602.xlsx',
      category: '著作信息',
      serial: 'LSH20240602001',
      importDate: '2024-06-02 14:30',
      updateDate: '2024-06-02 15:00',
      updateUser: '李四',
    },
    {
      name: 'monograph_0603.xlsx',
      category: '著作信息',
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
