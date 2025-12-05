-- 患者信息表
CREATE TABLE patient_info (
    patient_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '患者ID',
    visit_count INT NOT NULL COMMENT '就诊次数',
    admission_date DATE NOT NULL COMMENT '住院日期',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    age INT NOT NULL COMMENT '年龄',
    chief_complaint TEXT NOT NULL COMMENT '主诉',
    present_illness TEXT NOT NULL COMMENT '现病史',
    physical_examination TEXT NOT NULL COMMENT '查体',
    
    -- 动脉血气指标
    arterial_ph DECIMAL(4,2) COMMENT '动脉血气pH',
    arterial_po2 DECIMAL(6,2) COMMENT '动脉血气pO2(mmHg)',
    arterial_oxygenation_index DECIMAL(6,2) COMMENT '动脉血气氧合指数(mmHg)',
    arterial_pco2 DECIMAL(6,2) COMMENT '动脉血气pCO2(mmHg)',
    
    -- 血液检查指标
    platelet_count DECIMAL(8,2) COMMENT '血常规血小板计数(×10^9/L)',
    blood_urea_nitrogen DECIMAL(6,2) COMMENT '血尿素氮(mmol/L)',
    serum_creatinine DECIMAL(8,2) COMMENT '血肌酐(μmol/L)',
    total_bilirubin DECIMAL(6,2) COMMENT '总胆红素(μmol/L)',
    
    -- 胸部CT相关
    chest_ct_ordered BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否开具胸部CT',
    chest_ct_report TEXT COMMENT '胸部CT报告',
    
    -- 药物使用情况
    dopamine_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否应用多巴胺',
    dobutamine_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否应用多巴酚丁胺',
    norepinephrine_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否应用去甲肾上腺素',
    vasoactive_drugs_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否应用血管活性药物',
    special_antibiotics_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否应用特殊级/限制级抗生素',
    antibiotic_types VARCHAR(500) COMMENT '抗生素种类',
    
    -- 治疗设备使用
    ventilator_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否应用呼吸机',
    icu_admission BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否入住ICU',
    
    -- 通用字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by VARCHAR(50) NOT NULL COMMENT '创建人',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by VARCHAR(50) NOT NULL COMMENT '更新人',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    remark TEXT COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者信息表';

-- 创建索引
CREATE INDEX idx_patient_admission_date ON patient_info(admission_date);
CREATE INDEX idx_patient_gender ON patient_info(gender);
CREATE INDEX idx_patient_age ON patient_info(age);
CREATE INDEX idx_patient_created_at ON patient_info(created_at);
CREATE INDEX idx_patient_is_deleted ON patient_info(is_deleted);

-- 插入示例数据
INSERT INTO patient_info (
    visit_count, admission_date, gender, age, chief_complaint, present_illness, physical_examination,
    arterial_ph, arterial_po2, arterial_oxygenation_index, arterial_pco2,
    platelet_count, blood_urea_nitrogen, serum_creatinine, total_bilirubin,
    chest_ct_ordered, chest_ct_report,
    dopamine_used, dobutamine_used, norepinephrine_used, vasoactive_drugs_used,
    special_antibiotics_used, antibiotic_types,
    ventilator_used, icu_admission,
    created_by, updated_by, remark
) VALUES 
(
    3, '2025-01-25', '女', 50, 
    '发热、咳嗽、胸闷10天',
    '患者受凉后出现发热、咳嗽、胸闷10，自行服药治疗，症状未见好转，症状加重来我院就诊。患者自发病以来，精神可，食欲尚可，睡眠一般，大小便正常。',
    '神志清楚，精神可，查体合作。T 37.8℃，P 84次/分，R 24次/分，BP 119/75mmHg，SpO2 97%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，未闻及干湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
    7.43, 83.6, 398.1, 36.9,
    265, 6.11, 99.7, 7.3,
    TRUE, '【检查方法】行胸部CT平扫检查。\n\n【影像所见】双肺纹理增多，左肺下叶见斑片状影。双肺未见胸腔积液。气管、支气管通畅。\n\n【诊断意见】左下肺炎症改变。建议结合临床。',
    FALSE, FALSE, FALSE, FALSE,
    FALSE, '头孢呋辛',
    FALSE, FALSE,
    'admin', 'admin', '第一个患者示例数据'
),
(
    1, '2024-10-02', '女', 73,
    '咳嗽伴发热10天',
    '患者受凉后出现咳嗽伴发热10，曾于外院就诊，予抗生素治疗，效果欠佳，症状加重来我院就诊。患者自发病以来，精神可，食欲尚可，睡眠一般，大小便正常。',
    '神志清楚，精神萎靡，查体合作。T 38.6℃，P 109次/分，R 28次/分，BP 103/69mmHg，SpO2 92%。皮肤黏膜无黄染，浅表淋巴结未触及肿大。颈软，气管居中。胸廓对称无畸形。肺部：双肺呼吸音粗，可闻及湿性啰音。心脏：心律齐，未闻及病理性杂音。腹部：腹软，无压痛。双下肢无水肿。',
    7.37, 63.6, 219.3, 42.5,
    210, 8.17, 100.1, 19.4,
    TRUE, '【检查方法】行胸部CT平扫检查。\n\n【影像所见】双肺纹理明显增多，两肺中下叶见大片状实变影，可见支气管充气征。双侧胸膜轻度增厚。\n\n【诊断意见】两肺多叶段肺炎。建议密切观察，短期复查。',
    FALSE, FALSE, FALSE, FALSE,
    TRUE, '头孢哌酮舒巴坦+阿奇霉素',
    FALSE, FALSE,
    'admin', 'admin', '第二个患者示例数据'
);
