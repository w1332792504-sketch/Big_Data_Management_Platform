# 大数据管理平台 - Docker 部署文档

## 目录

- [项目简介](#项目简介)
- [技术架构](#技术架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [详细部署步骤](#详细部署步骤)
- [配置说明](#配置说明)
- [服务访问](#服务访问)
- [运维管理](#运维管理)
- [故障排查](#故障排查)
- [常见问题](#常见问题)

---

## 项目简介

大数据管理平台是一套基于 Java + Vue 的数据抽取管理工具，支持多种数据源（MySQL、Hive、Oracle 等）之间的数据同步任务配置、执行和监控。

### 核心功能

| 功能模块 | 说明 |
|---------|------|
| 数据源管理 | 支持多种数据库类型，可测试连接 |
| 抽取任务 | 全量/增量抽取、自定义 SQL、分片并发 |
| 定时调度 | 支持 Cron 表达式定时执行 |
| 执行监控 | 实时查看任务状态和执行记录 |
| 数据核对 | 自动统计源端和目标端记录数 |

---

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
└─────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│                    MySQL (3306)                      │
│              存储任务配置和执行记录                    │
└─────────────────────────────────────────────────────┘
```

---

## 环境要求

### 硬件要求

| 配置 | 最低要求 | 推荐配置 |
|------|---------|---------|
| CPU | 2 核 | 4 核+ |
| 内存 | 4GB | 8GB+ |
| 磁盘 | 10GB | 50GB+ |

### 软件要求

| 软件 | 版本 | 说明 |
|------|------|------|
| Docker | 20.10+ | 容器运行环境 |
| Docker Compose | 2.0+ | 容器编排工具 |

### 检查环境

```bash
# 检查 Docker
docker --version

# 检查 Docker Compose
docker-compose --version
```

---

## 快速开始

### 一键启动全部服务

```bash
# 进入项目目录
cd /Users/wangzhenyu/dev/python/data-manager

# 运行启动脚本
./docker/docker-start.sh

# 选择选项 2: 启动全部服务
```

### 仅启动 MySQL

```bash
# 方式一：使用脚本
./docker/start-mysql-only.sh

# 方式二：使用 docker-compose
docker-compose up -d mysql
```

---

## 详细部署步骤

### 步骤 1：克隆/准备项目

确保项目文件位于正确位置：

```
/Users/wangzhenyu/dev/python/data-manager/
```

### 步骤 2：创建必要目录

```bash
cd /Users/wangzhenyu/dev/python/data-manager

# 创建数据持久化目录
mkdir -p docker/mysql/data
mkdir -p docker/mysql/conf
mkdir -p docker/logs
mkdir -p docker/datax/job
```

### 步骤 3：启动服务

```bash
# 方式一：交互式启动（推荐）
./docker/docker-start.sh

# 方式二：直接使用 docker-compose
docker-compose up -d
```

### 步骤 4：验证服务

```bash
# 查看服务状态
docker-compose ps

# 预期输出：
# NAME            STATUS         PORTS
# mysql        Up (healthy)   0.0.0.0:3306->3306
# backend      Up (healthy)   0.0.0.0:8080->8080
# frontend     Up             0.0.0.0:3000->80
```

### 步骤 5：访问页面

浏览器访问：http://localhost:3000

---

## 配置说明

### docker-compose.yml 配置

```yaml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: root@123    # root 密码
      MYSQL_DATABASE: data_manager     # 默认创建的数据库
      MYSQL_ROOT_HOST: '%'             # 允许远程连接
      TZ: Asia/Shanghai                # 时区
    ports:
      - "3306:3306"                    # 端口映射
    volumes:
      - ./docker/mysql/data:/var/lib/mysql           # 数据持久化
      - ./scripts/init.sql:/docker-entrypoint-initdb.d/init.sql  # 初始化脚本
```

### 应用配置修改

如需修改配置，编辑 `backend/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://mysql:3306/data_manager?useSSL=false&characterEncoding=utf8
    username: root
    password: root@123
```

### 环境变量覆盖

通过环境变量覆盖配置：

```bash
# 启动时指定环境变量
docker run -e SPRING_DATASOURCE_PASSWORD=your_password ...
```

---

## 服务访问

| 服务 | 地址 | 账号/密码 | 说明 |
|------|------|----------|------|
| 前端页面 | http://localhost:3000 | - | 管理界面 |
| 后端 API | http://localhost:8080 | - | REST API |
| MySQL | localhost:3306 | root / root@123 | 数据库 |

### API 接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/datasource/list | GET | 获取数据源列表 |
| /api/datasource/save | POST | 保存数据源 |
| /api/datasource/test | POST | 测试数据源连接 |
| /api/task/list | GET | 获取任务列表 |
| /api/task/save | POST | 保存任务 |
| /api/task/{id}/run | POST | 执行任务 |
| /api/task/{id}/executions | GET | 获取执行记录 |

---

## 运维管理

### 查看日志

```bash
# 查看全部日志
docker-compose logs -f

# 查看后端日志
docker-compose logs -f backend

# 查看 MySQL 日志
docker-compose logs -f mysql

# 查看前端日志
docker-compose logs -f frontend

# 查看最近 100 行
docker-compose logs --tail=100 backend
```

### 服务管理

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（谨慎使用）
docker-compose down -v

# 重启服务
docker-compose restart

# 重启单个服务
docker-compose restart backend

# 重新构建并启动
docker-compose build
docker-compose up -d
```

### 进入容器

```bash
# 进入后端容器
docker exec -it backend bash

# 进入 MySQL 容器
docker exec -it mysql bash

# 进入 MySQL 命令行
docker exec -it mysql mysql -uroot -proot@123
```

### 数据备份

```bash
# 备份 MySQL 数据
docker exec mysql mysqldump -uroot -proot@123 data_manager > backup.sql

# 恢复 MySQL 数据
docker exec -i mysql mysql -uroot -proot@123 data_manager < backup.sql
```

---

## 故障排查

### 服务启动失败

```bash
# 1. 查看日志
docker-compose logs backend

# 2. 检查端口占用
lsof -i :8080
lsof -i :3306
lsof -i :3000

# 3. 检查容器状态
docker-compose ps

# 4. 重启服务
docker-compose restart
```

### MySQL 连接失败

```bash
# 1. 检查 MySQL 是否运行
docker-compose ps mysql

# 2. 测试连接
docker exec dm_mysql mysql -uroot -proot@123 -e "SELECT 1"

# 3. 查看 MySQL 日志
docker-compose logs mysql
```

### 后端无法连接 MySQL

```bash
# 1. 检查网络
docker network ls
docker network inspect data-manager_dm_network

# 2. 检查后端配置
docker exec dm_backend env | grep SPRING

# 3. 重启后端
docker-compose restart backend
```

### 前端无法访问后端

```bash
# 1. 检查后端是否运行
curl http://localhost:8080/api/datasource/list

# 2. 检查 Nginx 配置
docker exec dm_frontend cat /etc/nginx/conf.d/default.conf

# 3. 重启前端
docker-compose restart frontend
```

---

## 常见问题

### Q1: 端口被占用怎么办？

修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "8081:8080"   # 将 8080 改为 8081
```

### Q2: 如何修改 MySQL 密码？

1. 停止服务：`docker-compose down`
2. 修改 `docker-compose.yml` 中的 `MYSQL_ROOT_PASSWORD`
3. 删除数据卷：`rm -rf docker/mysql/data`
4. 重新启动：`docker-compose up -d`

### Q3: DataX 如何使用？

后端镜像已预装 DataX，位于 `/opt/datax`。

MySQL 数据已配置持久化到 `docker/mysql/data` 目录。

删除容器不会丢失数据，除非删除数据卷：

```bash
# 安全删除（保留数据）
docker-compose down

# 删除数据卷（数据丢失）
docker-compose down -v
```

### Q5: 如何在生产环境部署？

1. 修改默认密码
2. 配置 HTTPS（在 Nginx 中配置证书）
3. 限制 MySQL 远程访问
4. 配置日志轮转
5. 设置资源限制（CPU/内存）

---

## 附录

### 项目文件清单

```
data-manager/
├── docker-compose.yml              # Docker Compose 配置
├── docker/
│   ├── docker-start.sh             # 一键启动脚本
│   ├── start-mysql-only.sh         # 仅启动 MySQL 脚本
│   ├── mysql/
│   │   ├── data/                   # MySQL 数据目录
│   │   └── conf/                   # MySQL 配置目录
│   ├── logs/                       # 日志目录
│   └── datax/job/                  # DataX 任务目录
├── backend/
│   ├── pom.xml                     # Maven 配置
│   ├── Dockerfile                  # 后端镜像
│   └── src/main/
│       ├── java/com/datamanager/   # Java 源代码
│       └── resources/
│           └── application.yml     # 应用配置
├── frontend/
│   ├── package.json                # Node 依赖
│   ├── Dockerfile                  # 前端镜像
│   ├── nginx.conf                  # Nginx 配置
│   └── src/                        # Vue 源代码
├── scripts/
│   └── init.sql                    # 数据库初始化脚本
└── docs/
    └── datax-dm-config.md          # DataX 达梦配置指南
```

### 默认端口

| 服务 | 容器内端口 | 宿主机端口 |
|------|-----------|-----------|
| Nginx | 80 | 3000 |
| Spring Boot | 8080 | 8080 |
| MySQL | 3306 | 3306 |

### 技术支持

- 项目文档：`README.md`
- 初始化脚本：`scripts/init.sql`

---

**文档版本**: 1.0.0
**最后更新**: 2026-03-21
