-- 患者体检信息拆分迁移脚本
-- 创建体检详细信息表和关系表

-- 1. 创建体检详细信息表
CREATE TABLE IF NOT EXISTS `physical_examination_detail` (
    `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '体检详细信息ID',
    
    -- 体格检查拆解字段
    `general_condition` TEXT COMMENT '体格检查第一段信息（一般状况）',
    `temperature` DECIMAL(4,1) COMMENT '体温(T)',
    `pulse` INT COMMENT '脉搏(P)',
    `respiration` INT COMMENT '呼吸(R)',
    `systolic_bp` INT COMMENT '收缩压(SBP)',
    `diastolic_bp` INT COMMENT '舒张压(DBP)',
    `spo2` DECIMAL(5,2) COMMENT '血氧饱和度(SpO2)',
    `other_examination` TEXT COMMENT '体格检查第三段信息（其他检查）',
    `additional_examination` TEXT COMMENT '体格检查第四段信息（如果存在）',
    
    -- 胸部CT报告拆解字段
    `ct_examination_method` TEXT COMMENT 'CT检查方法',
    `ct_imaging_findings` TEXT COMMENT 'CT影像所见',
    `ct_diagnosis_opinion` TEXT COMMENT 'CT诊断意见',
    
    -- 通用字段
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    
    PRIMARY KEY (`detail_id`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体检详细信息表';

-- 2. 创建患者体检信息关系表
CREATE TABLE IF NOT EXISTS `patient_examination_relation` (
    `relation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `patient_id` BIGINT NOT NULL COMMENT '患者ID',
    `detail_id` BIGINT NOT NULL COMMENT '体检详细信息ID',
    `relation_type` VARCHAR(50) NOT NULL COMMENT '关系类型(PHYSICAL_EXAM/CHEST_CT/COMBINED)',
    `remark` VARCHAR(500) COMMENT '备注信息',
    
    -- 通用字段
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    
    PRIMARY KEY (`relation_id`),
    INDEX `idx_patient_id` (`patient_id`),
    INDEX `idx_detail_id` (`detail_id`),
    INDEX `idx_relation_type` (`relation_type`),
    INDEX `idx_is_deleted` (`is_deleted`),
    
    -- 外键约束
    CONSTRAINT `fk_patient_examination_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`patient_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_patient_examination_detail` FOREIGN KEY (`detail_id`) REFERENCES `physical_examination_detail` (`detail_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者体检信息关系表';

-- 3. 数据迁移存储过程
DELIMITER //

CREATE PROCEDURE `MigratePatientExaminationData`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_patient_id BIGINT;
    DECLARE v_physical_examination TEXT;
    DECLARE v_chest_ct_report TEXT;
    DECLARE v_detail_id BIGINT;
    
    -- 游标声明
    DECLARE patient_cursor CURSOR FOR 
        SELECT patient_id, physical_examination, chest_ct_report 
        FROM patient_info 
        WHERE is_deleted = 0 
        AND (physical_examination IS NOT NULL OR chest_ct_report IS NOT NULL);
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    OPEN patient_cursor;
    
    read_loop: LOOP
        FETCH patient_cursor INTO v_patient_id, v_physical_examination, v_chest_ct_report;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 插入体检详细信息
        INSERT INTO physical_examination_detail (
            general_condition,
            temperature,
            pulse,
            respiration,
            systolic_bp,
            diastolic_bp,
            spo2,
            other_examination,
            additional_examination,
            ct_examination_method,
            ct_imaging_findings,
            ct_diagnosis_opinion,
            created_at,
            created_by,
            updated_at,
            updated_by,
            is_deleted
        ) VALUES (
            -- 这里需要根据实际的拆分逻辑来处理
            -- 暂时先插入原始数据，后续可以通过程序进行详细拆分
            SUBSTRING_INDEX(v_physical_examination, '。', 1),
            NULL, -- temperature - 需要从第二段解析
            NULL, -- pulse - 需要从第二段解析
            NULL, -- respiration - 需要从第二段解析
            NULL, -- systolic_bp - 需要从BP解析
            NULL, -- diastolic_bp - 需要从BP解析
            NULL, -- spo2 - 需要从第二段解析
            SUBSTRING_INDEX(SUBSTRING_INDEX(v_physical_examination, '。', 3), '。', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(v_physical_examination, '。', 4), '。', -1),
            -- CT相关字段暂时为空，需要通过程序解析
            NULL, -- ct_examination_method
            NULL, -- ct_imaging_findings  
            NULL, -- ct_diagnosis_opinion
            NOW(),
            'SYSTEM_MIGRATION',
            NOW(),
            'SYSTEM_MIGRATION',
            0
        );
        
        SET v_detail_id = LAST_INSERT_ID();
        
        -- 插入关系记录
        INSERT INTO patient_examination_relation (
            patient_id,
            detail_id,
            relation_type,
            remark,
            created_at,
            created_by,
            updated_at,
            updated_by,
            is_deleted
        ) VALUES (
            v_patient_id,
            v_detail_id,
            'COMBINED',
            '系统迁移生成',
            NOW(),
            'SYSTEM_MIGRATION',
            NOW(),
            'SYSTEM_MIGRATION',
            0
        );
        
    END LOOP;
    
    CLOSE patient_cursor;
    
    -- 提交事务
    COMMIT;
    
    SELECT CONCAT('迁移完成，共处理 ', ROW_COUNT(), ' 条记录') AS result;
    
END //

DELIMITER ;

-- 4. 执行迁移（注释掉，需要手动执行）
-- CALL MigratePatientExaminationData();

-- 5. 清理存储过程（可选）
-- DROP PROCEDURE IF EXISTS MigratePatientExaminationData;
