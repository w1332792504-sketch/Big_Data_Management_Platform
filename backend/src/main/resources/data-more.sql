-- 补充更多测试数据

-- 用户表补充
INSERT IGNORE INTO t_user (username, password, nickname, status, create_time, update_time) VALUES
('chenxi', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '陈曦', 1, NOW(), NOW()),
('huangyan', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '黄燕', 1, NOW(), NOW()),
('zhoujie', '$2a$10$I6za1h5QelVLocXlEXVLYucXy1Vp9w1zOXI8FDcBJSh2wdKWD4GMG', '周杰', 0, NOW(), NOW());

-- 用户角色关联
INSERT IGNORE INTO t_user_role (user_id, role_id) VALUES
(5, 2), (6, 3), (7, 4);

-- 角色表补充
INSERT IGNORE INTO t_role (name, code, description, status, create_time, update_time) VALUES
('产品经理', 'PRODUCT_MANAGER', '可查看所有数据报表和仪表盘', 1, NOW(), NOW());

-- 角色权限关联补充
INSERT IGNORE INTO t_role_permission (role_id, permission_id) VALUES
(5, 7), (5, 8), (5, 9);

-- 权限表补充 - 按钮级权限
INSERT IGNORE INTO t_permission (parent_id, name, code, type, path, status, create_time) VALUES
(7, '新建数据源', 'datasource:create', 'BUTTON', '', 1, NOW()),
(7, '编辑数据源', 'datasource:edit', 'BUTTON', '', 1, NOW()),
(7, '删除数据源', 'datasource:delete', 'BUTTON', '', 1, NOW()),
(7, '测试连接', 'datasource:test', 'BUTTON', '', 1, NOW()),
(8, '新建任务', 'task:create', 'BUTTON', '', 1, NOW()),
(8, '执行任务', 'task:run', 'BUTTON', '', 1, NOW()),
(8, '停止任务', 'task:stop', 'BUTTON', '', 1, NOW());

-- 组织表补充
INSERT IGNORE INTO t_organization (parent_id, name, code, type, leader, phone, address, status, remark, create_time) VALUES
(1, '产品部', 'PRODUCT', 'DEPT', '周杰', '021-88886603', '上海市浦东新区张江高科技园区C座', 1, '产品设计与规划', NOW()),
(4, 'ETL开发岗', 'ETL_DEV', 'POSITION', '吴强', '', '', 1, '负责ETL流程开发', NOW()),
(5, '监控值班岗', 'MONITOR', 'POSITION', '郑浩', '', '', 1, '7x24小时监控值班', NOW());

-- 数据源表补充
INSERT IGNORE INTO t_datasource (name, type, jdbc_url, username, password, host, port, db_name, description, status, create_time) VALUES
('日志分析ClickHouse', 'CLICKHOUSE', 'jdbc:clickhouse://10.0.2.30:8123/logs', 'default', '', '10.0.2.30', 8123, 'logs', '应用日志分析数据库', 1, NOW()),
('数据集市MySQL', 'MYSQL', 'jdbc:mysql://10.0.1.40:3306/data_mart', 'mart_user', '***', '10.0.1.40', 3306, 'data_mart', '数据集市，供报表和BI使用', 0, NOW());

-- 任务表补充
INSERT IGNORE INTO t_extract_task (task_name, source_datasource_id, target_datasource_id, source_table, target_table, extract_mode, description, status, cron_expression, create_time) VALUES
('日志数据归档', 6, 4, 'app_logs', 'dwd_app_logs', 'INCREMENT', '每小时归档应用日志到Hive', 1, '0 5 * * * ?', NOW()),
('商品维度表更新', 2, 5, 't_product', 'dim_product_v2', 'FULL', '每日凌晨全量更新商品维度表', 0, '0 0 4 * * ?', NOW());

-- 任务执行记录补充
INSERT IGNORE INTO t_task_execution (task_id, task_name, execution_type, status, start_time, end_time, duration, extract_count, source_count, operator, error_msg) VALUES
(1, '用户数据同步至数仓', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 50 HOUR), DATE_SUB(NOW(), INTERVAL 49 HOUR), 3600, 11200, 11200, '系统调度', ''),
(2, '订单数据实时同步', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 1650, 7820, 7820, '系统调度', ''),
(3, '财务报表数据抽取', 'MANUAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 72 HOUR), DATE_SUB(NOW(), INTERVAL 71 HOUR), 2100, 4800, 4800, '刘洋', ''),
(4, '用户行为日志同步', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 90 MINUTE), DATE_SUB(NOW(), INTERVAL 60 MINUTE), 1800, 38500, 38500, '系统调度', ''),
(6, '日志数据归档', 'SCHEDULE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), 900, 128000, 128000, '系统调度', '');
