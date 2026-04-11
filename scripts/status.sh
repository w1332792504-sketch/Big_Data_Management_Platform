#!/bin/bash

# 服务状态检查脚本

echo "========================================="
echo "     服务状态检查"
echo "========================================="
echo ""

# 检查 Docker 容器
echo "Docker 容器状态:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "data_manager|redis|vector|geektime"
echo ""

# 检查监听端口
echo "监听端口:"
netstat -an | grep LISTEN | grep -E ":(3000|3306|5432|8080|16379|8091)"
echo ""

# 检查进程
echo "运行进程:"
ps aux | grep -E "java.*data-manager|node.*vite" | grep -v grep
echo ""

echo "========================================="
