-- 插入测试数据
-- 用户表 (密码统一为 admin123，BCrypt 加密)
INSERT IGNORE INTO t_user (username, password, nickname, status, create_time, update_time) VALUES
('admin', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '陈志强', 1, NOW(), NOW()),
('wangwei', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '王伟', 1, NOW(), NOW()),
('liuyang', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '刘洋', 1, NOW(), NOW()),
('zhangmin', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '张敏', 1, NOW(), NOW());

-- 角色表
INSERT IGNORE INTO t_role (name, code, description, status, create_time, update_time) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有系统全部权限，可管理所有模块', 1, NOW(), NOW()),
('数据工程师', 'DATA_ENGINEER', '负责数据源配置和抽取任务管理', 1, NOW(), NOW()),
('运维工程师', 'OPS_ENGINEER', '负责系统监控和任务调度运维', 1, NOW(), NOW()),
('数据分析师', 'DATA_ANALYST', '仅可查看仪表盘和数据报告', 1, NOW(), NOW());

-- 权限表
INSERT IGNORE INTO t_permission (parent_id, name, code, type, path, status, create_time) VALUES
(0, '系统管理', 'system', 'MENU', '/system', 1, NOW()),
(1, '用户管理', 'system:user', 'MENU', '/system/user', 1, NOW()),
(1, '角色管理', 'system:role', 'MENU', '/system/role', 1, NOW()),
(1, '权限管理', 'system:permission', 'MENU', '/system/permission', 1, NOW()),
(1, '组织管理', 'system:organization', 'MENU', '/system/organization', 1, NOW()),
(1, '字典管理', 'system:dictionary', 'MENU', '/system/dictionary', 1, NOW()),
(0, '数据源管理', 'datasource', 'MENU', '/datasource', 1, NOW()),
(0, '任务管理', 'task', 'MENU', '/task', 1, NOW()),
(0, '仪表盘', 'dashboard', 'MENU', '/dashboard', 1, NOW());

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS t_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO t_user_role (user_id, role_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4);

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS t_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO t_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9),
(2, 7), (2, 8), (2, 9),
(3, 8), (3, 9),
(4, 9);

-- 组织表
INSERT IGNORE INTO t_organization (parent_id, name, code, type, leader, phone, address, status, remark, create_time) VALUES
(0, '星云科技集团', 'STARTECH', 'DEPT', '陈志强', '021-88886666', '上海市浦东新区张江高科技园区', 1, '集团总部', NOW()),
(1, '数据平台事业部', 'DATA_PLATFORM', 'DEPT', '王伟', '021-88886601', '上海市浦东新区张江高科技园区A座', 1, '负责大数据平台开发与运维', NOW()),
(1, '基础架构部', 'INFRA', 'DEPT', '刘洋', '021-88886602', '上海市浦东新区张江高科技园区B座', 1, '负责基础设施和中间件', NOW()),
(2, '数据开发组', 'DATA_DEV', 'POSITION', '赵磊', '021-88886611', '', 1, '数据抽取与处理开发', NOW()),
(2, '数据运维组', 'DATA_OPS', 'POSITION', '钱进', '021-88886612', '', 1, '数据平台运维与监控', NOW()),
(3, '中间件组', 'MIDDLEWARE', 'POSITION', '孙鹏', '021-88886621', '', 1, 'Redis/Kafka/RabbitMQ运维', NOW());

-- 字典表
INSERT IGNORE INTO t_dictionary (name, code, description, status, create_time) VALUES
('用户状态', 'USER_STATUS', '用户账号状态', 1, NOW()),
('任务状态', 'TASK_STATUS', '数据抽取任务运行状态', 1, NOW()),
('数据源类型', 'DATASOURCE_TYPE', '支持的数据源类型', 1, NOW()),
('抽取模式', 'EXTRACT_MODE', '数据抽取方式', 1, NOW()),
('消息队列状态', 'MQ_STATUS', 'RabbitMQ 队列状态', 1, NOW());

-- 字典项表
INSERT IGNORE INTO t_dictionary_item (dictionary_id, label, value, sort, status) VALUES
(1, '启用', '1', 0, 1),
(1, '禁用', '0', 1, 1),
(2, '运行中', 'RUNNING', 0, 1),
(2, '已完成', 'COMPLETED', 1, 1),
(2, '失败', 'FAILED', 2, 1),
(2, '已停止', 'STOPPED', 3, 1),
(3, 'MySQL', 'MYSQL', 0, 1),
(3, 'PostgreSQL', 'POSTGRESQL', 1, 1),
(3, 'Oracle', 'ORACLE', 2, 1),
(3, 'Hive', 'HIVE', 3, 1),
(3, 'ClickHouse', 'CLICKHOUSE', 4, 1),
(4, '全量抽取', 'FULL', 0, 1),
(4, '增量抽取', 'INCREMENT', 1, 1),
(4, '自定义SQL', 'CUSTOM_SQL', 2, 1),
(5, '运行中', 'RUNNING', 0, 1),
(5, '空闲', 'IDLE', 1, 1),
(5, '阻塞', 'BLOCKED', 2, 1);

-- 数据源表
INSERT IGNORE INTO t_datasource (name, type, jdbc_url, username, password, host, port, db_name, description, status, create_time) VALUES
('用户中心MySQL', 'MYSQL', 'jdbc:mysql://10.0.1.10:3306/user_center', 'data_sync', '***', '10.0.1.10', 3306, 'user_center', '用户中心主库，存储用户注册和认证数据', 1, NOW()),
('订单系统PostgreSQL', 'POSTGRESQL', 'jdbc:postgresql://10.0.1.20:5432/order_db', 'etl_user', '***', '10.0.1.20', 5432, 'order_db', '电商订单系统数据库', 1, NOW()),
('财务系统Oracle', 'ORACLE', 'jdbc:oracle:thin:@10.0.1.30:1521:FINDB', 'finance_sync', '***', '10.0.1.30', 1521, 'FINDB', '财务ERP系统Oracle数据库', 1, NOW()),
('数据仓库Hive', 'HIVE', 'jdbc:hive2://10.0.2.10:10000/default', 'hive', '', '10.0.2.10', 10000, 'default', 'Hadoop数据仓库，存储历史数据', 1, NOW()),
('实时分析ClickHouse', 'CLICKHOUSE', 'jdbc:clickhouse://10.0.2.20:8123/analytics', 'default', '', '10.0.2.20', 8123, 'analytics', 'ClickHouse实时分析数据库', 1, NOW());

-- 任务表
INSERT IGNORE INTO t_extract_task (task_name, source_datasource_id, target_datasource_id, source_table, target_table, extract_mode, description, status, cron_expression, create_time) VALUES
('用户数据同步至数仓', 1, 4, 't_user', 'ods_user', 'INCREMENT', '每日增量同步用户注册数据到Hive数仓', 1, '0 0 2 * * ?', NOW()),
('订单数据实时同步', 2, 5, 't_order', 'ods_order', 'INCREMENT', '每小时增量同步订单数据到ClickHouse', 1, '0 0 * * * ?', NOW()),
('财务报表数据抽取', 3, 4, 't_financial_report', 'dwd_finance', 'FULL', '每日全量同步财务报表数据', 1, '0 30 1 * * ?', NOW()),
('用户行为日志同步', 1, 4, 't_user_behavior', 'dwd_user_behavior', 'INCREMENT', '同步用户行为日志用于用户画像分析', 1, '0 */30 * * * ?', NOW()),
('商品数据同步', 2, 5, 't_product', 'dim_product', 'FULL', '每日全量同步商品维度表', 0, '0 0 3 * * ?', NOW());

-- 任务执行记录表
INSERT IGNORE INTO t_task_execution (task_id, task_name, execution_type, status, start_time, end_time, duration, extract_count, source_count, operator, error_msg) VALUES
(1, '用户数据同步至数仓', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 3520, 15680, 15680, '系统调度', ''),
(1, '用户数据同步至数仓', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 26 HOUR), DATE_SUB(NOW(), INTERVAL 25 HOUR), 3180, 12340, 12340, '系统调度', ''),
(2, '订单数据实时同步', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), 1820, 8960, 8960, '系统调度', ''),
(2, '订单数据实时同步', 'SCHEDULE', 'FAILED', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 600, 0, 0, '系统调度', 'ClickHouse连接超时: Connection refused after 3 retries'),
(3, '财务报表数据抽取', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR), 2400, 5240, 5240, '系统调度', ''),
(4, '用户行为日志同步', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE), 1200, 42350, 42350, '系统调度', ''),
(5, '商品数据同步', 'MANUAL', 'FAILED', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 900, 0, 0, '王伟', '源表t_product不存在');
