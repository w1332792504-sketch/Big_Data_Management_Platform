# 大数据管理平台 - 项目指令

## 项目信息
- 项目目录: /Users/wangzhenyu/dev/python/data-manager
- 后端: Java 23 + Spring Boot 3.3.0 (端口 8080)
- 前端: Vue 3 + Vite (端口 3000)
- 数据库: MySQL 8.0 (root/root, 端口 3306)
- 缓存: Redis (端口 6379)

## 启动命令

### 启动依赖服务
```bash
docker start mysql redis
```

### 启动后端
```bash
cd /Users/wangzhenyu/dev/python/data-manager/backend
nohup java -jar target/data-manager-1.0.0.jar > /tmp/backend.log 2>&1 &
```

### 启动前端
```bash
cd /Users/wangzhenyu/dev/python/data-manager/frontend
npm run dev
```

## 工作规则

### 代码规范
- 后端使用 Spring Boot 标准结构
- 前端使用 Vue 3 Composition API
- API 返回格式: `{ code, message, data }`
- 数据库字段使用下划线命名 (snake_case)

### Git 提交
- 提交信息使用中文
- 每次提交前先 pull
- 功能开发完成后立即推送

### 错误处理
- 后端日志: `tail -f /tmp/backend.log`
- 前端控制台查看错误
- 数据库连接失败检查 Docker

## 常用 API
- Dashboard: GET /api/dashboard/stats
- 数据源列表: GET /api/datasource/list
- 任务列表: GET /api/task/list
- 质量规则: GET /api/quality/rules
- 告警规则: GET /api/alert/rules
- 审计日志: GET /api/audit/logs

## 用户偏好
- 使用中文交流
- 代码注释使用中文
- 响应简洁，避免冗余
