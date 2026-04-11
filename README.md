# 大数据管理平台

一个完整的大数据管理平台，包含数据源管理、数据抽取任务、数据质量检查、元数据管理、监控告警和审计日志等功能。

## 项目结构

```
├── backend/                # Spring Boot 后端
│   ├── src/               # Java 源代码
│   ├── pom.xml            # Maven 配置
│   └── Dockerfile         # 后端 Docker 镜像
├── frontend/              # Vue 3 前端
│   ├── src/               # Vue 源代码
│   ├── package.json       # npm 配置
│   └── Dockerfile         # 前端 Docker 镜像
├── docker-compose.yml     # Docker Compose 配置
└── README.md
```

## 技术栈

### 后端
- Java 23
- Spring Boot 3.3.0
- Spring Data JPA
- MySQL 8.0
- Redis
- RabbitMQ

### 前端
- Vue 3
- Vite
- Element Plus
- Axios

## 快速启动

### 1. 启动依赖服务
```bash
docker start mysql redis
```

### 2. 启动后端
```bash
cd backend
mvn clean package -DskipTests
java -jar target/data-manager-1.0.0.jar
```

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```

## 访问地址

- 前端: http://localhost:3000
- 后端 API: http://localhost:8080/api

## 功能模块

- **数据源管理**: 支持多种数据源类型（MySQL、PostgreSQL、MongoDB、Redis、ClickHouse、Hive）
- **数据抽取任务**: 配置和管理数据抽取任务
- **数据质量**: 数据完整性、唯一性、准确性等检查
- **元数据管理**: 自动同步和管理数据库元数据
- **监控告警**: 任务失败、超时等告警规则配置
- **审计日志**: 操作审计和追踪
