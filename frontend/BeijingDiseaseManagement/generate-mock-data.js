const XLSX = require('xlsx');
const path = require('path');

const filePath = path.join(__dirname, 'src/views/patient/data1000.xlsx');
const workbook = XLSX.readFile(filePath);
const worksheet = workbook.Sheets[workbook.SheetNames[0]];
const data = XLSX.utils.sheet_to_json(worksheet);

// 生成前10条记录的mock数据代码
const first10 = data.slice(0, 10);
const mockCode = `  // Mock 数据
  const mockData = ${JSON.stringify(first10, null, 4)};`;

console.log(mockCode);



