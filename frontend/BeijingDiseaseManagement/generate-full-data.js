const XLSX = require('xlsx');
const path = require('path');
const fs = require('fs');

const filePath = path.join(__dirname, 'src/views/patient/data1000.xlsx');
const workbook = XLSX.readFile(filePath);
const worksheet = workbook.Sheets[workbook.SheetNames[0]];
const data = XLSX.utils.sheet_to_json(worksheet);

// 生成完整的TypeScript数据文件
const tsCode = `// 自动生成的患者数据
export interface PatientRecord {
  患者ID: string;
  就诊次数: number;
  住院日期: string;
  性别: string;
  年龄: number;
  主诉: string;
  现病史: string;
  查体: string;
  '动脉血气pH': number;
  ' 动脉血气pO2(mmHg)': number;
  '动脉血气氧合指数(mmHg)': number;
  '动脉血气pCO2(mmHg)': number;
  '血常规血小板计数(×10^9/L)': number;
  '血尿素氮(mmol/L)': number;
  '血肌酐(μmol/L)': number;
  '总胆红素(μmol/L)': number;
  '是否开具胸部CT': string;
  '胸部CT报告': string;
  '是否应用多巴胺': string;
  '是否应用多巴酚丁胺': string;
  '是否应用去甲肾上腺素': string;
  '是否应用血管活性药物': string;
  '是否应用特殊级/限制级抗生素': string;
  '抗生素种类': string;
  '是否应用呼吸机': string;
  '是否入住ICU': string;
}

export const patientMockData: PatientRecord[] = ${JSON.stringify(data, null, 2)};
`;

fs.writeFileSync(path.join(__dirname, 'src/views/patient/patient-data.ts'), tsCode);
console.log('✓ Generated patient-data.ts with', data.length, 'records');



