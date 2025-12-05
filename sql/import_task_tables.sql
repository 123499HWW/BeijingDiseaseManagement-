-- 导入任务相关表创建脚本

-- 1. 创建导入任务表
CREATE TABLE IF NOT EXISTS `import_task` (
    `task_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `task_number` VARCHAR(50) NOT NULL COMMENT '任务编号',
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `import_type` VARCHAR(50) NOT NULL COMMENT '导入类型(PATIENT_DATA/USER_DATA)',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `task_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态(PENDING/PROCESSING/COMPLETED/FAILED/CANCELLED)',
    `total_count` INT DEFAULT 0 COMMENT '总记录数',
    `success_count` INT DEFAULT 0 COMMENT '成功记录数',
    `failure_count` INT DEFAULT 0 COMMENT '失败记录数',
    `skip_count` INT DEFAULT 0 COMMENT '跳过记录数',
    `start_time` DATETIME COMMENT '处理开始时间',
    `end_time` DATETIME COMMENT '处理结束时间',
    `duration` BIGINT COMMENT '处理耗时(毫秒)',
    `error_message` TEXT COMMENT '错误信息',
    `remark` VARCHAR(500) COMMENT '备注',
    
    -- 通用字段
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    
    PRIMARY KEY (`task_id`),
    UNIQUE KEY `uk_task_number` (`task_number`),
    INDEX `idx_import_type` (`import_type`),
    INDEX `idx_task_status` (`task_status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入任务表';

-- 2. 创建导入任务详情表
CREATE TABLE IF NOT EXISTS `import_task_detail` (
    `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '详情ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `row_number` INT NOT NULL COMMENT '行号(Excel中的行号)',
    `process_status` VARCHAR(20) NOT NULL COMMENT '处理状态(SUCCESS/FAILED/SKIPPED)',
    `original_data` TEXT COMMENT '原始数据(JSON格式)',
    `processed_data_id` BIGINT COMMENT '处理后的数据ID(如果成功导入)',
    `error_code` VARCHAR(50) COMMENT '错误代码',
    `error_message` TEXT COMMENT '错误信息',
    `validation_errors` TEXT COMMENT '验证失败字段',
    `process_time` DATETIME COMMENT '处理时间',
    `remark` VARCHAR(500) COMMENT '备注',
    
    -- 通用字段
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
    
    PRIMARY KEY (`detail_id`),
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_row_number` (`row_number`),
    INDEX `idx_process_status` (`process_status`),
    INDEX `idx_processed_data_id` (`processed_data_id`),
    INDEX `idx_process_time` (`process_time`),
    INDEX `idx_is_deleted` (`is_deleted`),
    
    -- 外键约束
    CONSTRAINT `fk_import_task_detail_task` FOREIGN KEY (`task_id`) REFERENCES `import_task` (`task_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入任务详情表';

-- 3. 创建导入任务统计视图
CREATE OR REPLACE VIEW `v_import_task_statistics` AS
SELECT 
    it.task_id,
    it.task_number,
    it.task_name,
    it.import_type,
    it.task_status,
    it.total_count,
    it.success_count,
    it.failure_count,
    it.skip_count,
    CASE 
        WHEN it.total_count > 0 THEN ROUND((it.success_count * 100.0 / it.total_count), 2)
        ELSE 0.0
    END as success_rate,
    it.start_time,
    it.end_time,
    it.duration,
    it.created_by,
    it.created_at,
    -- 统计详情信息
    COUNT(itd.detail_id) as detail_count,
    COUNT(CASE WHEN itd.process_status = 'SUCCESS' THEN 1 END) as detail_success_count,
    COUNT(CASE WHEN itd.process_status = 'FAILED' THEN 1 END) as detail_failure_count,
    COUNT(CASE WHEN itd.process_status = 'SKIPPED' THEN 1 END) as detail_skip_count
FROM import_task it
LEFT JOIN import_task_detail itd ON it.task_id = itd.task_id AND itd.is_deleted = 0
WHERE it.is_deleted = 0
GROUP BY it.task_id
ORDER BY it.created_at DESC;

-- 4. 创建导入任务状态统计视图
CREATE OR REPLACE VIEW `v_import_status_statistics` AS
SELECT 
    import_type,
    task_status,
    COUNT(*) as task_count,
    SUM(total_count) as total_records,
    SUM(success_count) as total_success,
    SUM(failure_count) as total_failure,
    AVG(CASE WHEN total_count > 0 THEN (success_count * 100.0 / total_count) ELSE 0 END) as avg_success_rate,
    MIN(created_at) as first_task_time,
    MAX(created_at) as last_task_time
FROM import_task 
WHERE is_deleted = 0
GROUP BY import_type, task_status
ORDER BY import_type, task_status;

-- 5. 创建存储过程：清理过期的导入任务
DELIMITER //

CREATE PROCEDURE `CleanExpiredImportTasks`(IN p_days_to_keep INT)
BEGIN
    DECLARE v_cutoff_date DATETIME;
    DECLARE v_deleted_tasks INT DEFAULT 0;
    DECLARE v_deleted_details INT DEFAULT 0;
    
    -- 计算截止日期
    SET v_cutoff_date = DATE_SUB(NOW(), INTERVAL p_days_to_keep DAY);
    
    -- 开始事务
    START TRANSACTION;
    
    -- 删除过期的任务详情
    DELETE FROM import_task_detail 
    WHERE task_id IN (
        SELECT task_id FROM import_task 
        WHERE created_at < v_cutoff_date 
          AND task_status IN ('COMPLETED', 'FAILED', 'CANCELLED')
          AND is_deleted = 0
    );
    
    SET v_deleted_details = ROW_COUNT();
    
    -- 删除过期的任务
    DELETE FROM import_task 
    WHERE created_at < v_cutoff_date 
      AND task_status IN ('COMPLETED', 'FAILED', 'CANCELLED')
      AND is_deleted = 0;
    
    SET v_deleted_tasks = ROW_COUNT();
    
    -- 提交事务
    COMMIT;
    
    SELECT CONCAT('清理完成，删除任务: ', v_deleted_tasks, ' 个，删除详情: ', v_deleted_details, ' 条') AS result;
    
END //

DELIMITER ;

-- 6. 创建存储过程：重置失败的导入任务
DELIMITER //

CREATE PROCEDURE `ResetFailedImportTasks`(IN p_task_ids TEXT)
BEGIN
    DECLARE v_reset_count INT DEFAULT 0;
    
    -- 开始事务
    START TRANSACTION;
    
    -- 重置任务状态
    UPDATE import_task 
    SET task_status = 'PENDING',
        start_time = NULL,
        end_time = NULL,
        duration = NULL,
        error_message = NULL,
        updated_at = NOW(),
        updated_by = 'SYSTEM_RESET'
    WHERE FIND_IN_SET(task_id, p_task_ids) > 0
      AND task_status = 'FAILED'
      AND is_deleted = 0;
    
    SET v_reset_count = ROW_COUNT();
    
    -- 删除相关的详情记录
    DELETE FROM import_task_detail 
    WHERE FIND_IN_SET(task_id, p_task_ids) > 0;
    
    -- 提交事务
    COMMIT;
    
    SELECT CONCAT('重置完成，共重置 ', v_reset_count, ' 个任务') AS result;
    
END //

DELIMITER ;

-- 7. 插入示例数据（可选）
-- INSERT INTO import_task (task_number, task_name, import_type, file_name, file_path, file_size, created_by, updated_by) 
-- VALUES 
-- ('IMPORT_1701234567890', '患者数据导入测试', 'PATIENT_DATA', 'patients.xlsx', '/uploads/patients.xlsx', 1024000, 'admin', 'admin');

-- 8. 创建定时清理任务（可选，需要配置定时任务）
-- 每月清理90天前的已完成任务
-- CALL CleanExpiredImportTasks(90);

-- 9. 清理存储过程（可选）
-- DROP PROCEDURE IF EXISTS CleanExpiredImportTasks;
-- DROP PROCEDURE IF EXISTS ResetFailedImportTasks;
