# 大数据管理平台后端

<div align="center">

基于 Spring Boot + Vue 的数据抽取管理平台

[![Java](https://img.shields.io/badge/Java-23-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[前端仓库](https://github.com/w1332792504-sketch/Big_Data_Management_Platform_Frontend)

</div>

## 功能特性

- **数据源管理** - 支持 MySQL、Hive、Oracle、PostgreSQL 等多种数据源
- **抽取任务管理** - 全量/增量抽取、自定义 SQL、分片并发
- **定时调度** - 支持 Cron 表达式定时执行
- **执行监控** - 实时查看任务状态和执行记录
- **Redis 缓存** - 提高数据访问性能
- **熔断器保护** - 集成 Resilience4j，服务故障时自动降级

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 23 | 开发语言 |
| Spring Boot | 3.3.0 | 应用框架 |
| Spring Data JPA | - | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| Redis | 7 | 缓存 |
| Resilience4j | 2.2.0 | 熔断器 |
| DataX | - | 数据同步工具 |

## 快速开始

### 环境要求

- JDK 23+
- Maven 3.8+
- MySQL 8.0+
- Redis 7+

### 1. 克隆项目

```bash
git clone https://github.com/w1332792504-sketch/Big_Data_Management_Platform.git
cd Big_Data_Management_Platform
```

### 2. 配置数据库和 Redis

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_manager
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 3. 初始化数据库

```bash
mysql -u root -p < scripts/init.sql
```

### 4. 启动应用

```bash
# 方式一：Maven 启动
mvn spring-boot:run

# 方式二：打包运行
mvn clean package
java -jar target/data-manager-1.0.0.jar

# 方式三：Docker 启动
docker-compose up -d
```

### 5. 访问服务

- API 地址：http://localhost:8080
- 健康检查：http://localhost:8080/actuator/health

## Docker 部署

### 启动全部服务

```bash
# 使用启动脚本
./start.sh
# 选择选项 1

# 或使用 docker-compose
docker-compose up -d
```

### 服务列表

| 服务 | 端口 | 说明 |
|------|------|------|
| backend | 8080 | 后端服务 |
| mysql | 3306 | MySQL 数据库 |
| redis | 6379 | Redis 缓存 |

## API 接口

### 数据源管理

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/datasource/list | GET | 获取数据源列表 |
| /api/datasource/{id} | GET | 获取单个数据源 |
| /api/datasource/save | POST | 保存数据源 |
| /api/datasource/{id} | DELETE | 删除数据源 |
| /api/datasource/test | POST | 测试连接 |

### 任务管理

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/task/list | GET | 获取任务列表 |
| /api/task/{id} | GET | 获取单个任务 |
| /api/task/save | POST | 保存任务 |
| /api/task/{id}/run | POST | 执行任务 |
| /api/task/{id}/stop | POST | 停止任务 |

## 配置说明

### 熔断器配置

```yaml
resilience4j:
  circuitbreaker:
    instances:
      datasource:
        slidingWindowSize: 10           # 滑动窗口大小
        failureRateThreshold: 50        # 失败率阈值 50%
        waitDurationInOpenState: 10s    # 打开状态等待时间
        minimumNumberOfCalls: 5         # 最小调用次数
```

### Redis 缓存配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2
```

## 项目结构

```
backend/
├── src/main/java/com/datamanager/
│   ├── config/              # 配置类
│   │   ├── RedisConfig.java
│   │   ├── CircuitBreakerConfig.java
│   │   └── GlobalExceptionHandler.java
│   ├── controller/          # 控制器
│   ├── service/             # 服务层
│   ├── repository/          # 数据访问层
│   ├── model/               # 实体类
│   ├── dto/                 # 数据传输对象
│   └── exception/           # 异常类
├── src/main/resources/
│   └── application.yml      # 应用配置
├── docker-compose.yml       # Docker 配置
└── pom.xml                  # Maven 配置
```

## 运维管理

### 查看日志

```bash
docker-compose logs -f backend
```

### 服务状态

```bash
docker-compose ps
```

### 进入容器

```bash
docker exec -it backend bash
```

## 常见问题

### 1. Redis 连接失败

确保 Redis 服务已启动：
```bash
redis-cli ping  # 应返回 PONG
```

### 2. 熔断器触发

当熔断器打开时，服务会返回 503 错误，等待 10 秒后自动进入半开状态。

### 3. 端口被占用

修改 `application.yml` 中的端口配置：
```yaml
server:
  port: 8081  # 改为其他端口
```

## License

MIT License

## 联系方式

如有问题，请提交 Issue 或联系作者。
