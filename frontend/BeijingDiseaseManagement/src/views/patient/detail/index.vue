<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patientImport.hisTable']" />
    <a-card class="general-card" :title="$t('menu.patientImport.hisTable')">
      <!-- 患者基本信息 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="24">
          <a-card title="患者基本信息" size="small">
            <a-descriptions :column="4" bordered>
              <a-descriptions-item label="住院号">{{
                currentPatient.admissionNumber
              }}</a-descriptions-item>
              <a-descriptions-item label="姓名">{{
                currentPatient.name
              }}</a-descriptions-item>
              <a-descriptions-item label="身份证号">{{
                currentPatient.idCard
              }}</a-descriptions-item>
              <a-descriptions-item label="职业">{{
                currentPatient.occupation
              }}</a-descriptions-item>
              <a-descriptions-item label="血型">{{
                currentPatient.bloodType
              }}</a-descriptions-item>
              <a-descriptions-item label="联系电话">{{
                currentPatient.phone
              }}</a-descriptions-item>
              <a-descriptions-item label="工作单位">{{
                currentPatient.workUnit
              }}</a-descriptions-item>
              <a-descriptions-item label="住址">{{
                currentPatient.address
              }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
      </a-row>

      <!-- 诊断信息 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="24">
          <a-card title="诊断信息" size="small">
            <a-descriptions :column="3" bordered>
              <a-descriptions-item label="诊断">{{
                currentPatient.diagnosis
              }}</a-descriptions-item>
              <a-descriptions-item label="住院诊断">{{
                currentPatient.hospitalDiagnosis
              }}</a-descriptions-item>
              <a-descriptions-item label="病理诊断">{{
                currentPatient.pathologyDiagnosis
              }}</a-descriptions-item>
              <a-descriptions-item label="T分期">{{
                currentPatient.t
              }}</a-descriptions-item>
              <a-descriptions-item label="N分期">{{
                currentPatient.n
              }}</a-descriptions-item>
              <a-descriptions-item label="M分期">{{
                currentPatient.m
              }}</a-descriptions-item>
              <a-descriptions-item label="TNM分期">{{
                currentPatient.tnmStage
              }}</a-descriptions-item>
              <a-descriptions-item label="临床分期">{{
                currentPatient.clinicalStage
              }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
      </a-row>

      <!-- 病史信息 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="24">
          <a-card title="病史信息" size="small">
            <a-descriptions :column="3" bordered>
              <a-descriptions-item label="个人史">{{
                currentPatient.personalHistory || '无'
              }}</a-descriptions-item>
              <a-descriptions-item label="婚育史">{{
                currentPatient.marriageHistory || '无'
              }}</a-descriptions-item>
              <a-descriptions-item label="月经史">{{
                currentPatient.menstruationHistory || '无'
              }}</a-descriptions-item>
              <a-descriptions-item label="生育史">{{
                currentPatient.fertilityHistory || '无'
              }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
      </a-row>

      <!-- 患者列表 -->
      <a-row :gutter="16">
        <a-col :span="24">
          <a-card title="患者列表" size="small">
            <div class="mb-4">
              <a-input-search
                v-model="searchKeyword"
                placeholder="搜索患者姓名、住院号、诊断"
                style="width: 300px"
                @search="handleSearch"
              />
            </div>
            <div>
              <p
                >当前显示患者：{{ currentPatient.name }} ({{
                  currentPatient.admissionNumber
                }})</p
              >
              <p>共 {{ patientData.length }} 条患者数据</p>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import Breadcrumb from '@/components/breadcrumb/index.vue';

  // 患者数据类型
  interface Patient {
    id: number;
    admissionNumber: string;
    name: string;
    idCard: string;
    occupation: string;
    bloodType: string;
    t: string;
    n: string;
    m: string;
    tnmStage: string;
    clinicalStage: string;
    address: string;
    phone: string;
    workUnit: string;
    diagnosis: string;
    hospitalDiagnosis: string;
    pathologyDiagnosis: string;
    personalHistory: string;
    marriageHistory: string;
    menstruationHistory: string;
    fertilityHistory: string;
  }

  // 静态患者数据（前20条作为示例）
  const patientData: Patient[] = [
    {
      id: 1,
      admissionNumber: 'ZY110001456658',
      name: '李*梅',
      idCard: '**********0026',
      occupation: '其他',
      bloodType: '未查',
      t: '3',
      n: '1',
      m: '1',
      tnmStage: 'IV',
      clinicalStage: '',
      address: '云南省楚雄彝族自治州武定县武定县狮山镇民中路97号',
      phone: '15891837168',
      workUnit: '云南省楚雄彝族自治州武定县武定县狮山镇民中路97号',
      diagnosis: '乙状结肠恶性肿瘤',
      hospitalDiagnosis: '恶性肿瘤靶向治疗',
      pathologyDiagnosis: '腺癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 2,
      admissionNumber: 'ZY260001291539',
      name: '母*义',
      idCard: '**********0343',
      occupation: '农民',
      bloodType: '不详',
      t: '',
      n: '',
      m: '',
      tnmStage: '',
      clinicalStage: '',
      address: '云南省大理白族自治州大理市大理市下关镇太和苑小区',
      phone: '13577264852',
      workUnit: '云南省大理白族自治州大理市大理市下关镇太和苑小区',
      diagnosis: '结肠恶性肿瘤',
      hospitalDiagnosis: '手术后恶性肿瘤化学治疗',
      pathologyDiagnosis: '腺癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 3,
      admissionNumber: 'ZY190001356605',
      name: '杨*萍',
      idCard: '**********1242',
      occupation: '其他',
      bloodType: 'A',
      t: '',
      n: '',
      m: '',
      tnmStage: '',
      clinicalStage: '',
      address: '云南省临沧市凤庆县凤庆县勐佑中学',
      phone: '13578436178',
      workUnit: '云南省临沧市凤庆县凤庆县勐佑中学',
      diagnosis: '结肠恶性肿瘤',
      hospitalDiagnosis: '手术后恶性肿瘤化学治疗',
      pathologyDiagnosis: '腺癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 4,
      admissionNumber: 'ZY370001297994',
      name: '陈*静',
      idCard: '**********2423',
      occupation: '职员',
      bloodType: '未查',
      t: '3',
      n: '0',
      m: '1',
      tnmStage: 'IV',
      clinicalStage: '',
      address: '云南省昆明市盘龙区东风巷82号3单元306',
      phone: '13888295251',
      workUnit: '云南省昆明市盘龙区东风巷82号3单元306',
      diagnosis: '乙状结肠恶性肿瘤',
      hospitalDiagnosis: '恶性肿瘤靶向治疗',
      pathologyDiagnosis: '腺癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 5,
      admissionNumber: 'ZY040001367784',
      name: '王*明',
      idCard: '**********2110',
      occupation: '退(离)休人员',
      bloodType: 'A',
      t: '4b',
      n: '2a',
      m: '1c',
      tnmStage: 'IVC',
      clinicalStage: '',
      address: '云南省楚雄彝族自治州武定县近城镇环城南路22号103室',
      phone: '18308788582',
      workUnit: '武定县市场监督管理局',
      diagnosis: '多发性结肠息肉',
      hospitalDiagnosis: '结肠良性肿瘤',
      pathologyDiagnosis: '印戒细胞癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 6,
      admissionNumber: 'ZY070001191722',
      name: '丁*生',
      idCard: '**********1232',
      occupation: '退(离)休人员',
      bloodType: 'AB',
      t: '3',
      n: '1b',
      m: '1b',
      tnmStage: 'ⅣB',
      clinicalStage: '',
      address: '云南省保山市隆阳区永昌街道太保北路37号',
      phone: '15987528168',
      workUnit: '-',
      diagnosis: '升结肠恶性肿瘤',
      hospitalDiagnosis: '恶性肿瘤终末期化疗',
      pathologyDiagnosis: '转移性癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 7,
      admissionNumber: 'ZY040001254228',
      name: '师*芬',
      idCard: '**********0020',
      occupation: '退(离)休人员',
      bloodType: 'B',
      t: '3',
      n: 'x',
      m: '0',
      tnmStage: '-',
      clinicalStage: '',
      address: '云南省红河哈尼族彝族自治州泸西县九华路70号',
      phone: '13808778033',
      workUnit: '-',
      diagnosis: '乙状结肠恶性肿瘤',
      hospitalDiagnosis: '恶性肿瘤支持治疗',
      pathologyDiagnosis: '转移性癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 8,
      admissionNumber: 'ZY190001348339',
      name: '张*芳',
      idCard: '**********1722',
      occupation: '无业人员',
      bloodType: '未查',
      t: '4',
      n: '2',
      m: '1',
      tnmStage: 'Ⅳ',
      clinicalStage: '',
      address: '云南省昆明市盘龙区万华路张官营村339号附1号',
      phone: '18087140276',
      workUnit: '-',
      diagnosis: '降结肠恶性肿瘤',
      hospitalDiagnosis: '恶性肿瘤终末期化疗',
      pathologyDiagnosis: '转移性癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 9,
      admissionNumber: 'ZY100001351940',
      name: '尾*春',
      idCard: '**********0513',
      occupation: '无业人员',
      bloodType: '不详',
      t: '3',
      n: '2a',
      m: '0',
      tnmStage: 'III',
      clinicalStage: '',
      address: '云南省玉溪市新平彝族傣族自治县新化乡新化社区大黑达25号',
      phone: '18725423396',
      workUnit: '云南省玉溪市新平彝族傣族自治县新化乡新化社区大黑达25号',
      diagnosis: '直肠恶性肿瘤',
      hospitalDiagnosis: '手术前恶性肿瘤化学治疗',
      pathologyDiagnosis: '腺癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
    {
      id: 10,
      admissionNumber: 'ZY090001373360',
      name: '李*',
      idCard: '**********0047',
      occupation: '工人',
      bloodType: '未查',
      t: '-',
      n: '-',
      m: '-',
      tnmStage: 'IV',
      clinicalStage: '',
      address: '云南省昆明市西山区双河湾',
      phone: '15087977753',
      workUnit: '-',
      diagnosis: '乙状结肠恶性肿瘤',
      hospitalDiagnosis: '恶性肿瘤终末期靶向治疗',
      pathologyDiagnosis: '转移性癌',
      personalHistory: '',
      marriageHistory: '',
      menstruationHistory: '',
      fertilityHistory: '',
    },
  ];

  // 响应式数据
  const searchKeyword = ref('');
  const currentPatient = ref<Patient>(patientData[0]);

  // 分页配置（暂时未使用）
  // const pagination = ref({
  //   current: 1,
  //   pageSize: 10,
  //   total: patientData.length,
  //   showTotal: true,
  //   showJumper: true,
  //   showPageSize: true,
  // });

  // 搜索患者
  const handleSearch = () => {
    Message.info(`搜索功能：${searchKeyword.value}`);
  };

  // 组件挂载时设置默认患者
  onMounted(() => {
    const [firstPatient] = patientData;
    if (firstPatient) {
      currentPatient.value = firstPatient;
    }
  });
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
  }

  .general-card {
    margin-bottom: 16px;
  }

  .mb-4 {
    margin-bottom: 16px;
  }

  :deep(.arco-descriptions-item) {
    padding-bottom: 8px;
  }

  :deep(.arco-card-body) {
    padding: 16px;
  }
</style>
