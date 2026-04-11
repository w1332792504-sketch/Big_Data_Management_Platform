# 大数据管理平台 - 部署文档

## 项目结构

本项目已拆分为两个独立仓库：

- **后端**: https://github.com/w1332792504-sketch/Big_Data_Management_Platform
- **前端**: https://github.com/w1332792504-sketch/Big_Data_Management_Platform_Frontend

## 技术架构

```
┌─────────────────────────────────────────────────────┐
│                    Nginx (80/3000)                   │
│                   前端静态资源 + 反向代理              │
└─────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot Backend (8080)              │
│         数据源管理 │ 任务调度 │ DataX 执行            │
│         Redis 缓存 │ 熔断器保护                        │
└─────────────────────────────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              ▼                         ▼
┌─────────────────────────┐   ┌─────────────────────────┐
│      MySQL (3306)       │   │      Redis (6379)       │
│   存储任务配置和记录     │   │    缓存和会话存储        │
└─────────────────────────┘   └─────────────────────────┘
```

## 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| Docker | 20.10+ | 容器运行环境 |
| Docker Compose | 2.0+ | 容器编排工具 |
| Java | 23 | 后端运行环境（本地开发） |
| Node.js | 18+ | 前端运行环境（本地开发） |

## 快速部署（推荐）

### 1. 后端部署

```bash
cd /Users/wangzhenyu/dev/python/data-manager-backend

# 启动全部服务（MySQL + Redis + Backend）
./start.sh
# 选择选项 1
```

### 2. 前端部署

```bash
cd /Users/wangzhenyu/dev/python/data-manager-frontend

# 启动前端服务
./start.sh
# 选择选项 1
```

## 本地开发模式

### 后端启动

```bash
cd /Users/wangzhenyu/dev/python/data-manager-backend

# 启动 MySQL 和 Redis
docker-compose up -d mysql redis

# 本地运行后端
mvn spring-boot:run
```

### 前端启动

```bash
cd /Users/wangzhenyu/dev/python/data-manager-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问 http://localhost:3000
```

## 配置说明

### 后端配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_manager
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379

resilience4j:
  circuitbreaker:
    instances:
      datasource:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
```

### 熔断器配置

| 参数 | 说明 | 默认值 |
|------|------|--------|
| slidingWindowSize | 滑动窗口大小 | 10 |
| failureRateThreshold | 失败率阈值 | 50% |
| waitDurationInOpenState | 打开状态等待时间 | 10s |
| minimumNumberOfCalls | 最小调用次数 | 5 |

## 服务访问

| 服务 | 地址 | 账号/密码 | 说明 |
|------|------|----------|------|
| 前端页面 | http://localhost:3000 | - | 管理界面 |
| 后端 API | http://localhost:8080 | - | REST API |
| MySQL | localhost:3306 | root / root | 数据库 |
| Redis | localhost:6379 | 无密码 | 缓存服务 |

## 运维管理

### 查看日志

```bash
# 后端日志
docker-compose logs -f backend

# MySQL 日志
docker-compose logs -f mysql

# Redis 日志
docker-compose logs -f redis
```

### 服务管理

```bash
# 停止所有服务
docker-compose down

# 重启服务
docker-compose restart

# 查看服务状态
docker-compose ps
```

### 进入容器

```bash
# 进入后端容器
docker exec -it backend bash

# 进入 MySQL 容器
docker exec -it mysql bash

# 进入 Redis 容器
docker exec -it redis bash
```

## 健康检查

```bash
# 检查后端 API
curl http://localhost:8080/api/datasource/list

# 检查 Redis
redis-cli ping

# 检查 MySQL
docker exec mysql mysql -uroot -proot -e "SELECT 1"
```

## 故障排查

### 后端启动失败

```bash
# 1. 检查端口占用
lsof -i :8080
lsof -i :3306
lsof -i :6379

# 2. 查看日志
docker-compose logs backend

# 3. 重启服务
docker-compose restart
```

### Redis 连接失败

```bash
# 1. 检查 Redis 是否运行
docker-compose ps redis

# 2. 测试连接
redis-cli ping

# 3. 查看 Redis 日志
docker-compose logs redis
```

### 熔断器触发

当熔断器打开时，服务会返回 503 错误，并记录日志：
```
熔断器开启，服务不可用
```

等待 10 秒后熔断器会自动进入半开状态，如果后续调用成功则会自动关闭。

## 数据持久化

| 服务 | 持久化路径 |
|------|-----------|
| MySQL | ./mysql/data |
| Redis | ./redis/data |
| 日志 | ./logs |
| DataX 任务 | ./datax/job |
