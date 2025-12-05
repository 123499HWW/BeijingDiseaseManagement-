-- CPIS评分结果表
DROP TABLE IF EXISTS `cpis_assessment_result`;
CREATE TABLE `cpis_assessment_result` (
  `cpis_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'CPIS评分ID',
  
  -- 评分指标
  `temperature_score` int(11) DEFAULT 0 COMMENT '体温评分(0-2分)',
  `wbc_score` int(11) DEFAULT 0 COMMENT '血白细胞评分(0-2分)',
  `secretion_score` int(11) DEFAULT 0 COMMENT '分泌物评分(0-2分)',
  `oxygenation_index_score` int(11) DEFAULT 0 COMMENT '氧合指数评分(0-2分)',
  `chest_xray_score` int(11) DEFAULT 0 COMMENT '胸片浸润影评分(0-2分)',
  `culture_score` int(11) DEFAULT 0 COMMENT '气管吸取物或痰培养评分(0-2分)',
  
  -- 实际数值记录
  `temperature` decimal(3,1) DEFAULT NULL COMMENT '体温(℃)',
  `wbc_count` decimal(10,2) DEFAULT NULL COMMENT '白细胞计数(×10^9/L)',
  `secretion_type` varchar(50) DEFAULT NULL COMMENT '分泌物类型',
  `oxygenation_index` decimal(10,2) DEFAULT NULL COMMENT '氧合指数(mmHg)',
  `chest_xray_finding` text COMMENT '胸片发现',
  `culture_result` text COMMENT '培养结果',
  
  -- 总分和风险评估
  `total_score` int(11) NOT NULL COMMENT 'CPIS总分(0-12分)',
  `risk_level` varchar(20) DEFAULT NULL COMMENT '风险等级(低风险/高风险)',
  `assessment_conclusion` text COMMENT '评估结论',
  `recommended_action` text COMMENT '建议措施',
  
  -- 评估信息
  `assessment_time` datetime DEFAULT NULL COMMENT '评估时间',
  `assessment_method` varchar(50) DEFAULT NULL COMMENT '评估方式',
  `data_source` varchar(100) DEFAULT NULL COMMENT '数据来源',
  
  -- 通用字段
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  `remark` text COMMENT '备注',
  
  PRIMARY KEY (`cpis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CPIS评分结果表';

-- CPIS评分与患者关联表
DROP TABLE IF EXISTS `cpis_patient_relation`;
CREATE TABLE `cpis_patient_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `cpis_id` bigint(20) NOT NULL COMMENT 'CPIS评分ID',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `patient_number` varchar(20) NOT NULL COMMENT '患者编号',
  `relation_type` varchar(50) DEFAULT 'ASSESSMENT' COMMENT '关联类型',
  `relation_status` varchar(20) DEFAULT 'ACTIVE' COMMENT '关联状态',
  
  -- 通用字段
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_cpis_patient` (`cpis_id`, `patient_id`),
  KEY `idx_cpis_id` (`cpis_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_patient_number` (`patient_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CPIS评分与患者关联表';

-- 注：由于数据不可用，不再添加sputum_character和sputum_culture_result字段
