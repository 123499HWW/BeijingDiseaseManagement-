<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patientAnalysis.profile']" />
    <a-card class="general-card" :title="$t('menu.patientAnalysis.profile')">
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
                <a-form-item field="患者ID" label="患者ID">
                  <a-input
                    v-model="formModel.患者ID"
                    placeholder="请输入患者ID"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="性别" label="性别">
                  <a-input v-model="formModel.性别" placeholder="请输入性别" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="住院日期" label="住院日期">
                  <a-range-picker
                    v-model="formModel.住院日期"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="年龄" label="年龄">
                  <a-input v-model="formModel.年龄" placeholder="请输入年龄" />
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
              更新评估
            </a-button>
            <a-button type="outline" @click="showDetailImport = true">
              <template #icon><icon-upload /></template>
              导入评估
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
        row-key="患者ID"
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

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  // 定义评分/诊断数据类型
  interface PatientScoreRecord {
    '患者ID': string;
    '就诊次数': number;
    '住院日期': string;
    '性别': string;
    '年龄': number;
    'CURB-65评分': number;
    'PSI评分': number;
    'CPIS评分': number;
    '重症肺炎诊断标准': string;
    'qSOFA': number;
    'SOFA': number;
    'COVID-19': string; // 是/否
    '是否获社区获得性肺炎': string; // 是/否
    '是否获脓毒症': string; // 是/否
    '是否属于患者呼吸道症候群': string; // 是/否
  }

  const generateFormModel = () => {
    return {
      患者ID: '',
      性别: '',
      住院日期: [],
      年龄: '',
    };
  };

  const { loading, setLoading } = useLoading(true);

  const renderData = ref<PatientScoreRecord[]>([]);
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
    { title: '患者ID', dataIndex: '患者ID', width: 120, align: 'center' },
    { title: '就诊次数', dataIndex: '就诊次数', width: 90, align: 'center' },
    { title: '住院日期', dataIndex: '住院日期', width: 120, align: 'center' },
    { title: '性别', dataIndex: '性别', width: 70, align: 'center' },
    { title: '年龄', dataIndex: '年龄', width: 70, align: 'center' },
    {
      title: 'CURB-65评分',
      dataIndex: 'CURB-65评分',
      width: 110,
      align: 'center',
    },
    { title: 'PSI评分', dataIndex: 'PSI评分', width: 90, align: 'center' },
    { title: 'CPIS评分', dataIndex: 'CPIS评分', width: 90, align: 'center' },
    {
      title: '重症肺炎诊断标准',
      dataIndex: '重症肺炎诊断标准',
      width: 200,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    { title: 'qSOFA', dataIndex: 'qSOFA', width: 80, align: 'center' },
    { title: 'SOFA', dataIndex: 'SOFA', width: 80, align: 'center' },
    { title: 'COVID-19', dataIndex: 'COVID-19', width: 100, align: 'center' },
    {
      title: '是否获社区获得性肺炎',
      dataIndex: '是否获社区获得性肺炎',
      width: 160,
      align: 'center',
    },
    {
      title: '是否获脓毒症',
      dataIndex: '是否获脓毒症',
      width: 130,
      align: 'center',
    },
    {
      title: '是否属于患者呼吸道症候群',
      dataIndex: '是否属于患者呼吸道症候群',
      width: 190,
      align: 'center',
    },
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

  // 30条评分/诊断 mock 数据
  const mockData: PatientScoreRecord[] = [
    {
      '患者ID': 'P1001',
      '就诊次数': 1,
      '住院日期': '2024-07-01',
      '性别': '男',
      '年龄': 68,
      'CURB-65评分': 2,
      'PSI评分': 85,
      'CPIS评分': 4,
      '重症肺炎诊断标准': '需要机械通气或出现脓毒性休克，伴血压下降需升压药',
      'qSOFA': 2,
      'SOFA': 6,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1002',
      '就诊次数': 2,
      '住院日期': '2024-07-05',
      '性别': '女',
      '年龄': 54,
      'CURB-65评分': 1,
      'PSI评分': 70,
      'CPIS评分': 3,
      '重症肺炎诊断标准': '影像学多叶受累，需高流量吸氧',
      'qSOFA': 1,
      'SOFA': 3,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1003',
      '就诊次数': 1,
      '住院日期': '2024-07-10',
      '性别': '女',
      '年龄': 79,
      'CURB-65评分': 3,
      'PSI评分': 110,
      'CPIS评分': 6,
      '重症肺炎诊断标准': 'PaO2/FiO2 ≤ 200 且乳酸升高',
      'qSOFA': 2,
      'SOFA': 8,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1004',
      '就诊次数': 3,
      '住院日期': '2024-07-12',
      '性别': '男',
      '年龄': 45,
      'CURB-65评分': 0,
      'PSI评分': 60,
      'CPIS评分': 2,
      '重症肺炎诊断标准': '无重症标准，常规住院治疗',
      'qSOFA': 0,
      'SOFA': 1,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1005',
      '就诊次数': 1,
      '住院日期': '2024-07-15',
      '性别': '男',
      '年龄': 61,
      'CURB-65评分': 2,
      'PSI评分': 95,
      'CPIS评分': 5,
      '重症肺炎诊断标准': '需要无创通气支持',
      'qSOFA': 1,
      'SOFA': 5,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1006',
      '就诊次数': 1,
      '住院日期': '2024-07-18',
      '性别': '女',
      '年龄': 36,
      'CURB-65评分': 0,
      'PSI评分': 48,
      'CPIS评分': 1,
      '重症肺炎诊断标准': '无重症标准',
      'qSOFA': 0,
      'SOFA': 0,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1007',
      '就诊次数': 2,
      '住院日期': '2024-07-21',
      '性别': '男',
      '年龄': 72,
      'CURB-65评分': 3,
      'PSI评分': 120,
      'CPIS评分': 6,
      '重症肺炎诊断标准': '休克并需要升压药物维持',
      'qSOFA': 3,
      'SOFA': 10,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1008',
      '就诊次数': 1,
      '住院日期': '2024-07-22',
      '性别': '女',
      '年龄': 29,
      'CURB-65评分': 0,
      'PSI评分': 40,
      'CPIS评分': 0,
      '重症肺炎诊断标准': '门诊治疗，随访复查',
      'qSOFA': 0,
      'SOFA': 0,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1009',
      '就诊次数': 1,
      '住院日期': '2024-07-24',
      '性别': '男',
      '年龄': 57,
      'CURB-65评分': 1,
      'PSI评分': 72,
      'CPIS评分': 3,
      '重症肺炎诊断标准': '影像多叶受累，需氧气支持',
      'qSOFA': 1,
      'SOFA': 2,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1010',
      '就诊次数': 4,
      '住院日期': '2024-07-26',
      '性别': '女',
      '年龄': 63,
      'CURB-65评分': 2,
      'PSI评分': 90,
      'CPIS评分': 4,
      '重症肺炎诊断标准': 'PaO2/FiO2 200-300，监护治疗',
      'qSOFA': 1,
      'SOFA': 4,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1011',
      '就诊次数': 2,
      '住院日期': '2024-08-01',
      '性别': '男',
      '年龄': 48,
      'CURB-65评分': 1,
      'PSI评分': 68,
      'CPIS评分': 2,
      '重症肺炎诊断标准': '无重症标准',
      'qSOFA': 0,
      'SOFA': 1,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1012',
      '就诊次数': 1,
      '住院日期': '2024-08-02',
      '性别': '女',
      '年龄': 84,
      'CURB-65评分': 4,
      'PSI评分': 135,
      'CPIS评分': 7,
      '重症肺炎诊断标准': '机械通气并ICU监护',
      'qSOFA': 3,
      'SOFA': 12,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1013',
      '就诊次数': 1,
      '住院日期': '2024-08-05',
      '性别': '男',
      '年龄': 31,
      'CURB-65评分': 0,
      'PSI评分': 42,
      'CPIS评分': 0,
      '重症肺炎诊断标准': '门诊随访',
      'qSOFA': 0,
      'SOFA': 0,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1014',
      '就诊次数': 3,
      '住院日期': '2024-08-06',
      '性别': '女',
      '年龄': 66,
      'CURB-65评分': 2,
      'PSI评分': 96,
      'CPIS评分': 4,
      '重症肺炎诊断标准': '氧合差，需高流量加监护',
      'qSOFA': 1,
      'SOFA': 5,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1015',
      '就诊次数': 2,
      '住院日期': '2024-08-08',
      '性别': '男',
      '年龄': 59,
      'CURB-65评分': 1,
      'PSI评分': 74,
      'CPIS评分': 3,
      '重症肺炎诊断标准': '影像多叶受累',
      'qSOFA': 1,
      'SOFA': 3,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1016',
      '就诊次数': 1,
      '住院日期': '2024-08-09',
      '性别': '男',
      '年龄': 52,
      'CURB-65评分': 1,
      'PSI评分': 70,
      'CPIS评分': 3,
      '重症肺炎诊断标准': '需氧气支持，关注体液管理',
      'qSOFA': 1,
      'SOFA': 2,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1017',
      '就诊次数': 1,
      '住院日期': '2024-08-10',
      '性别': '女',
      '年龄': 40,
      'CURB-65评分': 0,
      'PSI评分': 55,
      'CPIS评分': 1,
      '重症肺炎诊断标准': '无重症标准',
      'qSOFA': 0,
      'SOFA': 1,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1018',
      '就诊次数': 3,
      '住院日期': '2024-08-12',
      '性别': '男',
      '年龄': 77,
      'CURB-65评分': 3,
      'PSI评分': 118,
      'CPIS评分': 6,
      '重症肺炎诊断标准': '乳酸升高，需血流动力学监护',
      'qSOFA': 2,
      'SOFA': 8,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1019',
      '就诊次数': 2,
      '住院日期': '2024-08-14',
      '性别': '女',
      '年龄': 64,
      'CURB-65评分': 2,
      'PSI评分': 92,
      'CPIS评分': 4,
      '重症肺炎诊断标准': 'PaO2/FiO2 200-300',
      'qSOFA': 1,
      'SOFA': 4,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1020',
      '就诊次数': 1,
      '住院日期': '2024-08-15',
      '性别': '男',
      '年龄': 58,
      'CURB-65评分': 1,
      'PSI评分': 76,
      'CPIS评分': 3,
      '重症肺炎诊断标准': '多叶受累，需抗感染强化',
      'qSOFA': 1,
      'SOFA': 3,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1021',
      '就诊次数': 2,
      '住院日期': '2024-08-17',
      '性别': '男',
      '年龄': 69,
      'CURB-65评分': 2,
      'PSI评分': 100,
      'CPIS评分': 5,
      '重症肺炎诊断标准': '高流量吸氧+监护',
      'qSOFA': 2,
      'SOFA': 6,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1022',
      '就诊次数': 1,
      '住院日期': '2024-08-19',
      '性别': '女',
      '年龄': 33,
      'CURB-65评分': 0,
      'PSI评分': 52,
      'CPIS评分': 1,
      '重症肺炎诊断标准': '无',
      'qSOFA': 0,
      'SOFA': 0,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1023',
      '就诊次数': 1,
      '住院日期': '2024-08-20',
      '性别': '女',
      '年龄': 75,
      'CURB-65评分': 3,
      'PSI评分': 112,
      'CPIS评分': 6,
      '重症肺炎诊断标准': '血流动力学不稳定',
      'qSOFA': 2,
      'SOFA': 9,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1024',
      '就诊次数': 2,
      '住院日期': '2024-08-22',
      '性别': '男',
      '年龄': 47,
      'CURB-65评分': 1,
      'PSI评分': 69,
      'CPIS评分': 2,
      '重症肺炎诊断标准': '影像多叶受累',
      'qSOFA': 1,
      'SOFA': 2,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1025',
      '就诊次数': 1,
      '住院日期': '2024-08-24',
      '性别': '男',
      '年龄': 39,
      'CURB-65评分': 0,
      'PSI评分': 50,
      'CPIS评分': 1,
      '重症肺炎诊断标准': '无',
      'qSOFA': 0,
      'SOFA': 0,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1026',
      '就诊次数': 3,
      '住院日期': '2024-08-26',
      '性别': '女',
      '年龄': 71,
      'CURB-65评分': 2,
      'PSI评分': 90,
      'CPIS评分': 5,
      '重症肺炎诊断标准': '氧合下降，需监护',
      'qSOFA': 1,
      'SOFA': 5,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1027',
      '就诊次数': 2,
      '住院日期': '2024-08-28',
      '性别': '男',
      '年龄': 62,
      'CURB-65评分': 2,
      'PSI评分': 98,
      'CPIS评分': 4,
      '重症肺炎诊断标准': '需要无创通气',
      'qSOFA': 1,
      'SOFA': 6,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1028',
      '就诊次数': 1,
      '住院日期': '2024-08-30',
      '性别': '女',
      '年龄': 27,
      'CURB-65评分': 0,
      'PSI评分': 38,
      'CPIS评分': 0,
      '重症肺炎诊断标准': '门诊随访',
      'qSOFA': 0,
      'SOFA': 0,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '否',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1029',
      '就诊次数': 1,
      '住院日期': '2024-09-01',
      '性别': '男',
      '年龄': 80,
      'CURB-65评分': 4,
      'PSI评分': 140,
      'CPIS评分': 7,
      '重症肺炎诊断标准': '休克+机械通气，ICU',
      'qSOFA': 3,
      'SOFA': 13,
      'COVID-19': '否',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '是',
      '是否属于患者呼吸道症候群': '否',
    },
    {
      '患者ID': 'P1030',
      '就诊次数': 2,
      '住院日期': '2024-09-03',
      '性别': '女',
      '年龄': 56,
      'CURB-65评分': 1,
      'PSI评分': 78,
      'CPIS评分': 3,
      '重症肺炎诊断标准': '多叶受累，需氧气支持',
      'qSOFA': 1,
      'SOFA': 3,
      'COVID-19': '是',
      '是否获社区获得性肺炎': '是',
      '是否获脓毒症': '否',
      '是否属于患者呼吸道症候群': '否',
    },
  ];

  const fetchData = async (params: any = { current: 1, pageSize: 10 }) => {
    setLoading(true);
    try {
      setTimeout(() => {
        const start = (params.current - 1) * params.pageSize;
        const end = start + params.pageSize;
        const list = mockData.slice(start, end);
        renderData.value = list;
        pagination.current = params.current;
        pagination.total = mockData.length;
        setLoading(false);
      }, 200);
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

  const editRecord = (record: PatientScoreRecord) => {
    Message.info(`编辑记录: ${record.患者ID}`);
  };

  const deleteRecord = (record: PatientScoreRecord) => {
    Message.success(`删除记录: ${record.患者ID}`);
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
