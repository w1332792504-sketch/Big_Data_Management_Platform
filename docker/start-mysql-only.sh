#!/bin/bash

# ====================================
# 仅启动 MySQL 快速脚本
# ====================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "正在启动 MySQL..."

# 创建目录
mkdir -p docker/mysql/data
mkdir -p docker/mysql/conf

# 只启动 MySQL
docker-compose up -d mysql

echo ""
echo "✓ MySQL 已启动"
echo ""
echo "连接信息:"
echo "  主机：localhost:3306"
echo "  用户：root"
echo "  密码：root@123"
echo "  数据库：data_manager"
echo ""
echo "查看日志：docker-compose logs -f mysql"
echo "停止服务：docker-compose down"
