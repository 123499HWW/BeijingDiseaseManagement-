-- 重症肺炎诊断结果表
CREATE TABLE IF NOT EXISTS `severe_pneumonia_diagnosis` (
  `diagnosis_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '诊断ID',
  
  -- 主要标准（满足1项即为重症肺炎）
  `mechanical_ventilation` tinyint(1) DEFAULT 0 COMMENT '是否使用有创机械通气(0-否,1-是)',
  `vasoactive_drugs` tinyint(1) DEFAULT 0 COMMENT '是否需要血管活性药物(0-否,1-是)',
  
  -- 次要标准（满足3项即为重症肺炎）
  `respiratory_rate_high` tinyint(1) DEFAULT 0 COMMENT '呼吸频率≥30次/分(0-否,1-是)',
  `respiratory_rate` int(11) DEFAULT NULL COMMENT '实际呼吸频率(次/分)',
  
  `oxygenation_index_low` tinyint(1) DEFAULT 0 COMMENT 'PaO2/FiO2≤250mmHg(0-否,1-是)',
  `oxygenation_index` decimal(10,2) DEFAULT NULL COMMENT '实际氧合指数(mmHg)',
  
  `multilobar_infiltrates` tinyint(1) DEFAULT 0 COMMENT '多肺叶浸润(0-否,1-是)',
  `infiltrates_description` text DEFAULT NULL COMMENT '浸润描述',
  
  `consciousness_disorder` tinyint(1) DEFAULT 0 COMMENT '意识障碍或定向障碍(0-否,1-是)',
  `consciousness_description` varchar(500) DEFAULT NULL COMMENT '意识状态描述',
  
  `urea_nitrogen_high` tinyint(1) DEFAULT 0 COMMENT '血尿素氮≥7mmol/L(0-否,1-是)',
  `urea_nitrogen` decimal(10,2) DEFAULT NULL COMMENT '实际血尿素氮值(mmol/L)',
  
  `hypotension` tinyint(1) DEFAULT 0 COMMENT '低血压需要液体复苏(0-否,1-是)',
  `blood_pressure` varchar(50) DEFAULT NULL COMMENT '血压值',
  
  -- 诊断结果
  `major_criteria_count` int(11) DEFAULT 0 COMMENT '满足主要标准数量',
  `minor_criteria_count` int(11) DEFAULT 0 COMMENT '满足次要标准数量',
  `is_severe_pneumonia` tinyint(1) DEFAULT 0 COMMENT '是否为重症肺炎(0-否,1-是)',
  `diagnosis_basis` varchar(500) DEFAULT NULL COMMENT '诊断依据',
  `diagnosis_conclusion` text DEFAULT NULL COMMENT '诊断结论',
  `recommended_action` text DEFAULT NULL COMMENT '建议措施',
  
  -- 数据来源
  `data_source` varchar(200) DEFAULT NULL COMMENT '数据来源',
  `diagnosis_time` datetime DEFAULT NULL COMMENT '诊断时间',
  `diagnosis_method` varchar(50) DEFAULT NULL COMMENT '诊断方法',
  
  -- 通用字段
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  
  PRIMARY KEY (`diagnosis_id`),
  KEY `idx_diagnosis_time` (`diagnosis_time`),
  KEY `idx_is_severe` (`is_severe_pneumonia`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重症肺炎诊断结果表';

-- 重症肺炎诊断与患者关联表
CREATE TABLE IF NOT EXISTS `severe_pneumonia_patient_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `diagnosis_id` bigint(20) NOT NULL COMMENT '诊断ID',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `patient_number` varchar(50) DEFAULT NULL COMMENT '患者编号',
  
  -- 关联信息
  `relation_type` varchar(50) DEFAULT 'DIAGNOSIS' COMMENT '关联类型',
  `relation_status` varchar(20) DEFAULT 'ACTIVE' COMMENT '关联状态(ACTIVE-有效,INACTIVE-无效)',
  
  -- 通用字段
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_diagnosis_patient` (`diagnosis_id`, `patient_id`),
  KEY `idx_diagnosis_id` (`diagnosis_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_patient_number` (`patient_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重症肺炎诊断与患者关联表';
