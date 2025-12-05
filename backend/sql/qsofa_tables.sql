-- qSOFA评分结果表
CREATE TABLE IF NOT EXISTS `qsofa_assessment` (
  `assessment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评分ID',
  
  -- qSOFA评分项（每项1分，总分0-3分）
  `consciousness_altered` tinyint(1) DEFAULT 0 COMMENT '意识障碍(0-否,1-是)',
  `consciousness_description` varchar(500) DEFAULT NULL COMMENT '意识状态描述',
  
  `respiratory_rate_high` tinyint(1) DEFAULT 0 COMMENT '呼吸频率≥22次/分(0-否,1-是)',
  `respiratory_rate` int(11) DEFAULT NULL COMMENT '实际呼吸频率(次/分)',
  
  `systolic_bp_low` tinyint(1) DEFAULT 0 COMMENT '收缩压≤90mmHg(0-否,1-是)',
  `systolic_bp` int(11) DEFAULT NULL COMMENT '实际收缩压(mmHg)',
  `diastolic_bp` int(11) DEFAULT NULL COMMENT '实际舒张压(mmHg)',
  `blood_pressure` varchar(50) DEFAULT NULL COMMENT '血压值',
  
  -- 评分结果
  `total_score` int(11) DEFAULT 0 COMMENT '总分(0-3分)',
  `risk_level` varchar(50) DEFAULT NULL COMMENT '风险等级',
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
  KEY `idx_total_score` (`total_score`),
  KEY `idx_risk_level` (`risk_level`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='qSOFA评分结果表';

-- qSOFA评分与患者关联表
CREATE TABLE IF NOT EXISTS `qsofa_patient_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `assessment_id` bigint(20) NOT NULL COMMENT '评分ID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='qSOFA评分与患者关联表';
