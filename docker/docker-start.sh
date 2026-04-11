#!/bin/bash

# ====================================
# Docker 一键启动脚本
# ====================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================"
echo "  大数据管理平台 - Docker 启动"
echo "======================================"
echo ""

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "错误：未找到 Docker，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "错误：未找到 Docker Compose，请先安装"
    exit 1
fi

echo "✓ Docker 版本：$(docker --version)"
echo "✓ Docker Compose 版本：$(docker-compose --version)"
echo ""

# 创建必要目录
mkdir -p docker/mysql/data
mkdir -p docker/mysql/conf
mkdir -p docker/logs
mkdir -p docker/datax/job

# 菜单
echo "请选择启动模式:"
echo "1. 仅启动 MySQL"
echo "2. 启动全部服务 (MySQL + 后端 + 前端)"
echo "3. 查看服务状态"
echo "4. 停止所有服务"
echo "5. 重启所有服务"
echo "6. 查看日志"
echo ""

read -p "请输入选项 [1-6]: " choice

case $choice in
    1)
        echo ""
        echo "正在启动 MySQL..."
        docker-compose up -d mysql
        echo ""
        echo "✓ MySQL 已启动"
        echo "  连接信息："
        echo "    主机：localhost:3306"
        echo "    用户：root"
        echo "    密码：root@123"
        echo "    数据库：data_manager"
        ;;

    2)
        echo ""
        echo "正在构建并启动所有服务..."
        docker-compose build
        docker-compose up -d
        echo ""
        echo "======================================"
        echo "  全部服务已启动!"
        echo "======================================"
        echo ""
        echo "  服务访问地址:"
        echo "  ┌─────────────────────────────────┐"
        echo "  │ 前端页面：http://localhost:3000  │"
        echo "  │ 后端 API:  http://localhost:8080 │"
        echo "  │ MySQL:     localhost:3306       │"
        echo "  └─────────────────────────────────┘"
        echo ""
        echo "  默认账号：admin (前端页面)"
        echo ""
        ;;

    3)
        echo ""
        docker-compose ps
        ;;

    4)
        echo ""
        echo "正在停止所有服务..."
        docker-compose down
        echo "✓ 所有服务已停止"
        ;;

    5)
        echo ""
        echo "正在重启所有服务..."
        docker-compose restart
        echo "✓ 所有服务已重启"
        ;;

    6)
        echo ""
        echo "请选择查看日志:"
        echo "1. 全部日志"
        echo "2. MySQL 日志"
        echo "3. 后端日志"
        echo "4. 前端日志"
        read -p "选项 [1-4]: " log_choice
        case $log_choice in
            1) docker-compose logs -f ;;
            2) docker-compose logs -f mysql ;;
            3) docker-compose logs -f backend ;;
            4) docker-compose logs -f frontend ;;
            *) echo "无效选项" ;;
        esac
        ;;

    *)
        echo "无效选项"
        exit 1
        ;;
esac
