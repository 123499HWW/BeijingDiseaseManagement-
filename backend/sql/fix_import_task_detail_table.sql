-- 修复 import_task_detail 表的保留字问题
-- 将 row_number 字段重命名为 row_index

USE respiratory_infection;

-- 检查表是否存在
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'respiratory_infection' 
AND TABLE_NAME = 'import_task_detail';

-- 重命名字段（如果表已存在）
ALTER TABLE import_task_detail 
CHANGE COLUMN `row_number` `row_index` INT(11) NOT NULL COMMENT '行号';

-- 如果表不存在，创建新表
CREATE TABLE IF NOT EXISTS `import_task_detail` (
  `detail_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `task_id` BIGINT(20) NOT NULL COMMENT '任务ID',
  `row_index` INT(11) NOT NULL COMMENT '行号',
  `process_status` VARCHAR(20) NOT NULL COMMENT '处理状态',
  `original_data` LONGTEXT COMMENT '原始数据JSON',
  `processed_data_id` BIGINT(20) DEFAULT NULL COMMENT '处理后的数据ID',
  `error_code` VARCHAR(50) DEFAULT NULL COMMENT '错误代码',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `validation_errors` TEXT DEFAULT NULL COMMENT '验证失败字段',
  `process_time` DATETIME NOT NULL COMMENT '处理时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` VARCHAR(50) NOT NULL COMMENT '更新人',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`detail_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导入任务详情表';

-- 验证修改结果
DESCRIBE import_task_detail;
