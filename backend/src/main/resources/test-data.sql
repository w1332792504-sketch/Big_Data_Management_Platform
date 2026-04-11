-- 数据质量规则测试数据
INSERT INTO quality_rule (rule_name, rule_type, datasource_id, table_name, column_name, rule_expression, description, severity, status, create_time, update_time) VALUES
('用户表ID完整性检查', 'COMPLETENESS', 1, 't_user', 'id', NULL, '检查用户表ID字段是否为空', 'HIGH', 1, NOW(), NOW()),
('用户表用户名唯一性检查', 'UNIQUENESS', 1, 't_user', 'username', NULL, '检查用户名是否重复', 'HIGH', 1, NOW(), NOW()),
('用户表状态准确性检查', 'ACCURACY', 1, 't_user', 'status', 'status IN (0, 1)', '检查状态值是否在有效范围内', 'MEDIUM', 1, NOW(), NOW()),
('数据源表名称完整性', 'COMPLETENESS', 1, 't_datasource', 'name', NULL, '检查数据源名称是否为空', 'MEDIUM', 1, NOW(), NOW()),
('任务表创建时间时效性', 'TIMELINESS', 1, 't_extract_task', 'create_time', NULL, '检查任务创建时间是否在合理范围内', 'LOW', 1, NOW(), NOW());

-- 数据质量检查结果测试数据
INSERT INTO quality_check_result (rule_id, rule_name, datasource_id, table_name, column_name, check_status, total_records, pass_records, fail_records, pass_rate, check_detail, error_msg, check_time, duration) VALUES
(1, '用户表ID完整性检查', 1, 't_user', 'id', 'PASS', 5, 5, 0, 100.00, '总数: 5, 通过: 5, 失败: 0, 通过率: 100.00%', NULL, NOW(), 156),
(2, '用户表用户名唯一性检查', 1, 't_user', 'username', 'PASS', 5, 5, 0, 100.00, '总数: 5, 通过: 5, 失败: 0, 通过率: 100.00%', NULL, NOW(), 89),
(3, '用户表状态准确性检查', 1, 't_user', 'status', 'PASS', 5, 5, 0, 100.00, '总数: 5, 通过: 5, 失败: 0, 通过率: 100.00%', NULL, NOW(), 45),
(4, '数据源表名称完整性', 1, 't_datasource', 'name', 'PASS', 11, 11, 0, 100.00, '总数: 11, 通过: 11, 失败: 0, 通过率: 100.00%', NULL, NOW(), 67),
(5, '任务表创建时间时效性', 1, 't_extract_task', 'create_time', 'WARNING', 10, 9, 1, 90.00, '总数: 10, 通过: 9, 失败: 1, 通过率: 90.00%', NULL, NOW(), 234);

-- 告警规则测试数据
INSERT INTO alert_rule (rule_name, alert_type, alert_level, alert_condition, description, notify_channel, notify_config, status, create_time, update_time) VALUES
('任务执行失败告警', 'TASK_FAIL', 'HIGH', 'task.status = "FAILED"', '当任务执行失败时触发告警', 'EMAIL', '{"email": "admin@example.com"}', 1, NOW(), NOW()),
('任务执行超时告警', 'TASK_TIMEOUT', 'MEDIUM', 'task.duration > 3600', '当任务执行超过1小时触发告警', 'EMAIL', '{"email": "admin@example.com"}', 1, NOW(), NOW()),
('数据质量检查失败告警', 'QUALITY_FAIL', 'HIGH', 'quality.pass_rate < 80', '当数据质量检查通过率低于80%时触发告警', 'WEBHOOK', '{"url": "http://localhost:8080/api/alert/webhook"}', 1, NOW(), NOW()),
('系统异常告警', 'SYSTEM', 'CRITICAL', 'system.error_count > 10', '当系统错误数量超过阈值时触发告警', 'EMAIL', '{"email": "admin@example.com,ops@example.com"}', 1, NOW(), NOW()),
('数据库连接异常告警', 'SYSTEM', 'HIGH', 'db.connection_failed = true', '当数据库连接失败时触发告警', 'EMAIL', '{"email": "admin@example.com"}', 1, NOW(), NOW());

-- 告警记录测试数据
INSERT INTO alert_record (rule_id, rule_name, alert_type, alert_level, alert_content, status, notify_channel, alert_time, notify_time, acknowledge_time, acknowledged_by, resolve_time, resolved_by, remark) VALUES
(1, '任务执行失败告警', 'TASK_FAIL', 'HIGH', '任务"数据同步任务4"执行失败，错误信息：连接超时', 'RESOLVED', 'EMAIL', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 'admin', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'admin', '已修复网络连接问题'),
(3, '数据质量检查失败告警', 'QUALITY_FAIL', 'HIGH', '表t_extract_task数据质量检查失败，通过率仅90%', 'ACKNOWLEDGED', 'WEBHOOK', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'admin', NULL, NULL, NULL),
(2, '任务执行超时告警', 'TASK_TIMEOUT', 'MEDIUM', '任务"大数据同步任务"执行超时，已运行2小时', 'SENT', 'EMAIL', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, NULL, NULL, NULL, NULL),
(4, '系统异常告警', 'SYSTEM', 'CRITICAL', '系统错误数量达到15次，请立即检查', 'PENDING', 'EMAIL', NOW(), NULL, NULL, NULL, NULL, NULL, NULL),
(5, '数据库连接异常告警', 'SYSTEM', 'HIGH', 'MySQL数据库连接失败，请检查数据库服务', 'PENDING', 'EMAIL', NOW(), NULL, NULL, NULL, NULL, NULL, NULL);

-- 审计日志测试数据
INSERT INTO audit_log (module, operation, description, operator, operator_id, ip, method, request_url, request_params, response_data, response_status, duration, status, error_msg, create_time) VALUES
('DATASOURCE', 'CREATE', '创建数据源"测试MySQL"', 'admin', 1, '192.168.1.100', 'POST', '/api/datasource/save', '{"name":"测试MySQL","type":"MYSQL","jdbcUrl":"jdbc:mysql://localhost:3306/test"}', '{"code":200,"message":"success"}', 200, 156, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('DATASOURCE', 'UPDATE', '更新数据源"测试MySQL"', 'admin', 1, '192.168.1.100', 'PUT', '/api/datasource/1', '{"name":"测试MySQL-更新"}', '{"code":200,"message":"success"}', 200, 89, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('TASK', 'CREATE', '创建抽取任务"用户数据同步"', 'admin', 1, '192.168.1.100', 'POST', '/api/task', '{"taskName":"用户数据同步","sourceDatasourceId":1}', '{"code":200,"message":"success"}', 200, 234, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('TASK', 'EXECUTE', '执行任务"用户数据同步"', 'admin', 1, '192.168.1.100', 'POST', '/api/task/1/run', '{}', '{"code":200,"message":"success"}', 200, 567, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('TASK', 'DELETE', '删除任务"测试任务"', 'admin', 1, '192.168.1.100', 'DELETE', '/api/task/5', '{}', '{"code":200,"message":"success"}', 200, 78, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('USER', 'CREATE', '创建用户"zhangsan"', 'admin', 1, '192.168.1.100', 'POST', '/api/system/user', '{"username":"zhangsan","nickname":"张三"}', '{"code":200,"message":"success"}', 200, 123, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('USER', 'LOGIN', '用户登录', 'admin', 1, '192.168.1.100', 'POST', '/api/auth/login', '{"username":"admin"}', '{"code":200,"message":"success"}', 200, 45, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('QUALITY', 'CREATE', '创建质量规则"用户ID完整性检查"', 'admin', 1, '192.168.1.100', 'POST', '/api/quality/rules', '{"ruleName":"用户ID完整性检查"}', '{"code":200,"message":"success"}', 200, 89, 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
('QUALITY', 'EXECUTE', '执行质量检查"用户ID完整性检查"', 'admin', 1, '192.168.1.100', 'POST', '/api/quality/rules/1/execute', '{}', '{"code":200,"message":"success"}', 200, 156, 'SUCCESS', NULL, NOW()),
('ALERT', 'ACKNOWLEDGE', '确认告警"任务执行失败告警"', 'admin', 1, '192.168.1.100', 'PUT', '/api/alert/records/1/acknowledge', '{}', '{"code":200,"message":"success"}', 200, 34, 'SUCCESS', NULL, NOW());

-- 元数据测试数据（基于现有数据源）
INSERT INTO metadata (datasource_id, database_name, table_name, column_name, column_type, column_length, column_precision, column_scale, is_nullable, is_primary_key, default_value, comment, object_type, row_count, last_sync_time, create_time, update_time) VALUES
(1, 'data_manager', 't_user', 'id', 'BIGINT', 20, NULL, NULL, false, true, NULL, '用户ID', 'COLUMN', 5, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_user', 'username', 'VARCHAR', 50, NULL, NULL, false, false, NULL, '用户名', 'COLUMN', 5, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_user', 'password', 'VARCHAR', 255, NULL, NULL, false, false, NULL, '密码', 'COLUMN', 5, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_user', 'nickname', 'VARCHAR', 50, NULL, NULL, true, false, NULL, '昵称', 'COLUMN', 5, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_user', 'status', 'INT', 11, NULL, NULL, false, false, '1', '状态', 'COLUMN', 5, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_user', NULL, NULL, NULL, NULL, NULL, false, false, NULL, '用户表', 'TABLE', 5, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_datasource', 'id', 'BIGINT', 20, NULL, NULL, false, true, NULL, '数据源ID', 'COLUMN', 11, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_datasource', 'name', 'VARCHAR', 100, NULL, NULL, false, false, NULL, '数据源名称', 'COLUMN', 11, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_datasource', 'type', 'VARCHAR', 50, NULL, NULL, false, false, NULL, '数据源类型', 'COLUMN', 11, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_datasource', 'jdbc_url', 'VARCHAR', 500, NULL, NULL, false, false, NULL, 'JDBC连接URL', 'COLUMN', 11, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_datasource', NULL, NULL, NULL, NULL, NULL, false, false, NULL, '数据源表', 'TABLE', 11, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_extract_task', 'id', 'BIGINT', 20, NULL, NULL, false, true, NULL, '任务ID', 'COLUMN', 10, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_extract_task', 'task_name', 'VARCHAR', 100, NULL, NULL, false, false, NULL, '任务名称', 'COLUMN', 10, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_extract_task', 'status', 'INT', 11, NULL, NULL, false, false, '0', '任务状态', 'COLUMN', 10, NOW(), NOW(), NOW()),
(1, 'data_manager', 't_extract_task', NULL, NULL, NULL, NULL, NULL, false, false, NULL, '数据抽取任务表', 'TABLE', 10, NOW(), NOW(), NOW());
