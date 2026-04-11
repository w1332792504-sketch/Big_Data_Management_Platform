#!/bin/bash

# 大数据管理平台 - 关闭脚本

echo "========================================="
echo "     停止大数据管理平台服务"
echo "========================================="

# 1. 关闭 MySQL 数据库
echo "[1/3] 停止 MySQL 数据库..."
docker stop data_manager_mysql

# 2. 关闭后端 (Java 进程)
echo "[2/3] 停止后端服务..."
BACKEND_PID=$(ps aux | grep "data-manager.*jar" | grep -v grep | awk '{print $2}')
if [ -n "$BACKEND_PID" ]; then
    kill $BACKEND_PID
    echo "后端服务已停止 (PID: $BACKEND_PID)"
else
    echo "后端服务未运行"
fi

# 3. 关闭前端 (Vite 进程)
echo "[3/3] 停止前端服务..."
FRONTEND_PID=$(ps aux | grep "vite" | grep "data-manager" | grep -v grep | awk '{print $2}')
if [ -n "$FRONTEND_PID" ]; then
    kill $FRONTEND_PID
    echo "前端服务已停止 (PID: $FRONTEND_PID)"
else
    echo "前端服务未运行"
fi

echo "========================================="
echo "     所有服务已停止"
echo "========================================="
