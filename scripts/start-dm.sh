#!/bin/bash

# 大数据管理平台 - 启动脚本

echo "========================================="
echo "     启动大数据管理平台服务"
echo "========================================="

# 1. 启动 MySQL 数据库
echo "[1/3] 启动 MySQL 数据库..."
docker start data_manager_mysql
sleep 2

# 2. 启动后端 (Spring Boot Java 服务)
echo "[2/3] 启动后端服务..."
cd /Users/wangzhenyu/dev/python/data-manager/backend
if [ -f "target/data-manager-1.0.0.jar" ]; then
    java -jar target/data-manager-1.0.0.jar > /tmp/dm-backend.log 2>&1 &
    echo "后端服务已启动 (日志：/tmp/dm-backend.log)"
else
    echo "警告：未找到 jar 包，请先执行 mvn package"
fi

# 3. 启动前端 (Vue 3 + Vite)
echo "[3/3] 启动前端服务..."
cd /Users/wangzhenyu/dev/python/data-manager/frontend
npm run dev > /tmp/dm-frontend.log 2>&1 &
echo "前端服务已启动 (日志：/tmp/dm-frontend.log)"

echo "========================================="
echo "     启动完成!"
echo "========================================="
echo ""
echo "服务访问地址:"
echo "  - 前端：http://localhost:3000"
echo "  - 后端：http://localhost:8080"
echo "  - MySQL: localhost:3306"
echo ""
echo "查看日志：tail -f /tmp/dm-backend.log"
echo "停止服务：./stop-dm.sh"
