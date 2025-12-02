<template>
  <div class="container">
    <Breadcrumb
      :items="['menu.patient', 'menu.patient.pathologyTable.detail']"
    />
    <a-card
      class="general-card"
      :title="$t('menu.patient.pathologyTable.detail')"
    >
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
              导入患者
            </a-button>
            <a-button type="outline" @click="showDetailImport = true">
              <template #icon><icon-upload /></template>
              导入患者详情
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
            <!-- <a-button type="text" size="small" @click="viewRecord(record)">查看</a-button> -->
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
              >patients_0601.xlsx</td
            >
            <td style="border: 1px solid #e5e6eb; padding: 6px">病理信息</td>
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
    <!-- 导入患者详情弹窗 -->
    <a-modal
      v-model:visible="showDetailImport"
      title="批量导入患者详情"
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

  // 定义患者管理数据类型
  interface PatientRecord {
    '患者ID': string;
    '就诊次数': number;
    '住院日期': string;
    '性别': string;
    '年龄': number;
    '主诉': string;
    '现病史': string;
    '查体': string;
    '动脉血气pH': number;
    '动脉血气pO2(mmHg)': number;
    '动脉血气氧合指数(mmHg)': number;
    '动脉血气pCO2(mmHg)': number;
    '血常规血小板计数(×10^9/L)': number;
    '血尿素氮(mmol/L)': number;
    '血肌酐(μmol/L)': number;
    '总胆红素(μmol/L)': number;
    '是否开具胸部CT': string;
    '胸部CT报告': string;
    '是否应用多巴胺': string;
    '是否应用多巴酚丁胺': string;
    '是否应用去甲肾上腺素': string;
    '是否应用血管活性药物': string;
    '是否应用特殊级/限制级抗生素': string;
    '抗生素种类': string;
    '是否应用呼吸机': string;
    '是否入住ICU': string;
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

  const renderData = ref<PatientRecord[]>([]);
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

  // 患者数据表格列定义
  const columns = computed<TableColumnData[]>(() => [
    {
      title: '患者ID',
      dataIndex: '患者ID',
      width: 100,
      align: 'center',
    },
    { title: '就诊次数', dataIndex: '就诊次数', width: 80, align: 'center' },
    { title: '住院日期', dataIndex: '住院日期', width: 120, align: 'center' },
    { title: '性别', dataIndex: '性别', width: 60, align: 'center' },
    { title: '年龄', dataIndex: '年龄', width: 60, align: 'center' },
    { title: '主诉', dataIndex: '主诉', width: 150, align: 'center' },
    { title: '现病史', dataIndex: '现病史', width: 200, align: 'center' },
    { title: '查体', dataIndex: '查体', width: 200, align: 'center' },
    {
      title: '动脉血气pH',
      dataIndex: '动脉血气pH',
      width: 100,
      align: 'center',
    },
    {
      title: '动脉血气pO2(mmHg)',
      dataIndex: '动脉血气pO2(mmHg)',
      width: 130,
      align: 'center',
    },
    {
      title: '动脉血气氧合指数(mmHg)',
      dataIndex: '动脉血气氧合指数(mmHg)',
      width: 150,
      align: 'center',
    },
    {
      title: '动脉血气pCO2(mmHg)',
      dataIndex: '动脉血气pCO2(mmHg)',
      width: 130,
      align: 'center',
    },
    {
      title: '血常规血小板计数(×10^9/L)',
      dataIndex: '血常规血小板计数(×10^9/L)',
      width: 150,
      align: 'center',
    },
    {
      title: '血尿素氮(mmol/L)',
      dataIndex: '血尿素氮(mmol/L)',
      width: 130,
      align: 'center',
    },
    {
      title: '血肌酐(μmol/L)',
      dataIndex: '血肌酐(μmol/L)',
      width: 130,
      align: 'center',
    },
    {
      title: '总胆红素(μmol/L)',
      dataIndex: '总胆红素(μmol/L)',
      width: 130,
      align: 'center',
    },
    {
      title: '是否开具胸部CT',
      dataIndex: '是否开具胸部CT',
      width: 120,
      align: 'center',
    },
    {
      title: '胸部CT报告',
      dataIndex: '胸部CT报告',
      width: 200,
      align: 'center',
    },
    {
      title: '是否应用多巴胺',
      dataIndex: '是否应用多巴胺',
      width: 120,
      align: 'center',
    },
    {
      title: '是否应用多巴酚丁胺',
      dataIndex: '是否应用多巴酚丁胺',
      width: 130,
      align: 'center',
    },
    {
      title: '是否应用去甲肾上腺素',
      dataIndex: '是否应用去甲肾上腺素',
      width: 140,
      align: 'center',
    },
    {
      title: '是否应用血管活性药物',
      dataIndex: '是否应用血管活性药物',
      width: 140,
      align: 'center',
    },
    {
      title: '是否应用特殊级/限制级抗生素',
      dataIndex: '是否应用特殊级/限制级抗生素',
      width: 160,
      align: 'center',
    },
    {
      title: '抗生素种类',
      dataIndex: '抗生素种类',
      width: 120,
      align: 'center',
    },
    {
      title: '是否应用呼吸机',
      dataIndex: '是否应用呼吸机',
      width: 120,
      align: 'center',
    },
    {
      title: '是否入住ICU',
      dataIndex: '是否入住ICU',
      width: 120,
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

  // Mock 数据
  const mockData = [
    {
      '患者ID': 'P0001',
      '就诊次数': 3,
      '住院日期': '2025-01-25',
      '性别': '女',
      '年龄': 50,
      '主诉': '发热、咳嗽、胸闷10天',
      '现病史':
        '患者受凉后出现发热、咳嗽、胸闷10，自行服药治疗，症状未见好转，症状加重来我院就诊。患者自发病以来，精神可，食欲尚可，睡眠一般，大小便正常。',
      '查体':
        '神志清楚，精神可，查体合作。T 37.8℃，P 84次/分，R 24次/分，BP 119/75mmHg，SpO2 97%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，未闻及干湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.43,
      '动脉血气pO2(mmHg)': 83.6,
      '动脉血气氧合指数(mmHg)': 398.1,
      '动脉血气pCO2(mmHg)': 36.9,
      '血常规血小板计数(×10^9/L)': 265,
      '血尿素氮(mmol/L)': 6.11,
      '血肌酐(μmol/L)': 99.7,
      '总胆红素(μmol/L)': 7.3,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫检查。

  【影像所见】双肺纹理增多，左肺下叶见斑片状影。双肺未见胸腔积液。气管、支气管通畅。

  【诊断意见】左下肺炎症改变。建议结合临床。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '否',
      '抗生素种类': '头孢呋辛',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
    },
    {
      '患者ID': 'P0002',
      '就诊次数': 1,
      '住院日期': '2024-10-02',
      '性别': '女',
      '年龄': 73,
      '主诉': '咳嗽伴发热10天',
      '现病史':
        '患者受凉后出现咳嗽伴发热10，曾于外院就诊，予抗生素治疗，效果欠佳，症状加重来我院就诊。患者自发病以来，精神可，食欲尚可，睡眠一般，大小便正常。',
      '查体':
        '神志清楚，精神萎靡，查体合作。T 38.6℃，P 109次/分，R 28次/分，BP 103/69mmHg，SpO2 92%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，可闻及湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.37,
      '动脉血气pO2(mmHg)': 63.6,
      '动脉血气氧合指数(mmHg)': 219.3,
      '动脉血气pCO2(mmHg)': 42.5,
      '血常规血小板计数(×10^9/L)': 210,
      '血尿素氮(mmol/L)': 8.17,
      '血肌酐(μmol/L)': 100.1,
      '总胆红素(μmol/L)': 19.4,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫检查。

  【影像所见】双肺纹理明显增多，两肺中下叶见大片状实变影，可见支气管充气征。双侧胸膜轻度增厚。

  【诊断意见】两肺多叶段肺炎。建议密切观察，短期复查。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '是',
      '抗生素种类': '头孢哌酮舒巴坦+阿奇霉素',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
    },
    {
      '患者ID': 'P0003',
      '就诊次数': 3,
      '住院日期': '2024-07-27',
      '性别': '女',
      '年龄': 24,
      '主诉': '咳嗽、咳痰伴发热8天',
      '现病史':
        '患者劳累后出现咳嗽、咳痰伴发热8，未予特殊处理，症状加重来我院就诊。患者自发病以来，精神尚可，食欲尚可，睡眠一般，大小便正常。',
      '查体':
        '神志清楚，精神可，查体合作。T 38.5℃，P 99次/分，R 19次/分，BP 132/87mmHg，SpO2 97%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，未闻及干湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.42,
      '动脉血气pO2(mmHg)': 82.8,
      '动脉血气氧合指数(mmHg)': 394.3,
      '动脉血气pCO2(mmHg)': 35.1,
      '血常规血小板计数(×10^9/L)': 232,
      '血尿素氮(mmol/L)': 4.5,
      '血肌酐(μmol/L)': 62.9,
      '总胆红素(μmol/L)': 8.3,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫检查。

  【影像所见】双肺纹理增多，左肺下叶见斑片状影。双肺未见胸腔积液。气管、支气管通畅。

  【诊断意见】左下肺炎症改变。建议结合临床。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '否',
      '抗生素种类': '',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
    },
    {
      '患者ID': 'P0004',
      '就诊次数': 3,
      '住院日期': '2024-11-23',
      '性别': '男',
      '年龄': 24,
      '主诉': '气促、呼吸困难5天',
      '现病史':
        '患者劳累后出现气促、呼吸困难5，曾于外院就诊，予抗生素治疗，效果欠佳，症状加重来我院就诊。患者自发病以来，精神差，纳差，睡眠一般，大小便正常。',
      '查体':
        '嗜睡状态，查体欠合作。T 39.8℃，P 119次/分，R 30次/分，BP 90/57mmHg，SpO2 85%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，双肺可闻及密集湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.35,
      '动脉血气pO2(mmHg)': 56.5,
      '动脉血气氧合指数(mmHg)': 161.4,
      '动脉血气pCO2(mmHg)': 52.5,
      '血常规血小板计数(×10^9/L)': 85,
      '血尿素氮(mmol/L)': 13.16,
      '血肌酐(μmol/L)': 157.4,
      '总胆红素(μmol/L)': 33.9,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫+增强检查。

  【影像所见】双肺弥漫性病变，多发大片状密度增高影，病变融合成片。双侧胸膜增厚，双侧胸腔积液。

  【诊断意见】双肺弥漫性病变，考虑重症肺炎，伴双侧胸腔积液。建议ICU监护。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '是',
      '抗生素种类': '亚胺培南西司他丁+万古霉素',
      '是否应用呼吸机': '是',
      '是否入住ICU': '是',
    },
    {
      '患者ID': 'P0005',
      '就诊次数': 1,
      '住院日期': '2024-12-08',
      '性别': '女',
      '年龄': 50,
      '主诉': '咳嗽、咳痰伴喘息8天',
      '现病史':
        '患者劳累后出现咳嗽、咳痰伴喘息8，自行服药治疗，症状未见好转，症状加重来我院就诊。患者自发病以来，精神尚可，纳差，睡眠一般，大小便正常。',
      '查体':
        '神志清楚，精神尚可，查体合作。T 37.6℃，P 88次/分，R 20次/分，BP 115/76mmHg，SpO2 99%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，可闻及少许干啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.44,
      '动脉血气pO2(mmHg)': 92.3,
      '动脉血气氧合指数(mmHg)': 439.5,
      '动脉血气pCO2(mmHg)': 36.1,
      '血常规血小板计数(×10^9/L)': 347,
      '血尿素氮(mmol/L)': 3.52,
      '血肌酐(μmol/L)': 65.4,
      '总胆红素(μmol/L)': 6.5,
      '是否开具胸部CT': '否',
      '胸部CT报告': ``,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '否',
      '抗生素种类': '头孢克肟',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
    },
    {
      '患者ID': 'P0006',
      '就诊次数': 1,
      '住院日期': '2025-01-20',
      '性别': '男',
      '年龄': 73,
      '主诉': '发热、咳嗽、胸闷7天',
      '现病史':
        '患者劳累后出现发热、咳嗽、胸闷7，未予特殊处理，症状加重来我院就诊。患者自发病以来，精神可，食欲尚可，睡眠一般，大小便正常。',
      '查体':
        '神志清楚，查体合作。T 38.2℃，P 91次/分，R 19次/分，BP 121/84mmHg，SpO2 99%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，未闻及干湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.42,
      '动脉血气pO2(mmHg)': 93.9,
      '动脉血气氧合指数(mmHg)': 447.1,
      '动脉血气pCO2(mmHg)': 35.3,
      '血常规血小板计数(×10^9/L)': 182,
      '血尿素氮(mmol/L)': 4.89,
      '血肌酐(μmol/L)': 97.7,
      '总胆红素(μmol/L)': 9.9,
      '是否开具胸部CT': '否',
      '胸部CT报告': ``,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '否',
      '抗生素种类': '左氧氟沙星',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
    },
    {
      '患者ID': 'P0007',
      '就诊次数': 3,
      '住院日期': '2024-11-28',
      '性别': '女',
      '年龄': 24,
      '主诉': '咳嗽、咳痰伴喘息8天',
      '现病史':
        '患者劳累后出现咳嗽、咳痰伴喘息8，曾于外院就诊，予抗生素治疗，效果欠佳，症状加重来我院就诊。患者自发病以来，精神可，食欲差，睡眠一般，大小便正常。',
      '查体':
        '意识障碍，查体欠合作。T 39.0℃，P 111次/分，R 34次/分，BP 106/59mmHg，SpO2 92%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，双肺可闻及密集湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.31,
      '动脉血气pO2(mmHg)': 54.6,
      '动脉血气氧合指数(mmHg)': 188.3,
      '动脉血气pCO2(mmHg)': 45.2,
      '血常规血小板计数(×10^9/L)': 80,
      '血尿素氮(mmol/L)': 10.39,
      '血肌酐(μmol/L)': 127.1,
      '总胆红素(μmol/L)': 48.4,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫检查。

  【影像所见】双肺透亮度明显减低，双肺多发大片状实变影，累及双肺各叶。双侧胸腔见中等量积液。纵隔多发肿大淋巴结。

  【诊断意见】双肺弥漫性病变，考虑重症肺炎，伴双侧胸腔积液。建议ICU监护。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '是',
      '抗生素种类': '亚胺培南西司他丁',
      '是否应用呼吸机': '是',
      '是否入住ICU': '是',
    },
    {
      '患者ID': 'P0008',
      '就诊次数': 2,
      '住院日期': '2024-11-21',
      '性别': '女',
      '年龄': 50,
      '主诉': '发热、气促1天',
      '现病史':
        '患者受凉后出现发热、气促1，自行服药治疗，症状未见好转，症状加重来我院就诊。患者自发病以来，精神尚可，食欲尚可，睡眠一般，大小便正常。',
      '查体':
        '嗜睡状态，查体欠合作。T 38.5℃，P 144次/分，R 36次/分，BP 96/68mmHg，SpO2 92%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，双肺可闻及密集湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.3,
      '动脉血气pO2(mmHg)': 57.1,
      '动脉血气氧合指数(mmHg)': 196.9,
      '动脉血气pCO2(mmHg)': 56,
      '血常规血小板计数(×10^9/L)': 116,
      '血尿素氮(mmol/L)': 7.98,
      '血肌酐(μmol/L)': 194.8,
      '总胆红素(μmol/L)': 25.6,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫+增强检查。

  【影像所见】双肺弥漫性病变，多发大片状密度增高影，病变融合成片。双侧胸膜增厚，双侧胸腔积液。

  【诊断意见】双肺广泛实变，符合重症肺部感染。病情危重，建议积极治疗。`,
      '是否应用多巴胺': '是',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '是',
      '是否应用特殊级/限制级抗生素': '是',
      '抗生素种类': '亚胺培南西司他丁+万古霉素',
      '是否应用呼吸机': '是',
      '是否入住ICU': '是',
    },
    {
      '患者ID': 'P0009',
      '就诊次数': 3,
      '住院日期': '2024-11-09',
      '性别': '男',
      '年龄': 24,
      '主诉': '咳嗽、呼吸困难8天',
      '现病史':
        '患者受凉后出现咳嗽、呼吸困难8，自行服药治疗，症状未见好转，症状加重来我院就诊。患者自发病以来，精神尚可，纳差，睡眠一般，大小便正常。',
      '查体':
        '嗜睡状态，查体欠合作。T 39.0℃，P 125次/分，R 26次/分，BP 128/75mmHg，SpO2 95%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，可闻及散在干啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.41,
      '动脉血气pO2(mmHg)': 77.1,
      '动脉血气氧合指数(mmHg)': 367.1,
      '动脉血气pCO2(mmHg)': 41.8,
      '血常规血小板计数(×10^9/L)': 214,
      '血尿素氮(mmol/L)': 6.82,
      '血肌酐(μmol/L)': 89.9,
      '总胆红素(μmol/L)': 26.6,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫检查。

  【影像所见】双肺透亮度减低，双肺下叶及右肺中叶见多发斑片状高密度影，部分融合。纵隔内见肿大淋巴结。

  【诊断意见】双肺多发感染性病变，考虑肺炎。建议抗感染治疗。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '否',
      '抗生素种类': '头孢曲松',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
    },
    {
      '患者ID': 'P0010',
      '就诊次数': 3,
      '住院日期': '2024-10-16',
      '性别': '男',
      '年龄': 73,
      '主诉': '咳嗽伴发热8天',
      '现病史':
        '患者劳累后出现咳嗽伴发热8，自行服药治疗，症状未见好转，症状加重来我院就诊。患者自发病以来，精神差，食欲差，睡眠一般，大小便正常。',
      '查体':
        '神志清楚，精神可，查体合作。T 38.4℃，P 84次/分，R 23次/分，BP 127/73mmHg，SpO2 97%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，未闻及干湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
      '动脉血气pH': 7.42,
      '动脉血气pO2(mmHg)': 77.5,
      '动脉血气氧合指数(mmHg)': 369,
      '动脉血气pCO2(mmHg)': 41.8,
      '血常规血小板计数(×10^9/L)': 279,
      '血尿素氮(mmol/L)': 6.2,
      '血肌酐(μmol/L)': 86.2,
      '总胆红素(μmol/L)': 6.8,
      '是否开具胸部CT': '是',
      '胸部CT报告': `【检查方法】行胸部CT平扫检查。

  【影像所见】双肺纹理增多，左肺下叶见斑片状影。双肺未见胸腔积液。气管、支气管通畅。

  【诊断意见】右下肺感染性病变，考虑肺炎。`,
      '是否应用多巴胺': '否',
      '是否应用多巴酚丁胺': '否',
      '是否应用去甲肾上腺素': '否',
      '是否应用血管活性药物': '否',
      '是否应用特殊级/限制级抗生素': '否',
      '抗生素种类': '头孢呋辛',
      '是否应用呼吸机': '否',
      '是否入住ICU': '否',
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

  // 操作函数
  // const viewRecord = (record: ScholarRecord) => {
  //   Message.info(`查看记录: ${record.name}`);
  // };

  const editRecord = (record: PatientRecord) => {
    Message.info(`编辑记录: ${record.患者ID}`);
  };

  const deleteRecord = (record: PatientRecord) => {
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
        // 默认为所有列启用省略与悬浮提示
        // @ts-ignore
        item.ellipsis = true;
        // @ts-ignore
        item.tooltip = true;
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
    { label: '论文管理', value: 'paper' },
    { label: '著作管理', value: 'monograph' },
    { label: '专利管理', value: 'patent' },
    { label: '案例管理', value: 'case' },
  ];

  function importPatient() {
    document.getElementById('import-patient-input')?.click();
  }
  function handlePatientFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入患者文件', files);
    }
  }
  function handleDetailImport() {
    document.getElementById('import-detail-input')?.click();
    showDetailImport.value = false;
  }
  function handleDetailFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入患者详情文件', files, searchNo.value, importType.value);
    }
  }

  const importRecords = ref([
    {
      name: 'scholars_paper_0601.xlsx',
      category: '论文管理',
      serial: 'LSH20240601001',
      importDate: '2024-06-01 10:00',
      updateDate: '2024-06-01 12:00',
      updateUser: '张三',
    },
    {
      name: 'monograph_0602.xlsx',
      category: '著作管理',
      serial: 'LSH20240602001',
      importDate: '2024-06-02 14:30',
      updateDate: '2024-06-02 15:00',
      updateUser: '李四',
    },
    {
      name: 'patent_0603.xlsx',
      category: '专利管理',
      serial: 'LSH20240603001',
      importDate: '2024-06-03 09:15',
      updateDate: '2024-06-03 10:00',
      updateUser: '王五',
    },
  ]);
  function download({ name }: { name: string }) {
    Message.info(`下载示例：${name}`);
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
