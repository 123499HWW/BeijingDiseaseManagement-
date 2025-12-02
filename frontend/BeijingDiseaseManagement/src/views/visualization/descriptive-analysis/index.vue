<template>
  <div class="descriptive-analysis">
    <!-- 步骤内容 -->
    <div class="step-content">
      <!-- 步骤1：患者与指标选择 -->
      <div v-show="currentStep === 0" class="step-panel">
        <step-one
          :patient-list="patientList"
          :selected-patient-keys="selectedPatientKeys"
          :selected-indicators="selectedIndicators"
          :average-type="averageType"
          @update:selected-patient-keys="selectedPatientKeys = $event"
          @update:selected-indicators="
            (value) => {
              console.log('父组件接收到指标更新:', value);
              selectedIndicators = value;
            }
          "
          @update:average-type="averageType = $event"
          @data-selected="handleDataSelected"
        />
      </div>

      <!-- 步骤2：描述性分析 -->
      <div v-show="currentStep === 1" class="step-panel">
        <step-three
          :selected-indicators="selectedIndicators"
          @prev-step="prevStep"
          @reset-all="resetAll"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import StepOne from './components/step-one.vue';
  import StepThree from './components/step-three.vue';

  // 响应式数据
  const selectedIndicators = ref<string[]>([]);
  const averageType = ref<string>('average');

  // 步骤控制
  const currentStep = ref<number>(0);

  // 患者列表
  const patientList = ref<any[]>([]);
  const selectedPatientKeys = ref<string[]>([]);

  // 生成模拟患者数据
  const generateMockPatients = () => {
    const diagnoses = [
      '结肠癌',
      '直肠癌',
      '新闻学',
      '腺癌',
      '黏液腺癌',
      '印戒细胞癌',
    ];
    const stages = ['I期', 'II期', 'III期', 'IV期'];
    const bloodTypes = ['A型', 'B型', 'O型', 'AB型'];
    const occupations = ['工人', '农民', '教师', '医生', '学生', '退休'];
    const patients: any[] = [];

    for (let i = 1; i <= 50; i += 1) {
      patients.push({
        'key': `patient_${i}`,
        'patientId': `P${String(i).padStart(4, '0')}`,
        'name': `患者${i}`,
        'gender': Math.random() > 0.5 ? '男' : '女',
        'age': Math.floor(Math.random() * 50) + 30,
        'bloodType': bloodTypes[Math.floor(Math.random() * bloodTypes.length)],
        'occupation':
          occupations[Math.floor(Math.random() * occupations.length)],
        'diagnosis': diagnoses[Math.floor(Math.random() * diagnoses.length)],
        'stage': stages[Math.floor(Math.random() * stages.length)],
        // 肿瘤标志物
        'CA199': (Math.random() * 100).toFixed(1),
        'CEA': (Math.random() * 10).toFixed(1),
        'CA242': (Math.random() * 50).toFixed(1),
        'CA125': (Math.random() * 200).toFixed(1),
        'AFP': (Math.random() * 20).toFixed(1),
        'CA153': (Math.random() * 100).toFixed(1),
        'PSA': (Math.random() * 10).toFixed(1),
        'Fer': (Math.random() * 500).toFixed(1),
        'β-HCG': (Math.random() * 10).toFixed(1),
        // 血常规
        'whiteBloodCell': (Math.random() * 10 + 4).toFixed(1),
        'redBloodCell': (Math.random() * 2 + 4).toFixed(1),
        'hemoglobin': (Math.random() * 50 + 120).toFixed(1),
        'platelet': (Math.floor(Math.random() * 100) + 150).toString(),
        // 血脂
        'totalCholesterol': (Math.random() * 2 + 4).toFixed(1),
        'triglyceride': (Math.random() * 2 + 1).toFixed(1),
        'hdlCholesterol': (Math.random() * 1 + 1).toFixed(1),
        'ldlCholesterol': (Math.random() * 2 + 2).toFixed(1),
        // 肝功能
        'totalBilirubin': (Math.random() * 20 + 5).toFixed(1),
        'ast': (Math.random() * 40 + 20).toFixed(1),
        'alt': (Math.random() * 40 + 20).toFixed(1),
        // 肾功能
        'creatinine': (Math.random() * 50 + 50).toFixed(1),
        'urea': (Math.random() * 10 + 5).toFixed(1),
        // 其他
        'glucose': (Math.random() * 3 + 5).toFixed(1),
        'systolicPressure': Math.floor(Math.random() * 40 + 120),
        'diastolicPressure': Math.floor(Math.random() * 20 + 80),
        'weight': (Math.random() * 30 + 60).toFixed(1),
        'height': Math.floor(Math.random() * 30 + 160),
      });
    }

    return patients;
  };

  // 步骤控制函数
  const nextStep = () => {
    if (currentStep.value < 1) {
      currentStep.value += 1;
    }
  };

  const prevStep = () => {
    if (currentStep.value > 0) {
      currentStep.value -= 1;
    }
  };

  const onStepChange = (step: number) => {
    currentStep.value = step;
  };

  // 处理数据选择完成
  const handleDataSelected = (data: any) => {
    nextStep();
  };

  // 重置函数
  const resetAll = () => {
    currentStep.value = 0;
    selectedPatientKeys.value = [];
    selectedIndicators.value = [];
    averageType.value = 'average';
    Message.success('已重新开始');
  };

  onMounted(() => {
    const patients = generateMockPatients();
    console.log('生成的患者数据:', patients);
    patientList.value = patients;
    console.log('患者列表已设置:', patientList.value);

    // 预选一些患者作为演示
    selectedPatientKeys.value = [
      'patient_1',
      'patient_2',
      'patient_3',
      'patient_4',
      'patient_5',
    ];
    console.log('预选的患者keys:', selectedPatientKeys.value);

    // 预选一些指标作为演示
    selectedIndicators.value = [
      'age',
      'gender',
      'bloodType',
      'occupation',
      'diagnosis',
      'CA199',
      'CEA',
      'whiteBloodCell',
      'hemoglobin',
      'platelet',
    ];
    console.log('预选的指标:', selectedIndicators.value);
  });
</script>

<style scoped lang="less">
  .descriptive-analysis {
    padding: 24px;

    .step-content {
      .step-panel {
        min-height: 400px;
      }
    }
  }
</style>
