-- PSI评分结果表
CREATE TABLE IF NOT EXISTS `psi_assessment_result` (
  `psi_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'PSI评估ID',
  
  -- 人口统计学因素评分
  `gender_score` INT(11) DEFAULT 0 COMMENT '性别评分（女性-10分）',
  `age_score` INT(11) DEFAULT 0 COMMENT '年龄评分',
  
  -- 基础疾病评分
  `tumor_score` INT(11) DEFAULT 0 COMMENT '肿瘤评分（+30分）',
  `liver_disease_score` INT(11) DEFAULT 0 COMMENT '肝病评分（+20分）',
  `heart_failure_score` INT(11) DEFAULT 0 COMMENT '充血性心力衰竭评分（+10分）',
  `cerebrovascular_disease_score` INT(11) DEFAULT 0 COMMENT '脑血管疾病评分（+10分）',
  `kidney_disease_score` INT(11) DEFAULT 0 COMMENT '肾病评分（+10分）',
  
  -- 体格检查评分
  `mental_status_change_score` INT(11) DEFAULT 0 COMMENT '精神状态改变评分（+20分）',
  `heart_rate_score` INT(11) DEFAULT 0 COMMENT '心率>125次/分评分（+20分）',
  `respiratory_rate_score` INT(11) DEFAULT 0 COMMENT '呼吸频率>30次/分评分（+20分）',
  `systolic_bp_score` INT(11) DEFAULT 0 COMMENT '收缩压<90mmHg评分（+20分）',
  `temperature_score` INT(11) DEFAULT 0 COMMENT '体温<35℃或>40℃评分（+15分）',
  
  -- 实验室检查评分
  `arterial_ph_score` INT(11) DEFAULT 0 COMMENT '动脉血pH<7.35评分（+30分）',
  `blood_urea_nitrogen_score` INT(11) DEFAULT 0 COMMENT '血尿素氮>30mg/dl评分（+20分）',
  `serum_sodium_score` INT(11) DEFAULT 0 COMMENT '血钠<130mmol/L评分（+20分）',
  `blood_glucose_score` INT(11) DEFAULT 0 COMMENT '血糖>14mmol/L评分（+10分）',
  `hematocrit_score` INT(11) DEFAULT 0 COMMENT '红细胞压积<30%评分（+10分）',
  `pao2_score` INT(11) DEFAULT 0 COMMENT 'PaO2<60mmHg评分（+10分）',
  
  -- 影像学检查评分
  `pleural_effusion_score` INT(11) DEFAULT 0 COMMENT '胸腔积液评分（+10分）',
  
  -- 评分结果
  `total_score` INT(11) NOT NULL COMMENT 'PSI总分',
  `risk_class` VARCHAR(10) NOT NULL COMMENT 'PSI风险等级（I-V级）',
  `risk_description` VARCHAR(100) DEFAULT NULL COMMENT '风险等级描述',
  `recommended_treatment` VARCHAR(200) DEFAULT NULL COMMENT '建议处理方式',
  `mortality_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '预期死亡率（%）',
  
  -- 评估详情
  `assessment_time` DATETIME NOT NULL COMMENT '评估时间',
  `assessment_method` VARCHAR(50) DEFAULT NULL COMMENT '评估方法',
  `assessment_notes` TEXT DEFAULT NULL COMMENT '评估备注',
  `data_source` VARCHAR(100) DEFAULT NULL COMMENT '数据来源',
  
  -- 通用字段
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` VARCHAR(50) NOT NULL COMMENT '更新人',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  
  PRIMARY KEY (`psi_id`),
  KEY `idx_risk_class` (`risk_class`),
  KEY `idx_assessment_time` (`assessment_time`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSI评分结果表';

-- PSI患者关联关系表
CREATE TABLE IF NOT EXISTS `psi_patient_relation` (
  `relation_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关联关系ID',
  `psi_id` BIGINT(20) NOT NULL COMMENT 'PSI评估ID',
  `patient_id` BIGINT(20) NOT NULL COMMENT '患者ID',
  `patient_number` VARCHAR(50) NOT NULL COMMENT '患者编号',
  `relation_type` VARCHAR(20) DEFAULT 'ASSESSMENT' COMMENT '关联类型',
  `relation_status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '关联状态（ACTIVE-有效，INACTIVE-无效）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
  
  -- 通用字段
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` VARCHAR(50) NOT NULL COMMENT '更新人',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_psi_patient` (`psi_id`, `patient_id`),
  KEY `idx_psi_id` (`psi_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_patient_number` (`patient_number`),
  KEY `idx_relation_status` (`relation_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSI患者关联关系表';

-- 添加外键约束
ALTER TABLE `psi_patient_relation` 
ADD CONSTRAINT `fk_psi_relation_psi_id` 
FOREIGN KEY (`psi_id`) REFERENCES `psi_assessment_result` (`psi_id`) ON DELETE CASCADE;

ALTER TABLE `psi_patient_relation` 
ADD CONSTRAINT `fk_psi_relation_patient_id` 
FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`patient_id`) ON DELETE CASCADE;

-- 创建索引优化查询性能
CREATE INDEX `idx_psi_risk_mortality` ON `psi_assessment_result` (`risk_class`, `mortality_rate`);
CREATE INDEX `idx_psi_patient_assessment` ON `psi_patient_relation` (`patient_id`, `created_at` DESC);

-- 插入示例数据说明
INSERT INTO `psi_assessment_result` (
  `psi_id`, `gender_score`, `age_score`, `total_score`, 
  `risk_class`, `risk_description`, `recommended_treatment`, `mortality_rate`,
  `assessment_time`, `assessment_method`, `data_source`,
  `created_by`, `updated_by`
) VALUES (
  1, -10, 65, 55, 
  'II', '低风险', '门诊治疗或短期观察', 0.60,
  NOW(), '自动评分', '患者基本信息',
  'SYSTEM', 'SYSTEM'
) ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- 插入关联关系示例数据
INSERT INTO `psi_patient_relation` (
  `relation_id`, `psi_id`, `patient_id`, `patient_number`,
  `relation_type`, `relation_status`, `created_by`, `updated_by`
) VALUES (
  1, 1, 1, 'P001',
  'ASSESSMENT', 'ACTIVE', 'SYSTEM', 'SYSTEM'
) ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- 查询统计示例
-- 按风险等级统计患者数量
SELECT 
  risk_class,
  risk_description,
  COUNT(*) as patient_count,
  AVG(total_score) as avg_score,
  AVG(mortality_rate) as avg_mortality_rate
FROM psi_assessment_result 
WHERE is_deleted = 0 
GROUP BY risk_class, risk_description 
ORDER BY risk_class;

-- 查询高风险患者
SELECT 
  par.patient_number,
  par.total_score,
  par.risk_class,
  par.risk_description,
  par.mortality_rate,
  par.assessment_time
FROM psi_assessment_result par
WHERE par.is_deleted = 0 
  AND par.risk_class IN ('IV', 'V')
ORDER BY par.total_score DESC, par.assessment_time DESC;
