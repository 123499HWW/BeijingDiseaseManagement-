<template>
  <div class="report-container">
    <div class="report-paper">
      <div class="report-title">病理分析报告</div>
      <!-- 患者基本信息 -->
      <section>
        <div class="report-section-title">一、患者基本信息</div>
        <div class="report-content">
          姓名：<b>{{ patient.name }}</b
          ><br />
          年龄：<b>{{ patient.age }}岁</b><br />
          性别：<b>{{ patient.gender }}</b
          ><br />
          入院日期：<b>{{ patient.admissionDate || '-' }}</b
          ><br />
          科室：<b>{{ patient.department || '-' }}</b
          ><br />
          初步诊断：<b>{{ patient.diagnosis }}</b>
        </div>
      </section>
      <!-- 实验室检查结果分析 -->
      <section>
        <div class="report-section-title">二、实验室检查结果分析</div>
        <div v-for="row in labRows" :key="row.item" class="report-content">
          <span
            ><b>{{ row.item }}：</b>{{ row.result
            }}{{ row.unit }} （参考范围：{{ row.ref }}）<span
              v-if="row.abnormal"
              style="color: #f5222d; font-weight: bold"
            >
              ↑</span
            ></span
          >
        </div>
      </section>
      <!-- 异常指标分析 -->
      <section v-if="labAbnormalText.length">
        <div class="report-section-title">三、异常指标分析</div>
        <div class="report-content">
          <ul style="margin: 0; padding-left: 20px">
            <li
              v-for="(txt, idx) in labAbnormalText"
              :key="idx"
              style="margin-bottom: 4px"
              >{{ txt }}</li
            >
          </ul>
        </div>
      </section>
      <!-- 影像学检查分析 -->
      <section v-if="imageGroups.length">
        <div class="report-section-title">四、影像学检查分析</div>
        <div
          v-for="(img, idx) in imageGroups"
          :key="idx"
          class="report-content"
          style="margin-bottom: 16px"
        >
          <div
            ><b>{{ img.type }} - {{ img.date }}</b></div
          >
          <div><b>影像所见：</b>{{ img.finding }}</div>
          <div><b>影像结论：</b>{{ img.conclusion }}</div>
        </div>
      </section>
      <!-- 用药分析 -->
      <section v-if="medRows.length">
        <div class="report-section-title">五、用药分析</div>
        <div class="report-content"><b>当前用药方案：</b></div>
        <ul class="report-content" style="margin: 0; padding-left: 20px">
          <li v-for="(med, idx) in medRows" :key="idx">
            {{ med.name }} {{ med.dose }} {{ med.freq }}（自{{
              med.start
            }}开始，{{ med.purpose }}）
          </li>
        </ul>
        <div class="report-content" style="margin-top: 8px"
          ><b>用药评估：</b>{{ medAdvice }}</div
        >
      </section>
      <!-- AI综合分析与建议 -->
      <section>
        <div class="report-section-title">六、AI综合分析与建议</div>
        <div class="report-content">
          <ol style="margin: 0; padding-left: 20px">
            <li
              ><b>诊断评估：</b>结合临床表现及辅助检查，目前诊断“<b>{{
                patient.diagnosis
              }}</b
              >”明确，<span v-if="labAbnormalText.length"
                >血糖控制不佳，存在血脂异常风险。</span
              ><span v-else>各项指标基本正常。</span></li
            >
            <li
              ><b>治疗建议：</b>
              <ul style="margin: 0; padding-left: 20px">
                <li
                  >调整降糖方案：建议将二甲双胍剂量调整为0.85g，每日两次，并加用SGLT-2抑制剂类药物</li
                >
                <li
                  >血脂管理：建议启动他汀类药物治疗，如阿托伐他汀10mg，每晚一次</li
                >
                <li
                  >血压监测：继续当前降压方案，每日监测血压，目标控制在130/80mmHg以下</li
                >
              </ul>
            </li>
            <li
              ><b>进一步检查建议：</b>
              <ul style="margin: 0; padding-left: 20px">
                <li>完善眼底检查评估糖尿病视网膜病变情况</li>
                <li>检测肝肾功能、尿微量白蛋白/肌酐比值评估靶器官损害</li>
                <li>3个月后复查胸部CT评估肺结节变化</li>
              </ul>
            </li>
            <li
              ><b>生活方式指导：</b>
              <ul style="margin: 0; padding-left: 20px">
                <li>低盐低脂糖尿病饮食，控制总热量摄入</li>
                <li>适当运动，每周至少150分钟中等强度有氧运动</li>
                <li>戒烟限酒，保持规律作息</li>
              </ul>
            </li>
          </ol>
        </div>
      </section>
      <div style="margin-top: 32px; text-align: right">
        <a-button @click="$emit('back')">返回</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  const props = defineProps<{ data: any }>();
  const { patient } = props.data;

  // 实验室检查
  const labRows = (patient.lab || []).map((row: any) => {
    const { result, ref } = row;
    const value = Number(result);
    let abnormal = false;
    if (ref && typeof result !== 'undefined') {
      if (ref.includes('-')) {
        const [, max] = ref.split('-').map(Number);
        abnormal = value > max;
      } else if (ref.startsWith('<')) {
        const max = Number(ref.replace('<', ''));
        abnormal = value >= max;
      } else if (ref.startsWith('>')) {
        const min = Number(ref.replace('>', ''));
        abnormal = value <= min;
      }
    }
    return { ...row, abnormal };
  });

  // 异常指标分析
  const labAbnormalText = labRows
    .filter((row: any) => row.abnormal)
    .map(({ item, result, unit }: any) => {
      if (item.includes('血糖') || item.includes('糖化')) {
        return `患者${item}(${result}${unit})高于正常范围，提示血糖控制不佳。`;
      }
      if (item.includes('胆固醇')) {
        return `总胆固醇(${result}${unit})升高，存在血脂代谢异常风险。`;
      }
      return `${item}(${result}${unit})异常。`;
    });

  // 影像学分析（假设有type、date字段）
  const imageGroups = (patient.images || []).map((img: any) => ({
    type: img.type || '影像',
    date: img.checkTime || '-',
    finding: img.finding,
    conclusion: img.conclusion,
  }));

  // 用药分析（假数据结构）
  const medRows = patient.meds || [
    {
      name: '盐酸二甲双胍片',
      dose: '0.5g',
      freq: '每日三次',
      start: '2023-07-01',
      purpose: '降糖',
    },
    {
      name: '硝苯地平控释片',
      dose: '30mg',
      freq: '每日一次',
      start: '2023-07-01',
      purpose: '降压',
    },
    {
      name: '阿司匹林肠溶片',
      dose: '100mg',
      freq: '每日一次',
      start: '2023-07-02',
      purpose: '抗血小板聚集',
    },
  ];

  const medAdvice =
    '患者目前使用二甲双胍联合硝苯地平治疗，符合高血压合并2型糖尿病的基本用药原则。建议监测血糖变化，必要时调整降糖药物剂量或种类。';
</script>

<style scoped>
  .report-container {
    display: flex;
    align-items: flex-start;
    justify-content: center;
    min-height: 100vh;
    padding: 40px 0;
    background: #f5f6fa;
  }

  .report-paper {
    width: 100%;
    max-width: 820px;
    margin: 0 auto;
    padding: 48px 56px 40px;
    font-family: 'Microsoft YaHei', Arial, sans-serif;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 24px rgb(0 0 0 / 8%);
  }

  .report-title {
    margin-bottom: 32px;
    font-weight: bold;
    font-size: 2.2rem;
    letter-spacing: 2px;
    text-align: center;
  }

  .report-section-title {
    margin-top: 32px;
    margin-bottom: 12px;
    padding-left: 10px;
    color: #2d3a4b;
    font-weight: bold;
    font-size: 1.25rem;
    border-left: 4px solid #1765ad;
  }

  .report-content {
    margin-bottom: 18px;
    color: #222;
    font-size: 1.1rem;
    line-height: 2;
  }
</style>
