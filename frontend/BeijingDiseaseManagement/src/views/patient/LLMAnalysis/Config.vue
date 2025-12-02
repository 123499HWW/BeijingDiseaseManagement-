<template>
  <a-card title="报告生成配置" :bordered="false">
    <!-- 患者搜索与信息展示 -->
    <div style="margin-bottom: 24px">
      <div style="display: flex; gap: 12px; align-items: center">
        <a-input
          v-model="searchKey"
          placeholder="输入姓名/身份证/住院号搜索患者"
          style="width: 260px"
          @pressEnter="onSearch"
        />
        <a-button @click="onSearch">搜索</a-button>
        <a-select
          v-if="searchResults.length"
          v-model="selectedPatientId"
          placeholder="选择患者"
          style="width: 220px"
        >
          <a-option v-for="p in searchResults" :key="p.id" :value="p.id"
            >{{ p.name }}（{{ p.idCard }}）</a-option
          >
        </a-select>
      </div>
      <div v-if="selectedPatient" style="margin-top: 16px">
        <a-descriptions :column="3" bordered size="small">
          <a-descriptions-item label="姓名">{{
            selectedPatient.name
          }}</a-descriptions-item>
          <a-descriptions-item label="身份证号">{{
            selectedPatient.idCard
          }}</a-descriptions-item>
          <a-descriptions-item label="住院号">{{
            selectedPatient.hospitalId
          }}</a-descriptions-item>
          <a-descriptions-item label="性别">{{
            selectedPatient.gender
          }}</a-descriptions-item>
          <a-descriptions-item label="年龄">{{
            selectedPatient.age
          }}</a-descriptions-item>
          <a-descriptions-item label="诊断">{{
            selectedPatient.diagnosis
          }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </div>
    <!-- 检查化验表格 -->
    <div v-if="selectedPatient" style="margin-bottom: 16px">
      <div style="margin-bottom: 8px; font-weight: bold">检查化验</div>
      <a-table
        :columns="labColumns"
        :data="selectedPatient.lab"
        :pagination="false"
        size="small"
        row-key="item"
      />
    </div>
    <!-- 医学影像表格 -->
    <div
      v-if="
        selectedPatient &&
        selectedPatient.images &&
        selectedPatient.images.length
      "
      style="margin-bottom: 16px"
    >
      <div style="margin-bottom: 8px; font-weight: bold">医学影像</div>
      <a-table
        :columns="imageColumns"
        :data="selectedPatient.images"
        :pagination="false"
        size="small"
        row-key="checkId"
      />
    </div>
    <!-- 个人信息表格 -->
    <div v-if="selectedPatient" style="margin-bottom: 16px">
      <div style="margin-bottom: 8px; font-weight: bold">个人信息</div>
      <a-descriptions :column="3" bordered size="small">
        <a-descriptions-item label="职业">{{
          selectedPatient.job
        }}</a-descriptions-item>
        <a-descriptions-item label="血型">{{
          selectedPatient.blood
        }}</a-descriptions-item>
        <a-descriptions-item label="住址">{{
          selectedPatient.address
        }}</a-descriptions-item>
        <a-descriptions-item label="电话">{{
          selectedPatient.phone
        }}</a-descriptions-item>
        <a-descriptions-item label="工作单位">{{
          selectedPatient.company
        }}</a-descriptions-item>
        <a-descriptions-item label="病理诊断">{{
          selectedPatient.pathology
        }}</a-descriptions-item>
      </a-descriptions>
    </div>
    <!-- 报告配置区 -->
    <div style="display: flex; flex-wrap: wrap; gap: 32px; margin-bottom: 16px">
      <div style="flex: 1 1 320px; min-width: 280px">
        <div style="margin-bottom: 12px; font-weight: bold; font-size: 16px"
          >选择数据来源</div
        >
        <a-checkbox-group v-model="sources" direction="vertical">
          <a-checkbox value="group">病例分组</a-checkbox>
          <a-checkbox value="lab">指标检测数据</a-checkbox>
          <a-checkbox value="image">医学影像报告</a-checkbox>
          <a-checkbox value="med">用药医嘱信息</a-checkbox>
        </a-checkbox-group>
      </div>
      <div style="flex: 1 1 320px; min-width: 280px">
        <div style="margin-bottom: 12px; font-weight: bold; font-size: 16px"
          >报告详细程度</div
        >
        <a-radio-group v-model="detailLevel" direction="vertical">
          <a-radio value="detail"
            >详细报告<template #extra>包含全面分析和初步建议</template></a-radio
          >
          <a-radio value="comprehensive"
            >综合报告<template #extra
              >包含深度分析和完整诊疗方案</template
            ></a-radio
          >
        </a-radio-group>
      </div>
    </div>
    <div style="margin-top: 16px; text-align: center">
      <a-button
        type="primary"
        size="large"
        :disabled="!selectedPatient"
        @click="onGenerate"
        >生成病理分析报告</a-button
      >
    </div>
  </a-card>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue';

  // 假数据
  const patients = [
    {
      id: 'ZY010001490726',
      name: 'XAO',
      idCard: '',
      hospitalId: 'ZY010001490726',
      gender: '女',
      age: 52,
      diagnosis: '宫体多发肌瘤，子宫内膜癌',
      job: '',
      blood: '',
      address: '',
      phone: '',
      company: '',
      pathology: '',
      lab: [
        {
          item: '活化部分凝血活酶时间',
          result: 30.7,
          unit: 'sec',
          ref: '28-44',
          date: '2024/12/25 10:03',
        },
        {
          item: '凝血酶时间',
          result: 17.7,
          unit: 'sec',
          ref: '<21',
          date: '2024/12/25 10:03',
        },
        {
          item: '凝血酶原时间',
          result: 11.9,
          unit: 'sec',
          ref: '11-14.5',
          date: '2024/12/25 10:03',
        },
        {
          item: '纤维蛋白原',
          result: 4.09,
          unit: 'g/L',
          ref: '2-4',
          date: '2024/12/25 10:03',
        },
        {
          item: '凝血酶原时间比值',
          result: 0.9,
          unit: '',
          ref: '0.85-1.15',
          date: '2024/12/25 10:03',
        },
        {
          item: '凝血酶原活动度',
          result: 130,
          unit: '',
          ref: '70-120',
          date: '2024/12/25 10:03',
        },
      ],
      images: [
        {
          checkTime: '2012-10-09',
          finding:
            '子宫体积增大，形态失常，宫体前后壁浆膜下及肌壁间多发大小不等结节、肿块...（省略）',
          conclusion:
            '1、子宫内膜癌...2、子宫体多发肌瘤...3、肝右前叶小囊肿...4、胆、胰、脾、双肾、膀胱、直肠、腹盆淋巴结未见确切异常。',
          checkId: '20861',
          idCard: '530103195509141529',
          name: '赵延清',
          hospitalId: 'ZY010001490726',
        },
      ],
    },
  ];

  const searchKey = ref('');
  const searchResults = ref<any[]>([]);
  const selectedPatientId = ref<string | null>(null);
  const selectedPatient = computed(() =>
    patients.find((p) => p.id === selectedPatientId.value)
  );

  function onSearch() {
    searchResults.value = patients.filter(
      (p) =>
        p.name.includes(searchKey.value) ||
        p.id.includes(searchKey.value) ||
        p.idCard.includes(searchKey.value)
    );
    if (searchResults.value.length === 1) {
      selectedPatientId.value = searchResults.value[0].id;
    }
  }

  const sources = ref(['lab', 'image', 'med']);
  const detailLevel = ref('detail');
  const emit = defineEmits(['generate']);
  function onGenerate() {
    if (!selectedPatient.value) return;
    emit('generate', {
      patient: selectedPatient.value,
      sources: sources.value,
      detailLevel: detailLevel.value,
    });
  }

  const labColumns = [
    { title: '检验项目', dataIndex: 'item', key: 'item' },
    { title: '检验结果', dataIndex: 'result', key: 'result' },
    { title: '单位', dataIndex: 'unit', key: 'unit' },
    { title: '参考值', dataIndex: 'ref', key: 'ref' },
    { title: '检验日期', dataIndex: 'date', key: 'date' },
  ];
  const imageColumns = [
    { title: '检查时间', dataIndex: 'checkTime', key: 'checkTime' },
    { title: '影像所见', dataIndex: 'finding', key: 'finding' },
    { title: '影像结论', dataIndex: 'conclusion', key: 'conclusion' },
    { title: '检查流水号', dataIndex: 'checkId', key: 'checkId' },
    { title: '身份证号', dataIndex: 'idCard', key: 'idCard' },
    { title: '姓名', dataIndex: 'name', key: 'name' },
    { title: '住院号', dataIndex: 'hospitalId', key: 'hospitalId' },
  ];
</script>
