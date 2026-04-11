#!/bin/bash

# ====================================
# MySQL 快速启动脚本
# ====================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================"
echo "  启动 MySQL 数据库"
echo "======================================"

# 创建目录
mkdir -p docker-data/mysql-data docker-data/mysql-conf

# 停止并删除旧容器（如果存在）
docker-compose -f docker-mysql-only.yml down 2>/dev/null || true

# 启动 MySQL
docker-compose -f docker-mysql-only.yml up -d

echo ""
echo "等待 MySQL 启动..."
sleep 5

# 验证启动
echo ""
echo "======================================"
echo "  MySQL 启动成功!"
echo "======================================"
echo ""
echo "  连接信息:"
echo "  ┌──────────────────────────────┐"
echo "  │  主机：localhost:3306        │"
echo "  │  用户：root                  │"
echo "  │  密码：root                  │"
echo "  │  数据库：data_manager        │"
echo "  └──────────────────────────────┘"
echo ""
echo "  已初始化的表:"
echo "  - t_datasource     (数据源配置表)"
echo "  - t_extract_task   (抽取任务表)"
echo "  - t_task_execution (执行记录表)"
echo ""
echo "  常用命令:"
echo "  查看日志：docker logs -f data_manager_mysql"
echo "  进入 MySQL: docker exec -it data_manager_mysql mysql -uroot -proot"
echo "  停止服务：docker-compose -f docker-mysql-only.yml down"
echo ""
