#!/bin/bash

# 所有服务 - 关闭脚本

echo "========================================="
echo "     停止所有服务"
echo "========================================="

# 停止大数据平台服务
./stop-dm.sh

# 停止其他服务
echo ""
echo "停止其他服务..."
docker stop redis_ai
docker stop vector_db
docker stop geektime-docs

echo ""
echo "所有服务已停止!"
