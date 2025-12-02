const XLSX = require('xlsx');
const path = require('path');
const fs = require('fs');

const filePath = path.join(__dirname, 'src/views/patient/data1000.xlsx');
const workbook = XLSX.readFile(filePath);
const worksheet = workbook.Sheets[workbook.SheetNames[0]];
const data = XLSX.utils.sheet_to_json(worksheet);

// 只取前10条记录
const first10 = data.slice(0, 10);

// 生成Vue文件中的mock数据部分
let mockCode = '  // Mock 数据\n  const mockData = [\n';

first10.forEach((record, index) => {
  mockCode += '    {\n';
  Object.keys(record).forEach(key => {
    const value = record[key];
    let valueStr;
    if (typeof value === 'string') {
      // 转义字符串中的特殊字符
      valueStr = JSON.stringify(value);
    } else if (typeof value === 'number') {
      valueStr = value;
    } else if (value === null || value === undefined) {
      valueStr = 'null';
    } else {
      valueStr = JSON.stringify(value);
    }
    mockCode += `      "${key}": ${valueStr},\n`;
  });
  mockCode += '    },\n';
});

mockCode += '  ];\n';

fs.writeFileSync(path.join(__dirname, 'mock-data-output.txt'), mockCode, 'utf8');
console.log('✓ Generated mock data');
console.log(mockCode.substring(0, 500));



