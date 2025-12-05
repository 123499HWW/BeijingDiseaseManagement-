# 呼吸道感染智能评估系统

一个基于Spring Boot的医疗评估系统，提供8种呼吸道感染相关评分的自动计算，支持批量评估和RabbitMQ消息队列处理。

## 项目结构

- `common/` - 通用模块（实体类、DTO、Mapper、常量等）
- `exception/` - 异常处理模块
- `jwt/` - JWT认证模块，提供用户认证和授权功能
- `service/` - 业务逻辑模块（评估服务、消息队列服务等）  
- `web/` - Web应用模块，提供REST API接口

## 核心功能

### 8个医学评分系统
1. **CURB-65评分** - 社区获得性肺炎严重程度评估
2. **PSI评分** - 肺炎严重程度指数
3. **CPIS评分** - 临床肺部感染评分
4. **qSOFA评分** - 快速序贯器官功能衰竭评估
5. **SOFA评分** - 序贯器官功能衰竭评估
6. **重症肺炎诊断** - 基于主要和次要标准
7. **COVID-19重型诊断** - 成人/儿童重型判定
8. **COVID-19危重型诊断** - 危重程度评估

### RabbitMQ消息队列
- 患者数据导入队列处理
- 数据迁移队列（结构化处理）
- 综合评估队列（8个评估并行执行）
- 死信队列和重试机制
- 延迟消息处理

### 数据处理流程
1. Excel批量导入患者数据
2. 自动触发数据迁移（文本转结构化）
3. 迁移完成后自动触发8项评估
4. 评估结果存储和查询

## 快速开始

### 1. 数据库配置

创建MySQL数据库并执行初始化脚本：

```sql
-- 执行 web/src/main/resources/sql/init.sql
```

### 2. 配置文件

修改 `web/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/respiratory_infection
    username: your_username
    password: your_password
```

### 3. 启动应用

```bash
cd web
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API接口

### 认证接口

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test123456",
  "email": "test@example.com",
  "phone": "13800138000",
  "realName": "测试用户"
}
```

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test123456"
}
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "realName": "测试用户",
    "email": "test@example.com"
  }
}
```

### 测试接口

#### 公开接口
```
GET /api/test/public
```

#### 受保护接口
```
GET /api/test/protected
Authorization: Bearer <your_jwt_token>
```

## 技术栈

- Spring Boot 3.4.5
- Spring Security
- JWT (jjwt 0.11.2)
- MyBatis-Plus 3.5.7
- MySQL 8.0
- Druid连接池
- Lombok

## 密码规则

- 长度8-20位
- 必须包含大写字母、小写字母和数字
- 可包含特殊字符：@$!%*?&

## 用户名规则

- 长度4-20位
- 只能包含字母、数字、下划线

## JWT配置

- 默认过期时间：24小时（86400秒）
- 签名算法：HS256
- 密钥可在配置文件中修改

## 开发说明

### 添加新的受保护接口

1. 在Controller中添加接口方法
2. 需要认证的接口会自动被JWT过滤器保护
3. 可通过 `SecurityContextHolder.getContext().getAuthentication()` 获取当前用户信息

### 自定义权限控制

可以在 `JwtConfig.java` 中修改 `SecurityFilterChain` 配置来自定义权限规则。

### 扩展用户信息

1. 修改 `User` 实体类
2. 更新数据库表结构
3. 修改相关DTO和Service类
