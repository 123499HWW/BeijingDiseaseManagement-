# 呼吸道感染打分系统 - API文档

## 用户实体更新说明

### 数据库表结构 (tb_user)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| user_id | bigint | 用户ID | 主键，自增 |
| username | varchar(50) | 用户名 | 唯一，不能重复 |
| password | varchar(100) | 密码 | 加密存储 |
| role | enum('ADMIN','USER') | 用户角色 | 默认USER |
| created_at | datetime | 创建时间 | 自动填充 |
| created_by | varchar(50) | 创建人 | 必填 |
| updated_at | datetime | 更新时间 | 自动更新 |
| updated_by | varchar(50) | 更新人 | 必填 |
| is_deleted | tinyint | 逻辑删除标记 | 0-未删除，1-已删除 |
| remark | varchar(500) | 备注 | 可选 |

### 业务规则

1. **用户注册**：新建用户默认只创建role为USER的用户
2. **角色管理**：只有ADMIN用户能修改USER用户的role类型
3. **密码修改**：
   - USER用户修改密码时必须先输入旧密码，只能修改自己的密码
   - ADMIN用户修改密码时直接重置为默认密码'abc123'
4. **用户删除**：只有ADMIN用户可以删除其他用户，使用逻辑删除（is_deleted=1）
5. **逻辑删除**：新增用户默认is_deleted值为0，删除用户时修改为1，查询时自动过滤已删除用户

## API接口文档

### 1. 认证相关接口

#### 1.1 用户注册
```
POST /api/auth/register
Content-Type: application/json
```

**请求参数**：
```json
{
  "username": "testuser",
  "password": "Test123456",
  "remark": "测试用户备注"
}
```

**参数说明**：
- `username`: 用户名，4-20位字母数字下划线
- `password`: 密码，8-20位包含大小写字母和数字
- `remark`: 备注信息（可选）

**响应示例**：
```json
{
  "code": "200",
  "message": "注册成功",
  "data": null
}
```

#### 1.2 用户登录
```
POST /api/auth/login
Content-Type: application/json
```

**请求参数**：
```json
{
  "username": "testuser",
  "password": "Test123456"
}
```

**响应示例**：
```json
{
  "code": "200",
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "role": "USER",
    "remark": "测试用户备注"
  }
}
```

### 2. 用户管理接口

#### 2.1 修改密码
```
POST /api/user/change-password
Content-Type: application/json
Authorization: Bearer <token>
```

**USER用户修改自己密码**：
```json
{
  "username": "testuser",
  "oldPassword": "Test123456",
  "newPassword": "NewTest123456"
}
```

**ADMIN用户重置其他用户密码**：
```json
{
  "username": "targetuser",
  "newPassword": "任意值（会被忽略，重置为abc123）"
}
```

**参数说明**：
- `username`: 目标用户名
- `oldPassword`: 旧密码（USER用户必填，ADMIN用户可选）
- `newPassword`: 新密码（USER用户有效，ADMIN用户会重置为abc123）

**响应示例**：
```json
{
  "code": "200",
  "message": "密码修改成功",
  "data": null
}
```

#### 2.2 修改用户角色
```
POST /api/user/change-role
Content-Type: application/json
Authorization: Bearer <token>
```

**请求参数**（仅ADMIN用户可调用）：
```json
{
  "username": "targetuser",
  "role": "ADMIN"
}
```

**参数说明**：
- `username`: 目标用户名
- `role`: 新角色，可选值：ADMIN、USER

**响应示例**：
```json
{
  "code": "200",
  "message": "用户角色修改成功",
  "data": null
}
```

#### 2.3 删除用户
```
DELETE /api/user/{username}
Authorization: Bearer <token>
```

**参数说明**（仅ADMIN用户可调用）：
- `username`: 要删除的用户名（路径参数）

**响应示例**：
```json
{
  "code": "200",
  "message": "用户删除成功",
  "data": null
}
```

#### 2.4 分页查询用户列表
```
POST /api/user/list
Content-Type: application/json
Authorization: Bearer <token>
```

**请求参数**：
```json
{
  "username": "test",
  "role": "USER",
  "pageNum": 1,
  "pageSize": 10
}
```

**参数说明**：
- `username`: 用户名（模糊查询，可选）
- `role`: 用户角色（精确查询，可选值：ADMIN、USER）
- `pageNum`: 页码（默认1）
- `pageSize`: 每页大小（默认10）

**响应示例**：
```json
{
  "code": "200",
  "message": "操作成功",
  "data": {
    "records": [
      {
        "userId": 1,
        "username": "testuser",
        "role": "USER",
        "createdAt": "2025-11-29T17:00:00",
        "createdBy": "SYSTEM",
        "updatedAt": "2025-11-29T17:00:00",
        "updatedBy": "SYSTEM",
        "remark": "测试用户"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10,
    "pages": 1
  }
}
```

### 3. 测试接口

#### 3.1 公开测试接口
```
GET /api/test/public
```

**响应示例**：
```json
{
  "code": "200",
  "message": "操作成功",
  "data": "公开接口访问成功"
}
```

#### 3.2 受保护测试接口
```
GET /api/test/protected
Authorization: Bearer <token>
```

**响应示例**：
```json
{
  "code": "200",
  "message": "操作成功",
  "data": {
    "message": "认证接口访问成功",
    "username": "testuser",
    "userId": 1
  }
}
```

## 使用示例

### 1. 完整的用户注册登录流程

#### 步骤1：注册普通用户
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "normaluser",
    "password": "User123456",
    "remark": "普通用户"
  }'
```

#### 步骤2：用户登录获取token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "normaluser",
    "password": "User123456"
  }'
```

#### 步骤3：使用token访问受保护接口
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. 用户密码管理

#### 普通用户修改自己密码
```bash
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer USER_TOKEN" \
  -d '{
    "username": "normaluser",
    "oldPassword": "User123456",
    "newPassword": "NewUser123456"
  }'
```

#### 管理员重置用户密码
```bash
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{
    "username": "normaluser",
    "newPassword": "ignored"
  }'
```

### 3. 角色管理（仅管理员）

#### 将普通用户提升为管理员
```bash
curl -X POST http://localhost:8080/api/user/change-role \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{
    "username": "normaluser",
    "role": "ADMIN"
  }'
```

### 4. 用户管理示例

#### 删除用户（仅管理员）
```bash
curl -X DELETE http://localhost:8080/api/user/normaluser \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 分页查询用户列表
```bash
# 查询所有用户（第1页，每页10条）
curl -X POST http://localhost:8080/api/user/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "pageNum": 1,
    "pageSize": 10
  }'

# 按用户名模糊查询
curl -X POST http://localhost:8080/api/user/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "test",
    "pageNum": 1,
    "pageSize": 5
  }'

# 按角色查询
curl -X POST http://localhost:8080/api/user/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "role": "USER",
    "pageNum": 1,
    "pageSize": 10
  }'

# 组合条件查询
curl -X POST http://localhost:8080/api/user/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "admin",
    "role": "ADMIN",
    "pageNum": 1,
    "pageSize": 10
  }'
```

## 预置测试账户

系统预置了两个测试账户：

### 管理员账户
- **用户名**: admin
- **密码**: Admin123
- **角色**: ADMIN
- **权限**: 可以修改任何用户的密码和角色

### 普通用户账户
- **用户名**: user
- **密码**: User123
- **角色**: USER
- **权限**: 只能修改自己的密码

## 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 200 | 操作成功 | 请求成功 |
| 500 | 系统内部错误 | 服务器错误 |
| 1001 | 用户名或密码错误 | 登录失败 |
| 1003 | 无访问权限，请先登录 | 未认证 |
| 1004 | 该用户不存在或已被删除 | 用户不存在 |
| 1005 | 用户用户名或密码错误 | 密码验证失败 |
| 1006 | 该用户已存在 | 用户名重复 |
| 1010 | 请求体格式错误或缺失 | 参数验证失败 |

## 权限矩阵

| 操作 | USER角色 | ADMIN角色 |
|------|----------|-----------|
| 注册账户 | ✅ | ✅ |
| 登录系统 | ✅ | ✅ |
| 修改自己密码 | ✅（需旧密码） | ✅（重置为abc123） |
| 修改他人密码 | ❌ | ✅（重置为abc123） |
| 修改用户角色 | ❌ | ✅ |
| 删除用户 | ❌ | ✅（逻辑删除） |
| 查询用户列表 | ✅ | ✅ |
| 访问受保护接口 | ✅ | ✅ |

## 安全特性

1. **密码加密**: 使用BCrypt算法加密存储
2. **JWT认证**: 基于JWT token的无状态认证
3. **角色权限**: 基于角色的访问控制
4. **参数验证**: 严格的输入参数验证
5. **唯一性约束**: 用户名唯一性保证
6. **逻辑删除**: 数据安全删除，支持数据恢复

## 注意事项

1. **密码复杂度**: 密码必须包含大小写字母和数字，长度8-20位
2. **用户名规则**: 只能包含字母、数字、下划线，长度4-20位
3. **Token有效期**: JWT token默认有效期24小时
4. **默认密码**: ADMIN重置密码时统一设置为'abc123'
5. **权限检查**: 所有用户管理操作都会进行权限验证
6. **逻辑删除**: 删除用户时不会物理删除数据，只是标记为已删除
7. **分页查询**: 支持用户名模糊查询和角色精确查询，默认按创建时间倒序
