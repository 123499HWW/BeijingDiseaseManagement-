const XLSX = require('xlsx');
const path = require('path');
const fs = require('fs');

const filePath = path.join(__dirname, 'src/views/patient/data1000.xlsx');
const workbook = XLSX.readFile(filePath, { encoding: 'utf-8' });
const worksheet = workbook.Sheets[workbook.SheetNames[0]];
const data = XLSX.utils.sheet_to_json(worksheet);

// 获取表头
const headers = Object.keys(data[0] || {});
console.log('=== Headers ===');
console.log(JSON.stringify(headers, null, 2));

console.log('\n=== Total rows ===');
console.log(data.length);

console.log('\n=== First row ===');
console.log(JSON.stringify(data[0], null, 2));

// 生成TypeScript代码
const tsCode = `
interface PatientRecord {
${headers.map(h => `  ${h.replace(/[^a-zA-Z0-9_]/g, '_')}: any;`).join('\n')}
}

const mockData: PatientRecord[] = ${JSON.stringify(data.slice(0, 10), null, 2)};
`;

fs.writeFileSync(path.join(__dirname, 'patient-data.ts'), tsCode);
console.log('\n✓ Generated patient-data.ts');
