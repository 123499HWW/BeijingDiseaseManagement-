-- 呼吸道症候群评估表
CREATE TABLE IF NOT EXISTS `respiratory_syndrome_assessment` (
  `syndrome_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '症候群评估ID',
  
  -- ==================== 症状指标 ====================
  `has_dyspnea` TINYINT(1) DEFAULT 0 COMMENT '是否有呼吸困难（0-否，1-是）',
  `dyspnea_level` VARCHAR(20) DEFAULT NULL COMMENT '呼吸困难程度（MILD-轻度，MODERATE-中度，SEVERE-重度）',
  `has_consciousness_disorder` TINYINT(1) DEFAULT 0 COMMENT '是否有意识障碍（0-否，1-是）',
  `consciousness_level` VARCHAR(50) DEFAULT NULL COMMENT '意识障碍程度（嗜睡、昏睡、昏迷等）',
  
  -- ==================== 体征指标 ====================
  `temperature` DECIMAL(4,1) DEFAULT NULL COMMENT '体温（℃）',
  `temperature_level` VARCHAR(20) DEFAULT NULL COMMENT '体温分级（NORMAL-正常，FEVER-发热≥38.5，HIGH_FEVER-高热≥40.0）',
  `heart_rate` INT(11) DEFAULT NULL COMMENT '心率（bpm）',
  `is_tachycardia` TINYINT(1) DEFAULT 0 COMMENT '是否心动过速（>130bpm）',
  `systolic_bp` INT(11) DEFAULT NULL COMMENT '收缩压（mmHg）',
  `diastolic_bp` INT(11) DEFAULT NULL COMMENT '舒张压（mmHg）',
  `is_hypotension` TINYINT(1) DEFAULT 0 COMMENT '是否低血压（<90/60mmHg）',
  `oxygen_saturation` DECIMAL(5,2) DEFAULT NULL COMMENT '血氧饱和度（%）',
  `is_hypoxemia` TINYINT(1) DEFAULT 0 COMMENT '是否低氧血症（<93%）',
  
  -- ==================== 检验指标 - 动脉血气分析 ====================
  `arterial_ph` DECIMAL(4,2) DEFAULT NULL COMMENT '动脉血pH值',
  `is_acidosis` TINYINT(1) DEFAULT 0 COMMENT '是否酸中毒（pH<7.35）',
  `pao2` DECIMAL(6,2) DEFAULT NULL COMMENT '氧分压PaO2（mmHg）',
  `is_hypoxemia_pao2` TINYINT(1) DEFAULT 0 COMMENT '是否低氧血症（PaO2<60mmHg）',
  `pao2_fio2_ratio` DECIMAL(7,2) DEFAULT NULL COMMENT '氧合指数PaO2/FiO2',
  `is_oxygenation_disorder` TINYINT(1) DEFAULT 0 COMMENT '是否氧合障碍（PaO2/FiO2<300）',
  `paco2` DECIMAL(6,2) DEFAULT NULL COMMENT '二氧化碳分压PaCO2（mmHg）',
  `is_hypercapnia` TINYINT(1) DEFAULT 0 COMMENT '是否高碳酸血症（PaCO2>50mmHg）',
  
  -- ==================== 检验指标 - 血常规 ====================
  `platelet_count` DECIMAL(6,2) DEFAULT NULL COMMENT '血小板计数（×10^9/L）',
  `is_thrombocytopenia` TINYINT(1) DEFAULT 0 COMMENT '是否血小板减少（<100×10^9/L）',
  
  -- ==================== 检验指标 - 血生化 ====================
  `blood_urea_nitrogen` DECIMAL(6,2) DEFAULT NULL COMMENT '血尿素氮BUN（mmol/L）',
  `is_bun_elevated` TINYINT(1) DEFAULT 0 COMMENT '是否BUN升高（>7mmol/L）',
  `creatinine` DECIMAL(8,2) DEFAULT NULL COMMENT '肌酐Cr（μmol/L）',
  `is_creatinine_elevated` TINYINT(1) DEFAULT 0 COMMENT '是否肌酐升高',
  `total_bilirubin` DECIMAL(6,2) DEFAULT NULL COMMENT '总胆红素TBIL（μmol/L）',
  `is_bilirubin_elevated` TINYINT(1) DEFAULT 0 COMMENT '是否胆红素升高',
  
  -- ==================== 检查指标 ====================
  `has_chest_ct` TINYINT(1) DEFAULT 0 COMMENT '是否进行胸部CT检查',
  `ct_shows_infection` TINYINT(1) DEFAULT 0 COMMENT 'CT是否显示肺炎等感染征象',
  `ct_multi_lobe_involved` TINYINT(1) DEFAULT 0 COMMENT 'CT是否显示多肺叶受累',
  `ct_findings` TEXT DEFAULT NULL COMMENT 'CT检查发现详情',
  
  -- ==================== 治疗指标 ====================
  `uses_dopamine` TINYINT(1) DEFAULT 0 COMMENT '是否使用多巴胺',
  `uses_dobutamine` TINYINT(1) DEFAULT 0 COMMENT '是否使用多巴酚丁胺',
  `uses_norepinephrine` TINYINT(1) DEFAULT 0 COMMENT '是否使用去甲肾上腺素',
  `uses_special_antibiotics` TINYINT(1) DEFAULT 0 COMMENT '是否使用特殊级/限制级抗生素',
  `antibiotics_list` TEXT DEFAULT NULL COMMENT '使用的抗生素列表',
  `uses_ventilator` TINYINT(1) DEFAULT 0 COMMENT '是否使用呼吸机',
  `ventilator_mode` VARCHAR(50) DEFAULT NULL COMMENT '呼吸机模式',
  
  -- ==================== 治疗场所 ====================
  `in_icu` TINYINT(1) DEFAULT 0 COMMENT '是否住ICU',
  `icu_admission_date` DATETIME DEFAULT NULL COMMENT 'ICU入住时间',
  `icu_discharge_date` DATETIME DEFAULT NULL COMMENT 'ICU出院时间',
  
  -- ==================== 严重程度评估 ====================
  `severity_score` INT(11) DEFAULT 0 COMMENT '严重程度评分',
  `severity_level` VARCHAR(20) DEFAULT NULL COMMENT '严重程度等级（MILD-轻度，MODERATE-中度，SEVERE-重度，CRITICAL-危重）',
  `risk_factors_count` INT(11) DEFAULT 0 COMMENT '危险因素数量',
  `assessment_summary` TEXT DEFAULT NULL COMMENT '评估总结',
  
  -- ==================== 评估信息 ====================
  `assessment_time` DATETIME NOT NULL COMMENT '评估时间',
  `assessment_method` VARCHAR(50) DEFAULT 'AUTO' COMMENT '评估方式（AUTO-自动，MANUAL-手动）',
  `assessor` VARCHAR(100) DEFAULT NULL COMMENT '评估人员',
  `assessment_notes` TEXT DEFAULT NULL COMMENT '评估备注',
  
  -- ==================== 通用字段 ====================
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` VARCHAR(50) NOT NULL COMMENT '更新人',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  
  PRIMARY KEY (`syndrome_id`),
  KEY `idx_severity_level` (`severity_level`),
  KEY `idx_assessment_time` (`assessment_time`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='呼吸道症候群评估表';

-- 呼吸道症候群患者关联关系表
CREATE TABLE IF NOT EXISTS `syndrome_patient_relation` (
  `relation_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关联关系ID',
  `syndrome_id` BIGINT(20) NOT NULL COMMENT '症候群评估ID',
  `patient_id` BIGINT(20) NOT NULL COMMENT '患者ID',
  `patient_number` VARCHAR(50) NOT NULL COMMENT '患者编号',
  `diagnosis` VARCHAR(200) DEFAULT NULL COMMENT '诊断结果',
  `syndrome_type` VARCHAR(50) DEFAULT NULL COMMENT '症候群类型',
  `relation_status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '关联状态（ACTIVE-有效，INACTIVE-无效）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
  
  -- 通用字段
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` VARCHAR(50) NOT NULL COMMENT '更新人',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_syndrome_patient` (`syndrome_id`, `patient_id`),
  KEY `idx_syndrome_id` (`syndrome_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_patient_number` (`patient_number`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='呼吸道症候群患者关联关系表';

-- 添加外键约束
ALTER TABLE `syndrome_patient_relation` 
ADD CONSTRAINT `fk_syndrome_relation_syndrome_id` 
FOREIGN KEY (`syndrome_id`) REFERENCES `respiratory_syndrome_assessment` (`syndrome_id`) ON DELETE CASCADE;

ALTER TABLE `syndrome_patient_relation` 
ADD CONSTRAINT `fk_syndrome_relation_patient_id` 
FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`patient_id`) ON DELETE CASCADE;
