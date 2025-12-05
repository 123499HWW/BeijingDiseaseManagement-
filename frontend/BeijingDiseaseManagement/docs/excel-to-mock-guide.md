# Excel 转 Mock 数据指南

## 方法一：使用在线转换工具

### 1. ConvertCSV.com

1. 访问 https://www.convertcsv.com/excel-to-json.htm
2. 上传你的 Excel 文件
3. 选择输出格式为 JSON
4. 下载转换后的 JSON 文件
5. 将 JSON 数据复制到项目的 mock 文件中

### 2. Mockaroo

1. 访问 https://mockaroo.com/
2. 定义你的数据字段和类型
3. 设置生成数量（支持上万条）
4. 导出为 JSON 格式
5. 下载并集成到项目中

## 方法二：使用 JSON Server

### 1. 准备数据

```bash
# 安装json-server
npm install -g json-server

# 创建db.json文件
{
  "patients": [
    {
      "id": 1,
      "name": "张三",
      "age": 45,
      "diagnosis": "高血压"
    }
  ]
}
```

### 2. 启动服务

```bash
json-server --watch db.json --port 3001
```

### 3. 在项目中使用

```typescript
// api/patient.ts
export const getPatients = () => {
  return fetch('http://localhost:3001/patients').then((res) => res.json());
};
```

## 方法三：使用在线 API Mock 服务

### 1. MockAPI.io

1. 访问 https://mockapi.io/
2. 创建新项目
3. 定义数据模型
4. 生成 API 端点
5. 在项目中使用生成的 API URL

### 2. JSONPlaceholder

```typescript
// 直接使用免费API
const API_BASE = 'https://jsonplaceholder.typicode.com';

export const getUsers = () => {
  return fetch(`${API_BASE}/users`).then((res) => res.json());
};
```

## 方法四：Excel 转 JSON + 项目集成

### 步骤 1：转换 Excel 数据

1. 使用在线工具将 Excel 转换为 JSON
2. 下载 JSON 文件

### 步骤 2：创建 Mock 文件

```typescript
// src/mock/patient-data.ts
import Mock from 'mockjs';
import setupMock, { successResponseWrap } from '@/utils/setup-mock';

// 从Excel转换的数据
const patientData = [
  // 你的Excel数据
];

setupMock({
  setup() {
    Mock.mock(new RegExp('/api/patients'), () => {
      return successResponseWrap({
        list: patientData,
        total: patientData.length,
      });
    });
  },
});
```

### 步骤 3：集成到项目

```typescript
// src/mock/index.ts
import './patient-data'; // 添加这行
```

## 推荐工具对比

| 工具           | 优点     | 缺点         | 适用场景       |
| -------------- | -------- | ------------ | -------------- |
| ConvertCSV.com | 简单快速 | 功能有限     | 一次性转换     |
| Mockaroo       | 功能强大 | 免费版有限制 | 需要自定义数据 |
| JSON Server    | 完全控制 | 需要部署     | 开发环境       |
| MockAPI.io     | 专业服务 | 付费         | 生产环境       |

## 快速开始

### 对于你的项目，推荐使用：

1. **开发阶段**：使用 JSON Server
2. **演示阶段**：使用 MockAPI.io
3. **生产环境**：使用真实的 API 服务

### 示例：将 Excel 患者数据转换为 Mock

1. 准备 Excel 文件，包含字段：姓名、年龄、诊断、入院日期等
2. 使用 ConvertCSV.com 转换为 JSON
3. 创建 mock 文件并集成到项目中
4. 在组件中调用 API

这样你就可以快速将 Excel 中的上万条数据转换为可用的 mock 数据了！
