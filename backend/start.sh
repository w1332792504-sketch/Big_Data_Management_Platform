# 大数据管理平台 - 快速启动脚本

#!/bin/bash

echo "========================================"
echo "  大数据管理平台 - 启动脚本"
echo "========================================"

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误：Docker 未安装"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误：Docker Compose 未安装"
    exit 1
fi

echo ""
echo "请选择启动方式："
echo "1. 启动全部服务（MySQL + Redis + Backend）"
echo "2. 仅启动 MySQL"
echo "3. 仅启动 Redis"
echo "4. 停止全部服务"
echo "5. 查看服务状态"
echo ""
read -p "请输入选项 [1-5]: " choice

case $choice in
    1)
        echo "正在启动全部服务..."
        docker-compose up -d
        echo "启动完成!"
        ;;
    2)
        echo "正在启动 MySQL..."
        docker-compose up -d mysql
        echo "MySQL 启动完成!"
        ;;
    3)
        echo "正在启动 Redis..."
        docker-compose up -d redis
        echo "Redis 启动完成!"
        ;;
    4)
        echo "正在停止全部服务..."
        docker-compose down
        echo "服务已停止!"
        ;;
    5)
        docker-compose ps
        ;;
    *)
        echo "无效选项"
        exit 1
        ;;
esac
