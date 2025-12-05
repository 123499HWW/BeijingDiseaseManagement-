-- SOFA评分结果表
CREATE TABLE IF NOT EXISTS `sofa_assessment` (
  `assessment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评分ID',
  
  -- SOFA评分项（每个器官系统0-4分）
  -- 1. 呼吸系统
  `oxygenation_index` decimal(10,2) DEFAULT NULL COMMENT '氧合指数(PaO2/FiO2)',
  `respiration_score` int(11) DEFAULT 0 COMMENT '呼吸系统评分(0-4分)',
  
  -- 2. 凝血系统
  `platelet_count` decimal(10,2) DEFAULT NULL COMMENT '血小板计数(×10^9/L)',
  `coagulation_score` int(11) DEFAULT 0 COMMENT '凝血系统评分(0-4分)',
  
  -- 3. 肝脏系统
  `bilirubin` decimal(10,2) DEFAULT NULL COMMENT '总胆红素(μmol/L)',
  `liver_score` int(11) DEFAULT 0 COMMENT '肝脏系统评分(0-4分)',
  
  -- 4. 心血管系统
  `mean_arterial_pressure` decimal(10,2) DEFAULT NULL COMMENT '平均动脉压(mmHg)',
  `systolic_bp` int(11) DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic_bp` int(11) DEFAULT NULL COMMENT '舒张压(mmHg)',
  `vasoactive_drugs` tinyint(1) DEFAULT 0 COMMENT '是否使用血管活性药物(0-否,1-是)',
  `dopamine_dose` decimal(10,2) DEFAULT NULL COMMENT '多巴胺剂量(μg/kg/min)',
  `dobutamine_dose` decimal(10,2) DEFAULT NULL COMMENT '多巴酚丁胺剂量(μg/kg/min)',
  `norepinephrine_dose` decimal(10,2) DEFAULT NULL COMMENT '去甲肾上腺素剂量(μg/kg/min)',
  `cardiovascular_score` int(11) DEFAULT 0 COMMENT '心血管系统评分(0-4分)',
  
  -- 5. 神经系统
  `glasgow_coma_score` int(11) DEFAULT NULL COMMENT '格拉斯哥昏迷评分(3-15分)',
  `cns_score` int(11) DEFAULT 0 COMMENT '中枢神经系统评分(0-4分)',
  
  -- 6. 肾脏系统
  `creatinine` decimal(10,2) DEFAULT NULL COMMENT '肌酐(μmol/L)',
  `urine_output` decimal(10,2) DEFAULT NULL COMMENT '尿量(ml/d)',
  `renal_score` int(11) DEFAULT 0 COMMENT '肾脏系统评分(0-4分)',
  
  -- 评分结果
  `total_score` int(11) DEFAULT 0 COMMENT '总分(0-24分)',
  `organ_failures` int(11) DEFAULT 0 COMMENT '器官衰竭数量',
  `severity_level` varchar(50) DEFAULT NULL COMMENT '严重程度',
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
  KEY `idx_severity_level` (`severity_level`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOFA评分结果表';

-- SOFA评分与患者关联表
CREATE TABLE IF NOT EXISTS `sofa_patient_relation` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOFA评分与患者关联表';
