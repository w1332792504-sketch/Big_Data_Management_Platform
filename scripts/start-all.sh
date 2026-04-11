#!/bin/bash

# 所有服务 - 启动脚本

echo "========================================="
echo "     启动所有服务"
echo "========================================="

# 启动大数据平台服务
./start-dm.sh

# 启动其他服务
echo ""
echo "启动其他服务..."
docker start redis_ai
docker start vector_db
docker start geektime-docs

echo ""
echo "所有服务已启动!"
