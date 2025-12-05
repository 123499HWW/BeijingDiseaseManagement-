const XLSX = require('xlsx');
const path = require('path');
const fs = require('fs');

const filePath = path.join(__dirname, 'src/views/patient/data1000.xlsx');
const workbook = XLSX.readFile(filePath);
const worksheet = workbook.Sheets[workbook.SheetNames[0]];
const data = XLSX.utils.sheet_to_json(worksheet);

// 取前100条记录
const first100 = data.slice(0, 100);

// 生成Vue文件中的mock数据部分
let mockCode = '  // Mock 数据\n  const mockData = ';
mockCode += JSON.stringify(first100, null, 4);
mockCode += ';\n';

fs.writeFileSync(path.join(__dirname, 'mock-100-data.txt'), mockCode, 'utf8');
console.log('Generated 100 mock records');
console.log('File size:', mockCode.length, 'bytes');



