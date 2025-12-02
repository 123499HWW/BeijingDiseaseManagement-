<template>
  <div class="interaction-table">
    <!-- 表格上方工具栏 -->
    <div class="table-toolbar">
      <div class="search-area">
        <a-input
          v-model="searchKeyword"
          placeholder="搜索科室、医生或结论"
          style="width: 300px; margin-right: 12px"
          allow-clear
        />
        <a-button type="primary" @click="handleSearch">搜索</a-button>
      </div>
      <div class="action-area">
        <a-button type="primary" @click="showAddModal = true">
          <template #icon>
            <icon-plus />
          </template>
          新增记录
        </a-button>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data="filteredRecords"
      :pagination="{ pageSize: 10 }"
      row-key="id"
    >
      <template #actions="{ record }">
        <a-button type="text" size="small" @click="removeRecord(record.id)"
          >删除</a-button
        >
      </template>
    </a-table>

    <!-- 新增记录弹窗 -->
    <a-modal
      v-model:visible="showAddModal"
      title="新增记录"
      @ok="handleAddRecord"
      @cancel="handleCancelAdd"
      :confirm-loading="addLoading"
    >
      <a-form :model="newRecord" layout="vertical">
        <a-form-item label="科室" required>
          <a-input
            v-model="newRecord.dept"
            placeholder="请输入科室名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="医生" required>
          <a-input
            v-model="newRecord.doctor"
            placeholder="请输入医生姓名"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="结论/建议" required>
          <a-textarea
            v-model="newRecord.comment"
            placeholder="请输入结论或建议"
            :rows="4"
            allow-clear
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { IconPlus } from '@arco-design/web-vue/es/icon';

  const props = defineProps<{ type: string }>();

  interface RecordItem {
    id: number;
    time: string;
    dept: string;
    doctor: string;
    comment: string;
  }

  // 根据不同的type返回不同的记录数据
  const getRecordsByType = (type: string): RecordItem[] => {
    const baseRecords = [
      {
        id: 1,
        time: '2024-06-01 09:30:00',
        dept: '肿瘤内科',
        doctor: '张医生',
        comment: '年龄分布显示中青年患者较多，建议加强健康宣教。',
      },
      {
        id: 2,
        time: '2024-06-01 10:15:00',
        dept: '肿瘤外科',
        doctor: '李医生',
        comment: '地域分布以昆明为主，需关注外地患者流入。',
      },
      {
        id: 3,
        time: '2024-06-01 11:00:00',
        dept: '放疗科',
        doctor: '王医生',
        comment: '职业分布显示部分职业高发，建议定期体检。',
      },
      {
        id: 4,
        time: '2024-06-01 11:45:00',
        dept: '病理科',
        doctor: '赵医生',
        comment: '时间趋势显示近半年发病率上升，需进一步分析原因。',
      },
    ];

    const typeSpecificRecords = {
      'age': [
        {
          id: 5,
          time: '2024-06-01 14:20:00',
          dept: '老年肿瘤科',
          doctor: '陈医生',
          comment: '60岁以上患者占比达45%，建议加强老年患者筛查。',
        },
        {
          id: 6,
          time: '2024-06-01 15:10:00',
          dept: '肿瘤内科',
          doctor: '刘医生',
          comment: '青年患者发病率呈上升趋势，需关注生活方式干预。',
        },
      ],
      'region': [
        {
          id: 7,
          time: '2024-06-01 16:00:00',
          dept: '肿瘤预防科',
          doctor: '孙医生',
          comment: '昆明地区患者集中，建议建立区域医疗中心。',
        },
        {
          id: 8,
          time: '2024-06-01 16:45:00',
          dept: '肿瘤流行病科',
          doctor: '周医生',
          comment: '大理、楚雄地区患者增长明显，需加强基层医疗。',
        },
      ],
      'job': [
        {
          id: 9,
          time: '2024-06-01 17:30:00',
          dept: '肿瘤职业病科',
          doctor: '吴医生',
          comment: '农民群体发病率最高，建议加强农村健康宣教。',
        },
        {
          id: 10,
          time: '2024-06-01 18:15:00',
          dept: '肿瘤体检科',
          doctor: '郑医生',
          comment: '工人群体需定期职业健康检查，预防职业暴露。',
        },
      ],
      'time': [
        {
          id: 11,
          time: '2024-06-01 19:00:00',
          dept: '肿瘤统计科',
          doctor: '林医生',
          comment: '近3个月患者数量增长15%，需分析增长原因。',
        },
        {
          id: 12,
          time: '2024-06-01 19:45:00',
          dept: '肿瘤科研科',
          doctor: '黄医生',
          comment: '季节性分析显示冬季发病率较高，需加强预防。',
        },
      ],
      'diagnosis': [
        {
          id: 13,
          time: '2024-06-01 20:30:00',
          dept: '病理科',
          doctor: '马医生',
          comment: '结肠恶性肿瘤占比最高，建议加强早期筛查。',
        },
        {
          id: 14,
          time: '2024-06-01 21:15:00',
          dept: '肿瘤内科',
          doctor: '朱医生',
          comment: '转移性癌患者预后较差，需优化治疗方案。',
        },
      ],
      'diagnosis-trend': [
        {
          id: 15,
          time: '2024-06-01 22:00:00',
          dept: '肿瘤数据分析科',
          doctor: '胡医生',
          comment: '直肠恶性肿瘤呈上升趋势，需加强筛查力度。',
        },
        {
          id: 16,
          time: '2024-06-01 22:45:00',
          dept: '肿瘤预防科',
          doctor: '郭医生',
          comment: '乙状结肠恶性肿瘤增长最快，建议重点防控。',
        },
      ],
      'blood-disease': [
        {
          id: 17,
          time: '2024-06-02 09:00:00',
          dept: '肿瘤血液科',
          doctor: '何医生',
          comment: 'O型血患者占比最高，需进一步研究血型关联。',
        },
        {
          id: 18,
          time: '2024-06-02 09:45:00',
          dept: '肿瘤遗传科',
          doctor: '高医生',
          comment: 'AB型血患者发病率较低，可能具有保护作用。',
        },
      ],
      'tnm-heatmap': [
        {
          id: 19,
          time: '2024-06-02 10:30:00',
          dept: '肿瘤外科',
          doctor: '徐医生',
          comment: 'III期患者占比最高，需加强早期诊断。',
        },
        {
          id: 20,
          time: '2024-06-02 11:15:00',
          dept: '放疗科',
          doctor: '沈医生',
          comment: 'IV期患者预后差，建议多学科联合治疗。',
        },
      ],
      'region-occupation': [
        {
          id: 21,
          time: '2024-06-02 12:00:00',
          dept: '肿瘤社会医学',
          doctor: '韩医生',
          comment: '昆明地区农民患者最多，需加强农村医疗。',
        },
        {
          id: 22,
          time: '2024-06-02 12:45:00',
          dept: '肿瘤公共卫生科',
          doctor: '杨医生',
          comment: '地域职业关联明显，建议针对性预防策略。',
        },
      ],
      'patient-profile': [
        {
          id: 23,
          time: '2024-06-02 13:30:00',
          dept: '肿瘤个性化医疗科',
          doctor: '董医生',
          comment: '高风险患者特征明显，建议建立预警机制。',
        },
        {
          id: 24,
          time: '2024-06-02 14:15:00',
          dept: '肿瘤精准医学科',
          doctor: '袁医生',
          comment: '患者画像分析有助于个性化治疗方案制定。',
        },
      ],
      'treatment': [
        {
          id: 25,
          time: '2024-06-02 15:00:00',
          dept: '肿瘤药剂科',
          doctor: '蒋医生',
          comment: '不同分期患者用药方案差异明显，需个体化。',
        },
        {
          id: 26,
          time: '2024-06-02 15:45:00',
          dept: '肿瘤临床药学',
          doctor: '韦医生',
          comment: '联合用药方案需根据患者特征调整。',
        },
      ],
    };

    return [
      ...baseRecords,
      ...(typeSpecificRecords[type as keyof typeof typeSpecificRecords] || []),
    ];
  };

  const records = ref<RecordItem[]>(getRecordsByType(props.type));

  // 搜索相关
  const searchKeyword = ref('');

  // 弹窗相关
  const showAddModal = ref(false);
  const addLoading = ref(false);
  const newRecord = ref({ dept: '', doctor: '', comment: '' });

  const columns = [
    { title: '时间', dataIndex: 'time', width: 160 },
    { title: '科室', dataIndex: 'dept', width: 100 },
    { title: '医生', dataIndex: 'doctor', width: 100 },
    { title: '结论/建议', dataIndex: 'comment', width: 240 },
    { title: '操作', slotName: 'actions', width: 80 },
  ];

  // 过滤后的记录
  const filteredRecords = computed(() => {
    if (!searchKeyword.value) {
      return records.value;
    }
    const keyword = searchKeyword.value.toLowerCase();
    return records.value.filter(
      (record) =>
        record.dept.toLowerCase().includes(keyword) ||
        record.doctor.toLowerCase().includes(keyword) ||
        record.comment.toLowerCase().includes(keyword)
    );
  });

  // 搜索处理
  function handleSearch() {
    // 搜索逻辑已通过计算属性实现
    Message.success('搜索完成');
  }

  // 新增记录处理
  function handleAddRecord() {
    if (
      !newRecord.value.dept ||
      !newRecord.value.doctor ||
      !newRecord.value.comment
    ) {
      Message.error('请填写完整信息');
      return;
    }

    addLoading.value = true;

    // 模拟异步操作
    setTimeout(() => {
      records.value.push({
        id: Date.now(),
        time: new Date().toLocaleString(),
        dept: newRecord.value.dept,
        doctor: newRecord.value.doctor,
        comment: newRecord.value.comment,
      });

      // 重置表单
      newRecord.value.dept = '';
      newRecord.value.doctor = '';
      newRecord.value.comment = '';

      addLoading.value = false;
      showAddModal.value = false;
      Message.success('记录添加成功');
    }, 500);
  }

  // 取消新增
  function handleCancelAdd() {
    newRecord.value.dept = '';
    newRecord.value.doctor = '';
    newRecord.value.comment = '';
    showAddModal.value = false;
  }

  function removeRecord(id: number) {
    records.value = records.value.filter((r) => r.id !== id);
    Message.success('记录删除成功');
  }
</script>

<style scoped>
  .interaction-table {
    margin-top: 24px;
    padding: 16px;
    background: #fff;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgb(0 0 0 / 3%);
  }

  .table-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    padding: 16px;
    background: #fafafa;
    border-radius: 6px;
  }

  .search-area {
    display: flex;
    align-items: center;
  }

  .action-area {
    display: flex;
    align-items: center;
  }
</style>
