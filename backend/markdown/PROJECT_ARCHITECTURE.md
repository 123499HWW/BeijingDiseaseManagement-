# 呼吸道感染打分系统 - 项目架构文档

## 项目概述
本项目采用模块化架构设计，基于Spring Boot 3.4.5构建，使用Maven进行依赖管理。

## 模块架构

### 1. 父模块 (respiratory_infection)
- **作用**: 项目根模块，管理所有子模块和统一依赖版本
- **类型**: pom项目
- **职责**:
  - 统一管理依赖版本
  - 定义公共属性和配置
  - 管理子模块构建顺序

### 2. Common模块 (common)
- **作用**: 通用基础模块，提供项目中共享的基础类和工具
- **依赖**: 无其他内部模块依赖
- **职责**:
  - 实体类 (Entity)
  - 数据传输对象 (DTO)
  - 枚举类 (Enum)
  - 异常处理类 (Exception)
  - 通用工具类 (Util)
  - 统一响应结果类 (Result)
  - 数据访问层 (Mapper)

**主要包结构**:
```
com.hxj.common
├── dto/login/          # 登录相关DTO
├── entity/             # 实体类
├── enums/              # 枚举类
├── exception/          # 异常处理
├── mapper/             # MyBatis Mapper接口
└── result/             # 统一响应结果
```

### 3. JWT模块 (jwt)
- **作用**: JWT认证和授权模块
- **依赖**: 无其他内部模块依赖
- **职责**:
  - JWT token生成和验证
  - Spring Security配置
  - 认证过滤器
  - 用户详情服务

**主要包结构**:
```
com.hxj.jwt
├── config/             # Security配置
├── filter/             # JWT过滤器
├── service/            # 用户详情服务
└── util/               # JWT工具类
```

### 4. Service模块 (service)
- **作用**: 业务逻辑处理模块
- **依赖**: common模块, jwt模块
- **职责**:
  - 业务逻辑实现
  - 数据库操作
  - 业务规则验证
  - 事务管理

**主要包结构**:
```
com.hxj.service
├── impl/               # 业务逻辑实现
└── UserService.java    # 用户服务接口
```

### 5. Web模块 (web)
- **作用**: Web层，提供REST API接口
- **依赖**: service模块（间接依赖common和jwt模块）
- **职责**:
  - REST API控制器
  - 请求参数验证
  - 响应数据封装
  - 全局异常处理
  - 应用程序启动入口

**主要包结构**:
```
com.hxj.web
├── controller/         # REST控制器
└── WebApplication.java # 应用启动类
```

## 模块依赖关系

```
web
 └── service
     ├── common
     └── jwt
         └── (Spring Security, JWT库等外部依赖)
```

## 技术栈

### 核心框架
- **Spring Boot**: 3.4.5
- **Spring Security**: 用于安全认证
- **MyBatis-Plus**: 3.5.7 (数据持久化)

### 数据库
- **MySQL**: 8.0.28
- **Druid**: 1.2.5 (连接池)

### 认证授权
- **JWT**: 0.11.2 (JSON Web Token)

### 工具库
- **Jackson**: 2.15.2 (JSON处理)
- **EasyExcel**: 3.3.2 (Excel处理)
- **Lombok**: 简化代码

### 构建工具
- **Maven**: 依赖管理和项目构建
- **Java**: 21

## 设计原则

### 1. 单一职责原则
每个模块都有明确的职责边界，避免功能重叠。

### 2. 依赖倒置原则
高层模块不依赖低层模块，都依赖于抽象。

### 3. 开闭原则
对扩展开放，对修改关闭。通过接口和抽象类实现。

### 4. 模块化设计
- **Common**: 提供基础设施
- **JWT**: 专注认证授权
- **Service**: 处理业务逻辑
- **Web**: 处理HTTP请求

## 扩展建议

基于当前架构，建议考虑以下扩展模块：

### 1. 医疗业务模块 (medical)
- **作用**: 处理呼吸道感染打分相关的医疗业务逻辑
- **依赖**: common模块
- **包含**:
  - 患者信息管理
  - 症状评估
  - 打分算法
  - 诊断建议

### 2. 报表模块 (report)
- **作用**: 生成各种统计报表和数据分析
- **依赖**: common模块, medical模块
- **包含**:
  - 数据统计
  - 图表生成
  - Excel导出
  - PDF报告

### 3. 系统管理模块 (admin)
- **作用**: 系统管理和配置
- **依赖**: common模块
- **包含**:
  - 用户管理
  - 角色权限
  - 系统配置
  - 日志管理

### 4. 通知模块 (notification)
- **作用**: 消息通知和提醒
- **依赖**: common模块
- **包含**:
  - 邮件通知
  - 短信提醒
  - 系统消息
  - 推送服务

## 部署架构

### 开发环境
- 单体应用部署
- 内嵌Tomcat服务器
- H2/MySQL数据库

### 生产环境
- 可选择单体或微服务部署
- 外部Tomcat/Nginx
- MySQL集群
- Redis缓存
- 负载均衡

## 配置管理

### 配置文件层级
1. `application.yml` - 基础配置
2. `application-dev.yml` - 开发环境
3. `application-prod.yml` - 生产环境

### 配置内容
- 数据库连接
- JWT配置
- 日志配置
- 业务参数

## 安全考虑

### 认证授权
- JWT token认证
- Spring Security集成
- 密码加密存储

### 数据安全
- SQL注入防护
- XSS攻击防护
- CSRF保护

### 接口安全
- 请求参数验证
- 响应数据脱敏
- 访问频率限制

## 监控和运维

### 日志管理
- Logback日志框架
- 分级日志输出
- 日志文件轮转

### 性能监控
- Spring Boot Actuator
- 数据库连接池监控
- JVM性能指标

### 健康检查
- 应用健康状态
- 数据库连接状态
- 外部服务依赖检查
