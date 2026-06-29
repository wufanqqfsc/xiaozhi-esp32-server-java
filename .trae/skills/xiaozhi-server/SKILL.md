---
name: "xiaozhi-server"
description: "Provides expert knowledge of the xiaozhi-esp32-server-java Java backend (dual-process architecture: 8091 server + 8092 dialogue). Invoke when working on this repo, debugging service startup, modifying STT/TTS/LLM providers, configuring FunASR, WebSocket dialogue pipeline, or running start.sh commands."
---

# Xiaozhi ESP32 Server Java — Server-Side Skill

This skill distills the architecture, modules, startup, and operational knowledge of the [xiaozhi-esp32-server-java](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java) repository, so any subsequent task can act on the codebase without re-reading every file.

## When to invoke

Use this skill whenever the user asks about:

- Starting / stopping / debugging the backend (`./start.sh` family of commands)
- Adding or troubleshooting an STT/TTS/LLM provider (Vosk, FunASR, Aliyun, Tencent, Edge, MiniMax, sherpa-onnx, …)
- WebSocket dialogue pipeline (`SessionManager`, `Persona`, `WebSocketHandler`)
- FunASR Docker deployment (`docker-compose-funasr.yml`, `bin/funasr.sh`)
- Port 8091 (REST) / 8092 (WebSocket) / 8084 (Web admin)
- Maven module boundaries (`xiaozhi-common / -service / -ai / -dialogue / -server`)
- Database / Redis Flyway migrations under `db/`
- ESP32 device-side OTA + WebSocket URL configuration

## 1. Project at a glance

| Item | Value |
|------|-------|
| Language / Build | Java 21 + Maven 3.8+ |
| Framework | Spring Boot 3.5.8, Spring MVC, MyBatis-Plus, Flyway |
| Persistence | MySQL 8.0 + Redis 7 (Docker Compose via `docker-compose-db.yml`) |
| Real-time | Netty WebSocket + Sa-Token (port 8092) |
| Auth | Sa-Token 1.39.0 + Redisson 3.25.0 |
| LLM SDK | Spring AI 1.1.4 (MCP client included) |
| Local inference | ONNX Runtime + sherpa-onnx 1.12.21 |
| Frontend | Vue 3 + Ant Design (port 8084) |

Full wiki: [docs/CODE_WIKI.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/CODE_WIKI.md)

## 2. Dual-process architecture

```
┌─────────────────────────────────────────────────────────┐
│ xiaozhi-server      :8091   REST API + Admin UI backend │
│   ├── User / Role / Device / OTA controllers            │
│   └── Calls into service / ai / dialogue modules        │
│                                                         │
│ xiaozhi-dialogue    :8092   WebSocket dialogue engine   │
│   ├── SessionManager / WebSocketHandler                 │
│   ├── Persona (聚合根) → STT → LLM → TTS → Player       │
│   └── VAD (Silero) + AEC                                │
│                                                         │
│ Shared infra:  MySQL :3306  +  Redis :6379              │
│ Optional:      FunASR :10095/10096  (STT)               │
└─────────────────────────────────────────────────────────┘
```

The two processes **share DB and Redis only** — they do not call each other in-process. Horizontal scaling is done by running additional `xiaozhi-dialogue` instances behind a load balancer.

## 3. Maven module map

```
xiaozhi-parent
├── xiaozhi-common       utils, enums, domain events, BO/DO/req/resp
├── xiaozhi-service      user, device, role, config, token, storage, agent…
├── xiaozhi-ai           LLM / STT / TTS / Memory / MCP / Tools
├── xiaozhi-dialogue     WS server, audio, session, persona, playback
└── xiaozhi-server       Spring Boot launcher (8091) — aggregator
```

Never put business logic in `xiaozhi-server`; it should only host Controllers and configuration.

## 4. Dialogue pipeline (port 8092)

```
ESP32 device ──WebSocket──► WebSocketHandler
                              │
                              ▼
                       SessionManager.register()
                              │
                              ▼
                          Persona (runtime)
                              │
   ┌──────────────┬───────────┼───────────┬──────────────┐
   ▼              ▼           ▼           ▼              ▼
 SileroVAD   SttService   ChatModel   Synthesizer    ToolCallback
 (VAD)     (FunASR/…)   (LLM SDK)    (TTS provider)   (MCP)
                              │
                              ▼
                        Player (Opus out → device)
```

Key files:
- `xiaozhi-dialogue/src/main/java/com/xiaozhi/dialogue/runtime/Persona.java` — core aggregate
- `xiaozhi-dialogue/src/main/java/com/xiaozhi/communication/common/SessionManager.java` — session registry
- `xiaozhi-dialogue/src/main/java/com/xiaozhi/communication/server/websocket/WebSocketHandler.java`
- `xiaozhi-dialogue/src/main/java/com/xiaozhi/dialogue/audio/VadService.java`
- `xiaozhi-dialogue/src/main/java/com/xiaozhi/dialogue/playback/Synthesizer.java`

## 5. AI provider factories (port 8092)

All providers live under `xiaozhi-ai/` and are wired through factories using `Map<providerName, Provider>`.

- **LLM** (`ChatModelFactory`): OpenAI, ZhiPu, XingHuo, XingChen, Ollama, Dify, Coze
- **TTS** (`TtsServiceFactory`): Edge, Aliyun, Tencent, Volcengine, MiniMax, sherpa-onnx (local), Xfyun
- **STT** (`SttServiceFactory`): Vosk (local), FunASR (Docker WS), Aliyun, Tencent, Volcengine, Xfyun

Adding a new provider:
1. Implement the SPI interface in `xiaozhi-ai/{stt,tts,llm}/providers/<name>/`.
2. Register it in the corresponding `*ServiceFactory` `@PostConstruct` map.
3. Add a row in `sys_config` (`configType='stt'|'tts'|'llm'`, `provider=<name>`).
4. Bind `sttId` / `ttsId` / `llmId` in the role record.
5. Restart `xiaozhi-dialogue`.

## 6. FunASR integration (recommended STT)

Complete deployment doc: [docs/FUNASR_DEPLOYMENT.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/FUNASR_DEPLOYMENT.md)

Quick reference:
```bash
# 1. Pre-download 4 models into ./funasr-runtime-resources/models/
./bin/download-funasr-models.sh

# 2. Start the FunASR container (port 10095 inside, mapped to 10096 host)
./bin/funasr.sh start

# 3. Verify
./bin/funasr.sh status
./bin/test-funasr-connection.py

# 4. Wire it up via web admin: STT 配置 → provider=funasr → apiUrl=ws://localhost:10096
```

Stack: `funasr-runtime-sdk` (C++) + `Paraformer-Large` + `2pass` mode + VAD + punctuation.

## 7. start.sh cheat-sheet

`start.sh` is the single source of truth for lifecycle (uses `set -euo pipefail`).

| Command | Purpose |
|---------|---------|
| `./start.sh` / `./start.sh start` | One-shot backend: clean logs → free ports → DB → download models → mvn build → start server+dialogue |
| `./start.sh all` | All-in: DB + backend + frontend dev server (port 8084) |
| `./start.sh stop` | Stop `xiaozhi-server` and `xiaozhi-dialogue` (graceful → SIGKILL after 15 s) |
| `./start.sh restart` | Stop + start backend (does not touch web) |
| `./start.sh status` | DB + backend + frontend running state with PIDs |
| `./start.sh logs [server\|dialogue\|web\|all]` | `tail -f` selected log |
| `./start.sh db-only` | Start MySQL + Redis containers only |
| `./start.sh db-down` | Tear down DB containers (DATA LOSS) |
| `./start.sh web` | Start Vue admin only (port 8084) |
| `./start.sh clean` | Remove `run/*.pid` + Maven `target/` |
| `./start.sh -h` / `--help` | Usage |

Environment variables:
- `SKIP_DOWNLOAD=1` — skip native libs/model downloads
- `SKIP_BUILD=1` — skip `mvn install`
- `JAVA_HOME` — defaults to Homebrew openjdk@25 path
- `DB_MODE=docker` (default) | `local`

PID files live in `run/`, logs in `logs/`.

## 8. Port & module inventory

| Port | Service | Module | PID file |
|------|---------|--------|----------|
| 3306 | MySQL 8.0 | docker (`xiaozhi-mysql`) | — |
| 6379 | Redis 7 | docker (`xiaozhi-redis`) | — |
| 8091 | Admin REST API | `xiaozhi-server` | `run/xiaozhi-server.pid` |
| 8092 | WebSocket dialogue | `xiaozhi-dialogue` | `run/xiaozhi-dialogue.pid` |
| 8084 | Vue admin (dev) | `web/` | `run/xiaozhi-web.pid` |
| 10095 | FunASR (container) | docker (`funasr-wss-server-2pass`) | — |
| 10096 | FunASR (host map) | same as above | — |

## 9. Configuration files

| File | Purpose |
|------|---------|
| `docker-compose-db.yml` | MySQL + Redis containers |
| `docker-compose-funasr.yml` | FunASR runtime |
| `web/.env.development` | Frontend dev vars |
| `web/.env.production` | Frontend prod vars |
| `bin/funasr.sh` / `bin/download-funasr-models.sh` / `bin/docker-entrypoint-funasr.sh` | FunASR lifecycle |
| `db/` (Flyway) | Auto-applied on first server boot |
| `scripts/download_base.sh` | Vosk / sherpa-onnx native libs |

## 10. Common troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `port 8091 已被占用` | Old PID left behind | `./start.sh stop` or `lsof -nP -iTCP:8091` |
| `需要 Java 21+` | Wrong JDK | Set `JAVA_HOME` to Java 21+ |
| `Docker daemon 未运行` | Docker Desktop stopped | Restart Docker Desktop |
| `MySQL 健康检查未在 60s 内完成` | Slow image pull | Re-run or `docker logs xiaozhi-mysql` |
| Vosk init: `vosk_recognizer_set_grm symbol not found` | JNI lib mismatch | Use FunASR or cloud STT instead |
| FunASR container exits | Subprocess `exec` issue | Container entrypoint must `exec funasr-wss-server-2pass` as PID 1 (see `bin/docker-entrypoint-funasr.sh`) |
| OTA `firmware.version` stuck at `1.0.0` | Not implemented yet | Implement firmware upload endpoint under `xiaozhi-server` |
| WebSocket `token` empty | Design choice | Devices authenticate via `Device-Id` header → `sys_device` table |

## 11. Code-navigation cheat-codes

When investigating, jump straight to:

- Dialogue entrypoint → `xiaozhi-dialogue/src/main/java/com/xiaozhi/dialogue/DialogueApplication.java`
- Admin entrypoint → `xiaozhi-server/src/main/java/com/xiaozhi/server/XiaozhiApplication.java`
- OTA endpoint → `xiaozhi-server/.../web/device/DeviceController.java`
- WebSocket config → `xiaozhi-dialogue/.../communication/server/websocket/WebSocketConfig.java`
- Persona factory → `xiaozhi-dialogue/.../llm/factory/PersonaFactory.java`
- ChatModel factory → `xiaozhi-ai/.../llm/factory/ChatModelFactory.java`
- STT factory → `xiaozhi-ai/.../stt/SttServiceFactory.java`
- FunASR client → `xiaozhi-ai/.../stt/providers/funasr/`
- Flyway migrations → `db/`

## 12. Quick links

- Full Wiki: [docs/CODE_WIKI.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/CODE_WIKI.md)
- FunASR deployment: [docs/FUNASR_DEPLOYMENT.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/FUNASR_DEPLOYMENT.md)
- Startup script: [start.sh](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/start.sh)
- FunASR compose: [docker-compose-funasr.yml](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docker-compose-funasr.yml)
- FunASR launcher: [bin/funasr.sh](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/bin/funasr.sh)
- Model downloader: [bin/download-funasr-models.sh](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/bin/download-funasr-models.sh)
- DB compose: [docker-compose-db.yml](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docker-compose-db.yml)
- Server info & creds: [SERVER_INFO.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/SERVER_INFO.md)
- **REST API 文档**: [docs/API.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/API.md)（12660 行完整 OpenAPI/Swagger 描述）

## 13. REST API 端点速查表

xiaozhi-server (port 8091) 暴露 **12 个 Controller / 41 个端点**，全部在 `/api/**` 前缀下，详见 [docs/API.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/API.md)。Swagger UI 实时浏览：`http://localhost:8091/swagger-ui/index.html`。

| Controller | Base Path | 端点数 | 主要职责 |
|-----------|-----------|--------|---------|
| `UserController` | `/api/user` | 11 | 登录/注册/Token/密码重置/微信登录/手机号登录 |
| `DeviceController` | `/api/device` | 8 | OTA、CRUD、激活状态、批量更新 |
| `RoleController` | `/api/role` | 7 | 角色 CRUD + TTS 测试 + sherpa-onnx 音色列表 |
| `ConfigController` | `/api/config` | 4 | LLM/TTS/STT 配置 CRUD |
| `TemplateController` | `/api/template` | 4 | 提示词模板 CRUD |
| `MessageController` | `/api/message` | 4 | 对话消息 CRUD + 会话列表 |
| `McpToolController` | `/api/mcpTool` | 5 | MCP 工具启用/禁用（角色级/全局） |
| `AuthRoleController` | `/api/auth-role` | 3 | 后台 RBAC 权限分配 |
| `MemoryController` | `/api/memory` | 2 | 摘要记忆查询/批量删除 |
| `WebChatController` | `/api/chat` | 3 | Web 端 SSE 流式聊天 |
| `AgentController` | `/api/agent` | 1 | Coze/Dify 智能体查询 |
| `FileUploadController` + `MusicController` | `/api/file` | 2 | 通用文件上传 / 音乐上传 |

### 13.1 完整端点路径

#### 用户管理 (`/api/user`)
```
GET  /check-token          验证当前 Token，返回用户信息
POST /refresh-token        刷新 Token 有效期
POST /login                用户名/邮箱/手机号 + 密码登录
POST /tel-login            手机号 + 短信验证码登录（未注册自动注册）
POST /wx-login             微信 code 登录（未注册自动注册）
POST /                     用户注册
GET  /                     分页查询用户列表
PUT  /{userId}             修改用户信息（需权限 system:setting:account:api:update）
POST /resetPassword        通过邮箱验证码重置密码
POST /sendEmailCaptcha     发送邮箱验证码
POST /sendSmsCaptcha       发送短信验证码
GET  /checkUser            检查用户名/手机号是否已存在
```

#### 设备管理 (`/api/device`)
```
GET|POST /ota              ESP32 OTA 请求处理（自动激活设备）
PUT  /{deviceId}           更新设备信息
DELETE /{deviceId}         删除设备
POST /                     添加设备
GET  /                     条件查询设备列表
POST /ota/activate         查询 OTA 激活状态
POST /batchUpdate          批量更新设备
```

#### 角色管理 (`/api/role`)
```
PUT  /{roleId}             更新角色信息
DELETE /{roleId}           删除角色
GET  /                     条件查询角色列表
POST /                     添加角色
GET  /testVoice            TTS 测试（返回音频流）
GET  /sherpaVoices         获取本地 sherpa-onnx 可用音色列表
```

#### 配置管理 (`/api/config`)
```
PUT  /{configId}           更新配置（LLM/TTS/STT）
DELETE /{configId}         删除配置
GET  /                     条件查询配置列表
POST /                     添加配置
```

#### 提示词模板 (`/api/template`)
```
GET    /                   分页查询模板
POST   /                   添加模板
PUT    /{templateId}       更新模板
DELETE /{templateId}       删除模板
```

#### 对话消息 (`/api/message`)
```
GET    /                   分页查询对话消息
POST   /batchDelete        批量删除设备消息
GET    /conversations      查询用户的会话列表
DELETE /{messageId}        删除单条消息
```

#### Web 聊天 (`/api/chat`) — SSE 流式
```
POST /open                 创建/续接 Web 聊天会话，返回 sessionId
GET  /stream               SSE 流式聊天（?sessionId=...&text=...），event: thinking|content
POST /close                关闭聊天会话
```

#### MCP 工具管理 (`/api/mcpTool`)
```
POST /role/{roleId}/exclude-tools    批量设置角色排除工具
POST /role/{roleId}/tools-switch     切换单个角色工具启用状态
POST /system-global                  切换全局工具状态
GET  /system-global                  获取系统全局工具列表
GET  /role/{roleId}/disabled-tools   获取禁用的工具列表
```

#### 后台权限角色 (`/api/auth-role`)
```
GET  /{authRoleId}/permissions        获取授权配置
PUT  /{authRoleId}/permissions        更新授权配置
GET  /                                条件查询后台角色列表
```

#### 记忆管理 (`/api/memory`)
```
GET    /summary/{roleId}/{deviceId}      查询指定角色的摘要记忆
DELETE /summary/{roleId}/{deviceId}      批量删除指定角色的摘要记忆
```

#### 智能体管理 (`/api/agent`)
```
GET  /                       条件查询 Coze/Dify 智能体列表
```

#### 文件上传 (`/api/file`)
```
POST /upload                 通用文件上传（图片/音频/固件）
POST /music                  音乐文件上传（铃声/提示音）
```

### 13.2 认证与权限约定

- **认证方式**：Sa-Token（`Authorization: Bearer <token>` 或 Cookie）
- **权限注解**：`@SaCheckPermission("system:xxx:yyy")`
- **审计日志**：`@AuditLog(module="...", operation="...")`
- **响应包装**：`ApiResponse<T>` 统一格式 `{ code, message, data }`
- **参数校验**：`@Valid` + JSR-303 (`@NotBlank`, `@Email` 等)

### 13.3 OTA 协议（ESP32 设备端）

```
请求：GET|POST /api/device/ota
Header: Device-Id: <mac>, Client-Id: <uuid>, Authorization: Bearer <token>
Body:  {
  "application": { "name": "...", "version": "1.0.0" },
  "device":       { "mac": "...", "chipModel": "esp32s3" }
}
响应：200 { server_time, websocket={url, token}, mqtt=..., firmware=... }
```

OTA 端点是**双端点**设计（GET + POST 同时存在），ESP32 固件侧通常用 POST（可携带更复杂 JSON）。