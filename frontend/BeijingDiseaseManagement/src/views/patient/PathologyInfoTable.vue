<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patient.pathologyTable']" />
    <a-card class="general-card" :title="$t('menu.patient.pathologyTable')">
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
                <a-form-item field="hospitalNo" label="住院流水号">
                  <a-input
                    v-model="formModel.hospitalNo"
                    placeholder="请输入住院流水号"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="name" label="姓名">
                  <a-input v-model="formModel.name" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="pathologyNo" label="病理号">
                  <a-input
                    v-model="formModel.pathologyNo"
                    placeholder="请输入病理号"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="reportTime" label="报告日期">
                  <a-range-picker
                    v-model="formModel.reportTime"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="diagnosis" label="诊断">
                  <a-input
                    v-model="formModel.diagnosis"
                    placeholder="请输入诊断"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item field="pathologyDiagnosis" label="病理诊断">
                  <a-input
                    v-model="formModel.pathologyDiagnosis"
                    placeholder="请输入病理诊断"
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
              导入病理
            </a-button>
            <a-button type="outline" @click="showDetailImport = true">
              <template #icon><icon-upload /></template>
              导入病理详情
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
              >pathology_0601.xlsx</td
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
    <!-- 导入病理详情弹窗 -->
    <a-modal
      v-model:visible="showDetailImport"
      title="批量导入病理详情"
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
  import type { SelectOptionData } from '@arco-design/web-vue/es/select/interface';
  import type { TableColumnData } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';
  import { Message } from '@arco-design/web-vue';
  import getPathologyData from './pathology-mock';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  // 定义病理数据类型
  interface PathologyRecord {
    id: number;
    hospitalNo: string;
    name: string;
    idCard: string;
    occupation: string;
    bloodType: string;
    tStage: string;
    nStage: string;
    mStage: string;
    tnmStage: string;
    clinicalStage: string;
    address: string;
    phone: string;
    workUnit: string;
    diagnosis: string;
    hospitalDiagnosis: string;
    pathologyDiagnosis: string;
    personalHistory: string;
    maritalHistory: string;
    menstrualHistory: string;
    fertilityHistory: string;
    pathologyNo: string;
    reportTime: string;
    grossAppearance: string;
    detailedPathologyDiagnosis: string;
    reportDoctor: string;
    reviewDoctor: string;
  }

  const generateFormModel = () => {
    return {
      hospitalNo: '',
      name: '',
      pathologyNo: '',
      reportTime: [],
      diagnosis: '',
      pathologyDiagnosis: '',
    };
  };

  const { loading, setLoading } = useLoading(true);

  const renderData = ref<PathologyRecord[]>([]);
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
      title: '住院流水号',
      dataIndex: 'hospitalNo',
      width: 120,
      align: 'center',
    },
    { title: '姓名', dataIndex: 'name', width: 80, align: 'center' },
    { title: '身份证号', dataIndex: 'idCard', width: 120, align: 'center' },
    { title: '职业', dataIndex: 'occupation', width: 80, align: 'center' },
    { title: '血型', dataIndex: 'bloodType', width: 60, align: 'center' },
    { title: 'T', dataIndex: 'tStage', width: 50, align: 'center' },
    { title: 'N', dataIndex: 'nStage', width: 50, align: 'center' },
    { title: 'M', dataIndex: 'mStage', width: 50, align: 'center' },
    { title: 'TNM分期', dataIndex: 'tnmStage', width: 80, align: 'center' },
    {
      title: '临床分期',
      dataIndex: 'clinicalStage',
      width: 80,
      align: 'center',
    },
    {
      title: '住址',
      dataIndex: 'address',
      width: 150,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    { title: '电话', dataIndex: 'phone', width: 100, align: 'center' },
    {
      title: '工作单位',
      dataIndex: 'workUnit',
      width: 120,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    { title: '诊断', dataIndex: 'diagnosis', width: 120, align: 'center' },
    {
      title: '住院诊断',
      dataIndex: 'hospitalDiagnosis',
      width: 120,
      align: 'center',
    },
    {
      title: '病理诊断',
      dataIndex: 'pathologyDiagnosis',
      width: 120,
      align: 'center',
    },
    {
      title: '个人史',
      dataIndex: 'personalHistory',
      width: 150,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '婚育史',
      dataIndex: 'maritalHistory',
      width: 100,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '月经史',
      dataIndex: 'menstrualHistory',
      width: 100,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '生育史',
      dataIndex: 'fertilityHistory',
      width: 100,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    { title: '病理号', dataIndex: 'pathologyNo', width: 100, align: 'center' },
    { title: '报告日期', dataIndex: 'reportTime', width: 120, align: 'center' },
    {
      title: '肉眼所见',
      dataIndex: 'grossAppearance',
      width: 200,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '病理诊断',
      dataIndex: 'detailedPathologyDiagnosis',
      width: 200,
      align: 'center',
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '报告医生',
      dataIndex: 'reportDoctor',
      width: 100,
      align: 'center',
    },
    {
      title: '审核医生',
      dataIndex: 'reviewDoctor',
      width: 100,
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
      id: 1,
      hospitalNo: 'ZY240001272777',
      name: '朱*兰',
      pathologyNo: '2019-03959',
      reportDate: '2019/4/17 11:18',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"回盲部升结肠乙状结肠直肠及肿瘤"肠管切除标本两段，乙状结肠及直肠长25cm，一切缘周径4cm，另一切缘周径6cm，距一切缘8cm，另一切缘7cm处见一肿块，肿块大小7x5x4cm，肿块切面灰白灰黄实性质中，肿块侵犯浆膜层，并与第二段肠管回盲部粘连。升结肠及回盲部肠管长20cm，一切缘周径2cm，另一切缘周径4cm，距一切缘3cm，另一切缘15cm升结肠处见一溃疡区域，范围2x1.5cm；距一切缘8cm，另一切缘5cm回盲部与第一段肠管乙状结肠肠壁粘连，浆膜面受肿块侵犯；另触及阑尾一根，长3cm，直径0.5cm，肉眼见肿瘤侵犯。另触及肠系膜淋巴结数枚，另送"上切缘"带吻合器、"下切缘"、"肠系膜下动脉根部淋巴结"、"右输卵管表面结节"、"右侧圆韧带及卵巢肿瘤"灰褐组织一块，大小4x4x3cm，疑似卵巢大小2x1x1cm，疑似肿块大小3x2x1cm，切面灰白灰黄实性质中，疑似圆韧带大小4x2x1cm。',
      microscopicDescription:
        '<"回盲部升结肠乙状结肠直肠及肿瘤">两个肿块：腺癌，中-低分化，癌组织浸润肠壁全层达周围脂肪组织，局部累及神经，于镜下未见明确脉管内癌栓；标本两切缘及另送"上切缘、下切缘"于镜下未见癌组织；肠系膜淋巴结（0/13）于镜下未见癌转移，见肿瘤沉积结节两枚；"肠系膜下动脉根部淋巴结"（0/2）于镜下未见癌转移；另送"右输卵管表面结节"、"右侧圆韧带及卵巢肿瘤"慢性炎伴间质纤维化，未见癌组织，并见少许卵巢及输卵管组织。补充取材：阑尾：慢性炎，局部浆膜层见癌组织。  Vg：纤维组织阳性',
      immunohistochemistry: 'Vg：纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '胡红艳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/4/17',
    },
    {
      id: 2,
      hospitalNo: 'ZY040001393823',
      name: '蒋*昌',
      pathologyNo: '',
      reportDate: '',
      diagnosis: '转移性腺癌',
      tissueType: '结肠组织',
      grossDescription: '',
      microscopicDescription: '',
      immunohistochemistry: 'Vg：纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '',
    },
    {
      id: 3,
      hospitalNo: 'ZY030001263603',
      name: '郭*华',
      pathologyNo: '2018-16459',
      reportDate: '2018/12/29 8:19',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"右半结肠及肿瘤"肠管切除标本一段，回肠长12cm，结肠长21cm，回肠切缘周径4cm，结肠切缘周径7cm，距结肠切缘10cm回盲瓣7cm处见一溃疡性肿块，大小5x4x2cm，切面灰白实性质中。于回盲瓣见阑尾一根，长约5.5cm，直径0.5cm。附网膜组织一堆，总积17x13x10cm，未触及明显结节及淋巴结。肠系膜触及淋巴结数枚。',
      microscopicDescription:
        '<"右半结肠及肿瘤">腺癌,中-高分化，癌组织浸润至肠壁全层；标本回肠切缘、结肠切缘、环周切缘、网膜组织未见癌组织；肠系膜动脉根部淋巴结(0/1)、回肠系膜淋巴结(0/9)、结肠系膜淋巴结(0/13)未见癌转移。  Vg:胶原纤维+。',
      immunohistochemistry: 'Vg：胶原纤维阳性',
      molecularPathology: '待检测',
      pathologist: '张宗梅',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2018/12/29',
    },
    {
      id: 4,
      hospitalNo: 'ZY030001272623',
      name: '郭*妃',
      pathologyNo: '2019-04227',
      reportDate: '2019/4/22 9:46',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"右半结肠及肿瘤"肠管切除标本一段，回肠长3cm，结肠长21cm，回肠切缘周径3cm，结肠切缘周径4cm，距回盲瓣13cm，结肠切缘5cm处见一溃疡型肿块，肿块大小3x2x1.5cm，肿块切面灰白实性质中，回盲部见阑尾一根，长6cm，直径0.5cm，附网膜组织一堆，总积6x5x4cm，未触及明显结节及淋巴结，肠系膜触及淋巴结数枚，另送袋装淋巴结一组。',
      microscopicDescription:
        '<"右半结肠及肿瘤">腺癌，中分化，癌组织浸润肠壁固有层肌全层达肠周脂肪组织；回肠切缘、结肠切缘、环周切缘、阑尾、网膜组织未见癌组织；肠系膜淋巴结（0/17)、"结肠中动脉根部淋巴结"（0/1）未见癌转移。  Vg：纤维组织阳性  Ag：银纤维阳性',
      immunohistochemistry: 'Vg：纤维组织阳性  Ag：银纤维阳性',
      molecularPathology: '待检测',
      pathologist: '张宗梅',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/4/22',
    },
    {
      id: 5,
      hospitalNo: 'ZY060001267185',
      name: '罗*秦',
      pathologyNo: '2019-01335',
      reportDate: '2019/2/18 10:31',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"左半结肠"肠管切除标本一段，长10cm，一切缘周径2cm，另一切缘周径3cm，距一切缘5cm，另一切缘1cm处见一溃疡隆起型肿块，肿块大小2x2x0.7cm，切面灰白灰红实性质中，肠系膜触及淋巴结数枚，另送"上切缘"带吻合器，"下切缘"。',
      microscopicDescription:
        '<"左半结肠">腺癌，中分化，癌组织浸润肠壁固有肌层全层达浆膜下脂肪组织。标本一切缘、另一切缘、环周切缘、"上切缘"、"下切缘"于镜下未见癌组织。肠系膜淋巴结（1/8）于镜下见癌转移。  VG:纤维组织阳性。',
      immunohistochemistry: 'VG:纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '郭姜艳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/2/18',
    },
    {
      id: 6,
      hospitalNo: 'ZY040001428265',
      name: '杜*云',
      pathologyNo: '2019-04482',
      reportDate: '2019/4/26 10:40',
      diagnosis: '转移性腺癌',
      tissueType: '宫颈组织',
      grossDescription:
        '"宫颈"带针头宫颈锥切标本一个，大小1.5x1.5x1cm，另送"颈管"灰褐组织一块，大小2x1.5x0.3cm，"颈管搔刮"灰褐胶冻样碎组织一堆，总积1x1x0.5cm。',
      microscopicDescription:
        '<"宫颈">宫颈各点呈慢性活动性炎改变，伴部分腺体鳞化，个别点灶区CINI。标本锥顶及锥底各切缘于镜下未见CIN病变。另送"颈管"慢性炎。"颈管搔刮物"镜下示黏液渗出物及破碎颈管腺体。  VG:纤维组织阳性',
      immunohistochemistry: 'VG:纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '郭姜艳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/4/26',
    },
    {
      id: 7,
      hospitalNo: 'ZY170001276969',
      name: '施*武',
      pathologyNo: '2019-06899',
      reportDate: '2019/6/17 9:59',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"左半结肠及肿瘤"肠管切除标本一段，长25cm，一切缘周径6cm，，另一切缘周径5cm，距一切缘5cm，另一切缘8cm处见一肿块，大小3x2x1cm。肿块切面灰白，实性，质中，肠系膜触及淋巴结数枚，附网膜组织一堆，总积9x6x2cm，其间未触及明显结节。另送"肠系膜下血管"灰黄组织两块，总积2x2x0.6cm。',
      microscopicDescription:
        '<"左半结肠及肿瘤">腺癌，中分化，癌组织浸润肠壁全层达周围脂肪组织，可见脉管内癌栓；标本两切缘、环周切缘、网膜组织于镜下未见癌组织；肠系膜LN（2/12）见癌转移；肠系膜动脉根部淋LN（0/1）、"肠系膜下血管"（0/5）于镜下未见癌转移。  Vg：纤维组织阳性',
      immunohistochemistry: 'Vg：纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '袁婧',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/6/17',
    },
    {
      id: 8,
      hospitalNo: 'ZY020001269664',
      name: '李*花',
      pathologyNo: '2019-02261',
      reportDate: '2019/3/12 14:30',
      diagnosis: '腺癌',
      tissueType: '乳腺组织',
      grossDescription: '"右乳肿块"灰白灰黄灰褐碎组织一堆，总积4x3x2cm。',
      microscopicDescription:
        '<"右乳肿块">肉芽肿性小叶性乳腺炎。  VG：胶原纤维阳性。',
      immunohistochemistry: 'VG：胶原纤维阳性',
      molecularPathology: '待检测',
      pathologist: '吴琳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/3/12',
    },
    {
      id: 9,
      hospitalNo: 'ZY030001277749',
      name: '和*华',
      pathologyNo: '2019-06999',
      reportDate: '2019/6/19 9:58',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"右半结肠及肿瘤"肠管切除标本一段，长11cm，一端切缘周径5cm，另一端切缘周径4cm，距一端切缘7.5cm，另一端切缘3cm处见隆起型肿块，大小3x3x1.5cm。切面灰白，实性，质中。另见阑尾一条，长3cm，直径0.5cm。肠系膜触及淋巴结数枚。另送"小肠切缘"、"结肠切缘"。',
      microscopicDescription:
        '<"右半结肠及肿瘤">腺癌，中分化，癌组织浸润肠壁全层；标本两切缘、环周切缘、"小肠切缘"、"结肠切缘"于镜下未见癌组织；肠系膜淋巴结（0/21）于镜下未见癌转移；阑尾：慢性炎。  Vg：纤维组织阳性  Ag：银纤维阳性',
      immunohistochemistry: 'Vg：纤维组织阳性  Ag：银纤维阳性',
      molecularPathology: '待检测',
      pathologist: '袁婧',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/6/19',
    },
    {
      id: 10,
      hospitalNo: 'ZY030001270459',
      name: '郭*贤',
      pathologyNo: '2019-03414',
      reportDate: '2019/4/3 17:33',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"直肠及乙状结肠及肿瘤"肠管切除标本一段，长20cm，一切缘周径9cm，另一切缘周径6cm，距一切缘11cm，另一切缘处见一溃疡隆起型肿块1，肿块大小5.5x5x1cm，切面灰白实性质中，距肿块1（0.3cm）处可见肿块2，肿块2大小2x2x0.8cm，切面灰白实性质中，肠系膜触及淋巴结数枚。',
      microscopicDescription:
        '<"直肠及乙状结肠及肿瘤">腺癌，中分化，癌组织浸润肠壁浅肌层。标本两端切缘于镜下未见癌组织。肠系膜周围淋巴结（0/16）于镜下未见癌转移。  VG:纤维组织阳性。',
      immunohistochemistry: 'VG:纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '郭姜艳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/4/3',
    },
    {
      id: 11,
      hospitalNo: 'ZY020001281768',
      name: '李*芬',
      pathologyNo: '2019-08890',
      reportDate: '2019/7/26 12:32',
      diagnosis: '腺癌',
      tissueType: '肺组织',
      grossDescription:
        '"右肺上叶结节"肺叶切除标本一叶，大小12x7.5x3cm。于肺门处见支气管残端，长0.5cm。于另一肺表面见两个残腔，较大者2x1x1cm，内见一稍灰白区域，1x0.8cm。小者1x1x0.6cm。另送袋装淋巴结2组。',
      microscopicDescription:
        '<"右肺上叶结节">灰白区域全：见少许浸润性癌，乳头型为主；标本较大残腔壁、较小残腔壁、支气管残端于镜下未见癌组织；"2+4组淋巴结"（0/3）、"11组淋巴结"（0/1）于镜下未见癌转移。  Vg:纤维组织阳性。',
      immunohistochemistry: 'Vg:纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '袁婧',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/7/26',
    },
    {
      id: 12,
      hospitalNo: 'ZY020001265048',
      name: '范*琳',
      pathologyNo: '2019-00489',
      reportDate: '2019/1/17 11:40',
      diagnosis: '浸润性尿路上皮癌',
      tissueType: '肾组织',
      grossDescription:
        '"右肾及右输尿管全长"肾切除标本一个，大小9x5.5x5cm，沿肾门对侧剖开，于肾实质近肾盂处见肿块，肿块大小3.5x3x2.5cm，切面灰白灰红实性质稍软，输尿管长8cm，直径0.5cm，未见明显肿块。',
      microscopicDescription:
        '<"右肾及右输尿管全长">浸润性尿路上皮癌，高级别。标本输尿管断端、血管断端、肾周脂肪组织未见癌组织。  Vg:纤维组织阳性。',
      immunohistochemistry: 'Vg:纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '郭姜艳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/1/17',
    },
    {
      id: 13,
      hospitalNo: 'ZY150001277565',
      name: '郑*仙',
      pathologyNo: '2019-06824',
      reportDate: '2019/6/14 10:31',
      diagnosis: '腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"右半结肠及肿瘤"肠管切除标本一段，回肠长5cm，结肠长21cm，回肠切缘周径3cm，结肠切缘周径4cm，距回盲瓣7cm结肠切缘10cm处见一溃疡型肿块，肿块大小4x3x1cm，切面灰白实性质中，于肿块浆膜面见一挂线处，挂线处见一结节，直径约3cm，切面灰白灰黄实性质中，回盲瓣处见阑尾一根，长4cm，直径0.5cm，附网膜组织一堆，总积8x7x6cm，未触及明显结节，肠系膜触及淋巴结数枚，另送"结肠切缘"。',
      microscopicDescription:
        '<"右半结肠及及肿瘤">腺癌，中分化，癌组织浸润肠壁全层达周围脂肪组织；标本回肠切缘、结肠切缘、环周切缘、网膜组织、阑尾及另送"结肠切缘"未见癌组织；系线处结节镜下示癌组织；回肠系膜淋巴结（0/4）、肠系膜动脉根部淋巴结（0/1）未见癌转移；结肠系膜淋巴结（5/13）见癌转移。  Vg：纤维组织阳性。',
      immunohistochemistry: 'Vg：纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '冉凤明',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/6/14',
    },
    {
      id: 14,
      hospitalNo: 'ZY070001216358',
      name: '熊*萍',
      pathologyNo: '2019-02649',
      reportDate: '2019/3/19 13:14',
      diagnosis: '浸润性癌',
      tissueType: '膀胱组织',
      grossDescription:
        '"膀胱肿瘤（主要）"灰白碎组织一堆，总积3x2x1cm，另送"膀胱肿瘤"灰白灰红碎组织一堆，总积2x1x1cm。',
      microscopicDescription:
        '<"膀胱肿瘤(主要)"、"膀胱肿瘤">浸润性癌，待免疫组化协助进一步分型。  Vg：纤维组织阳性  Ag：银纤维阳性',
      immunohistochemistry: 'Vg：纤维组织阳性  Ag：银纤维阳性',
      molecularPathology: '待检测',
      pathologist: '郭姜艳',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/3/19',
    },
    {
      id: 15,
      hospitalNo: 'ZY240001157591',
      name: '黄*尧',
      pathologyNo: '2019-08240',
      reportDate: '2019/7/15 11:51',
      diagnosis: '腺癌',
      tissueType: '肺组织',
      grossDescription:
        '"左肺下叶"肺叶切除标本一叶，大小13x10x4cm，肺表面见支气管残端，支气管残端旁触及淋巴结数枚，直径0.4-0.6cm，距支气管残端约4cm处见一肿块，肿块大小2x2x1cm，切面灰白实性质中，其余多切面切开未见明显肿块，另送袋装淋巴结四组。',
      microscopicDescription:
        '<"左肺下叶">癌，待免疫组化协助诊断。支气管残端未见癌组织。支气管旁淋巴结（4/8）、"10组L"（3/3）、"11组L"（1/3）见癌转移。"5组L"（0/1）、"9组L"（0/1）未见癌转移。  Vg：纤维组织阳性。',
      immunohistochemistry: 'Vg：纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '张宗梅',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/7/15',
    },
    {
      id: 16,
      hospitalNo: 'ZY160001278969',
      name: '李*惠',
      pathologyNo: '2019-12240',
      reportDate: '2019/9/24 11:04',
      diagnosis: '腺癌',
      tissueType: '直肠组织',
      grossDescription:
        '"直肠及肿瘤"肠管切除标本一段，长约23cm，一切缘周径6cm，另一切缘周径7cm，距上切缘6.5cm处囊壁见灰红区域，范围1.5x1cm，距上切缘15.5cm处见肠腔缩窄区，距下切缘1cm处肠腔见广泛灰红区域，范围3x3cm，另见息肉一枚，大小0.5x0.4x0.2cm。肠系膜触及灰黄结节两枚。另送"肠系膜下动脉根部淋巴结"及"上切缘"、"下切缘"。',
      microscopicDescription:
        '<"直肠及肿瘤">腺癌，中分化，癌组织浸润肠壁全层达脂肪组织。息肉：符合管状绒毛状腺瘤，伴腺上皮低级别上皮内瘤变。标本肠管两切缘、环周切缘、"上切缘"、"下切缘"未见癌组织；肠系膜淋巴结（1/13）见癌转移；另见癌结节1枚，"肠系膜动脉根部淋巴结（0/1）未见癌转移。"  Vg：胶原纤维阳性。  Ag：银纤维阳性。',
      immunohistochemistry: 'Vg：胶原纤维阳性  Ag：银纤维阳性',
      molecularPathology: '待检测',
      pathologist: '汪钰钦',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/9/24',
    },
    {
      id: 17,
      hospitalNo: 'ZY190001222672',
      name: '王*铮',
      pathologyNo: '2019-13379',
      reportDate: '2019/10/22 15:12',
      diagnosis: '绒毛状管状腺瘤',
      tissueType: '直肠组织',
      grossDescription:
        '"直肠"固定于泡沫板上息肉一枚，大小1x1x0.8cm，右口测至肛侧每2mm顺序取材。',
      microscopicDescription:
        '<"直肠">绒毛状管状腺瘤，腺上皮呈低级别上皮内瘤变，基底及各切缘均未见肿瘤组织。  Vg：纤维组织阳性。  Vg：银纤维阳性。',
      immunohistochemistry: 'Vg：纤维组织阳性  Vg：银纤维阳性',
      molecularPathology: '待检测',
      pathologist: '冉凤明',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/10/22',
    },
    {
      id: 18,
      hospitalNo: 'ZY090001372776',
      name: '王*林',
      pathologyNo: '2019-03575',
      reportDate: '2019/4/8 11:02',
      diagnosis: '管状腺瘤',
      tissueType: '结肠组织',
      grossDescription:
        '灰褐组织两块，大者大小2.5x1.5x1.5cm，小者大小1.2x1x1cm，切面均灰黄灰褐实性质中。',
      microscopicDescription:
        '<乙状结肠肿物>管状腺瘤，腺上皮呈高级别上皮内瘤变，含粘膜内腺癌，局部浸润粘膜下层。大、者基底切缘未见癌组织，小者近基底处小于1mm处见少许腺瘤成分。  Vg：胶原纤维阳性。',
      immunohistochemistry: 'Vg：胶原纤维阳性',
      molecularPathology: '待检测',
      pathologist: '冉凤明',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/4/8',
    },
    {
      id: 19,
      hospitalNo: 'ZY090001272765',
      name: '王*芳',
      pathologyNo: '2019-09764',
      reportDate: '2019/8/9 14:51',
      diagnosis: '管状腺癌',
      tissueType: '结肠组织',
      grossDescription:
        '"右半结肠及肿瘤"肠管切除标本一段，结肠长11cm，回肠长7cm，结肠切缘周径7cm，回肠切缘周径4cm。距结肠切缘9.5cm处见一肿块，肿块大小2.5x2x1cm。切面灰白，实性，质中。回盲部见阑尾一条，长5cm，直径0.9cm。附网膜组织一堆，总积9x8x2.5cm。于其间未触及明显结节。肠系膜触及淋巴结数枚。',
      microscopicDescription:
        '<"右半结肠及肿瘤">管状腺癌，中分化，癌组织浸润肠壁全层达周围脂肪组织；标本两切缘、网膜组织、阑尾未见癌组织；结肠系膜淋巴结（3/16）见癌转移；回肠系膜淋巴结（0/3）未见癌转移。  Vg：纤维组织阳性',
      immunohistochemistry: 'Vg：纤维组织阳性',
      molecularPathology: '待检测',
      pathologist: '张宗梅',
      department: '病理科',
      sampleType: '手术标本',
      sampleDate: '2019/8/9',
    },
  ];

  let sortableInstance: Sortable | null = null;

  const fetchData = async (params: any = { current: 1, pageSize: 10 }) => {
    setLoading(true);
    try {
      // 使用 pathology-mock.ts 的数据
      setTimeout(() => {
        const result = getPathologyData(params.current, params.pageSize);

        // 转换数据格式以匹配新的列结构
        const convertedList = result.data.map((item: any, index: number) => ({
          id: index + 1,
          hospitalNo: item.hospitalNo,
          name: item.patientName,
          idCard: item.idCard,
          occupation: item.occupation,
          bloodType: item.bloodType,
          tStage: item.tStage,
          nStage: item.nStage,
          mStage: item.mStage,
          tnmStage: item.tnmStage,
          clinicalStage: item.clinicalStage,
          address: item.address,
          phone: item.phone,
          workUnit: item.workUnit,
          diagnosis: item.diagnosis,
          hospitalDiagnosis: item.hospitalDiagnosis,
          pathologyDiagnosis: item.pathologyDiagnosis,
          personalHistory: item.personalHistory,
          maritalHistory: item.maritalHistory,
          menstrualHistory: item.menstrualHistory,
          fertilityHistory: item.fertilityHistory,
          pathologyNo: item.pathologyNo,
          reportTime: item.reportTime,
          grossAppearance: item.grossAppearance,
          detailedPathologyDiagnosis: item.detailedPathologyDiagnosis,
          reportDoctor: item.reportDoctor,
          reviewDoctor: item.reviewDoctor,
        }));

        renderData.value = convertedList;
        pagination.current = params.current;
        pagination.total = result.total;
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

  const editRecord = (record: PathologyRecord) => {
    Message.info(`编辑记录: ${record.name}`);
  };

  const deleteRecord = (record: PathologyRecord) => {
    Message.success(`删除记录: ${record.name}`);
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
    { label: '手术标本', value: 'surgery' },
    { label: '穿刺标本', value: 'biopsy' },
    { label: '细胞学标本', value: 'cytology' },
    { label: '冰冻切片', value: 'frozen' },
  ];

  function importPatient() {
    document.getElementById('import-patient-input')?.click();
  }
  function handlePatientFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入病理文件', files);
    }
  }
  function handleDetailImport() {
    document.getElementById('import-detail-input')?.click();
    showDetailImport.value = false;
  }
  function handleDetailFile(e: Event) {
    const { files } = e.target as HTMLInputElement;
    if (files && files.length) {
      // 示例：console.log('导入病理详情文件', files, searchNo.value, importType.value);
    }
  }

  const importRecords = ref([
    {
      name: 'pathology_0601.xlsx',
      category: '病理信息',
      serial: 'LSH20240601001',
      importDate: '2024-06-01 10:00',
      updateDate: '2024-06-01 12:00',
      updateUser: '张三',
    },
    {
      name: 'pathology_0602.xlsx',
      category: '病理信息',
      serial: 'LSH20240602001',
      importDate: '2024-06-02 14:30',
      updateDate: '2024-06-02 15:00',
      updateUser: '李四',
    },
    {
      name: 'pathology_0603.xlsx',
      category: '病理信息',
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
