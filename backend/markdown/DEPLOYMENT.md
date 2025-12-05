# 呼吸道感染系统部署指南

## 📋 系统要求

### 硬件要求
- **CPU**: 2核心以上
- **内存**: 4GB以上（推荐8GB）
- **存储**: 20GB以上可用空间
- **网络**: 稳定的互联网连接

### 软件要求
- **操作系统**: Linux (Ubuntu 20.04+, CentOS 7+)
- **Docker**: 20.10+
- **Docker Compose**: 1.29+

## 🚀 快速部署

### 1. 准备服务器环境

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 启动Docker服务
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到docker组
sudo usermod -aG docker $USER
```

### 2. 部署应用

```bash
# 克隆项目代码
git clone <your-repository-url>
cd respiratory_infection

# 配置环境变量
cp .env.example .env
vim .env  # 编辑配置文件

# 确保JAR文件存在
# 将构建好的web-0.0.1.jar文件放到项目根目录

# 注意：RabbitMQ延迟消息功能
# 项目使用了RabbitMQ延迟消息插件，会在构建时自动安装

# 给部署脚本执行权限
chmod +x deploy.sh

# 部署应用
./deploy.sh prod restart
```

### 3. 验证部署

```bash
# 检查服务状态
./deploy.sh prod status

# 查看服务日志
./deploy.sh prod logs

# 健康检查
./deploy.sh prod health

# 访问应用
curl http://localhost:1129/actuator/health
```

## 🔧 配置说明

### 环境变量配置 (.env)

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your-secure-root-password
MYSQL_DATABASE=respiratory_infection
MYSQL_USER=respiratory
MYSQL_PASSWORD=your-secure-password

# Redis配置
REDIS_PASSWORD=your-redis-password

# 应用配置
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-very-long-and-secure-jwt-secret-key

# 文件上传配置
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB
```

### 数据库初始化

1. 将SQL脚本放在 `sql/` 目录下
2. 容器启动时会自动执行初始化脚本

### SSL证书配置（可选）

```bash
# 创建SSL证书目录
mkdir -p ssl

# 放置证书文件
# ssl/cert.pem - 证书文件
# ssl/key.pem - 私钥文件

# 启用HTTPS配置
# 编辑 nginx/conf.d/default.conf，取消HTTPS配置的注释
```

## 📊 监控和维护

### 服务监控

```bash
# 查看容器状态
docker-compose ps

# 查看资源使用情况
docker stats

# 查看日志
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f nginx
```

### 数据备份

```bash
# 自动备份
./deploy.sh prod backup

# 手动备份
docker-compose exec mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD --all-databases > backup.sql
```

### 数据恢复

```bash
# 恢复数据库
docker-compose exec -T mysql mysql -u root -p$MYSQL_ROOT_PASSWORD < backup.sql
```

## 🔒 安全配置

### 防火墙配置

```bash
# 开放必要端口
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw enable
```

### 定期更新

```bash
# 更新镜像
docker-compose pull

# 重启服务
./deploy.sh prod restart

# 清理旧镜像
docker image prune -f
```

## 🛠️ 故障排除

### 常见问题

1. **容器启动失败**
   ```bash
   # 查看详细日志
   docker-compose logs app
   
   # 检查配置文件
   docker-compose config
   ```

2. **数据库连接失败**
   ```bash
   # 检查数据库状态
   docker-compose exec mysql mysqladmin ping
   
   # 查看数据库日志
   docker-compose logs mysql
   ```

3. **内存不足**
   ```bash
   # 调整JVM参数
   # 编辑 docker-compose.yml 中的 JAVA_OPTS
   ```

### 性能优化

1. **数据库优化**
   - 调整MySQL配置参数
   - 定期清理日志文件
   - 优化查询语句

2. **应用优化**
   - 调整JVM堆内存大小
   - 启用连接池
   - 配置缓存策略

3. **Nginx优化**
   - 启用Gzip压缩
   - 配置静态文件缓存
   - 调整worker进程数

## 📞 技术支持

如遇到部署问题，请提供以下信息：

1. 操作系统版本
2. Docker和Docker Compose版本
3. 错误日志
4. 配置文件内容

## 🔄 版本更新

```bash
# 拉取最新代码
git pull origin main

# 重新构建镜像
./deploy.sh prod build

# 重启服务
./deploy.sh prod restart
```

## 📝 注意事项

1. **生产环境部署前请务必修改默认密码**
2. **定期备份数据库数据**
3. **监控服务器资源使用情况**
4. **及时更新系统和应用版本**
5. **配置适当的日志轮转策略**
