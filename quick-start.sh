#!/bin/bash

# ====================================
# 快速启动脚本 - 本地运行方式
# ====================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================"
echo "  大数据管理平台 - 快速启动"
echo "======================================"

# 检查 MySQL 容器
if docker ps | grep -q data_manager_mysql; then
    echo "✓ MySQL 已运行"
else
    echo "正在启动 MySQL..."
    mkdir -p docker-data/mysql-data docker-data/mysql-conf
    docker-compose -f docker-mysql-only.yml up -d
    sleep 5
    echo "✓ MySQL 已启动"
fi

# 安装后端依赖 (使用 gradle 或手动下载 jar)
echo ""
echo "正在准备后端..."

# 检查是否有打包好的 jar
if [ -f "backend/target/data-manager-1.0.0.jar" ]; then
    echo "✓ 后端 JAR 已存在"
else
    echo "后端需要先编译打包..."
    echo "请运行以下命令："
    echo ""
    echo "  cd backend"
    echo "  mvn clean package -DskipTests"
    echo "  cd .."
    echo ""
fi

# 安装前端依赖
echo ""
echo "正在安装前端依赖..."
cd frontend
if [ ! -d "node_modules" ]; then
    npm install --registry=https://registry.npmmirror.com
fi
cd ..

echo ""
echo "======================================"
echo "  启动说明"
echo "======================================"
echo ""
echo "  方式 1: 使用 idea 或其他 IDE 启动后端"
echo "    - 打开 backend 目录中的 Spring Boot 项目"
echo "    - 运行 DataManagerApplication.java"
echo ""
echo "  方式 2: 使用 Maven 启动"
echo "    cd backend"
echo "    mvn spring-boot:run"
echo ""
echo "  前端启动命令"
echo "    cd frontend"
echo "    npm run dev"
echo ""
echo "======================================"
