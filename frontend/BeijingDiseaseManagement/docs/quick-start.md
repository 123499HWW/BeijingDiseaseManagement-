# Excel 转 Mock 数据 - 快速开始

## 🚀 最简单的方法

### 方法 1：使用 ConvertCSV.com（推荐）

1. **准备 Excel 文件**

   - 确保第一行是字段名
   - 数据从第二行开始
   - 保存为.xlsx 格式

2. **在线转换**

   - 访问：https://www.convertcsv.com/excel-to-json.htm
   - 上传你的 Excel 文件
   - 选择"JSON"作为输出格式
   - 点击"Convert"按钮
   - 下载转换后的 JSON 文件

3. **集成到项目**
   ```typescript
   // 将下载的JSON数据复制到这里
   const patientData = [
     // 你的Excel数据
   ];
   ```

### 方法 2：使用 Mockaroo（适合生成大量数据）

1. **访问 Mockaroo**

   - 网址：https://mockaroo.com/
   - 免费版可以生成 1000 条数据

2. **定义字段**

   - 添加字段名（如：name, age, diagnosis）
   - 选择数据类型（如：Full Name, Age, Disease）
   - 设置生成数量

3. **导出数据**
   - 选择"JSON"格式
   - 点击"Generate Data"
   - 下载 JSON 文件

## 📊 实际使用示例

### 步骤 1：转换 Excel 数据

假设你的 Excel 文件包含以下列：

- 姓名
- 年龄
- 性别
- 诊断
- 入院日期
- 科室

### 步骤 2：创建 API 文件

```typescript
// src/api/patient.ts
export const getPatients = (params?: {
  page?: number;
  pageSize?: number;
  search?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}) => {
  const searchParams = new URLSearchParams();
  if (params?.page) searchParams.append('page', params.page.toString());
  if (params?.pageSize)
    searchParams.append('pageSize', params.pageSize.toString());
  if (params?.search) searchParams.append('search', params.search);
  if (params?.sortBy) searchParams.append('sortBy', params.sortBy);
  if (params?.sortOrder) searchParams.append('sortOrder', params.sortOrder);

  return fetch(`/api/patients?${searchParams.toString()}`).then((res) =>
    res.json()
  );
};
```

### 步骤 3：在组件中使用

```vue
<template>
  <div>
    <a-table :data="patientList" :loading="loading">
      <a-table-column title="姓名" data-index="name" />
      <a-table-column title="年龄" data-index="age" />
      <a-table-column title="诊断" data-index="diagnosis" />
      <a-table-column title="科室" data-index="department" />
    </a-table>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import { getPatients } from '@/api/patient';

  const patientList = ref([]);
  const loading = ref(false);

  const loadPatients = async () => {
    loading.value = true;
    try {
      const response = await getPatients({
        page: 1,
        pageSize: 10,
      });
      patientList.value = response.data.list;
    } catch (error) {
      console.error('加载患者数据失败:', error);
    } finally {
      loading.value = false;
    }
  };

  onMounted(() => {
    loadPatients();
  });
</script>
```

## 🔧 高级配置

### 支持分页

```typescript
// 获取第2页，每页20条数据
const response = await getPatients({
  page: 2,
  pageSize: 20,
});
```

### 支持搜索

```typescript
// 搜索包含"高血压"的患者
const response = await getPatients({
  search: '高血压',
});
```

### 支持排序

```typescript
// 按年龄降序排列
const response = await getPatients({
  sortBy: 'age',
  sortOrder: 'desc',
});
```

## 📝 注意事项

1. **数据格式**：确保 Excel 第一行是字段名
2. **数据量**：对于上万条数据，建议分批处理
3. **字段类型**：注意数字、日期等字段的格式
4. **特殊字符**：避免使用特殊字符作为字段名

## 🎯 推荐工作流程

1. **准备数据**：整理 Excel 文件，确保格式正确
2. **在线转换**：使用 ConvertCSV.com 转换
3. **测试数据**：先导入少量数据测试
4. **批量导入**：确认无误后导入全部数据
5. **功能测试**：测试分页、搜索、排序等功能

这样你就可以快速将 Excel 中的上万条数据转换为可用的 mock 数据了！
