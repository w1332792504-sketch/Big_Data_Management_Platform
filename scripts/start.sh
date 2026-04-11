#!/bin/bash

# ====================================
# 大数据管理平台 - 快速启动脚本
# ====================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================"
echo "  大数据管理平台 - 启动脚本"
echo "======================================"

# 检查 MySQL 是否运行
check_mysql() {
    if ! command -v mysql &> /dev/null; then
        echo "警告：未找到 MySQL 客户端"
        return 1
    fi
    echo "✓ MySQL 检查通过"
}

# 检查 Java 是否安装
check_java() {
    if ! command -v java &> /dev/null; then
        echo "错误：未找到 Java，请安装 Java 11+"
        exit 1
    fi
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "✓ Java: $JAVA_VERSION"
}

# 检查 Node.js 是否安装
check_node() {
    if ! command -v node &> /dev/null; then
        echo "警告：未找到 Node.js，前端将无法启动"
        return 1
    fi
    NODE_VERSION=$(node -v)
    echo "✓ Node.js: $NODE_VERSION"
}

# 检查 DataX 是否安装
check_datax() {
    if [ -d "/opt/datax" ]; then
        echo "✓ DataX: /opt/datax"
    else
        echo "警告：DataX 未安装，数据抽取功能将无法使用"
        echo "      请安装 DataX 到 /opt/datax"
    fi
}

# 初始化数据库
init_database() {
    echo ""
    echo "正在初始化数据库..."
    read -p "请输入 MySQL root 密码：" -s MYSQL_PASSWORD
    echo ""

    mysql -u root -p"$MYSQL_PASSWORD" < scripts/init.sql

    if [ $? -eq 0 ]; then
        echo "✓ 数据库初始化成功"
    else
        echo "✗ 数据库初始化失败"
        exit 1
    fi
}

# 启动后端
start_backend() {
    echo ""
    echo "正在启动后端服务..."
    cd backend

    if [ -f "target/data-manager-1.0.0.jar" ]; then
        java -jar target/data-manager-1.0.0.jar &
        echo "✓ 后端服务已启动 (端口 8080)"
    else
        echo "正在编译后端..."
        mvn clean package -DskipTests
        java -jar target/data-manager-1.0.0.jar &
        echo "✓ 后端服务已启动 (端口 8080)"
    fi

    cd ..
}

# 启动前端
start_frontend() {
    echo ""
    echo "正在启动前端服务..."
    cd frontend

    if [ ! -d "node_modules" ]; then
        echo "正在安装依赖..."
        npm install
    fi

    npm run dev &
    echo "✓ 前端服务已启动 (端口 3000)"

    cd ..
}

# 主菜单
main() {
    echo ""
    echo "请选择启动模式:"
    echo "1. 仅后端 (Java)"
    echo "2. 仅前端 (Node.js)"
    echo "3. 全部启动"
    echo "4. 初始化数据库"
    echo "5. 检查环境"
    echo ""

    read -p "请输入选项 [1-5]: " choice

    case $choice in
        1)
            check_java
            start_backend
            ;;
        2)
            check_node
            start_frontend
            ;;
        3)
            check_java
            check_node
            start_backend
            start_frontend
            echo ""
            echo "======================================"
            echo "  全部服务已启动!"
            echo "======================================"
            echo "  后端 API: http://localhost:8080"
            echo "  前端页面：http://localhost:3000"
            echo "======================================"
            ;;
        4)
            init_database
            ;;
        5)
            check_java
            check_node
            check_mysql
            check_datax
            ;;
        *)
            echo "无效选项"
            exit 1
            ;;
    esac
}

main
