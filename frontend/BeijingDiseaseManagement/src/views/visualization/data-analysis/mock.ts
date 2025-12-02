import Mock from 'mockjs';
import setupMock, { successResponseWrap } from '@/utils/setup-mock';
import { PostData } from '@/types/global';

setupMock({
  setup() {
    Mock.mock(
      new RegExp('/api/public-opinion-analysis'),
      (params: PostData) => {
        const { quota = 'visitors' } = JSON.parse(params.body);
        if (['visitors', 'comment'].includes(quota)) {
          const year = new Date().getFullYear();
          const getLineData = (name: number) => {
            return new Array(12).fill(0).map((_item, index) => ({
              x: `${index + 1}月`,
              y: Mock.Random.natural(0, 100),
              name: String(name),
            }));
          };
          return successResponseWrap({
            count: 5670,
            growth: 206.32,
            chartData: [...getLineData(year), ...getLineData(year - 1)],
          });
        }
        if (['published'].includes(quota)) {
          const year = new Date().getFullYear();
          const getLineData = (name: number) => {
            return new Array(12).fill(0).map((_item, index) => ({
              x: `${index + 1}日`,
              y: Mock.Random.natural(20, 100),
              name: String(name),
            }));
          };
          return successResponseWrap({
            count: 5670,
            growth: 206.32,
            chartData: [...getLineData(year)],
          });
        }
        if (quota === 'cancerIncidence') {
          const cancerTypes = [
            '呼吸道症候群',
            '脓毒症',
            '社区获得性肺炎',
            '其他',
          ];
          const chartData: Array<{ x: string; y: number; name: string }> = [];
          cancerTypes.forEach((type) => {
            for (let i = 1; i <= 12; i += 1) {
              chartData.push({
                x: `${i}月`,
                y: Mock.Random.natural(10, 100),
                name: type,
              });
            }
          });
          return successResponseWrap({
            count: 5670,
            growth: 206.32,
            chartData,
          });
        }
        if (quota === 'ageDistribution') {
          const ageGroups = [
            '0-18岁',
            '19-30岁',
            '31-40岁',
            '41-50岁',
            '51-60岁',
            '61-70岁',
            '71-80岁',
            '81岁及以上',
          ];
          const chartData = ageGroups.map((group) => ({
            name: group,
            y: Mock.Random.natural(10, 100),
          }));
          return successResponseWrap({
            count: 5670,
            growth: 206.32,
            chartData,
          });
        }
        if (quota === 'cancerTypeRatio') {
          // 复用content-period-analysis的特定呼吸道感染性疾病
          const tumorTypes = [
            '直肠恶性肿瘤',
            '乙状结肠恶性肿瘤',
            '横结肠恶性肿瘤',
          ];
          const chartData = tumorTypes.map((name, idx) => ({
            name,
            value: Mock.Random.natural(10, 50),
            itemStyle: { color: ['#5470c6', '#91cc75', '#fac858'][idx % 3] },
          }));
          return successResponseWrap({
            count: 5670,
            growth: 206.32,
            chartData,
          });
        }
        if (quota === 'treatmentType') {
          // 复用content-publish的主要治疗方式
          const treatmentTypes = ['手术', '化疗', '放疗'];
          const chartData = treatmentTypes.map((name, idx) => ({
            name,
            value: Mock.Random.natural(10, 50),
            itemStyle: { color: ['#5470c6', '#91cc75', '#fac858'][idx % 3] },
          }));
          return successResponseWrap({
            count: 5670,
            growth: 206.32,
            chartData,
          });
        }
        return successResponseWrap({
          count: 5670,
          growth: 206.32,
          chartData: [
            // itemStyle for demo
            {
              name: '直肠恶性肿瘤',
              value: 25,
              itemStyle: { color: '#8D4EDA' },
            },
            { name: '图文类', value: 35, itemStyle: { color: '#165DFF' } },
            { name: '视频类', value: 40, itemStyle: { color: '#00B2FF' } },
          ],
        });
      }
    );

    Mock.mock(new RegExp('/api/content-period-analysis'), () => {
      const getLineData = (name: string) => {
        return {
          name,
          value: new Array(12).fill(0).map(() => Mock.Random.natural(30, 90)),
        };
      };
      return successResponseWrap({
        xAxis: new Array(12).fill(0).map((_item, index) => `${index * 2}:00`),
        data: [
          getLineData('直肠恶性肿瘤'),
          getLineData('乙状结肠恶性肿瘤'),
          getLineData('横结肠恶性肿瘤'),
        ],
      });
    });

    Mock.mock(new RegExp('/api/content-publish'), () => {
      const generateLineData = (name: string) => {
        const result = {
          name,
          x: [] as string[],
          y: [] as number[],
        };
        new Array(12).fill(0).forEach((_item, index) => {
          result.x.push(`${index * 2}:00`);
          result.y.push(Mock.Random.natural(1000, 3000));
        });
        return result;
      };
      return successResponseWrap([
        generateLineData('手术'),
        generateLineData('化疗'),
        generateLineData('放疗'),
      ]);
    });

    Mock.mock(new RegExp('/api/popular-author/list'), () => {
      const generateData = () => {
        const list = new Array(7).fill(0).map((_item, index) => ({
          ranking: index + 1,
          author: Mock.mock('@ctitle(5)'),
          contentCount: Mock.mock(/[0-9]{4}/),
          clickCount: Mock.mock(/[0-9]{4}/),
        }));
        return {
          list,
        };
      };
      return successResponseWrap({
        ...generateData(),
      });
    });
  },
});
