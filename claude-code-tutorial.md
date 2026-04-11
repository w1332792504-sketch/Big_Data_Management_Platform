# 大数据管理平台 - 完整教程

## 项目概述

大数据管理平台是一个企业级的数据集成和管理解决方案，提供数据源管理、数据抽取任务调度、数据质量监控、元数据管理、告警通知等核心功能。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.0 | 核心框架 |
| Java | 23 | 编程语言 |
| Spring Data JPA | - | ORM框架 |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.x | 缓存中间件 |
| RabbitMQ | 3.x | 消息队列 |
| Resilience4j | 2.2.0 | 熔断器 |
| DataX | - | 数据同步工具 |
| Lombok | 1.18.36 | 代码简化 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.x | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.x | HTTP客户端 |

## 项目结构

```
/Users/wangzhenyu/dev/python/
├── data-manager-backend/          # 后端项目
│   ├── src/main/java/com/datamanager/
│   │   ├── config/                # 配置类
│   │   │   ├── CircuitBreakerConfig.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── JacksonConfig.java
│   │   │   ├── RabbitMQConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/            # 控制器
│   │   │   ├── AlertController.java
│   │   │   ├── AuditController.java
│   │   │   ├── AuthController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── DataSourceController.java
│   │   │   ├── ExtractTaskController.java
│   │   │   ├── MetadataController.java
│   │   │   ├── MessageQueueController.java
│   │   │   ├── QualityController.java
│   │   │   └── system/            # 系统管理控制器
│   │   ├── dto/                   # 数据传输对象
│   │   ├── exception/             # 异常处理
│   │   ├── model/                 # 实体类
│   │   │   ├── AlertRecord.java
│   │   │   ├── AlertRule.java
│   │   │   ├── AuditLog.java
│   │   │   ├── DataSource.java
│   │   │   ├── Dictionary.java
│   │   │   ├── ExtractTask.java
│   │   │   ├── Metadata.java
│   │   │   ├── Organization.java
│   │   │   ├── Permission.java
│   │   │   ├── QualityCheckResult.java
│   │   │   ├── QualityRule.java
│   │   │   ├── Role.java
│   │   │   ├── TaskExecution.java
│   │   │   └── User.java
│   │   ├── repository/            # 数据访问层
│   │   └── service/               # 业务逻辑层
│   │       ├── AlertService.java
│   │       ├── AuditService.java
│   │       ├── DataSourceService.java
│   │       ├── ExtractTaskService.java
│   │       ├── MessageQueueService.java
│   │       ├── MetadataService.java
│   │       ├── QualityService.java
│   │       └── UserService.java
│   └── src/main/resources/
│       └── application.yml        # 配置文件
│
└── data-manager-frontend/         # 前端项目
    └── frontend/
        ├── src/
        │   ├── api/               # API接口
        │   │   ├── alert.js
        │   │   ├── audit.js
        │   │   ├── auth.js
        │   │   ├── dashboard.js
        │   │   ├── datasource.js
        │   │   ├── metadata.js
        │   │   ├── quality.js
        │   │   ├── request.js
        │   │   ├── task.js
        │   │   └── system/
        │   ├── components/        # 公共组件
        │   ├── router/            # 路由配置
        │   └── views/             # 页面组件
        │       ├── Alert.vue
        │       ├── Audit.vue
        │       ├── Dashboard.vue
        │       ├── Datasource.vue
        │       ├── HomePage.vue
        │       ├── Login.vue
        │       ├── Metadata.vue
        │       ├── MQMonitor.vue
        │       ├── Quality.vue
        │       ├── Task.vue
        │       ├── TaskExecutions.vue
        │       └── system/
        └── vite.config.js
```

## 核心功能模块

### 1. 数据源管理

支持多种数据源类型的统一管理：
- MySQL
- Oracle
- PostgreSQL
- Hive

**API接口：**
- `GET /api/datasource` - 获取数据源列表
- `POST /api/datasource` - 创建数据源
- `PUT /api/datasource/{id}` - 更新数据源
- `DELETE /api/datasource/{id}` - 删除数据源
- `POST /api/datasource/{id}/test` - 测试连接

### 2. 数据抽取任务

基于DataX的数据同步任务管理：
- 支持自定义SQL查询
- 支持增量同步
- 支持并发执行
- 实时日志监控

**API接口：**
- `GET /api/task` - 获取任务列表
- `POST /api/task` - 创建任务
- `POST /api/task/{id}/run` - 执行任务
- `POST /api/task/{id}/stop` - 停止任务
- `GET /api/task/{id}/executions` - 获取执行记录

### 3. 数据质量管理

提供多种数据质量检查规则：
- **完整性检查**：检查空值
- **唯一性检查**：检查重复值
- **准确性检查**：自定义条件验证
- **一致性检查**：数据一致性验证
- **时效性检查**：数据时效验证

**API接口：**
- `GET /api/quality/rules` - 获取规则列表
- `POST /api/quality/rules` - 创建规则
- `POST /api/quality/rules/{id}/execute` - 执行检查
- `GET /api/quality/results` - 获取检查结果

### 4. 元数据管理

自动同步和管理数据源元数据：
- 数据库/表/列信息同步
- 表行数统计
- 主键识别
- 注释管理

**API接口：**
- `POST /api/metadata/sync/{datasourceId}` - 同步元数据
- `GET /api/metadata/databases/{datasourceId}` - 获取数据库列表
- `GET /api/metadata/tables/{datasourceId}` - 获取表列表
- `GET /api/metadata/columns/{datasourceId}` - 获取列信息

### 5. 监控告警

灵活的告警规则配置和通知：
- 任务失败告警
- 任务超时告警
- 数据质量告警
- 系统异常告警

**支持的通知渠道：**
- 邮件
- 短信
- Webhook
- 消息队列

**API接口：**
- `GET /api/alert/rules` - 获取告警规则
- `POST /api/alert/rules` - 创建规则
- `GET /api/alert/records` - 获取告警记录
- `PUT /api/alert/records/{id}/acknowledge` - 确认告警
- `PUT /api/alert/records/{id}/resolve` - 解决告警

### 6. 审计日志

完整的操作审计追踪：
- 用户操作记录
- API调用日志
- 系统事件记录

**API接口：**
- `GET /api/audit/logs` - 查询日志
- `GET /api/audit/logs/module/{module}` - 按模块查询
- `GET /api/audit/logs/operator/{operator}` - 按操作人查询
- `GET /api/audit/stats/today` - 今日统计

### 7. 系统管理

完善的RBAC权限管理：
- 用户管理
- 角色管理
- 权限管理
- 组织管理
- 字典管理

## 快速开始

### 环境要求

- JDK 23+
- Node.js 18+
- MySQL 8.0+
- Redis 7.x+
- RabbitMQ 3.x+ (可选)
- Docker & Docker Compose

### 启动步骤

#### 1. 启动依赖服务

```bash
# 使用Docker启动MySQL和Redis
docker start mysql redis

# 或使用Docker Compose
cd /Users/wangzhenyu/dev/python/data-manager-frontend
docker-compose up -d mysql redis
```

#### 2. 启动后端

```bash
cd /Users/wangzhenyu/dev/python/data-manager-backend

# 编译项目
mvn clean package -DskipTests

# 启动服务
java -jar target/data-manager-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动。

#### 3. 启动前端

```bash
cd /Users/wangzhenyu/dev/python/data-manager-frontend/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

### 访问系统

- 前端地址：http://localhost:3000
- 后端API：http://localhost:8080/api
- 默认账号：admin / admin123

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_manager?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  data:
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

# DataX 配置
datax:
  home: /opt/datax
  job-path: /opt/datax/job
  python-path: /opt/datax/bin/datax.py
```

### 前端配置 (vite.config.js)

```javascript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## API文档

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 部署指南

### Docker部署

```bash
# 构建并启动所有服务
cd /Users/wangzhenyu/dev/python/data-manager-frontend
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 生产环境配置建议

1. **数据库优化**
   - 配置连接池
   - 启用慢查询日志
   - 定期备份数据

2. **Redis配置**
   - 设置密码
   - 配置持久化
   - 监控内存使用

3. **安全加固**
   - 启用HTTPS
   - 配置防火墙规则
   - 定期更新依赖

4. **性能优化**
   - 启用Gzip压缩
   - 配置CDN加速
   - 数据库索引优化

## 常见问题

### 1. 中文乱码问题

确保配置了正确的字符编码：
- 数据库连接URL添加 `characterEncoding=utf-8`
- Spring配置 `server.servlet.encoding.charset=UTF-8`
- Jackson配置 `spring.jackson.date-format`

### 2. 跨域问题

后端已配置CORS，允许所有来源访问。生产环境建议配置具体域名。

### 3. DataX任务执行失败

检查：
- DataX安装路径是否正确
- Python环境是否配置
- 数据源连接是否正常

## 更新日志

### v1.0.0 (2026-04-11)

**新增功能：**
- 数据源管理模块
- 数据抽取任务管理
- 数据质量管理
- 元数据管理
- 监控告警系统
- 审计日志
- 系统管理（用户、角色、权限、组织、字典）
- 消息队列监控

**技术优化：**
- 集成Redis缓存
- 集成RabbitMQ消息队列
- 集成Resilience4j熔断器
- UTF-8字符编码支持
- Jackson日期格式化配置

## 开发团队

大数据管理平台开发团队

## 许可证

MIT License
