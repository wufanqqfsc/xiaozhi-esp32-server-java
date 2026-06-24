# Xiaozhi ESP32 Server Java — 服务信息

> 本文档记录 xiaozhi-esp32-server-java 项目在本机的部署信息、默认凭据与启动方式。
> 生成时间：2026-06-23

---

## 一、启动方式

### 快速启动（后端）

```bash
cd /Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java
./start.sh start          # 下载模型 + Maven 编译 + 启动后端
./start.sh status        # 查看服务状态
./start.sh logs          # 查看日志
./start.sh stop          # 停止后端
```

### 全部启动（后端 + 前端 + DB）

```bash
./start.sh all           # DB 容器 + 后端 + 前端全部启动
./start.sh web           # 仅启动前端（Vue dev server）
./start.sh db-only       # 仅启动 MySQL + Redis（Docker）
./start.sh db-down       # 停止并销毁 DB 容器
```

> **启动脚本**：`./start.sh`（已 chmod +x，位于项目根目录）

---

## 二、当前服务状态

| 服务 | 容器/进程 | 端口 | 状态 | 验证 |
|------|-----------|------|------|------|
| MySQL | xiaozhi-mysql | 3306 | ✅ 健康 | `SELECT 1` 成功 |
| Redis | xiaozhi-redis | 6379 | ✅ 健康 | `PONG` |
| xiaozhi-server | pid=15802 | 8091 | ✅ 运行中 | HTTP 200 `/actuator/health` |
| xiaozhi-dialogue | pid=11944 | 8092 | ✅ 运行中 | HTTP 200 |
| xiaozhi-web 前端 | pid=17240 | 8084 | ✅ 运行中 | HTTP 200 |

### 查看状态

```bash
./start.sh status
```

---

## 三、访问地址

| 服务 | 地址 |
|------|------|
| 前端管理平台 | http://localhost:8084 |
| Server API | http://localhost:8091 |
| Server Swagger | http://localhost:8091/doc.html |
| Dialogue WebSocket | ws://localhost:8092/ws/xiaozhi/v1/ |
| MySQL | localhost:3306 |
| Redis | localhost:6379 |

---

## 四、默认凭据

### 4.1 数据库（MySQL）

| 用途 | 用户名 | 密码 | 主机 |
|------|--------|------|------|
| 应用连接 | `xiaozhi` | `123456` | localhost:3306 |
| 管理员（容器内） | `root` | `abc123456` | 容器内 localhost |

> 连接字符串（xiaozhi 用户）：
> `jdbc:mysql://localhost:3306/xiaozhi?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true`

### 4.2 Redis

| 用户名 | 密码 | 端口 |
|--------|------|------|
| 无认证 | — | 6379 |

### 4.3 前端管理平台

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `123456` | 管理员（authRoleId=1） |

> 登录路径：http://localhost:8084
> 登录 API：`POST /api/user/login`
> 请求体：`{"username":"admin","password":"123456"}`
> 返回 Token 放在 Header：`Authorization: <token>`

### 4.4 管理员用户信息

| 字段 | 值 |
|------|-----|
| 用户名 | `admin` |
| 显示名 | `小智` |
| 用户 ID | `1` |
| 角色 ID | `1`（管理员） |
| 状态 | 正常（state=1） |
| 创建时间 | 2025-03-09 |

---

## 五、数据库信息

### 5.1 数据库名称

`xiaozhi`

### 5.2 主要数据表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `sys_device` | 设备表 |
| `sys_role` | 角色表 |
| `sys_auth_role` | 角色权限表 |
| `sys_auth_role_permission` | 角色权限关联表 |
| `sys_permission` | 权限表 |
| `sys_config` | AI 配置表（STT/TTS/LLM 等） |
| `sys_code` | 激活码表 |
| `sys_message` | 消息记录表 |
| `sys_operation_log` | 操作日志表 |
| `sys_template` | 模板表 |
| `sys_mcp_tool_exclude` | MCP 工具排除表 |
| `sys_summary` | 摘要表 |
| `flyway_schema_history` | Flyway 迁移历史 |

### 5.3 角色定义

| 角色 ID | 角色名 | Key | 说明 |
|---------|--------|-----|------|
| 1 | 管理员 | `admin` | 系统管理员，拥有所有权限 |
| 2 | 普通用户 | `user` | 普通用户，拥有基本操作权限 |

---

## 六、Flyway 迁移记录

| 版本 | 描述 | 状态 |
|------|------|------|
| V0 | Flyway Baseline | ✅ 成功 |
| V1 | init（建表 + 初始化数据） | ✅ 成功 |
| V2 | add default local oss config | ✅ 成功 |
| V3 | add tool sender type | ✅ 成功 |
| V4 | extract message metrics table | ✅ 成功 |
| V5 | add web chat permission | ✅ 成功 |
| V6 | add message source | ✅ 成功 |
| V7 | add device mcp list | ✅ 成功 |
| V8 | add sys message metadata | ✅ 成功 |
| V9 | add config enable thinking | ✅ 成功 |
| V10 | change tts pitch speed to double | ✅ 成功 |

---

## 七、环境信息

| 项目 | 版本 |
|------|------|
| Java | OpenJDK 25.0.2（需要 Java 21+） |
| Maven | 3.9.14 |
| Node.js | v22.16.0 |
| MySQL | 8.0（Docker 容器） |
| Redis | 7（Docker 容器） |
| 前端包管理器 | npm |
| 项目版本 | 5.0.0 |

### Maven 编译命令

```bash
mvn clean install -DskipTests -q -f pom.xml
```

### Java 启动命令

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
java -Djava.library.path=lib -jar xiaozhi-server/target/xiaozhi-server-5.0.0.jar
java -Djava.library.path=lib -jar xiaozhi-dialogue/target/xiaozhi-dialogue-5.0.0-exec.jar
```

---

## 八、常见问题

### Q1：启动脚本报 `name: unbound variable`
**原因**：`set -u` 开启时 `$!` 在某些环境下为空。
**解决**：脚本已修复（v1.1+），`$!` 先存入变量再使用。

### Q2：MySQL 镜像拉取超时
**原因**：本机 Docker Hub 访问慢。
**解决**：本地已有 `mysql:8.0` 镜像，已改用本地镜像。

### Q3：Flyway 迁移失败（CREATE USER 权限不足）
**原因**：`xiaozhi` 用户缺少全局 `CREATE USER` 权限。
**解决**：已在 MySQL 中手动执行：
```sql
GRANT CREATE USER ON *.* TO 'xiaozhi'@'%';
FLUSH PRIVILEGES;
```
**已在本地修复**，`flyway_schema_history` 已更新。

### Q4：前端端口 8084 无法访问
**解决**：确保先 `npm install`（首次运行自动执行），然后 `npm run dev`。

---

## 九、相关文件

| 文件 | 说明 |
|------|------|
| `start.sh` | 一键启动脚本（项目根目录） |
| `bin/all.sh` | 原始后端管理脚本 |
| `bin/_common.sh` | 后端脚本公共函数 |
| `docker-compose-db.yml` | DB 容器编排（MySQL + Redis） |
| `scripts/download_base.sh` | 原生库下载脚本 |
| `scripts/download_models.sh` | 模型下载脚本 |
| `db/init.sql` | 数据库初始化 SQL |

---

*本文档由 AI 助手自动生成，内容基于实际运行环境和数据库查询。*
