-- CURB-65评分相关表创建脚本

-- 1. 创建CURB-65评分结果表
CREATE TABLE IF NOT EXISTS `curb_assessment_result` (
    `curb_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'CURB评分结果ID',
    
    -- CURB-65评分项目
    `age_result` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '年龄结果(年龄≥65岁为1)',
    `confusion_result` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '意识障碍结果(存在意识障碍为1)',
    `urea_result` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '尿素氮结果(尿素氮＞7mmol/L为1)',
    `respiration_result` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '呼吸频率结果(呼吸频率≥30次/分为1)',
    `blood_pressure_result` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '血压结果(SBP＜90mmHg或DBP≤60mmHg为1)',
    
    -- 评分结果
    `total_score` INT NOT NULL DEFAULT 0 COMMENT '总得分(0-5分)',
    `risk_level` VARCHAR(20) NOT NULL DEFAULT '低风险' COMMENT '风险等级(低风险/中风险/高风险)',
    
    -- 备注信息
    `remark` VARCHAR(500) COMMENT '备注',
    
    -- 通用字段
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    
    PRIMARY KEY (`curb_id`),
    INDEX `idx_total_score` (`total_score`),
    INDEX `idx_risk_level` (`risk_level`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CURB-65评分结果表';

-- 2. 创建患者CURB-65评分关联表
CREATE TABLE IF NOT EXISTS `patient_curb_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `patient_id` BIGINT NOT NULL COMMENT '患者ID',
    `curb_id` BIGINT NOT NULL COMMENT 'CURB评分结果ID',
    `assessment_date` DATETIME NOT NULL COMMENT '评估时间',
    `assessment_type` VARCHAR(50) NOT NULL DEFAULT 'ADMISSION' COMMENT '评估类型(ADMISSION/FOLLOW_UP/DISCHARGE)',
    `remark` VARCHAR(500) COMMENT '备注',
    
    -- 通用字段
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    
    PRIMARY KEY (`id`),
    INDEX `idx_patient_id` (`patient_id`),
    INDEX `idx_curb_id` (`curb_id`),
    INDEX `idx_assessment_date` (`assessment_date`),
    INDEX `idx_assessment_type` (`assessment_type`),
    INDEX `idx_is_deleted` (`is_deleted`),
    
    -- 外键约束
    CONSTRAINT `fk_patient_curb_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`patient_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_patient_curb_result` FOREIGN KEY (`curb_id`) REFERENCES `curb_assessment_result` (`curb_id`) ON DELETE CASCADE,
    
    -- 唯一约束：同一患者同一类型的评估在同一时间只能有一条记录
    UNIQUE KEY `uk_patient_assessment` (`patient_id`, `assessment_type`, `assessment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者CURB-65评分关联表';

-- 3. 创建CURB-65评分统计视图
CREATE OR REPLACE VIEW `v_curb_assessment_statistics` AS
SELECT 
    car.risk_level,
    COUNT(*) as patient_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM curb_assessment_result WHERE is_deleted = 0), 2) as percentage,
    AVG(car.total_score) as avg_score,
    MIN(car.total_score) as min_score,
    MAX(car.total_score) as max_score
FROM curb_assessment_result car
WHERE car.is_deleted = 0
GROUP BY car.risk_level
ORDER BY 
    CASE car.risk_level 
        WHEN '低风险' THEN 1 
        WHEN '中风险' THEN 2 
        WHEN '高风险' THEN 3 
        ELSE 4 
    END;

-- 4. 创建患者CURB-65评分详情视图
CREATE OR REPLACE VIEW `v_patient_curb_detail` AS
SELECT 
    p.patient_id,
    p.age,
    p.gender,
    p.admission_date,
    car.curb_id,
    car.age_result,
    car.confusion_result,
    car.urea_result,
    car.respiration_result,
    car.blood_pressure_result,
    car.total_score,
    car.risk_level,
    pcr.assessment_date,
    pcr.assessment_type,
    car.created_at as assessment_created_at
FROM patient_info p
INNER JOIN patient_curb_relation pcr ON p.patient_id = pcr.patient_id
INNER JOIN curb_assessment_result car ON pcr.curb_id = car.curb_id
WHERE p.is_deleted = 0 
  AND pcr.is_deleted = 0 
  AND car.is_deleted = 0
ORDER BY p.patient_id, pcr.assessment_date DESC;

-- 5. 插入示例数据（可选）
-- INSERT INTO curb_assessment_result (age_result, confusion_result, urea_result, respiration_result, blood_pressure_result, total_score, risk_level, created_by, updated_by) 
-- VALUES 
-- (1, 0, 1, 1, 0, 3, '高风险', 'SYSTEM', 'SYSTEM'),
-- (0, 0, 0, 1, 1, 2, '中风险', 'SYSTEM', 'SYSTEM'),
-- (1, 0, 0, 0, 0, 1, '低风险', 'SYSTEM', 'SYSTEM');

-- 6. 创建存储过程：批量计算CURB-65评分
DELIMITER //

CREATE PROCEDURE `CalculateAllCurbScores`(IN p_created_by VARCHAR(100))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_patient_id BIGINT;
    DECLARE v_age INT;
    DECLARE v_blood_urea_nitrogen DECIMAL(10,2);
    DECLARE v_curb_id BIGINT;
    DECLARE v_age_result TINYINT DEFAULT 0;
    DECLARE v_confusion_result TINYINT DEFAULT 0;
    DECLARE v_urea_result TINYINT DEFAULT 0;
    DECLARE v_respiration_result TINYINT DEFAULT 0;
    DECLARE v_blood_pressure_result TINYINT DEFAULT 0;
    DECLARE v_total_score INT DEFAULT 0;
    DECLARE v_risk_level VARCHAR(20);
    
    -- 游标声明
    DECLARE patient_cursor CURSOR FOR 
        SELECT patient_id, age, blood_urea_nitrogen 
        FROM patient_info 
        WHERE is_deleted = 0;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    OPEN patient_cursor;
    
    read_loop: LOOP
        FETCH patient_cursor INTO v_patient_id, v_age, v_blood_urea_nitrogen;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 检查是否已经评估过
        IF EXISTS (SELECT 1 FROM patient_curb_relation WHERE patient_id = v_patient_id AND is_deleted = 0) THEN
            ITERATE read_loop;
        END IF;
        
        -- 重置评分结果
        SET v_age_result = 0;
        SET v_confusion_result = 0;
        SET v_urea_result = 0;
        SET v_respiration_result = 0;
        SET v_blood_pressure_result = 0;
        
        -- 1. 评估年龄
        IF v_age >= 65 THEN
            SET v_age_result = 1;
        END IF;
        
        -- 2. 评估意识障碍（需要查询体检详细信息）
        IF EXISTS (
            SELECT 1 
            FROM patient_examination_relation per
            INNER JOIN physical_examination_detail ped ON per.detail_id = ped.detail_id
            WHERE per.patient_id = v_patient_id 
              AND per.is_deleted = 0 
              AND ped.is_deleted = 0
              AND (ped.general_condition LIKE '%意识障碍%' 
                   OR ped.general_condition LIKE '%意识不清%'
                   OR ped.general_condition LIKE '%昏迷%'
                   OR ped.general_condition LIKE '%嗜睡%'
                   OR ped.general_condition LIKE '%意识模糊%'
                   OR ped.general_condition LIKE '%神志不清%')
        ) THEN
            SET v_confusion_result = 1;
        END IF;
        
        -- 3. 评估尿素氮
        IF v_blood_urea_nitrogen > 7 THEN
            SET v_urea_result = 1;
        END IF;
        
        -- 4. 评估呼吸频率
        IF EXISTS (
            SELECT 1 
            FROM patient_examination_relation per
            INNER JOIN physical_examination_detail ped ON per.detail_id = ped.detail_id
            WHERE per.patient_id = v_patient_id 
              AND per.is_deleted = 0 
              AND ped.is_deleted = 0
              AND ped.respiration >= 30
        ) THEN
            SET v_respiration_result = 1;
        END IF;
        
        -- 5. 评估血压
        IF EXISTS (
            SELECT 1 
            FROM patient_examination_relation per
            INNER JOIN physical_examination_detail ped ON per.detail_id = ped.detail_id
            WHERE per.patient_id = v_patient_id 
              AND per.is_deleted = 0 
              AND ped.is_deleted = 0
              AND (ped.systolic_bp < 90 OR ped.diastolic_bp <= 60)
        ) THEN
            SET v_blood_pressure_result = 1;
        END IF;
        
        -- 计算总分
        SET v_total_score = v_age_result + v_confusion_result + v_urea_result + v_respiration_result + v_blood_pressure_result;
        
        -- 确定风险等级
        IF v_total_score <= 1 THEN
            SET v_risk_level = '低风险';
        ELSEIF v_total_score = 2 THEN
            SET v_risk_level = '中风险';
        ELSE
            SET v_risk_level = '高风险';
        END IF;
        
        -- 插入评估结果
        INSERT INTO curb_assessment_result (
            age_result, confusion_result, urea_result, respiration_result, blood_pressure_result,
            total_score, risk_level, created_by, updated_by, is_deleted
        ) VALUES (
            v_age_result, v_confusion_result, v_urea_result, v_respiration_result, v_blood_pressure_result,
            v_total_score, v_risk_level, p_created_by, p_created_by, 0
        );
        
        SET v_curb_id = LAST_INSERT_ID();
        
        -- 插入关联关系
        INSERT INTO patient_curb_relation (
            patient_id, curb_id, assessment_date, assessment_type, 
            created_by, updated_by, is_deleted
        ) VALUES (
            v_patient_id, v_curb_id, NOW(), 'ADMISSION', 
            p_created_by, p_created_by, 0
        );
        
    END LOOP;
    
    CLOSE patient_cursor;
    
    -- 提交事务
    COMMIT;
    
    SELECT CONCAT('CURB-65评分计算完成，共处理 ', ROW_COUNT(), ' 条记录') AS result;
    
END //

DELIMITER ;

-- 7. 执行批量计算（注释掉，需要手动执行）
-- CALL CalculateAllCurbScores('SYSTEM');

-- 8. 清理存储过程（可选）
-- DROP PROCEDURE IF EXISTS CalculateAllCurbScores;
