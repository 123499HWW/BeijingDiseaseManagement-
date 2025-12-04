-- 更新PSI相关表的主键为自动递增
-- 注意：如果表中已有数据，请先备份数据

-- 1. 修改PSI评分结果表主键为自动递增
ALTER TABLE `psi_assessment_result` MODIFY COLUMN `psi_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'PSI评估ID';

-- 2. 修改PSI患者关联关系表主键为自动递增
ALTER TABLE `psi_patient_relation` MODIFY COLUMN `relation_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关联关系ID';

-- 3. 如果需要重置自增起始值（可选）
-- ALTER TABLE `psi_assessment_result` AUTO_INCREMENT = 1;
-- ALTER TABLE `psi_patient_relation` AUTO_INCREMENT = 1;

-- 验证表结构
SHOW CREATE TABLE `psi_assessment_result`;
SHOW CREATE TABLE `psi_patient_relation`;
