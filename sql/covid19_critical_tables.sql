-- COVID-19危重型诊断结果表
CREATE TABLE IF NOT EXISTS `covid19_critical_assessment` (
  `assessment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '诊断ID',
  
  -- 危重型诊断标准
  -- 1. 呼吸衰竭
  `respiratory_failure` tinyint(1) DEFAULT 0 COMMENT '是否呼吸衰竭(0-否,1-是)',
  `mechanical_ventilation` tinyint(1) DEFAULT 0 COMMENT '是否需要机械通气(0-否,1-是)',
  `oxygenation_index` decimal(10,2) DEFAULT NULL COMMENT '氧合指数(mmHg)',
  `co2_partial_pressure` decimal(10,2) DEFAULT NULL COMMENT '二氧化碳分压(mmHg)',
  `ventilator_used` tinyint(1) DEFAULT 0 COMMENT '是否使用呼吸机(0-否,1-是)',
  
  -- 2. 休克
  `shock_present` tinyint(1) DEFAULT 0 COMMENT '是否出现休克(0-否,1-是)',
  `systolic_bp` int(11) DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic_bp` int(11) DEFAULT NULL COMMENT '舒张压(mmHg)',
  `blood_pressure` varchar(50) DEFAULT NULL COMMENT '血压值',
  
  -- 3. ICU监护
  `icu_admission` tinyint(1) DEFAULT 0 COMMENT '是否ICU监护(0-否,1-是)',
  `intensive_care` tinyint(1) DEFAULT 0 COMMENT '是否重症监护(0-否,1-是)',
  
  -- 4. 其他器官衰竭（参考SOFA评分）
  `organ_failure` tinyint(1) DEFAULT 0 COMMENT '是否其他器官衰竭(0-否,1-是)',
  `organ_failure_count` int(11) DEFAULT 0 COMMENT '器官衰竭数量',
  `organ_failure_details` text DEFAULT NULL COMMENT '器官衰竭详情',
  `sofa_score` int(11) DEFAULT NULL COMMENT 'SOFA评分',
  
  -- 诊断结果
  `criteria_met_count` int(11) DEFAULT 0 COMMENT '满足标准数量',
  `is_critical_type` tinyint(1) DEFAULT 0 COMMENT '是否为危重型(0-否,1-是)',
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
  KEY `idx_is_critical_type` (`is_critical_type`),
  KEY `idx_severity_level` (`severity_level`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='COVID-19危重型诊断结果表';

-- COVID-19危重型诊断与患者关联表
CREATE TABLE IF NOT EXISTS `covid19_critical_patient_relation` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='COVID-19危重型诊断与患者关联表';
