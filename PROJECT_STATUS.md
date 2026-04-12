# 大数据管理平台 - 项目状态

> 最后更新: 2026-04-12

## 项目概述

一个完整的大数据管理平台，包含数据源管理、数据抽取任务、数据质量检查、元数据管理、监控告警和审计日志等功能。

## 目录结构

```
/Users/wangzhenyu/dev/python/data-manager/
├── backend/                # Spring Boot 后端
│   ├── src/main/java/com/datamanager/
│   │   ├── controller/    # 控制器
│   │   ├── service/       # 服务层
│   │   ├── repository/    # 数据访问层
│   │   └── model/         # 实体类
│   ├── pom.xml
│   └── target/data-manager-1.0.0.jar
├── frontend/              # Vue 3 前端
│   ├── src/
│   │   ├── api/          # API接口
│   │   ├── views/        # 页面
│   │   └── components/   # 组件
│   └── package.json
└── README.md
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 23 + Spring Boot 3.3.0 |
| 前端 | Vue 3 + Vite + Element Plus |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 消息队列 | RabbitMQ (可选) |

## 快速启动

### 1. 启动依赖服务
```bash
docker start mysql redis
```

### 2. 启动后端
```bash
cd /Users/wangzhenyu/dev/python/data-manager/backend
nohup java -jar target/data-manager-1.0.0.jar > /tmp/backend.log 2>&1 &
# 查看日志: tail -f /tmp/backend.log
```

### 3. 启动前端
```bash
cd /Users/wangzhenyu/dev/python/data-manager/frontend
npm run dev
```

### 访问地址
- 前端: http://localhost:3000
- 后端API: http://localhost:8080/api

## 功能模块

| 模块 | 路由 | API前缀 | 状态 |
|------|------|---------|------|
| 数据源管理 | /datasource | /api/datasource | ✅ |
| 数据抽取任务 | /task | /api/task | ✅ |
| 数据质量 | /quality | /api/quality | ✅ |
| 元数据管理 | /metadata | /api/metadata | ✅ |
| 监控告警 | /alert | /api/alert | ✅ |
| 审计日志 | /audit | /api/audit | ✅ |
| 系统管理 | /system/* | /api/system/* | ✅ |

## 数据库信息

| 服务 | 端口 | 凭证 |
|------|------|------|
| MySQL | 3306 | root / root |
| Redis | 6379 | 无密码 |

数据库名: `data_manager`，JPA配置了 `ddl-auto: update`，自动建表。

## Git仓库

- 远程仓库: https://github.com/w1332792504-sketch/Big_Data_Management_Platform.git
- 本地目录: `/Users/wangzhenyu/dev/python/data-manager/`

```bash
# 提交代码
cd /Users/wangzhenyu/dev/python/data-manager
git add -A
git commit -m "描述"
git push origin main
```

## 最近更新 (2026-04-12)

1. **重构Git仓库结构**
   - 将独立的 backend 和 frontend 仓库整合为单一仓库
   - 清理了不必要的文件（docker-data、日志等）
   - 统一在 `data-manager/` 目录管理

2. **新增功能模块**
   - 数据质量检查（完整性、唯一性、准确性、时效性）
   - 元数据管理（自动同步数据库元数据）
   - 监控告警（任务失败、超时、质量检查失败）
   - 审计日志（操作追踪）

3. **测试数据**
   - 已在数据库中添加测试数据
   - 所有API接口正常返回数据

## 常见问题

### 后端启动失败
- 检查MySQL和Redis是否启动: `docker ps`
- 查看日志: `tail -f /tmp/backend.log`

### 前端页面空白
- 检查后端是否启动: `curl http://localhost:8080/api/dashboard/stats`
- 检查前端是否启动: `curl http://localhost:3000`

### RabbitMQ连接失败
- RabbitMQ是可选的，不影响核心功能
- 如需启用: `docker start rabbitmq`

## 停止服务

```bash
# 停止后端
ps aux | grep java | grep data-manager | awk '{print $2}' | xargs kill

# 停止Docker服务
docker stop mysql redis
```
