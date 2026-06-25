# Xiaozhi ESP32 Server Java - Code Wiki

## 目录

1. [项目概述](#1-项目概述)
2. [项目架构](#2-项目架构)
3. [模块结构](#3-模块结构)
4. [核心模块详解](#4-核心模块详解)
5. [依赖关系](#5-依赖关系)
6. [关键类与函数](#6-关键类与函数)
7. [配置说明](#7-配置说明)
8. [运行方式](#8-运行方式)
   - [8.1 start.sh 一键启动脚本](#81-startsh-一键启动脚本)
   - [8.2 开发环境](#82-开发环境)
   - [8.3 Docker 部署](#83-docker-部署)
   - [8.4 配置文件](#84-配置文件)
   - [8.5 数据库初始化](#85-数据库初始化)
9. [附录](#附录)
   - [A. 关键设计模式](#a-关键设计模式)
   - [B. 消息类型定义](#b-消息类型定义)
   - [C. 设备状态](#c-设备状态)
   - [D. STT 服务集成](#d-stt-服务集成)

---

## 1. 项目概述

### 1.1 项目简介

Xiaozhi ESP32 Server Java 是基于 [Xiaozhi ESP32](https://github.com/78/xiaozhi-esp32) 项目开发的 **Java 企业级服务端**，采用多模块 + 双进程架构设计，为 ESP32 智能硬件提供完整的后端支撑和可视化管理平台。

### 1.2 核心功能

- **多 AI 平台集成** — OpenAI / 智谱 / 讯飞 / Ollama / Dify / Coze，MCP 工具协议扩展
- **语音全链路** — 本地 & 云端 STT/TTS，音色克隆，实时打断，双向流式交互
- **WebSocket + MQTT** — 实时双向通信，服务端主动唤醒，OTA 远程升级
- **IoT 智能家居** — 语音指令控制设备，多设备协同，Function Call 智能决策
- **RAG 知识库** — 文档上传，智能检索增强生成，长期记忆管理
- **全链路监控** — Token / 时延 / 设备活跃度等多维度数据可视化

### 1.3 技术栈

| 类别 | 技术选型 |
|------|----------|
| **后端** | Spring Boot 3.5.8、Spring MVC、MyBatis-Plus、Flyway、WebSocket |
| **前端** | Vue.js 3、Ant Design、响应式布局 |
| **数据层** | MySQL 8.0、Redis 7 |
| **语音识别** | Vosk、FunASR、阿里云、腾讯云、讯飞 |
| **语音合成** | sherpa-onnx（本地）、火山引擎、阿里云、Edge TTS |
| **大语言模型** | OpenAI、智谱 AI、讯飞星火、Ollama、Dify、Coze |
| **扩展能力** | MCP 工具协议、Function Call、RAG 知识库 |

---

## 2. 项目架构

### 2.1 双进程架构

项目采用**双进程架构**，两个独立进程共享 MySQL 和 Redis，可分别部署与扩容：

```
┌─────────────────────────────────────────────────────────────┐
│                      Xiaozhi ESP32 Server                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │  xiaozhi-server  │         │ xiaozhi-dialogue │         │
│  │      :8091        │         │      :8092       │         │
│  │   管理后台        │   共享   │   对话服务        │         │
│  │   REST API       │   MySQL │   WebSocket      │         │
│  │   用户管理        │   Redis │   实时音频流      │         │
│  │   设备管理        │         │   AI 对话管道     │         │
│  │   OTA 升级       │         │   MCP 工具调用    │         │
│  └──────────────────┘         └──────────────────┘         │
│           │                           │                     │
│           └───────────┬───────────────┘                     │
│                       ▼                                     │
│              ┌────────────────┐                            │
│              │     MySQL       │                            │
│              │      Redis      │                            │
│              └────────────────┘                            │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 模块依赖层次

```
xiaozhi-server (启动器 + Controller)
    ├── xiaozhi-service (业务逻辑层)
    ├── xiaozhi-ai (AI 能力：TTS/STT/LLM)
    └── xiaozhi-dialogue (对话层：通信 + 音频处理)

xiaozhi-dialogue (独立进程)
    └── xiaozhi-ai
        └── xiaozhi-common
            └── xiaozhi-service

xiaozhi-ai (AI 核心)
    ├── xiaozhi-common
    └── xiaozhi-service
```

---

## 3. 模块结构

### 3.1 模块概览

| 模块 | 描述 | 端口 |
|------|------|------|
| `xiaozhi-common` | 公共模块：工具类、事件、模型定义 | - |
| `xiaozhi-service` | 业务逻辑层：用户、设备、角色、权限等 | - |
| `xiaozhi-ai` | AI 核心：TTS、STT、LLM、Memory、MCP | - |
| `xiaozhi-dialogue` | 对话层：通信、音频处理、会话管理 | 8092 |
| `xiaozhi-server` | 启动器 + Controller | 8091 |
| `web` | 前端管理平台 | 8084 |

### 3.2 模块详细结构

#### 3.2.1 xiaozhi-common

公共基础模块，提供通用组件和定义。

```
xiaozhi-common/
├── annotation/           # 注解：@AuditLog, @CheckOwner, @CheckOwners
├── config/               # 配置：RedisCacheConfig, RuntimePathConfig
├── domain/               # 领域事件：AbstractDomainEvent, DomainEvent
├── exception/             # 异常：UnauthorizedException, ResourceNotFoundException 等
├── model/                # 数据模型
│   ├── bo/              # 业务对象：UserBO, DeviceBO, RoleBO, MessageBO 等
│   ├── dataobject/      # 数据对象：BaseDO
│   ├── req/             # 请求对象：分页、创建、更新请求
│   └── resp/            # 响应对象：统一响应结构
├── communication/         # 通信相关
│   ├── registry/        # 服务注册：DialogueServerRegistry
│   └── common/         # Redis 广播等
├── enums/               # 枚举：DeviceState, ListenMode, ListenState
├── event/               # Spring 事件：设备/会话/对话相关事件
└── utils/              # 工具类：音频、加密、JSON 等
```

#### 3.2.2 xiaozhi-service

业务逻辑层，包含用户、设备、角色、权限等管理功能。

```
xiaozhi-service/
├── agent/               # AI Agent 管理
├── authrole/            # 角色权限关联管理
├── common/config/       # MyBatisPlusConfig
├── communication/       # 对话服务注册
├── config/             # 配置管理（LLM、TTS、STT等）
├── device/              # 设备管理
├── mcptoolexclude/      # MCP 工具排除管理
├── message/             # 消息管理
├── operationlog/        # 操作日志
├── permission/         # 权限管理
├── role/                # 角色管理
├── security/            # 安全认证
├── service/config/      # 事务配置
├── storage/             # 文件存储（本地、阿里OSS、腾讯COS）
├── summary/             # 对话摘要管理
├── task/                # 定时任务
├── template/            # 提示词模板管理
├── token/               # Token 管理
├── user/                # 用户管理
└── userauth/            # 用户认证
```

#### 3.2.3 xiaozhi-ai

AI 核心模块，提供语音识别(STT)、语音合成(TTS)、大语言模型(LLM)能力。

```
xiaozhi-ai/
├── llm/                  # LLM 相关
│   ├── factory/         # ChatModel 工厂（策略+工厂模式）
│   ├── memory/          # 对话记忆管理
│   ├── providers/       # LLM 提供商实现（Coze, Dify, 星火等）
│   ├── service/         # 意图检测、视觉服务
│   └── tool/            # 工具调用相关
├── mcp/                  # MCP 协议支持
│   ├── config/          # MCP 客户端配置
│   ├── registrar/       # 工具注册
│   └── server/          # MCP 工具查询服务
├── stt/                  # 语音识别 (Speech-To-Text)
│   └── providers/       # STT 提供商（Vosk, FunASR, 阿里, 腾讯, 讯飞等）
├── tool/                 # 工具调用管理
│   ├── handler/         # 工具日志处理
│   ├── observation/     # 工具调用观察
│   └── session/        # 工具会话管理
├── tts/                  # 语音合成 (Text-To-Speech)
│   └── providers/       # TTS 提供商（Edge, 阿里, 腾讯, 火山, MiniMax等）
└── utils/               # HTTP 工具类
```

#### 3.2.4 xiaozhi-dialogue

对话服务模块，处理 WebSocket 实时通信和音频处理管道。

```
xiaozhi-dialogue/
├── adapter/              # 工具适配器
├── audio/               # 音频处理
│   ├── vad/            # 语音活动检测（SileroVadModel）
│   ├── AecService.java # 回声消除服务
│   └── VadService.java # VAD 服务
├── communication/       # 通信层
│   ├── common/         # SessionManager, DeviceRegistry, MessageHandler 等
│   ├── controller/     # HTTP 控制器（VLChat）
│   ├── domain/        # 消息定义（Hello, Listen, Abort, Goodbye 等）
│   ├── message/       # MessageSender
│   └── server/        # WebSocket 配置和处理器
├── llm/                 # LLM 交互
│   ├── factory/       # PersonaFactory
│   ├── handler/       # 对话监听器
│   └── tool/         # 函数调用（播放音乐、绘本、控制IoT等）
├── playback/            # 音频播放
│   ├── Player.java    # 播放器抽象
│   ├── Synthesizer.java # 语音合成器
│   ├── OpusRecorder.java # Opus 录制
│   └── ScheduledPlayer.java # 调度播放器实现
└── runtime/             # 对话运行时
    ├── Persona.java    # 角色实体（核心聚合根）
    ├── DialogueTurn.java # 对话轮次
    └── DialogueContext.java # 对话上下文
```

#### 3.2.5 xiaozhi-server

启动器模块，聚合所有模块，提供 REST API。

```
xiaozhi-server/
├── agent/               # Agent 控制器
├── authrole/            # 角色权限控制器
├── config/              # 配置控制器
├── device/              # 设备控制器
├── file/                # 文件上传控制器
├── mcpserver/           # MCP 工具控制器
├── memory/              # 记忆管理控制器
├── message/             # 消息控制器
├── music/               # 音乐控制器
├── role/                # 角色控制器
├── server/              # 服务器配置
│   ├── config/         # SaToken, Swagger, WebMvc, RateLimit 等
│   ├── exception/      # 全局异常处理
│   └── web/           # Web 拦截器、过滤器
├── template/            # 模板控制器
├── user/                # 用户控制器
└── utils/               # 工具类（短信、邮件、验证码）
```

---

## 4. 核心模块详解

### 4.1 对话管道架构

对话处理的核心流程如下：

```
ESP32 设备 ←→ WebSocket ←→ WebSocketHandler
                              │
                              ▼
                        SessionManager
                              │
                              ▼
                        MessageHandler
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
   DialogueService       VadService          AecService
   (对话逻辑)           (语音活动检测)      (回声消除)
        │                     │
        └─────────┬───────────┘
                  ▼
            Persona (角色)
              │
     ┌────────┼────────┐
     ▼        ▼        ▼
   STT     ChatModel   TTS
  (识别)    (LLM)     (合成)
     │        │        │
     └────────┼────────┘
              ▼
          Player (播放)
              │
              ▼
         发送 Opus 音频到设备
```

### 4.2 Persona 核心类

**Persona** 是对话的核心聚合根，管理整个对话生命周期：

```java
// 位置：xiaozhi-dialogue/src/main/java/com/xiaozhi/dialogue/runtime/Persona.java

public class Persona {
    // 对话记忆
    private Conversation conversation;
    
    // STT 服务
    private SttService sttService;
    
    // LLM 模型
    private ChatModel chatModel;
    
    // 语音合成器
    private Synthesizer synthesizer;
    
    // 播放器
    private Player player;
    
    // 工具回调列表
    private List<ToolCallback> toolCallbacks;
    
    // 对话监听器
    private PersonaListener listener;
    
    // 主要对话方法
    public void chat(UserMessage userMessage, boolean useFunctionCall);
    
    // 发送告别语
    public void sendGoodbyeMessage();
    
    // 检查是否活跃
    public boolean isActive();
}
```

### 4.3 SessionManager 会话管理

**SessionManager** 负责管理所有 WebSocket 会话：

```java
// 位置：xiaozhi-dialogue/src/main/java/com/xiaozhi/communication/common/SessionManager.java

public class SessionManager {
    // 会话注册表
    ConcurrentHashMap<String, ChatSession> sessions;
    
    // 设备ID → SessionId 反向索引
    ConcurrentHashMap<String, String> deviceIdToSessionId;
    
    // 注册会话
    public void registerSession(String sessionId, ChatSession chatSession);
    
    // 获取会话
    public ChatSession getSession(String sessionId);
    
    // 根据设备ID获取会话
    public ChatSession getSessionByDeviceId(String deviceId);
    
    // 注册设备
    public void registerDevice(String sessionId, DeviceBO device);
    
    // 关闭会话
    public void closeSession(String sessionId);
}
```

### 4.4 ChatModelFactory 工厂类

**ChatModelFactory** 使用工厂+策略模式创建 LLM 实例：

```java
// 位置：xiaozhi-ai/src/main/java/com/xiaozhi/ai/llm/factory/ChatModelFactory.java

public class ChatModelFactory {
    // 所有提供商 Map<providerName, ChatModelProvider>
    private Map<String, ChatModelProvider> providers;
    
    // 获取 ChatModel
    public ChatModel getChatModel(RoleBO role);
    
    // 获取视觉模型
    public ChatModel getVisionModel();
    
    // 获取意图识别模型
    public ChatModel getIntentModel();
}
```

支持的 LLM 提供商：
- OpenAI
- 智谱 AI (ZhiPu)
- 讯飞星火 (XingHuo)
- 星辰 (XingChen)
- Ollama
- Dify
- Coze

### 4.5 TTS/STS 服务工厂

**TtsServiceFactory** 和 **SttServiceFactory** 采用类似工厂模式：

```java
// TTS 提供商
- EdgeTtsService (微软 Edge TTS)
- AliyunTtsService / AliyunNlsTtsService
- TencentTtsService
- VolcengineTtsService
- MiniMaxTtsService
- SherpaOnnxTtsService (本地)
- XfyunTtsService

// STT 提供商
- VoskSttService (本地)
- FunASRSttService
- AliyunSttService / AliyunNlsSttService
- TencentSttService
- VolcengineSttService
- XfyunSttService
```

---

## 5. 依赖关系

### 5.1 Maven 模块依赖

```
xiaozhi-parent (pom.xml)
├── xiaozhi-common        # 无外部模块依赖
├── xiaozhi-service      # → xiaozhi-common
├── xiaozhi-ai           # → xiaozhi-common, xiaozhi-service
├── xiaozhi-dialogue     # → xiaozhi-ai
└── xiaozhi-server       # → xiaozhi-service, xiaozhi-ai, xiaozhi-dialogue
```

### 5.2 核心依赖版本

| 依赖 | 版本 |
|------|------|
| Spring Boot | 3.5.8 |
| Java | 21 |
| MyBatis-Plus | 3.5.16 |
| Sa-Token | 1.39.0 |
| Spring AI | 1.1.4 |
| Redisson | 3.25.0 |
| Netty | 4.1.110.Final |
| sherpa-onnx | 1.12.21 |

### 5.3 对话服务关键依赖

```xml
<!-- WebSocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Netty (WebSocket 服务器) -->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
</dependency>

<!-- MCP Client -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-client</artifactId>
</dependency>

<!-- WebRTC -->
<dependency>
    <groupId>dev.onvoid.webrtc</groupId>
    <artifactId>webrtc-java</artifactId>
</dependency>

<!-- ONNX Runtime (本地推理) -->
<dependency>
    <groupId>com.microsoft.onnxruntime</groupId>
    <artifactId>onnxruntime</artifactId>
</dependency>
```

---

## 6. 关键类与函数

### 6.1 启动类

| 类 | 路径 | 描述 |
|----|------|------|
| `XiaozhiApplication` | xiaozhi-server | 管理后台启动入口 (端口 8091) |
| `DialogueApplication` | xiaozhi-dialogue | 对话服务启动入口 (端口 8092) |

### 6.2 WebSocket 处理

| 类 | 路径 | 描述 |
|----|------|------|
| `WebSocketHandler` | communication/server/websocket | 处理 WebSocket 连接/消息/关闭 |
| `WebSocketConfig` | communication/server/websocket | WebSocket 配置 |
| `MessageSender` | communication/message | 发送消息到客户端 |

### 6.3 会话管理

| 类 | 路径 | 描述 |
|----|------|------|
| `SessionManager` | communication/common | 会话注册表，管理所有会话状态 |
| `ChatSession` | communication/common | 会话实体，包含设备、播放器等 |
| `DeviceRegistry` | communication/common | 设备注册表，维护设备在线状态 |
| `InactiveSessionChecker` | communication/common | 检测不活跃会话并清理 |

### 6.4 对话核心

| 类 | 路径 | 描述 |
|----|------|------|
| `Persona` | dialogue/runtime | 角色实体，核心聚合根 |
| `PersonaFactory` | dialogue/llm/factory | 构建 Persona 实例 |
| `DialogueService` | dialogue | 对话处理服务 |
| `DialogueListener` | dialogue/llm/handler | 对话事件监听，持久化消息 |

### 6.5 音频处理

| 类 | 路径 | 描述 |
|----|------|------|
| `VadService` | dialogue/audio | 语音活动检测 |
| `AecService` | dialogue/audio | 回声消除服务 |
| `Player` | dialogue/playback | 播放器抽象 |
| `ScheduledPlayer` | dialogue/playback | 调度播放器实现 |
| `Synthesizer` | dialogue/playback | 语音合成器 |
| `OpusRecorder` | dialogue/playback | Opus 音频录制 |

### 6.6 AI 能力

| 类 | 路径 | 描述 |
|----|------|------|
| `ChatModelFactory` | ai/llm/factory | LLM 工厂类 |
| `TtsServiceFactory` | ai/tts | TTS 服务工厂 |
| `SttServiceFactory` | ai/stt | STT 服务工厂 |
| `Conversation` | ai/llm/memory | 对话记忆 |
| `XiaoZhiToolCallingManager` | ai/tool | 工具调用管理器 |

### 6.7 业务服务

| 类 | 路径 | 描述 |
|----|------|------|
| `DeviceService` | service/device | 设备管理服务 |
| `RoleService` | service/role | 角色管理服务 |
| `MessageService` | service/message | 消息管理服务 |
| `ConfigService` | service/config | 配置管理服务 |
| `UserService` | service/user | 用户管理服务 |

---

## 7. 配置说明

### 7.1 数据库配置 (MySQL)

```yaml
# 默认配置
jdbc:
  url: jdbc:mysql://localhost:3306/xiaozhi?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
  username: xiaozhi
  password: 123456
```

### 7.2 Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 7.3 应用配置

```yaml
# xiaozhi-server (管理后台)
server:
  port: 8091

# xiaozhi-dialogue (对话服务)
dialogue:
  server:
    port: 8092
```

### 7.4 STT/TTS 配置

在 `sys_config` 表中配置各类 AI 服务：

| configType | 配置类型 |
|------------|----------|
| `llm` | 大语言模型配置 |
| `tts` | 语音合成配置 |
| `stt` | 语音识别配置 |
| `vision` | 视觉模型配置 |

### 7.5 Docker 环境变量

```bash
# Docker 构建代理
BUILD_PROXY=http://host.docker.internal:10808

# 宿主机 IP
HOST_IP=192.168.1.100

# VOSK 模型大小
VOSK_MODEL_SIZE=small

# TTS 模型
TTS_MODEL=vits-melo-tts-zh_en
```

---

## 8. 运行方式

### 8.1 start.sh 一键启动脚本

项目根目录下的 [`start.sh`](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/start.sh) 是统一的服务管理脚本，封装了端口清理、DB 启动、模型下载、Maven 编译、后端启停、前端启动等全流程，底层使用 `set -euo pipefail`，任意一步失败会立即退出。

#### 8.1.1 完整命令速查表

| 命令 | 等价操作 | 说明 |
|------|---------|------|
| `./start.sh` | `./start.sh start` | **一键启动后端**：清理日志 → 清理端口 → 启动 DB → 下载模型 → 编译 → 启动 server+dialogue |
| `./start.sh start` | 同上 | 一键启动后端（默认命令） |
| `./start.sh all` | — | **启动全部服务**：DB + 后端 + 前端（Vue dev server） |
| `./start.sh stop` | — | 停止后端两个进程（xiaozhi-server、xiaozhi-dialogue） |
| `./start.sh restart` | — | 停止后重新启动后端（**不重启前端**） |
| `./start.sh status` | — | 显示数据库、后端、前端运行状态及 PID |
| `./start.sh logs` | `./start.sh logs all` | 查看后端 + 前端日志（每个 20 行） |
| `./start.sh logs server` | — | 实时跟踪 server 日志（`tail -f`） |
| `./start.sh logs dialogue` | — | 实时跟踪 dialogue 日志 |
| `./start.sh logs web` | — | 实时跟踪前端 dev server 日志 |
| `./start.sh db-only` | — | 仅启动 MySQL + Redis 容器，不启动后端 |
| `./start.sh db-down` | — | 停止并销毁 MySQL + Redis 容器 |
| `./start.sh web` | — | 仅启动前端管理平台（端口 8084） |
| `./start.sh clean` | — | 清理 `run/*.pid` 和 Maven 编译产物 |
| `./start.sh --help` | `./start.sh -h` / `./start.sh help` | 打印完整用法 |

#### 8.1.2 环境变量

| 变量 | 默认值 | 作用 |
|------|--------|------|
| `SKIP_DOWNLOAD=1` | 不跳过 | 跳过原生库与基础模型的下载 |
| `SKIP_BUILD=1` | 不跳过 | 跳过 Maven 编译 |
| `JAVA_HOME` | `/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home` | Java 安装目录 |
| `DB_MODE=docker` | `docker` | DB 模式：`docker`（默认，启动容器） \| `local`（使用本机 MySQL+Redis） |

#### 8.1.3 服务端口与进程名

| 服务 | 端口 | 模块 | 进程名（PID 文件） | 日志文件 |
|------|------|------|------------------|---------|
| MySQL | 3306 | docker | `xiaozhi-mysql` | Docker 管理 |
| Redis | 6379 | docker | `xiaozhi-redis` | Docker 管理 |
| xiaozhi-server（管理后台 REST API） | 8091 | `xiaozhi-server` | `run/xiaozhi-server.pid` | `logs/xiaozhi-server.log` |
| xiaozhi-dialogue（WebSocket 对话） | 8092 | `xiaozhi-dialogue` | `run/xiaozhi-dialogue.pid` | `logs/xiaozhi-dialogue.log` |
| xiaozhi-web（Vue 管理平台） | 8084 | `web/` | `run/xiaozhi-web.pid` | `logs/xiaozhi-web.log` |

#### 8.1.4 典型使用流程

```bash
# 1. 首次启动（下载模型 + 编译 + 启动后端）
cd xiaozhi-esp32-server-java
./start.sh

# 2. 验证运行状态
./start.sh status

# 3. 查看实时日志（新开终端）
./start.sh logs server
./start.sh logs dialogue

# 4. 启动管理后台前端
./start.sh web
# 浏览器访问 http://localhost:8084
# 默认账号 admin / 123456

# 5. 仅启动 DB（开发调试场景，先 DB 再后端）
./start.sh db-only
SKIP_BUILD=1 ./start.sh start

# 6. 重启后端（代码更新后）
./start.sh restart

# 7. 销毁 DB 容器（清空数据）
./start.sh db-down

# 8. 一键停止
./start.sh stop
```

#### 8.1.5 前置依赖检查

`start.sh` 在启动前会自动检查（任意缺失会立即报错并退出）：

| 工具 | 检查位置 | 最低版本 |
|------|---------|---------|
| Java | `$JAVA_HOME/bin/java` | **Java 21+** |
| Maven | `mvn` | 任意版本 |
| Node.js | `node` | **v20+**（仅启动 web 时） |
| Docker | `docker` + `docker info` | 运行中的 Docker Desktop（DB 模式） |
| `lsof` | — | 端口占用检查（macOS 自带） |

#### 8.1.6 关键内部函数

| 函数 | 行号范围 | 职责 |
|------|---------|------|
| `cleanup_port` | 73-104 | 释放指定端口占用进程（`lsof` → `kill` → `kill -9`） |
| `cleanup_all_ports` | 106-111 | 批量清理 8091/8092/8084 应用端口 |
| `check_java / check_maven / check_node / check_docker` | 119-148 | 前置依赖检查 |
| `wait_port` | 159-171 | TCP 端口就绪探测（带超时，默认 90s） |
| `start_service` | 297-324 | 启动单个 Java 模块（写 PID、输出日志） |
| `start_backend` | 326-332 | 编译 + 启动 server + dialogue + 端口探测 |
| `stop_service / stop_backend` | 334-362 | 通过 PID 文件优雅停止（kill → 等 15s → kill -9） |
| `db_up / db_down / db_status` | 192-252 | MySQL + Redis Docker Compose 生命周期管理 |
| `download_models` | 258-274 | 调用 `scripts/download_base.sh` 下载原生库 |
| `build_backend` | 280-291 | `mvn clean install -DskipTests -q` |

#### 8.1.7 常见问题排查

| 现象 | 原因 | 排查方法 |
|------|------|---------|
| `port 8091 已被占用` | 之前启动的进程未正常停止 | `./start.sh stop` 后再启动；或 `lsof -nP -iTCP:8091` 查看占用进程 |
| `需要 Java 21+` | JDK 版本过低 | `brew install openjdk@25` 或设置 `JAVA_HOME` |
| `Docker daemon 未运行` | Docker Desktop 未启动 | 启动 Docker Desktop 后重试 |
| `MySQL 健康检查未在 60s 内完成` | 镜像拉取慢或机器性能差 | 重试，或 `docker logs xiaozhi-mysql` 查看详情 |
| `mvn` 卡住 | 网络问题导致依赖下载慢 | 配置 Maven 镜像（`~/.m2/settings.xml`） |
| 日志乱码 | 终端编码非 UTF-8 | `export LANG=en_US.UTF-8` |

### 8.2 开发环境

#### 8.2.1 准备依赖

```bash
# 安装 MySQL 8.0+ 和 Redis 7+
# 创建数据库和用户
mysql -u root -p < db/init.sql

# 安装 Java 21
# 安装 Maven 3.8+
```

#### 8.2.2 编译项目

```bash
cd xiaozhi-esp32-server-java
mvn clean install -DskipTests
```

#### 8.2.3 启动服务

```bash
# 启动管理后台 (8091)
cd xiaozhi-server
mvn spring-boot:run

# 启动对话服务 (8092) - 新开终端
cd xiaozhi-dialogue
mvn spring-boot:run
```

#### 8.2.4 前端开发

```bash
cd web
npm install
npm run dev
# 访问 http://localhost:8084
```

### 8.3 Docker 部署

```bash
# 一键启动所有服务
docker-compose up -d

# 或使用便捷脚本
./start.sh
```

服务地址：
- 前端管理平台: http://localhost:8084
- 管理后台 API: http://localhost:8091
- 对话服务: ws://localhost:8092
- Swagger文档: http://localhost:8091/doc.html

### 8.4 配置文件

| 环境 | 配置文件 |
|------|----------|
| 开发 | `web/.env.development` |
| 生产 | `web/.env.production` |
| 示例 | `web/.env.local.example` |

### 8.5 数据库初始化

Flyway 会自动执行 `db/` 目录下的迁移脚本：

```
db/
└── init.sql  # 自动建表和初始数据
```

---

## 附录

### A. 关键设计模式

1. **工厂模式** - `ChatModelFactory`, `TtsServiceFactory`, `SttServiceFactory`
2. **策略模式** - 各 AI 服务提供商实现统一接口
3. **观察者模式** - Spring Event 机制处理各类事件
4. **聚合根模式** - `Persona` 作为对话核心聚合根
5. **会话模式** - `SessionManager` 管理 WebSocket 会话

### B. 消息类型定义

| 消息类型 | 描述 |
|----------|------|
| `HelloMessage` | 连接建立时的握手消息 |
| `ListenMessage` | 设备监听状态/文本/唤醒词 |
| `AbortMessage` | 中止对话消息 |
| `GoodbyeMessage` | 告别消息 |
| `IotMessage` | IoT 设备状态/描述更新 |
| `DeviceMcpMessage` | MCP 协议消息 |
| `AudioParams` | 音频参数配置 |

### C. 设备状态

| 状态 | 描述 |
|------|------|
| `IDLE` | 空闲 |
| `LISTENING` | 聆听中 |
| `THINKING` | AI 处理中 |
| `SPEAKING` | 说话中 |
| `ONLINE` | 在线 |
| `OFFLINE` | 离线 |

### D. STT 服务集成

#### D.1 STT 提供商概览

项目通过 `SttServiceFactory` 统一管理多种 STT 服务商：

| Provider | 部署方式 | 适用场景 | 配置字段 |
|----------|----------|----------|----------|
| `vosk` | 本地 JNI | 默认，离线/低算力 | 模型目录 |
| `funasr` | Docker / WebSocket | 生产环境，中文高精度 | apiUrl |
| `aliyun` | 云端 API | 大规模，低延迟 | appId, apiKey |
| `aliyun-nls` | 云端 API | 阿里云生态 | ak, sk, appKey |
| `tencent` | 云端 API | 腾讯云生态 | appId, apiKey, secretKey |
| `xfyun` | 云端 API | 讯飞生态 | appId, apiKey, apiSecret |
| `volcengine` | 云端 API | 字节跳动生态 | appId, token |

#### D.2 FunASR 部署（推荐方案）

**完整部署文档**：[FUNASR_DEPLOYMENT.md](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/docs/FUNASR_DEPLOYMENT.md)

**快速部署**：

```bash
# 1. 一键启动 FunASR
./bin/funasr.sh start

# 2. 验证服务
./bin/funasr.sh status

# 3. 集成到 xiaozhi
#    - Web 后台 → STT 配置 → 新建 → provider=funasr, apiUrl=ws://localhost:10096
#    - 角色配置 → 关联 STT 服务
```

**架构图**：

```
┌──────────────────────────────────────────────────────┐
│  Mac / Linux 部署环境                                │
│  ┌─────────────────┐     ┌──────────────────────┐   │
│  │ xiaozhi-        │     │  Docker Container    │   │
│  │ server/dialogue │     │  ┌────────────────┐  │   │
│  │ Java Process    │     │  │   FunASR       │  │   │
│  │  :8091 / :8092  │─────┼─▶│  WebSocket     │  │   │
│  │                 │ WS  │  │   :10095       │  │   │
│  └─────────────────┘     │  └────────────────┘  │   │
│         │                 │  Paraformer-Large    │   │
│         ▼                 │  + 2pass 模式        │   │
│   ┌──────────┐            └──────────────────────┘   │
│   │  MySQL   │                                       │
│   │  Redis   │                                       │
│   └──────────┘                                       │
└──────────────────────────────────────────────────────┘
```

**关键优势**：
- 🔒 **数据隐私**：音频不出本机
- 💰 **零边际成本**：无云端调用费
- 🎯 **高精度**：Paraformer-Large + 2pass 模式
- 🔌 **零代码修改**：项目已实现 FunASR 客户端

#### D.3 其他 STT 方案对比

| 维度 | Vosk (默认) | FunASR | 云端 ASR |
|------|-------------|--------|----------|
| 中文精度 | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 资源占用 | 200MB | 1.5GB | 0 |
| 延迟 | 低 | 中 | 受网络影响 |
| 离线 | ✅ | ✅ | ❌ |
| 成本 | 免费 | 免费 | 按量 |
| 推荐场景 | 开发/低算力 | 生产/隐私 | 大规模 |

#### D.4 STT 切换流程

1. **数据库配置**：在 `sys_config` 表插入 STT 服务
2. **角色绑定**：在 `sys_role` 表设置 `sttId` 字段
3. **Web 后台**：管理界面配置并启用
4. **重启生效**：对话服务重启后加载新 STT

---

*文档版本: 5.1.0*
*最后更新: 2026-06-24*
