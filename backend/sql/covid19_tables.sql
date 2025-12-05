-- COVID-19重型诊断结果表
CREATE TABLE IF NOT EXISTS `covid19_assessment` (
  `assessment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '诊断ID',
  
  -- 重型诊断标准
  `respiratory_rate_high` tinyint(1) DEFAULT 0 COMMENT '呼吸频率≥30次/分(0-否,1-是)',
  `respiratory_rate` int(11) DEFAULT NULL COMMENT '实际呼吸频率(次/分)',
  
  `oxygen_saturation_low` tinyint(1) DEFAULT 0 COMMENT '氧饱和度≤93%(0-否,1-是)',
  `oxygen_saturation` decimal(5,2) DEFAULT NULL COMMENT '实际氧饱和度(%)',
  
  `oxygenation_index_low` tinyint(1) DEFAULT 0 COMMENT '氧合指数≤300mmHg(0-否,1-是)',
  `oxygenation_index` decimal(10,2) DEFAULT NULL COMMENT '实际氧合指数(mmHg)',
  
  `lung_lesion_progression` tinyint(1) DEFAULT 0 COMMENT '肺部病灶进展>50%(0-否,1-是)',
  `lung_lesion_description` text DEFAULT NULL COMMENT '肺部病灶描述',
  
  -- 诊断结果
  `criteria_met_count` int(11) DEFAULT 0 COMMENT '满足标准数量',
  `is_severe_type` tinyint(1) DEFAULT 0 COMMENT '是否为重型(0-否,1-是)',
  `severity_level` varchar(50) DEFAULT NULL COMMENT '严重程度',
  `diagnosis_basis` text DEFAULT NULL COMMENT '诊断依据',
  `assessment_conclusion` text DEFAULT NULL COMMENT '评估结论',
  `recommended_action` text DEFAULT NULL COMMENT '建议措施',
  
  -- 数据来源
  `data_source` varchar(200) DEFAULT NULL COMMENT '数据来源',
  `assessment_time` datetime DEFAULT NULL COMMENT '评估时间',
  `assessment_method` varchar(50) DEFAULT NULL COMMENT '评估方法',
  
  -- 通用字段
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  
  PRIMARY KEY (`assessment_id`),
  KEY `idx_assessment_time` (`assessment_time`),
  KEY `idx_is_severe_type` (`is_severe_type`),
  KEY `idx_severity_level` (`severity_level`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='COVID-19重型诊断结果表';

-- COVID-19诊断与患者关联表
CREATE TABLE IF NOT EXISTS `covid19_patient_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `assessment_id` bigint(20) NOT NULL COMMENT '诊断ID',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `patient_number` varchar(50) DEFAULT NULL COMMENT '患者编号',
  
  -- 关联信息
  `relation_type` varchar(50) DEFAULT 'ASSESSMENT' COMMENT '关联类型',
  `relation_status` varchar(20) DEFAULT 'ACTIVE' COMMENT '关联状态(ACTIVE-有效,INACTIVE-无效)',
  
  -- 通用字段
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_assessment_patient` (`assessment_id`, `patient_id`),
  KEY `idx_assessment_id` (`assessment_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_patient_number` (`patient_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='COVID-19诊断与患者关联表';
