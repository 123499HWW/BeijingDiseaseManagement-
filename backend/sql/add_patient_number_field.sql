-- 为patient_info表添加patient_number字段的迁移脚本

-- 1. 添加patient_number字段
ALTER TABLE `patient_info` 
ADD COLUMN `patient_number` VARCHAR(20) NULL COMMENT '患者编号(格式如P0001)' 
AFTER `patient_id`;

-- 2. 为patient_number字段添加唯一索引
ALTER TABLE `patient_info` 
ADD UNIQUE INDEX `uk_patient_number` (`patient_number`);

-- 3. 创建函数生成下一个患者编号
DELIMITER //

CREATE FUNCTION `GenerateNextPatientNumber`() RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE next_number INT DEFAULT 1;
    DECLARE patient_number VARCHAR(20);
    
    -- 获取当前最大的患者编号
    SELECT COALESCE(MAX(CAST(SUBSTRING(patient_number, 2) AS UNSIGNED)), 0) + 1 
    INTO next_number
    FROM patient_info 
    WHERE patient_number IS NOT NULL 
      AND patient_number REGEXP '^P[0-9]+$';
    
    -- 生成格式化的患者编号
    SET patient_number = CONCAT('P', LPAD(next_number, 4, '0'));
    
    RETURN patient_number;
END //

DELIMITER ;

-- 4. 创建存储过程为现有患者生成编号
DELIMITER //

CREATE PROCEDURE `GeneratePatientNumbers`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_patient_id BIGINT;
    DECLARE v_patient_number VARCHAR(20);
    DECLARE counter INT DEFAULT 1;
    
    -- 游标声明：查询所有没有患者编号的患者
    DECLARE patient_cursor CURSOR FOR 
        SELECT patient_id 
        FROM patient_info 
        WHERE patient_number IS NULL 
          AND is_deleted = 0
        ORDER BY patient_id;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    OPEN patient_cursor;
    
    read_loop: LOOP
        FETCH patient_cursor INTO v_patient_id;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 生成患者编号
        SET v_patient_number = CONCAT('P', LPAD(counter, 4, '0'));
        
        -- 更新患者编号
        UPDATE patient_info 
        SET patient_number = v_patient_number,
            updated_at = NOW(),
            updated_by = 'SYSTEM_MIGRATION'
        WHERE patient_id = v_patient_id;
        
        SET counter = counter + 1;
        
    END LOOP;
    
    CLOSE patient_cursor;
    
    -- 提交事务
    COMMIT;
    
    SELECT CONCAT('患者编号生成完成，共处理 ', counter - 1, ' 条记录') AS result;
    
END //

DELIMITER ;

-- 5. 执行存储过程为现有患者生成编号（注释掉，需要手动执行）
-- CALL GeneratePatientNumbers();

-- 6. 创建触发器：新增患者时自动生成编号
DELIMITER //

CREATE TRIGGER `tr_patient_insert_generate_number`
BEFORE INSERT ON `patient_info`
FOR EACH ROW
BEGIN
    -- 如果patient_number为空，则自动生成
    IF NEW.patient_number IS NULL OR NEW.patient_number = '' THEN
        SET NEW.patient_number = GenerateNextPatientNumber();
    END IF;
END //

DELIMITER ;

-- 7. 验证数据完整性的查询
-- 查看患者编号生成情况
-- SELECT 
--     COUNT(*) as total_patients,
--     COUNT(patient_number) as patients_with_number,
--     COUNT(*) - COUNT(patient_number) as patients_without_number
-- FROM patient_info 
-- WHERE is_deleted = 0;

-- 查看患者编号格式
-- SELECT patient_id, patient_number 
-- FROM patient_info 
-- WHERE is_deleted = 0 
-- ORDER BY patient_id 
-- LIMIT 10;

-- 8. 清理函数和存储过程（可选，在确认数据正确后执行）
-- DROP FUNCTION IF EXISTS GenerateNextPatientNumber;
-- DROP PROCEDURE IF EXISTS GeneratePatientNumbers;
