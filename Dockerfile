# 使用官方 OpenJDK 21 作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 创建必要的目录
RUN mkdir -p /app/logs /app/uploads /app/config

# 添加元数据标签
LABEL maintainer="Respiratory Infection System"
LABEL version="0.0.1"
LABEL description="Respiratory Infection Management System"

# 设置时区为上海
RUN apt-get update && apt-get install -y --no-install-recommends \
    tzdata \
    curl \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 设置环境变量
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx2048m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:+UseStringDeduplication"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=1129

# 复制JAR文件
COPY web-0.0.1.jar /app/respiratory.jar

# 暴露应用程序端口
EXPOSE ${SERVER_PORT}

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# 设置容器启动命令
ENTRYPOINT ["java", "-Xms512m", "-Xmx2048m", "-XX:+UseG1GC", "-Dserver.address=0.0.0.0", "-jar", "/app/respiratory.jar", "--spring.profiles.active=prod"]
