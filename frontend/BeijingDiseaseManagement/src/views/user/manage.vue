<template>
  <div class="container">
    <a-card class="general-card" title="医生管理">
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
                <a-form-item field="jobId" label="工号">
                  <a-input v-model="formModel.jobId" placeholder="请输入工号" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="name" label="姓名">
                  <a-input v-model="formModel.name" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="gender" label="性别">
                  <a-select
                    v-model="formModel.gender"
                    :options="genderOptions"
                    placeholder="请选择性别"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="department" label="科室">
                  <a-input
                    v-model="formModel.department"
                    placeholder="请输入科室"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="title" label="职称">
                  <a-select
                    v-model="formModel.title"
                    :options="titleOptions"
                    placeholder="请选择职称"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="phone" label="手机号">
                  <a-input
                    v-model="formModel.phone"
                    placeholder="请输入手机号"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="status" label="状态">
                  <a-select
                    v-model="formModel.status"
                    :options="statusOptions"
                    placeholder="请选择状态"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="entryDate" label="入职时间">
                  <a-range-picker
                    v-model="formModel.entryDate"
                    style="width: 100%"
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
              <template #icon><icon-search /></template>查询
            </a-button>
            <a-button @click="reset">
              <template #icon><icon-refresh /></template>重置
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-divider style="margin-top: 0" />
      <a-row style="margin-bottom: 16px">
        <a-col :span="12">
          <a-space>
            <a-button type="primary">
              <template #icon><icon-plus /></template>新增
            </a-button>
            <a-upload action="/">
              <template #upload-button>
                <a-button>导入</a-button>
              </template>
            </a-upload>
          </a-space>
        </a-col>
        <a-col
          :span="12"
          style="display: flex; align-items: center; justify-content: end"
        >
          <a-button>
            <template #icon><icon-download /></template>导出
          </a-button>
          <a-tooltip content="刷新">
            <div class="action-icon" @click="search"
              ><icon-refresh size="18"
            /></div>
          </a-tooltip>
          <a-dropdown @select="handleSelectDensity">
            <a-tooltip content="表格密度">
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
                    <div style="margin-right: 4px; cursor: move"
                      ><icon-drag-arrow
                    /></div>
                    <div>
                      <a-checkbox
                        v-model="item.checked"
                        @change="handleChange($event, item, index)"
                      ></a-checkbox>
                    </div>
                    <div class="title">{{
                      item.title === '#' ? '序列号' : item.title
                    }}</div>
                  </div>
                </div>
              </template>
            </a-popover>
          </a-tooltip>
        </a-col>
      </a-row>
      <a-table
        row-key="id"
        :loading="loading"
        :pagination="pagination"
        :columns="(cloneColumns as TableColumnData[])"
        :data="renderData"
        :bordered="false"
        :size="size"
        @page-change="onPageChange"
      >
        <template #index="{ rowIndex }">
          {{ rowIndex + 1 + (pagination.current - 1) * pagination.pageSize }}
        </template>
        <template #status="{ record }">
          <span v-if="record.status === '离职'" class="circle"></span>
          <span v-else class="circle pass"></span>
          {{ record.status }}
        </template>
        <template #operations>
          <a-button type="text" size="small">查看</a-button>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, reactive, watch, nextTick } from 'vue';
  import useLoading from '@/hooks/loading';
  import type { SelectOptionData } from '@arco-design/web-vue/es/select/interface';
  import type { TableColumnData } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  const generateFormModel = () => ({
    jobId: '',
    name: '',
    gender: '',
    department: '',
    title: '',
    phone: '',
    status: '',
    entryDate: [],
  });
  const { loading, setLoading } = useLoading(true);
  const renderData = ref<any[]>([
    {
      id: 1,
      jobId: 'D001',
      name: '王医生',
      gender: '男',
      department: '内科',
      title: '主任医师',
      phone: '13800000001',
      status: '在职',
      entryDate: '2015-03-01',
    },
    {
      id: 2,
      jobId: 'D002',
      name: '李医生',
      gender: '女',
      department: '外科',
      title: '副主任医师',
      phone: '13800000002',
      status: '在职',
      entryDate: '2017-07-15',
    },
    {
      id: 3,
      jobId: 'D003',
      name: '张医生',
      gender: '男',
      department: '儿科',
      title: '主治医师',
      phone: '13800000003',
      status: '离职',
      entryDate: '2012-11-20',
    },
  ]);
  const formModel = ref(generateFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);

  const size = ref<SizeProps>('medium');

  const basePagination = {
    current: 1,
    pageSize: 20,
  };
  const pagination = reactive({
    ...basePagination,
    total: 3,
  });
  const densityList = computed(() => [
    { name: '紧凑', value: 'mini' },
    { name: '偏小', value: 'small' },
    { name: '中等', value: 'medium' },
    { name: '宽松', value: 'large' },
  ]);
  const columns = computed<TableColumnData[]>(() => [
    { title: '序号', dataIndex: 'index', slotName: 'index' },
    { title: '工号', dataIndex: 'jobId' },
    { title: '姓名', dataIndex: 'name' },
    { title: '性别', dataIndex: 'gender' },
    { title: '科室', dataIndex: 'department' },
    { title: '职称', dataIndex: 'title' },
    { title: '手机号', dataIndex: 'phone' },
    { title: '状态', dataIndex: 'status', slotName: 'status' },
    { title: '入职时间', dataIndex: 'entryDate' },
    { title: '操作', dataIndex: 'operations', slotName: 'operations' },
  ]);
  const genderOptions = [
    { label: '男', value: '男' },
    { label: '女', value: '女' },
  ];
  const titleOptions = [
    { label: '主任医师', value: '主任医师' },
    { label: '副主任医师', value: '副主任医师' },
    { label: '主治医师', value: '主治医师' },
    { label: '住院医师', value: '住院医师' },
  ];
  const statusOptions = [
    { label: '在职', value: '在职' },
    { label: '离职', value: '离职' },
  ];

  const search = () => {
    setLoading(true);
    setTimeout(() => setLoading(false), 500);
  };
  const reset = () => {
    formModel.value = generateFormModel();
  };
  const onPageChange = (current: number) => {
    pagination.current = current;
  };
  const handleSelectDensity = (val: string) => {
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
        const sortable = new Sortable(el, {
          onEnd(e: any) {
            const { oldIndex, newIndex } = e;
            exchangeArray(cloneColumns.value, oldIndex, newIndex);
            exchangeArray(showColumns.value, oldIndex, newIndex);
          },
        });
      });
    }
  };
  watch(
    () => columns.value,
    (val) => {
      cloneColumns.value = cloneDeep(val);
      cloneColumns.value.forEach((item, index) => {
        item.checked = true;
      });
      showColumns.value = cloneDeep(cloneColumns.value);
    },
    { deep: true, immediate: true }
  );

  // 页面加载后自动关闭表格loading
  setLoading(false);
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
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
</style>
