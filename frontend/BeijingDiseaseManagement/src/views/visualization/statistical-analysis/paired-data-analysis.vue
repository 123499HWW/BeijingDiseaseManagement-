<template>
  <div class="paired-data-analysis">
    <a-page-header title="配对数据分析" subtitle="新闻学患者指标统计分析" />

    <!-- 步骤条 -->
    <a-card :bordered="false" style="margin-bottom: 24px">
      <a-steps
        :current="currentStep"
        @change="onStepChange"
        :default-current="0"
      >
        <a-step title="患者与指标选择" description="选择患者和分析指标" />
        <a-step title="数据概览" description="查看数据概览" />
        <a-step title="分析结果" description="查看分析结果" />
      </a-steps>
    </a-card>

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
          @update:selected-indicators="selectedIndicators = $event"
          @update:average-type="averageType = $event"
          @data-selected="handleDataSelected"
        />
      </div>

      <!-- 步骤2：数据概览 -->
      <div v-show="currentStep === 1" class="step-panel">
        <step-two
          :data-overview="dataOverview"
          :selected-indicators="selectedIndicators"
          @prev-step="prevStep"
          @next-step="nextStep"
        />
      </div>

      <!-- 步骤3：分析结果 -->
      <div v-show="currentStep === 2" class="step-panel">
        <step-four
          :analysis-result="analysisResult"
          @prev-step="prevStep"
          @reset-all="resetAll"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import StepOne from './components/step-one.vue';
  import StepTwo from './components/step-two.vue';
  import StepFour from './components/step-four.vue';

  // 响应式数据
  const selectedIndicators = ref<string[]>([]);
  const averageType = ref<string>('average');

  const isLoading = ref(false);
  const isAnalyzing = ref(false);
  const dataOverview = ref<any>(null);
  const analysisResult = ref<any>(null);

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

  // 生成分析结果
  const generateAnalysisResult = () => {
    const pValue = (Math.random() * 0.1).toFixed(4);
    const isSignificant = parseFloat(pValue) < 0.05;
    const statistic = (Math.random() * 10 + 2).toFixed(3);
    const df = Math.floor(Math.random() * 10) + 1;
    const effectSize = (Math.random() * 0.8 + 0.2).toFixed(3);

    let effectInterpretation: string;
    if (parseFloat(effectSize) > 0.5) {
      effectInterpretation = '大效应';
    } else if (parseFloat(effectSize) > 0.3) {
      effectInterpretation = '中等效应';
    } else {
      effectInterpretation = '小效应';
    }

    analysisResult.value = {
      method: '卡方检验（χ²）',
      significanceLevel: 'α = 0.05',
      statistic,
      pValue,
      df,
      effectSize,
      significance: isSignificant ? '统计显著' : '统计不显著',
      effectInterpretation,
      practicalSignificance: isSignificant ? '具有实际意义' : '无实际意义',
      conclusion: `采用卡方检验（χ²）进行分析，结果显示${
        isSignificant ? '存在显著性差异' : '无显著性差异'
      }（p=${pValue}），效应量为${effectSize}。`,
    };
  };

  // 步骤控制函数
  const nextStep = () => {
    if (currentStep.value < 2) {
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
    dataOverview.value = data;
    // 自动生成分析结果
    generateAnalysisResult();
    nextStep();
  };

  // 重置函数
  const resetAll = () => {
    currentStep.value = 0;
    selectedPatientKeys.value = [];
    selectedIndicators.value = [];
    dataOverview.value = null;
    analysisResult.value = null;
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
  .paired-data-analysis {
    padding: 24px;

    .step-content {
      .step-panel {
        min-height: 400px;
      }
    }
  }
</style>
