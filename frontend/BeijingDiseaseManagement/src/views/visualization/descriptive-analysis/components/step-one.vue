<template>
  <a-card title="患者与指标选择" :bordered="false">
    <!-- 搜索表格组件 -->
    <div class="search-table-container">
      <a-row>
        <a-col :flex="1">
          <a-form
            :model="searchFormModel"
            :label-col-props="{ span: 6 }"
            :wrapper-col-props="{ span: 18 }"
            label-align="left"
          >
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item
                  field="patientId"
                  label="患者ID"
                  style="width: 200px"
                >
                  <a-input
                    v-model="searchFormModel.patientId"
                    placeholder="请输入患者ID"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="name" label="姓名" style="width: 100%">
                  <a-input
                    v-model="searchFormModel.name"
                    placeholder="请输入患者姓名"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="gender" label="性别" style="width: 100%">
                  <a-select
                    v-model="searchFormModel.gender"
                    :options="genderOptions"
                    placeholder="请选择性别"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item
                  field="ageRange"
                  label="年龄区间"
                  style="width: 100%"
                >
                  <a-input-number
                    v-model="searchFormModel.minAge"
                    placeholder="最小年龄"
                    :min="0"
                    :max="120"
                    style="width: 45%"
                  />
                  <span style="margin: 0 8px">-</span>
                  <a-input-number
                    v-model="searchFormModel.maxAge"
                    placeholder="最大年龄"
                    :min="0"
                    :max="120"
                    style="width: 45%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="bloodType" label="血型" style="width: 100%">
                  <a-select
                    v-model="searchFormModel.bloodType"
                    :options="bloodTypeOptions"
                    placeholder="请选择血型"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item
                  field="diagnosis"
                  label="病种类型"
                  style="width: 100%"
                >
                  <a-select
                    v-model="searchFormModel.diagnosis"
                    :options="diagnosisOptions"
                    placeholder="请选择病种"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-col>
        <a-divider style="height: 84px" direction="vertical" />
        <a-col :flex="'86px'" style="text-align: right">
          <a-space direction="vertical" :size="18">
            <a-button type="primary" @click="searchPatients">
              <template #icon>
                <icon-search />
              </template>
              搜索患者
            </a-button>
            <a-button @click="resetSearch">
              <template #icon>
                <icon-refresh />
              </template>
              重置搜索
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-divider style="margin-top: 0" />

      <!-- 表格操作栏 -->
      <a-row style="margin-bottom: 16px">
        <a-col :span="12">
          <a-space>
            <a-button type="primary" @click="selectAllPatients">
              <template #icon>
                <icon-check />
              </template>
              全选患者
            </a-button>
            <a-button @click="clearSelection">
              <template #icon>
                <icon-close />
              </template>
              清除选择
            </a-button>
            <a-select
              :model-value="props.averageType"
              :options="averageTypeOptions"
              placeholder="选择计算方式"
              style="width: 120px"
              @update:model-value="(value: string) => emit('update:averageType', value)"
            />
          </a-space>
        </a-col>
        <a-col
          :span="12"
          style="display: flex; align-items: center; justify-content: end"
        >
          <a-button @click="exportSelectedPatients">
            <template #icon>
              <icon-download />
            </template>
            导出选中
          </a-button>
          <a-tooltip content="刷新数据">
            <div class="action-icon" @click="searchPatients">
              <icon-refresh size="18" />
            </div>
          </a-tooltip>
          <a-dropdown @select="handleSelectDensity">
            <a-tooltip content="表格密度">
              <div class="action-icon">
                <icon-line-height size="18" />
              </div>
            </a-tooltip>
            <template #content>
              <a-doption
                v-for="item in densityList"
                :key="item.value"
                :value="item.value"
                :class="{ active: item.value === tableSize }"
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
              <div class="action-icon">
                <icon-settings size="18" />
              </div>
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
                        @change="handleChange($event, item, index)"
                      >
                      </a-checkbox>
                    </div>
                    <div class="title">
                      {{ item.title }}
                    </div>
                  </div>
                </div>
              </template>
            </a-popover>
          </a-tooltip>
        </a-col>
      </a-row>

      <!-- 分析指标选择 -->
      <a-row :gutter="16" style="margin-bottom: 16px">
        <a-col :span="12">
          <a-form-item label="分析指标" required>
            <a-input
              :model-value="selectedIndicatorsDisplay"
              placeholder="请选择要分析的指标"
              readonly
              @click="showIndicatorModal = true"
              style="cursor: pointer"
            >
              <template #suffix>
                <icon-down />
              </template>
            </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="指标说明">
            <div class="indicator-info">
              <p>已选择 {{ selectedIndicatorsCount }} 个指标</p>
              <p v-if="selectedIndicatorsCount > 0">
                选中指标：{{ selectedIndicatorsList.join(', ') }}
              </p>
              <p style="margin-top: 8px; font-size: 12px; color: #999">
                包含患者基本属性（年龄、性别、血型、职业、病种类型）和检验指标
              </p>
            </div>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 指标选择弹窗 -->
      <a-modal
        v-model:visible="showIndicatorModal"
        title="选择分析指标"
        :width="600"
        :footer="false"
        @cancel="showIndicatorModal = false"
      >
        <div class="indicator-modal-content">
          <div class="indicator-list">
            <div
              v-for="item in indicatorOptions"
              :key="item.value"
              class="indicator-item"
            >
              <div>
                <a-checkbox
                  v-model="item.checked"
                  @change="handleIndicatorChange($event, item)"
                >
                </a-checkbox>
              </div>
              <div class="indicator-title">
                {{ item.label }}
              </div>
            </div>
          </div>
          <div class="indicator-modal-footer">
            <a-space>
              <a-button @click="selectAllIndicators">全选</a-button>
              <a-button @click="clearAllIndicators">清空</a-button>
              <a-button type="primary" @click="confirmIndicatorSelection"
                >确定</a-button
              >
            </a-space>
          </div>
        </div>
      </a-modal>

      <!-- 患者表格 -->
      <a-table
        row-key="key"
        :loading="tableLoading"
        :pagination="pagination"
        :columns="columns"
        :data="filteredPatientList"
        :bordered="false"
        :size="tableSize"
        :row-selection="rowSelection"
        :scroll="{ x: 4000 }"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #index="{ rowIndex }">
          {{ rowIndex + 1 + (pagination.current - 1) * pagination.pageSize }}
        </template>
        <template #gender="{ record }">
          <a-tag :color="record.gender === '男' ? 'blue' : 'pink'">
            {{ record.gender }}
          </a-tag>
        </template>
        <template #diagnosis="{ record }">
          <a-tag :color="getDiagnosisColor(record.diagnosis)">
            {{ record.diagnosis }}
          </a-tag>
        </template>
        <template #stage="{ record }">
          <a-tag :color="getStageColor(record.stage)">
            {{ record.stage }}
          </a-tag>
        </template>
      </a-table>
    </div>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-form-item>
          <a-space>
            <a-button
              type="primary"
              @click="confirmDataSelection"
              :loading="isLoading"
            >
              数据选定
            </a-button>
          </a-space>
        </a-form-item>
      </a-col>
    </a-row>
  </a-card>
</template>

<script lang="ts" setup>
  import { ref, computed, watch, nextTick } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import type { SelectOptionData } from '@arco-design/web-vue/es/select/interface';
  import type { TableColumnData } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  // Props
  interface Props {
    patientList: any[];
    selectedPatientKeys: string[];
    selectedIndicators: string[];
    averageType: string;
  }

  // Emits
  interface Emits {
    (e: 'update:selectedPatientKeys', keys: string[]): void;
    (e: 'update:selectedIndicators', indicators: string[]): void;
    (e: 'update:averageType', type: string): void;
    (e: 'dataSelected', data: any): void;
  }

  const props = defineProps<Props>();
  const emit = defineEmits<Emits>();

  // 指标选项数据
  const indicatorOptions = ref([
    { label: '年龄', value: 'age', checked: false },
    { label: '性别', value: 'gender', checked: false },
    { label: '血型', value: 'bloodType', checked: false },
    { label: '职业', value: 'occupation', checked: false },
    { label: '病种类型', value: 'diagnosis', checked: false },
    { label: 'CA199', value: 'CA199', checked: false },
    { label: 'CEA', value: 'CEA', checked: false },
    { label: 'CA242', value: 'CA242', checked: false },
    { label: 'CA125', value: 'CA125', checked: false },
    { label: 'AFP', value: 'AFP', checked: false },
    { label: 'CA153', value: 'CA153', checked: false },
    { label: 'PSA', value: 'PSA', checked: false },
    { label: 'Fer', value: 'Fer', checked: false },
    { label: 'β-HCG', value: 'β-HCG', checked: false },
    { label: '白细胞', value: 'whiteBloodCell', checked: false },
    { label: '红细胞', value: 'redBloodCell', checked: false },
    { label: '血红蛋白', value: 'hemoglobin', checked: false },
    { label: '血小板', value: 'platelet', checked: false },
    { label: '总胆固醇', value: 'totalCholesterol', checked: false },
    { label: '甘油三酯', value: 'triglyceride', checked: false },
    { label: '高密度脂蛋白', value: 'hdlCholesterol', checked: false },
    { label: '低密度脂蛋白', value: 'ldlCholesterol', checked: false },
    { label: '总胆红素', value: 'totalBilirubin', checked: false },
    { label: '谷草转氨酶', value: 'ast', checked: false },
    { label: '谷丙转氨酶', value: 'alt', checked: false },
    { label: '肌酐', value: 'creatinine', checked: false },
    { label: '尿素氮', value: 'urea', checked: false },
    { label: '血糖', value: 'glucose', checked: false },
    { label: '血压(收缩压)', value: 'systolicPressure', checked: false },
    { label: '血压(舒张压)', value: 'diastolicPressure', checked: false },
    { label: '体重(kg)', value: 'weight', checked: false },
    { label: '身高(cm)', value: 'height', checked: false },
  ]);

  // 计算选中的指标数量和列表
  const selectedIndicatorsCount = computed(() => {
    return indicatorOptions.value.filter((item) => item.checked).length;
  });

  const selectedIndicatorsList = computed(() => {
    return indicatorOptions.value
      .filter((item) => item.checked)
      .map((item) => item.label);
  });

  // 监听props变化，同步到本地选项
  watch(
    () => props.selectedIndicators,
    (newValue) => {
      indicatorOptions.value.forEach((item) => {
        item.checked = newValue.includes(item.value);
      });
    },
    { immediate: true }
  );

  // 弹窗控制
  const showIndicatorModal = ref(false);

  // 显示选中的指标文本
  const selectedIndicatorsDisplay = computed(() => {
    if (selectedIndicatorsCount.value === 0) {
      return '';
    }
    if (selectedIndicatorsCount.value <= 3) {
      return selectedIndicatorsList.value.join(', ');
    }
    return `${selectedIndicatorsList.value.slice(0, 3).join(', ')} 等${
      selectedIndicatorsCount.value
    }个指标`;
  });

  // 处理指标选择变化
  const handleIndicatorChange = (checked: boolean, item: any) => {
    item.checked = checked;
  };

  // 全选指标
  const selectAllIndicators = () => {
    indicatorOptions.value.forEach((item) => {
      item.checked = true;
    });
  };

  // 清空指标
  const clearAllIndicators = () => {
    indicatorOptions.value.forEach((item) => {
      item.checked = false;
    });
  };

  // 确认指标选择
  const confirmIndicatorSelection = () => {
    const selectedValues = indicatorOptions.value
      .filter((option) => option.checked)
      .map((option) => option.value);
    console.log('指标选择变化:', selectedValues);
    emit('update:selectedIndicators', selectedValues);
    showIndicatorModal.value = false;
  };

  // 响应式数据
  const searchFormModel = ref({
    patientId: '',
    name: '',
    gender: '',
    minAge: null as number | null,
    maxAge: null as number | null,
    bloodType: '',
    diagnosis: '',
  });

  const isLoading = ref(false);
  const tableLoading = ref(false);

  // 表格相关
  const tableSize = ref<SizeProps>('medium');
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);

  const basePagination = {
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: true,
    pageSizeOptions: ['10', '20', '50', '100'],
  };
  const pagination = ref(basePagination);

  // 选项数据
  const genderOptions = computed<SelectOptionData[]>(() => [
    { label: '男', value: '男' },
    { label: '女', value: '女' },
  ]);

  const bloodTypeOptions = computed<SelectOptionData[]>(() => [
    { label: 'A型', value: 'A型' },
    { label: 'B型', value: 'B型' },
    { label: 'O型', value: 'O型' },
    { label: 'AB型', value: 'AB型' },
  ]);

  const diagnosisOptions = computed<SelectOptionData[]>(() => [
    { label: '结肠癌', value: '结肠癌' },
    { label: '直肠癌', value: '直肠癌' },
    { label: '新闻学', value: '新闻学' },
    { label: '腺癌', value: '腺癌' },
    { label: '黏液腺癌', value: '黏液腺癌' },
    { label: '印戒细胞癌', value: '印戒细胞癌' },
  ]);

  const densityList = computed(() => [
    { name: '紧凑', value: 'mini' },
    { name: '小', value: 'small' },
    { name: '中', value: 'medium' },
    { name: '大', value: 'large' },
  ]);

  const averageTypeOptions = computed<SelectOptionData[]>(() => [
    { label: '平均数', value: 'average' },
    { label: '中位数', value: 'median' },
    { label: '最近一次', value: 'recent' },
  ]);

  // 过滤后的患者列表
  const filteredPatientList = computed(() => {
    let filtered = props.patientList;

    if (searchFormModel.value.patientId) {
      filtered = filtered.filter((patient) =>
        patient.patientId
          .toLowerCase()
          .includes(searchFormModel.value.patientId.toLowerCase())
      );
    }
    if (searchFormModel.value.name) {
      filtered = filtered.filter((patient) =>
        patient.name.includes(searchFormModel.value.name)
      );
    }
    if (searchFormModel.value.gender) {
      filtered = filtered.filter(
        (patient) => patient.gender === searchFormModel.value.gender
      );
    }
    if (searchFormModel.value.minAge !== null) {
      filtered = filtered.filter(
        (patient) => patient.age >= searchFormModel.value.minAge!
      );
    }
    if (searchFormModel.value.maxAge !== null) {
      filtered = filtered.filter(
        (patient) => patient.age <= searchFormModel.value.maxAge!
      );
    }
    if (searchFormModel.value.bloodType) {
      filtered = filtered.filter(
        (patient) => patient.bloodType === searchFormModel.value.bloodType
      );
    }
    if (searchFormModel.value.diagnosis) {
      filtered = filtered.filter(
        (patient) => patient.diagnosis === searchFormModel.value.diagnosis
      );
    }

    return filtered;
  });

  // 监听过滤后的患者列表变化，更新分页
  watch(filteredPatientList, (newFilteredList) => {
    pagination.value.total = newFilteredList.length;
  });

  // 表格列定义
  const columns = computed<TableColumnData[]>(() => [
    {
      title: '#',
      dataIndex: 'index',
      slotName: 'index',
      width: 60,
      fixed: 'left',
    },
    {
      title: '患者ID',
      dataIndex: 'patientId',
      key: 'patientId',
      width: 100,
      fixed: 'left',
    },
    { title: '姓名', dataIndex: 'name', key: 'name', width: 80, fixed: 'left' },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 60,
      slotName: 'gender',
    },
    { title: '年龄', dataIndex: 'age', key: 'age', width: 60 },
    { title: '血型', dataIndex: 'bloodType', key: 'bloodType', width: 60 },
    { title: '职业', dataIndex: 'occupation', key: 'occupation', width: 80 },
    {
      title: '病种',
      dataIndex: 'diagnosis',
      key: 'diagnosis',
      width: 100,
      slotName: 'diagnosis',
    },
    {
      title: '分期',
      dataIndex: 'stage',
      key: 'stage',
      width: 60,
      slotName: 'stage',
    },
    { title: 'CA199', dataIndex: 'CA199', key: 'CA199', width: 80 },
    { title: 'CEA', dataIndex: 'CEA', key: 'CEA', width: 80 },
    { title: 'CA242', dataIndex: 'CA242', key: 'CA242', width: 80 },
    { title: 'CA125', dataIndex: 'CA125', key: 'CA125', width: 80 },
    { title: 'AFP', dataIndex: 'AFP', key: 'AFP', width: 80 },
    { title: 'CA153', dataIndex: 'CA153', key: 'CA153', width: 80 },
    { title: 'PSA', dataIndex: 'PSA', key: 'PSA', width: 80 },
    { title: 'Fer', dataIndex: 'Fer', key: 'Fer', width: 80 },
    { title: 'β-HCG', dataIndex: 'β-HCG', key: 'β-HCG', width: 80 },
    {
      title: '白细胞',
      dataIndex: 'whiteBloodCell',
      key: 'whiteBloodCell',
      width: 80,
    },
    {
      title: '红细胞',
      dataIndex: 'redBloodCell',
      key: 'redBloodCell',
      width: 80,
    },
    {
      title: '血红蛋白',
      dataIndex: 'hemoglobin',
      key: 'hemoglobin',
      width: 80,
    },
    { title: '血小板', dataIndex: 'platelet', key: 'platelet', width: 80 },
    {
      title: '总胆固醇',
      dataIndex: 'totalCholesterol',
      key: 'totalCholesterol',
      width: 100,
    },
    {
      title: '甘油三酯',
      dataIndex: 'triglyceride',
      key: 'triglyceride',
      width: 100,
    },
    {
      title: '高密度脂蛋白',
      dataIndex: 'hdlCholesterol',
      key: 'hdlCholesterol',
      width: 100,
    },
    {
      title: '低密度脂蛋白',
      dataIndex: 'ldlCholesterol',
      key: 'ldlCholesterol',
      width: 100,
    },
    {
      title: '总胆红素',
      dataIndex: 'totalBilirubin',
      key: 'totalBilirubin',
      width: 100,
    },
    { title: '谷草转氨酶', dataIndex: 'ast', key: 'ast', width: 100 },
    { title: '谷丙转氨酶', dataIndex: 'alt', key: 'alt', width: 100 },
    { title: '肌酐', dataIndex: 'creatinine', key: 'creatinine', width: 80 },
    { title: '尿素氮', dataIndex: 'urea', key: 'urea', width: 80 },
    { title: '血糖', dataIndex: 'glucose', key: 'glucose', width: 80 },
    {
      title: '血压(收缩压)',
      dataIndex: 'systolicPressure',
      key: 'systolicPressure',
      width: 120,
    },
    {
      title: '血压(舒张压)',
      dataIndex: 'diastolicPressure',
      key: 'diastolicPressure',
      width: 120,
    },
    { title: '体重(kg)', dataIndex: 'weight', key: 'weight', width: 100 },
    { title: '身高(cm)', dataIndex: 'height', key: 'height', width: 100 },
  ]);

  // 行选择配置
  const rowSelection = computed(() => ({
    selectedRowKeys: props.selectedPatientKeys,
    onChange: (selectedRowKeys: string[]) => {
      emit('update:selectedPatientKeys', selectedRowKeys);
    },
    showCheckedAll: true,
    title: '选择',
  }));

  // 表格相关函数
  const handleSelectDensity = (
    val: string | number | Record<string, any> | undefined
  ) => {
    tableSize.value = val as SizeProps;
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

  const onPageChange = (current: number) => {
    pagination.value.current = current;
  };

  const onPageSizeChange = (pageSize: number) => {
    pagination.value.pageSize = pageSize;
    pagination.value.current = 1; // 重置到第一页
  };

  // 颜色函数
  const getDiagnosisColor = (diagnosis: string) => {
    const colorMap: Record<string, string> = {
      结肠癌: 'red',
      直肠癌: 'orange',
      新闻学: 'purple',
      腺癌: 'blue',
      黏液腺癌: 'green',
      印戒细胞癌: 'volcano',
    };
    return colorMap[diagnosis] || 'default';
  };

  const getStageColor = (stage: string) => {
    const colorMap: Record<string, string> = {
      I期: 'green',
      II期: 'blue',
      III期: 'orange',
      IV期: 'red',
    };
    return colorMap[stage] || 'default';
  };

  // 表格操作函数
  const selectAllPatients = () => {
    if (filteredPatientList.value.length === 0) {
      Message.warning('没有可选择的患者');
      return;
    }
    const allKeys = filteredPatientList.value.map((patient) => patient.key);
    emit('update:selectedPatientKeys', allKeys);
    Message.success(`已全选 ${allKeys.length} 个患者`);
  };

  const clearSelection = () => {
    if (props.selectedPatientKeys.length === 0) {
      Message.info('当前没有选中的患者');
      return;
    }
    emit('update:selectedPatientKeys', []);
    Message.success('已清除所有选择');
  };

  const exportSelectedPatients = () => {
    if (props.selectedPatientKeys.length === 0) {
      Message.warning('请先选择患者');
      return;
    }
    Message.success(`已导出 ${props.selectedPatientKeys.length} 个患者数据`);
  };

  // 搜索患者
  const searchPatients = () => {
    tableLoading.value = true;
    setTimeout(() => {
      tableLoading.value = false;
      Message.success('搜索完成');
    }, 1000);
  };

  // 重置搜索
  const resetSearch = () => {
    searchFormModel.value = {
      patientId: '',
      name: '',
      gender: '',
      minAge: null,
      maxAge: null,
      bloodType: '',
      diagnosis: '',
    };
    Message.success('搜索条件已重置');
  };

  // 确认数据选择
  const confirmDataSelection = async () => {
    if (props.selectedPatientKeys.length === 0) {
      Message.warning('请先选择患者');
      return;
    }
    if (props.selectedIndicators.length === 0) {
      Message.warning('请先选择分析指标');
      return;
    }

    isLoading.value = true;
    try {
      await new Promise<void>((resolve) => {
        setTimeout(() => {
          resolve();
        }, 1500);
      });

      const dataOverview = {
        selectedPatients: props.selectedPatientKeys.length,
        selectedIndicators: props.selectedIndicators.length,
        dataCompleteness: (Math.random() * 20 + 80).toFixed(1),
        dataQuality: (Math.random() * 15 + 85).toFixed(1),
      };

      emit('dataSelected', dataOverview);
      Message.success('数据选定完成');
    } catch (error) {
      Message.error('数据选定失败');
    } finally {
      isLoading.value = false;
    }
  };

  // 监听列变化
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
</script>

<style scoped lang="less">
  .indicator-info {
    padding: 12px;
    background-color: #f5f5f5;
    border-radius: 6px;

    p {
      margin: 0;
      color: #666;
      font-size: 14px;
    }
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

  .ant-table {
    .ant-table-thead > tr > th {
      background-color: #fafafa;
      font-weight: 600;
    }
  }

  .indicator-modal-content {
    max-height: 400px;
    overflow-y: auto;
  }

  .indicator-list {
    margin-bottom: 16px;
  }

  .indicator-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    margin-bottom: 4px;
    border-radius: 4px;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f5f5f5;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  .indicator-title {
    margin-left: 12px;
    cursor: pointer;
    font-size: 14px;
    color: #1d2129;
  }

  .indicator-modal-footer {
    border-top: 1px solid #e5e6eb;
    padding-top: 16px;
    text-align: right;
  }
</style>
