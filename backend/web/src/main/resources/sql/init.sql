-- 创建数据库
CREATE DATABASE IF NOT EXISTS respiratory_infection DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE respiratory_infection;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `tb_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` enum('ADMIN','USER') NOT NULL DEFAULT 'USER' COMMENT '用户角色',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(50) NOT NULL COMMENT '创建人',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(50) NOT NULL COMMENT '更新人',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
-- 管理员用户：admin/Admin123
-- 普通用户：user/User123
-- 注意：这些密码哈希需要使用真实的BCrypt生成，下面是示例
INSERT INTO `tb_user` (`username`, `password`, `role`, `created_by`, `updated_by`, `remark`) VALUES
('admin', '$2a$10$DowJonesIndex123456789uL.977uium/.og/at2uheWG/igi.', 'ADMIN', 'SYSTEM', 'SYSTEM', '系统管理员'),
('user', '$2a$10$TestUserPassword123456uL.977uium/.og/at2uheWG/igi.', 'USER', 'SYSTEM', 'SYSTEM', '测试用户');
