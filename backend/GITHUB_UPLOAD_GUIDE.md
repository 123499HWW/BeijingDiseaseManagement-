# GitHub上传指南

## 准备工作（已完成）
✅ Git仓库初始化完成
✅ .gitignore文件已配置
✅ README.md已更新

## 上传步骤

### 1. 配置Git用户信息（如果未配置）
```bash
git config --global user.name "你的GitHub用户名"
git config --global user.email "你的GitHub邮箱"
```

### 2. 添加远程仓库
请提供您的GitHub仓库URL，然后执行：
```bash
# 替换下面的URL为您的实际仓库地址
git remote add origin https://github.com/用户名/仓库名.git

# 或者使用SSH（如果已配置SSH密钥）
git remote add origin git@github.com:用户名/仓库名.git
```

### 3. 添加所有文件到暂存区
```bash
git add .
```

### 4. 创建第一次提交
```bash
git commit -m "Initial commit: 呼吸道感染智能评估系统"
```

### 5. 推送到GitHub
```bash
# 首次推送，设置上游分支
git push -u origin main

# 如果默认分支是master
git push -u origin master
```

## 可能遇到的问题及解决方案

### 问题1：main vs master分支
GitHub新仓库默认分支可能是`main`，如果推送失败，尝试：
```bash
git branch -M main
git push -u origin main
```

### 问题2：认证失败
如果使用HTTPS且密码认证失败，需要使用Personal Access Token：
1. 在GitHub设置中生成Personal Access Token
2. 推送时使用Token作为密码

### 问题3：仓库已有内容
如果远程仓库已有README或其他文件：
```bash
# 先拉取远程内容
git pull origin main --allow-unrelated-histories

# 解决冲突后再推送
git push origin main
```

### 问题4：文件过大
如果有大文件（>100MB），考虑：
- 添加到.gitignore
- 使用Git LFS处理大文件
- 删除target目录（已在.gitignore中）

## 项目文件说明

### 需要上传的核心文件
- **源代码**: src/目录下的所有Java文件
- **配置文件**: pom.xml, application.yml等
- **文档**: *.md文档文件
- **SQL脚本**: 数据库初始化脚本

### 已忽略的文件（.gitignore）
- target/ - 编译输出目录
- .idea/ - IDE配置文件
- *.iml - IDE模块文件
- .vscode/ - VS Code配置

## 后续维护

### 日常提交流程
```bash
# 查看状态
git status

# 添加修改
git add .

# 提交
git commit -m "描述本次修改内容"

# 推送
git push
```

### 创建分支开发
```bash
# 创建并切换到新分支
git checkout -b feature/新功能名称

# 开发完成后推送分支
git push origin feature/新功能名称

# 在GitHub上创建Pull Request合并到主分支
```

## 项目亮点（可在GitHub页面展示）

1. **完整的医疗评分系统** - 实现8个标准化评分算法
2. **消息队列架构** - RabbitMQ异步处理
3. **批量数据处理** - Excel导入和批量评估
4. **完善的文档** - 包含评分规则、API文档、故障排查指南
5. **模块化设计** - 清晰的项目结构，易于扩展

## 建议的GitHub仓库设置

1. **添加描述**: "Respiratory Infection Assessment System - 呼吸道感染智能评估系统"
2. **添加标签**: 
   - `spring-boot`
   - `rabbitmq`
   - `medical`
   - `assessment-system`
   - `java`
3. **选择协议**: 根据需要选择MIT、Apache 2.0等
4. **添加README徽章**:
   - 构建状态
   - 代码覆盖率
   - 许可证

---

请提供您的GitHub仓库URL，我将帮您执行具体的上传命令。
