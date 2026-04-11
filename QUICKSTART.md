# 大数据管理平台 - 快速启动指南

## 环境要求

| 软件 | 版本 | 用途 |
|------|------|------|
| Java | 11+ | 运行后端 |
| Node.js | 16+ | 运行前端 |
| Maven | 3.8+ | 编译后端 (可选，可用 IDE) |
| Docker | 20.10+ | 运行 MySQL |

---

## 快速启动（3 步）

### 步骤 1: 启动 MySQL（Docker）

```bash
cd /Users/wangzhenyu/dev/python/data-manager

# 启动 MySQL
./start-mysql.sh
```

MySQL 连接信息：
- 地址：localhost:3306
- 用户：root
- 密码：root
- 数据库：data_manager

---

### 步骤 2: 启动后端

#### 方式 A: 使用 IDEA（推荐）

1. 打开 `backend` 目录
2. 用 IDEA 打开（自动识别为 Maven 项目）
3. 运行 `DataManagerApplication.java`

#### 方式 B: 使用 Maven 命令

```bash
cd backend

# 编译打包
mvn clean package -DskipTests

# 启动
java -jar target/data-manager-1.0.0.jar
```

后端启动后访问：http://localhost:8080/api/datasource/list

---

### 步骤 3: 启动前端

```bash
cd frontend

# 安装依赖（首次需要）
npm install

# 启动开发服务器
npm run dev
```

前端访问：http://localhost:3000

---

## 验证

1. 浏览器访问 http://localhost:3000
2. 点击左侧 **数据源管理**
3. 查看已预置的 3 个数据源

---

## 常见问题

### Q: 没有 Maven 怎么办？

使用 IDEA 直接打开 backend 目录，IDEA 会自动下载依赖。

### Q: 前端依赖安装慢？

使用国内镜像：
```bash
npm install --registry=https://registry.npmmirror.com
```

### Q: 端口被占用？

修改配置：
- 后端：`backend/src/main/resources/application.yml` 修改 `server.port`
- 前端：`frontend/vite.config.js` 修改 `server.port`

---

## 目录结构

```
data-manager/
├── backend/              # Java 后端
│   ├── src/main/java/   # 源代码
│   └── pom.xml          # Maven 配置
├── frontend/             # Vue 前端
│   ├── src/             # 源代码
│   └── package.json     # Node 依赖
├── scripts/
│   └── init.sql         # 数据库初始化脚本
├── docker-mysql-only.yml # Docker MySQL 配置
└── QUICKSTART.md        # 本文档
```
