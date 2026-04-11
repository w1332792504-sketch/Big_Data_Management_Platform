-- =============================================
-- 大数据管理平台 - 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `data_manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `data_manager`;

-- ---------------------------------------------
-- 数据源配置表
-- ---------------------------------------------
DROP TABLE IF EXISTS `t_datasource`;
CREATE TABLE `t_datasource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '数据源名称',
  `type` VARCHAR(50) NOT NULL COMMENT '数据源类型：DM/MYSQL/HIVE/ORACLE',
  `jdbc_url` VARCHAR(500) NOT NULL COMMENT 'JDBC 连接 URL',
  `username` VARCHAR(100) NOT NULL COMMENT '用户名',
  `password` VARCHAR(200) NOT NULL COMMENT '密码 (加密)',
  `database` VARCHAR(100) DEFAULT NULL COMMENT '数据库名/Schema',
  `host` VARCHAR(200) DEFAULT NULL COMMENT '主机地址',
  `port` INT DEFAULT NULL COMMENT '端口',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源配置表';

-- ---------------------------------------------
-- 数据抽取任务表
-- ---------------------------------------------
DROP TABLE IF EXISTS `t_extract_task`;
CREATE TABLE `t_extract_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '任务描述',
  `source_datasource_id` BIGINT DEFAULT NULL COMMENT '源数据源 ID',
  `target_datasource_id` BIGINT DEFAULT NULL COMMENT '目标数据源 ID',
  `source_table` VARCHAR(200) DEFAULT NULL COMMENT '源表名',
  `target_table` VARCHAR(200) DEFAULT NULL COMMENT '目标表名',
  `extract_mode` VARCHAR(20) DEFAULT 'FULL' COMMENT '抽取模式：FULL-全量，INCREMENT-增量',
  `increment_field` VARCHAR(100) DEFAULT NULL COMMENT '增量字段',
  `query_sql` TEXT DEFAULT NULL COMMENT '抽取 SQL',
  `write_mode` VARCHAR(20) DEFAULT 'insert' COMMENT '写入模式：INSERT/UPDATE/REPLACE/TRUNCATE',
  `filter_condition` VARCHAR(1000) DEFAULT NULL COMMENT '过滤条件',
  `split_pk` VARCHAR(100) DEFAULT NULL COMMENT '分片字段',
  `split_pk_num` INT DEFAULT 1 COMMENT '并发数',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-停止，1-运行中，2-已完成，3-失败',
  `cron_expression` VARCHAR(50) DEFAULT NULL COMMENT 'Cron 表达式',
  `last_run_time` DATETIME DEFAULT NULL COMMENT '最后执行时间',
  `last_run_status` TINYINT DEFAULT NULL COMMENT '最后执行状态：0-成功，1-失败',
  `last_run_duration` BIGINT DEFAULT NULL COMMENT '最后执行耗时 (秒)',
  `last_run_records` BIGINT DEFAULT NULL COMMENT '最后抽取记录数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_name` (`task_name`),
  KEY `idx_source_ds` (`source_datasource_id`),
  KEY `idx_target_ds` (`target_datasource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据抽取任务表';

-- ---------------------------------------------
-- 任务执行记录表
-- ---------------------------------------------
DROP TABLE IF EXISTS `t_task_execution`;
CREATE TABLE `t_task_execution` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `task_id` BIGINT NOT NULL COMMENT '任务 ID',
  `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `execution_type` VARCHAR(20) DEFAULT 'MANUAL' COMMENT '执行类型：MANUAL-手动，SCHEDULE-定时',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `duration` BIGINT DEFAULT NULL COMMENT '执行耗时 (秒)',
  `status` VARCHAR(20) DEFAULT 'RUNNING' COMMENT '执行状态：RUNNING/SUCCESS/FAILED',
  `source_count` BIGINT DEFAULT NULL COMMENT '源表记录数',
  `target_count` BIGINT DEFAULT NULL COMMENT '目标表记录数',
  `extract_count` BIGINT DEFAULT NULL COMMENT '抽取记录数',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
  `log_path` VARCHAR(500) DEFAULT NULL COMMENT '日志路径',
  `operator` VARCHAR(100) DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行记录表';

-- ---------------------------------------------
-- 用户表
-- ---------------------------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------
-- 初始化示例数据
-- ---------------------------------------------

-- 默认管理员账户
INSERT INTO `t_user` (`username`, `password`, `nickname`, `status`) VALUES
('admin', 'admin', '管理员', 1);

-- 示例数据源 - 达梦
INSERT INTO `t_datasource` (`name`, `type`, `jdbc_url`, `username`, `password`, `database`, `host`, `port`, `description`) VALUES
('达梦生产库', 'DM', 'jdbc:dm://192.168.1.100:5236/PROD', 'SYSDBA', 'Dm@123456', 'PROD', '192.168.1.100', 5236, '生产环境达梦数据库'),
('MySQL 目标库', 'MYSQL', 'jdbc:mysql://192.168.1.200:3306/dw_db?useSSL=false&characterEncoding=utf8', 'root', 'mysql@123', 'dw_db', '192.168.1.200', 3306, '数据仓库 MySQL 库'),
('Hive 数仓', 'HIVE', 'jdbc:hive2://192.168.1.150:10000/default', 'hive', 'hive@123', 'default', '192.168.1.150', 10000, 'Hive 数据仓库');

-- 示例任务
INSERT INTO `t_extract_task` (`task_name`, `description`, `source_datasource_id`, `target_datasource_id`, `source_table`, `target_table`, `extract_mode`, `write_mode`, `status`) VALUES
('人员信息同步', '从达梦同步人员基础信息到 MySQL', 1, 2, 'T_PERSON', 't_person', 'FULL', 'truncate', 0),
('科室数据抽取', '抽取科室统计相关数据', 1, 2, 'T_DEPT_STATS', 't_dept_stats', 'INCREMENT', 'insert', 0);
