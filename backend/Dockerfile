FROM maven:3.9-openjdk-23 AS builder

WORKDIR /app

# 复制 pom.xml
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline -Dmaven.test.skip=true

# 复制源代码
COPY src ./src

# 打包
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:23-jre-slim

WORKDIR /app

# 安装 Python 和 DataX (用于数据抽取)
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 安装 DataX
RUN curl -L https://github.com/alibaba/DataX/releases/download/datax-release/datax.tar.gz -o /tmp/datax.tar.gz \
    && tar -zxvf /tmp/datax.tar.gz -C /opt \
    && mv /opt/datax /opt/datax \
    && rm /tmp/datax.tar.gz

# 复制应用 JAR
COPY --from=builder /app/target/data-manager-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
