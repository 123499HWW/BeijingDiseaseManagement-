<template>
  <div class="container">
    <Breadcrumb :items="['menu.patient', 'menu.patient.profile']" />
    <a-space direction="vertical" :size="16" fill>
      <!-- 异常指标监控区域 -->
      <PatientInfoCardNew />
      <!-- 图表信息区域 -->

      <div style="padding: 24px; background: #fff; border-radius: 8px">
        <h3 style="margin-bottom: 16px">常见医学指标趋势</h3>

        <Chart :options="easingChartOption" height="500px" />
      </div>
    </a-space>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import Breadcrumb from '@/components/breadcrumb/index.vue';
  import Chart from '@/components/chart/index.vue';
  import PatientInfoCardNew from './PatientInfoCardNew.vue';

  // 缓动函数可视化逻辑
  const N_POINT = 30;
  const easingFuncs: Record<string, (k: number) => number> = {
    linear(k) {
      return k;
    },
    quadraticIn(k) {
      return k * k;
    },
    quadraticOut(k) {
      return k * (2 - k);
    },
    quadraticInOut(k) {
      const t = k * 2;
      if (t < 1) {
        return 0.5 * t * t;
      }
      const t2 = t - 1;
      return -0.5 * (t2 * (t2 - 2) - 1);
    },
    cubicIn(k) {
      return k * k * k;
    },
    cubicOut(k) {
      const t = k - 1;
      return t * t * t + 1;
    },
    cubicInOut(k) {
      const t = k * 2;
      if (t < 1) {
        return 0.5 * t * t * t;
      }
      const t2 = t - 2;
      return 0.5 * (t2 * t2 * t2 + 2);
    },
    quarticIn(k) {
      return k * k * k * k;
    },
    quarticOut(k) {
      const t = k - 1;
      return 1 - t * t * t * t;
    },
    quarticInOut(k) {
      const t = k * 2;
      if (t < 1) {
        return 0.5 * t * t * t * t;
      }
      const t2 = t - 2;
      return -0.5 * (t2 * t2 * t2 * t2 - 2);
    },
    quinticIn(k) {
      return k * k * k * k * k;
    },
    quinticOut(k) {
      const t = k - 1;
      return t * t * t * t * t + 1;
    },
    quinticInOut(k) {
      const t = k * 2;
      if (t < 1) {
        return 0.5 * t * t * t * t * t;
      }
      const t2 = t - 2;
      return 0.5 * (t2 * t2 * t2 * t2 * t2 + 2);
    },
    sinusoidalIn(k) {
      return 1 - Math.cos((k * Math.PI) / 2);
    },
    sinusoidalOut(k) {
      return Math.sin((k * Math.PI) / 2);
    },
    sinusoidalInOut(k) {
      return 0.5 * (1 - Math.cos(Math.PI * k));
    },
    exponentialIn(k) {
      return k === 0 ? 0 : 1024 ** (k - 1);
    },
    exponentialOut(k) {
      return k === 1 ? 1 : 1 - 2 ** (-10 * k);
    },
    exponentialInOut(k) {
      if (k === 0) {
        return 0;
      }
      if (k === 1) {
        return 1;
      }
      const t = k * 2;
      if (t < 1) {
        return 0.5 * 1024 ** (t - 1);
      }
      const t2 = t - 1;
      return 0.5 * (-(2 ** (10 * t2)) + 2);
    },
    circularIn(k) {
      return 1 - Math.sqrt(1 - k * k);
    },
    circularOut(k) {
      const t = k - 1;
      return Math.sqrt(1 - t * t);
    },
    circularInOut(k) {
      const t = k * 2;
      if (t < 1) {
        return -0.5 * (Math.sqrt(1 - t * t) - 1);
      }
      const t2 = t - 2;
      return 0.5 * (Math.sqrt(1 - t2 * t2) + 1);
    },
    elasticIn(k) {
      let s;
      let a = 0.1;
      const p = 0.4;
      if (k === 0) return 0;
      if (k === 1) return 1;
      if (!a || a < 1) {
        a = 1;
        s = p / 4;
      } else {
        s = (p * Math.asin(1 / a)) / (2 * Math.PI);
      }
      k -= 1;
      return -(a * 2 ** (10 * k) * Math.sin(((k - s) * (2 * Math.PI)) / p));
    },
    elasticOut(k) {
      let s;
      let a = 0.1;
      const p = 0.4;
      if (k === 0) return 0;
      if (k === 1) return 1;
      if (!a || a < 1) {
        a = 1;
        s = p / 4;
      } else {
        s = (p * Math.asin(1 / a)) / (2 * Math.PI);
      }
      return a * 2 ** (-10 * k) * Math.sin(((k - s) * (2 * Math.PI)) / p) + 1;
    },
    elasticInOut(k) {
      let s;
      let a = 0.1;
      const p = 0.4;
      if (k === 0) return 0;
      if (k === 1) return 1;
      if (!a || a < 1) {
        a = 1;
        s = p / 4;
      } else {
        s = (p * Math.asin(1 / a)) / (2 * Math.PI);
      }
      let t = k * 2;
      if (t < 1) {
        t -= 1;
        return (
          -0.5 * (a * 2 ** (10 * t) * Math.sin(((t - s) * (2 * Math.PI)) / p))
        );
      }
      t -= 1;
      return (
        a * 2 ** (-10 * t) * Math.sin(((t - s) * (2 * Math.PI)) / p) * 0.5 + 1
      );
    },
    backIn(k) {
      const s = 1.70158;
      return k * k * ((s + 1) * k - s);
    },
    backOut(k) {
      const s = 1.70158;
      const t = k - 1;
      return t * t * ((s + 1) * t + s) + 1;
    },
    backInOut(k) {
      const s = 1.70158 * 1.525;
      const t = k * 2;
      if (t < 1) {
        return 0.5 * (t * t * ((s + 1) * t - s));
      }
      const t2 = t - 2;
      return 0.5 * (t2 * t2 * ((s + 1) * t2 + s) + 2);
    },
    bounceIn(k) {
      return 1 - easingFuncs.bounceOut(1 - k);
    },
    bounceOut(k) {
      const t = k;
      if (t < 1 / 2.75) {
        return 7.5625 * t * t;
      }
      if (t < 2 / 2.75) {
        const t2 = t - 1.5 / 2.75;
        return 7.5625 * t2 * t2 + 0.75;
      }
      if (t < 2.5 / 2.75) {
        const t2 = t - 2.25 / 2.75;
        return 7.5625 * t2 * t2 + 0.9375;
      }
      const t2 = t - 2.625 / 2.75;
      return 7.5625 * t2 * t2 + 0.984375;
    },
    bounceInOut(k) {
      if (k < 0.5) {
        return easingFuncs.bounceIn(k * 2) * 0.5;
      }
      return easingFuncs.bounceOut(k * 2 - 1) * 0.5 + 0.5;
    },
  };

  const grids: any[] = [];
  const xAxes: any[] = [];
  const yAxes: any[] = [];
  const series: any[] = [];
  const titles: any[] = [];
  // 替换曲线名字为图片中出现的医学指标参数
  const indicatorNames = [
    '二氧化碳',
    '碳酸氢根浓度',
    '钠',
    '总蛋白质',
    '白蛋白',
    '球蛋白',
    '白球比',
    '总胆红素',
    '直接胆红素',
    '间接胆红素',
    '谷丙转氨酶',
    '谷草转氨酶',
    '碱性磷酸酶',
    'γ-谷氨酰转肽酶',
    '乳酸脱氢酶',
    '肌酸激酶',
    '肌酸激酶同工酶',
    'α-羟丁酸脱氢酶',
    '总胆固醇',
    '甘油三酯',
    '高密度脂蛋白胆固醇',
    '低密度脂蛋白胆固醇',
    '载脂蛋白A1',
    '载脂蛋白B',
    '脂蛋白a',
    '葡萄糖',
    '尿素',
    '肌酐',
    '尿酸',
    '钾',
    '氯',
    '钙',
    '磷',
    '镁',
    '铁',
    '铁蛋白',
    '转铁蛋白',
    '铜',
    '锌',
    '血清淀粉酶',
    'C-反应蛋白',
    '前白蛋白',
    'β2-微球蛋白',
    '同型半胱氨酸',
    '叶酸',
    '维生素B12',
    '促甲状腺激素',
    '三碘甲状腺原氨酸',
    '甲状腺素',
    '游离三碘甲状腺原氨酸',
    '游离甲状腺素',
    '甲状腺球蛋白',
    '抗甲状腺球蛋白抗体',
    '抗甲状腺过氧化物酶抗体',
    '促卵泡生成素',
    '促黄体生成素',
    '雌二醇',
    '孕酮',
    '睾酮',
    '泌乳素',
    '皮质醇',
    '促肾上腺皮质激素',
    '生长激素',
    '胰岛素样生长因子1',
    '胰岛素样生长因子结合蛋白3',
    '降钙素',
    '甲状旁腺激素',
    '胰高血糖素',
    '胰岛素',
    'C-肽',
    '血清胱抑素C',
    '血清β2-微球蛋白',
    '血清同型半胱氨酸',
    '血清叶酸',
    '血清维生素B12',
    '血清25-羟维生素D',
    '血清甲状腺球蛋白',
    '血清抗甲状腺球蛋白抗体',
    '血清抗甲状腺过氧化物酶抗体',
    '血清促甲状腺激素',
    '血清三碘甲状腺原氨酸',
    '血清游离三碘甲状腺原氨酸',
    '血清甲状腺素',
    '血清游离甲状腺素',
    '血清降钙素',
    '血清甲状旁腺激素',
    '血清胰高血糖素',
    '血清胰岛素样生长因子1',
    '血清胰岛素样生长因子结合蛋白3',
    '血清皮质醇',
    '血清促肾上腺皮质激素',
    '血清雌二醇',
    '血清孕酮',
    '血清睾酮',
    '血清泌乳素',
    '血清促卵泡激素',
    '血清促黄体生成素',
    '血清生长激素',
    '血清胰岛素样生长因子',
    '血清胰岛素样生长因子结合蛋白',
    // ...如有更多可继续补充
  ];
  let count = 0;
  Object.keys(easingFuncs).forEach(function (easingName, idx) {
    const easingFunc = easingFuncs[easingName];
    const data = [];
    for (let i = 0; i <= N_POINT; i += 1) {
      const x = i / N_POINT;
      const y = easingFunc(x);
      data.push([x, y]);
    }
    grids.push({
      show: true,
      borderWidth: 0,
      shadowColor: 'rgba(0, 0, 0, 0.3)',
      shadowBlur: 2,
    });
    xAxes.push({
      type: 'value',
      show: false,
      min: 0,
      max: 1,
      gridIndex: count,
    });
    yAxes.push({
      type: 'value',
      show: false,
      min: -0.4,
      max: 1.4,
      gridIndex: count,
    });
    // 用指标名替换曲线名
    const indicatorName = indicatorNames[idx] || easingName;
    series.push({
      name: indicatorName,
      type: 'line',
      xAxisIndex: count,
      yAxisIndex: count,
      data,
      showSymbol: false,
      animationEasing: easingName,
      animationDuration: 1000,
    });
    titles.push({
      textAlign: 'center',
      text: indicatorName,
      textStyle: {
        fontSize: 12,
        fontWeight: 'normal',
      },
    });
    count += 1;
  });

  const rowNumber = Math.ceil(Math.sqrt(count));
  grids.forEach(function (grid, idx) {
    grid.left = `${((idx % rowNumber) / rowNumber) * 100 + 0.5}%`;
    grid.top = `${(Math.floor(idx / rowNumber) / rowNumber) * 100 + 0.5}%`;
    grid.width = `${(1 / rowNumber) * 100 - 1}%`;
    grid.height = `${(1 / rowNumber) * 100 - 1}%`;

    titles[idx].left = `${parseFloat(grid.left) + parseFloat(grid.width) / 2}%`;
    titles[idx].top = `${parseFloat(grid.top)}%`;
  });

  const easingChartOption = {
    title: titles.concat([
      {
        text: 'Different Easing Functions',
        top: 'bottom',
        left: 'center',
      },
    ]),
    grid: grids,
    xAxis: xAxes,
    yAxis: yAxes,
    series,
  };
</script>
