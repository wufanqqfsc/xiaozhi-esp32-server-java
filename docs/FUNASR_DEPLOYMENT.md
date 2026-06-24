# FunASR 实时语音听写服务 Mac 本地部署方案

> **适用项目**: xiaozhi-esp32-server-java
> **目标设备**: MacBook Air M1 / 8GB RAM / macOS 15.6.1 / arm64
> **文档类型**: 技术部署方案（仅文档，未真实部署）
> **文档版本**: v1.0
> **更新日期**: 2026-06-23

---

## 目录

1. [调研结论与方案总览](#1-调研结论与方案总览)
2. [模型选型分析](#2-模型选型分析)
3. [本机环境评估](#3-本机环境评估)
4. [部署架构设计](#4-部署架构设计)
5. [Docker 部署方案](#5-docker-部署方案)
6. [与现有项目集成方案](#6-与现有项目集成方案)
7. [性能调优与资源配置](#7-性能调优与资源配置)
8. [安全与网络配置](#8-安全与网络配置)
9. [运行验证与测试方案](#9-运行验证与测试方案)
10. [运维与故障排查](#10-运维与故障排查)
11. [方案对比与选型建议](#11-方案对比与选型建议)
12. [附录](#12-附录)

---

## 1. 调研结论与方案总览

### 1.1 FunASR 模型能力概览

FunASR 是阿里达摩院开源的语音识别工具包，提供**多模型协同**的实时听写能力。根据官方模型选择指南，核心能力如下：

| 模型 | 主要能力 | 适用场景 |
|------|----------|----------|
| **SenseVoiceSmall** | 多语种 ASR、情感标签、音频事件标签 | Demo、私有 API、Agent 语音输入 |
| **Paraformer-Large** | 中文生产级 ASR，组合 VAD 和标点 | 中文生产流量 |
| **paraformer-en** | 轻量英文 | OpenAI 风格客户端英文路径 |
| **Fun-ASR-Nano** | LLM-based，31 语种 | 实验性/低延迟场景 |

### 1.2 三种服务模式

| 模式 | 描述 | 延迟 | 精度 |
|------|------|------|------|
| `online` | 纯流式实时识别 | 最低 | 中等 |
| `offline` | 非实时一句话识别 | 高 | 最高 |
| `2pass` | 流式 + 句尾离线修正 | 较低 | 最高（推荐） |

### 1.3 推荐方案

针对 xiaozhi-esp32-server-java 项目和 MacBook Air M1 设备，本文档推荐：

> **方案 A（推荐）**: Docker 容器化部署 Paraformer-Large + 2pass 模式，通过 WebSocket 协议供 Java 服务调用。
>
> **方案 B（备选）**: SenseVoiceSmall 单模型部署，资源占用更低，CPU 推理更优。

### 1.4 现有项目集成状态

项目已实现 `FunASRSttService`，位于：

```
xiaozhi-ai/src/main/java/com/xiaozhi/ai/stt/providers/FunASRSttService.java
```

**关键特性**：
- 通过 `ConfigBO.apiUrl` 配置 WebSocket 服务地址
- 工厂 `SttServiceFactory` 已注册 `funasr` provider
- 协议：`mode=2pass`，格式 `pcm`，`chunk_size=[5,10,5]`
- 通讯协议：WebSocket（Java-WebSocket 客户端）

**结论**：Java 端**无需修改代码**，仅需在数据库 `sys_config` 表中配置 provider=`funasr`、apiUrl=`ws://localhost:10096` 即可使用。

---

## 2. 模型选型分析

### 2.1 决策表

| 需求 | 优先选择 | 原因 |
|------|----------|------|
| 快速多语种私有转写 | SenseVoiceSmall | 兼顾 ASR、情感标签、CPU 可用性 |
| **中文生产 ASR（推荐）** | **Paraformer-Large** | **成熟中文 ASR，可组合 VAD 和标点** |
| 实时字幕/客服流式 | 2pass 模式 | 流式 + 句尾高精度修正 |
| OpenAI 兼容 API | paraformer-en | 适合轻量英文验证 |

### 2.2 设备对话场景分析

xiaozhi-esp32-server-java 主要使用场景为 **ESP32 智能硬件的实时语音对话**：

| 特征 | 项目要求 | FunASR 对应能力 |
|------|----------|-----------------|
| 实时性 | 流式识别，需要快速首字 | Paraformer-Large 流式 + 2pass |
| 准确性 | 中文为主，需标点 | Paraformer-Large + PUNC |
| 噪声 | 设备远场，存在噪声 | FSMN-VAD 端点检测 + 噪声鲁棒 |
| 断句 | 需要自然语言断句 | VAD 自动断句 |
| 语种 | 主要中文，可选英文 | Paraformer 多语种支持 |

**结论**：选择 **Paraformer-Large + 2pass 模式** 最佳。

### 2.3 模型清单

2pass 模式需要以下模型协同工作：

| 模型名称 | 用途 | 大小（参考） |
|----------|------|--------------|
| `speech_fsmn_vad_zh-cn-16k-common-onnx` | 语音活动检测 | ~10MB |
| `speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx` | 流式 ASR | ~200MB |
| `speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx` | 离线 ASR（含 VAD/PUNC） | ~1GB |
| `punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx` | 标点预测 | ~100MB |
| `speech_ngram_lm_zh-cn-ai-wesp-fst` | N-gram 语言模型（可选） | ~200MB |
| `fst_itn_zh` | 文本规范化 ITN（可选） | ~10MB |

---

## 3. 本机环境评估

### 3.1 MacBook Air M1 配置

| 项目 | 规格 | 影响 |
|------|------|------|
| 芯片 | Apple M1 (arm64) | ✅ Docker 已支持 arm64（`funasr-runtime-sdk-online-cpu-0.1.9+`） |
| 内存 | 8GB | ⚠️ 资源紧张，需限制模型和并发 |
| 核心 | 8 核（4P + 4E） | ✅ 足够单路流式推理 |
| 存储 | 256GB+ | ✅ 模型总计 ~1.5GB |
| 系统 | macOS 15.6.1 | ✅ 兼容 Docker Desktop |

### 3.2 推荐资源配置

| 参数 | 配置 1（保守） | 配置 2（推荐） |
|------|----------------|----------------|
| 服务模式 | online | **2pass** |
| 核心 ASR 模型 | SenseVoiceSmall | **Paraformer-Large** |
| 内存限制 | 2GB | **4GB** |
| CPU 限制 | 2 核 | **4 核** |
| 并发数 | 1 路 | **2 路** |
| PUNC 模型 | ✅ | ✅ |
| N-gram LM | ❌ | ❌（内存紧张） |
| ITN | ❌ | ❌ |

**注意**：8GB 物理内存中，macOS 本身占用约 3-4GB，剩余约 4-5GB 可供 Docker 使用。

---

## 4. 部署架构设计

### 4.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                       MacBook Air M1                            │
│                                                                 │
│  ┌─────────────────────────────────────────────────────┐       │
│  │  xiaozhi-esp32-server-java (现有服务)               │       │
│  │  ┌──────────────────┐  ┌──────────────────┐         │       │
│  │  │ xiaozhi-server   │  │ xiaozhi-dialogue │         │       │
│  │  │ :8091            │  │ :8092            │         │       │
│  │  └──────────────────┘  └─────────┬────────┘         │       │
│  └──────────────────────────────────│──────────────────┘       │
│                                     │ WebSocket                │
│                                     │ (apiUrl)                  │
│                                     ▼                           │
│  ┌──────────────────────────────────────────────────────┐      │
│  │  Docker 容器: funasr-runtime                         │      │
│  │  ┌──────────────────────────────────────────────┐    │      │
│  │  │  funasr-wss-server-2pass                     │    │      │
│  │  │  监听端口: 10095 (容器) → 10096 (宿主机)     │    │      │
│  │  │                                              │    │      │
│  │  │  ┌────────┐  ┌──────────┐  ┌──────────────┐  │    │      │
│  │  │  │  VAD   │  │ASR Online│  │ ASR Offline  │  │    │      │
│  │  │  │(FSMN)  │  │(Parafr)  │  │  (Paraformer)│  │    │      │
│  │  │  └────────┘  └──────────┘  └──────────────┘  │    │      │
│  │  │  ┌────────┐  ┌──────────┐                    │    │      │
│  │  │  │ PUNC   │  │   ITN    │                    │    │      │
│  │  │  └────────┘  └──────────┘                    │    │      │
│  │  └──────────────────────────────────────────────┘    │      │
│  │  资源限制: 4GB RAM, 4 CPU                            │      │
│  └──────────────────────────────────────────────────────┘      │
│           │                                                     │
│           │ WebSocket (ws://localhost:10096)                   │
│           ▼                                                     │
│  ┌──────────────────────────────────────────────────────┐      │
│  │  MySQL 8.0 + Redis 7  (现有服务)                    │      │
│  └──────────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 数据流

```
ESP32 设备
   │ (Opus 音频流)
   ▼
xiaozhi-dialogue :8092
   │ WebSocket
   ▼
WebSocketHandler → DialogueService
   │ 流式 PCM (16kHz, s16le, mono)
   ▼
FunASRSttService (Java)
   │ WebSocket (apiUrl)
   ▼
funasr-wss-server-2pass :10095
   │ 2pass 模式
   ├─ online 流式结果 ──→ 实时反馈
   └─ offline 句尾修正 ──→ 最终结果
   ▼
返回文本 → LLM (Persona) → TTS → ESP32
```

---

## 5. Docker 部署方案

### 5.1 前置条件

```bash
# 1. 安装 Docker Desktop for Mac (Apple Silicon)
# 下载地址: https://www.docker.com/products/docker-desktop/

# 2. 验证 Docker 版本（需 4.0+）
docker --version
docker compose version

# 3. 验证架构支持
docker info | grep Architecture
# 预期输出: Architecture: aarch64 (Apple Silicon)
```

### 5.2 镜像选择

FunASR 提供官方 Docker 镜像（arm64 需 ≥ 0.1.9）：

| 镜像版本 | 镜像 ID | 备注 |
|----------|---------|------|
| `funasr-runtime-sdk-online-cpu-0.1.13` | latest | ✅ 推荐 |
| `funasr-runtime-sdk-online-cpu-0.1.9` | `4a875e08c7a2` | 首个 arm64 版本 |

```bash
# 拉取镜像（推荐）
docker pull registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13
```

**镜像地址备选**：
- Docker Hub: `docker.io/alibaba-damo-academy/funasr-runtime-sdk-online-cpu-0.1.13`
- 阿里云: `registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13`

### 5.3 Docker Compose 部署（推荐）

在项目根目录创建 `docker-compose-funasr.yml`：

```yaml
version: '3.8'

services:
  funasr:
    image: registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13
    container_name: xiaozhi-funasr
    restart: unless-stopped
    # arm64 Mac 必须启用，x86_64 需设置 platform: linux/amd64
    platform: linux/arm64
    ports:
      - "10096:10095"   # WebSocket 端口（避免与其他服务冲突）
    volumes:
      - ./funasr-runtime-resources/models:/workspace/models
      - ./funasr-runtime-resources/hotwords.txt:/workspace/models/hotwords.txt
    environment:
      - TZ=Asia/Shanghai
    deploy:
      resources:
        limits:
          cpus: '4.0'         # 限制 4 核
          memory: 4G          # 限制 4GB
        reservations:
          cpus: '1.0'
          memory: 1G
    # 健康检查（可选）
    healthcheck:
      test: ["CMD-SHELL", "netstat -tln | grep 10095 || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 120s     # 模型加载时间较长
    networks:
      - xiaozhi-network

networks:
  xiaozhi-network:
    name: xiaozhi-funasr-network
    driver: bridge
```

### 5.4 启动脚本

创建 `start-funasr.sh`：

```bash
#!/bin/bash
# start-funasr.sh
# FunASR 启动脚本

set -e

WORKSPACE_DIR="$HOME/xiaozhi-funasr"
MODEL_DIR="$WORKSPACE_DIR/models"

echo "==> 创建工作目录..."
mkdir -p "$MODEL_DIR"

# 准备空热词文件（不配置热词时使用）
touch "$WORKSPACE_DIR/hotwords.txt"

echo "==> 启动 Docker 容器..."
cd "$(dirname "$0")"
docker compose -f docker-compose-funasr.yml up -d

echo "==> 等待服务启动..."
echo "    首次启动需要下载模型（约 1-2 GB），请耐心等待..."
sleep 30

echo "==> 查看启动日志..."
docker logs -f xiaozhi-funasr
```

**使用方式**：

```bash
chmod +x start-funasr.sh
./start-funasr.sh
```

### 5.5 模型预下载（可选）

为避免首次启动阻塞，可预先下载模型：

```bash
# 创建模型下载脚本
cat > download-models.sh << 'EOF'
#!/bin/bash
# 使用 modelscope CLI 预先下载模型
pip install modelscope
python -c "
from modelscope import snapshot_download
models = [
    'damo/speech_fsmn_vad_zh-cn-16k-common-onnx',
    'damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx',
    'damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx',
    'damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx',
]
for m in models:
    print(f'Downloading {m}...')
    snapshot_download(m, cache_dir='./funasr-runtime-resources/models')
"
EOF
chmod +x download-models.sh
./download-models.sh
```

---

## 6. 与现有项目集成方案

### 6.1 系统架构现状

xiaozhi-esp32-server-java 现有 STT 工厂实现：

```java
// xiaozhi-ai/src/main/java/com/xiaozhi/ai/stt/SttServiceFactory.java
// 第 133 行
case "funasr" -> new FunASRSttService(config);
```

**结论**：Java 端**无需修改任何代码**，仅需配置数据库。

### 6.2 数据库配置

在 `sys_config` 表中插入 FunASR 配置：

```sql
-- 假设当前配置表结构（参考 db/init.sql）
INSERT INTO xiaozhi.sys_config (
    configId, userId, configName, configDesc, configType,
    modelType, provider, apiUrl, state, isDefault,
    createTime, updateTime
) VALUES (
    NULL,                           -- configId 自增
    1,                              -- userId（管理员）
    'FunASR 实时识别 (本地)',
    '本地 Docker 部署的 Paraformer-Large 2pass 服务',
    'stt',                          -- configType
    NULL,                           -- modelType
    'funasr',                       -- provider 关键字段
    'ws://host.docker.internal:10096',  -- apiUrl (Mac Docker 特殊地址)
    '1',                            -- state 启用
    '1',                            -- isDefault 默认
    NOW(), NOW()
);
```

**关键说明**：

| 环境 | apiUrl 写法 |
|------|-------------|
| Mac (Docker Desktop) | `ws://host.docker.internal:10096` |
| Linux (Docker) | `ws://localhost:10096` 或 `ws://funasr:10095` |
| 跨主机 | `ws://192.168.x.x:10096` |

### 6.3 角色配置关联

在 `sys_role` 表中设置 STT 服务：

```sql
-- 将角色关联到 FunASR 配置
UPDATE xiaozhi.sys_role 
SET sttId = <刚才插入的 configId>
WHERE roleId = 1;  -- 默认角色
```

或在 **Web 管理后台** → **角色配置** 中选择新创建的 STT 服务。

### 6.4 SSL 配置

`FunASRSttService` 默认启用 SSL 校验，调试时需关闭：

**FunASR 服务端**（不启用 SSL）：

```bash
# 在容器中执行
nohup bash run_server_2pass.sh \
  --certfile 0 \
  --keyfile 0 \
  --download-model-dir /workspace/models \
  --model-dir damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx \
  --online-model-dir damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx \
  --vad-dir damo/speech_fsmn_vad_zh-cn-16k-common-onnx \
  --punc-dir damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx \
  > log.txt 2>&1 &
```

或使用 Docker 一键部署脚本的 `update --ssl 0` 命令。

---

## 7. 性能调优与资源配置

### 7.1 Docker 资源限制

修改 `docker-compose-funasr.yml`：

```yaml
deploy:
  resources:
    limits:
      cpus: '4.0'         # 4 核（Mac M1 共有 8 核）
      memory: 4G          # 4GB（物理 8GB 的一半）
    reservations:
      cpus: '1.0'
      memory: 1G
```

### 7.2 FunASR 服务端参数

`run_server_2pass.sh` 主要参数：

| 参数 | 推荐值 | 说明 |
|------|--------|------|
| `--decoder-thread-num` | 4 | 解码线程数（≈ 最大并发数） |
| `--io-thread-num` | 2 | IO 线程数 |
| `--port` | 10095 | 监听端口 |
| `--certfile` | 0 | 关闭 SSL（本地开发） |
| `--chunk_size` | `[5,10,5]` | 流式延迟配置（600ms） |

### 7.3 Mac 专项优化

#### 7.3.1 Docker Desktop 资源分配

```
Docker Desktop → Settings → Resources
  ├── CPUs: 4
  ├── Memory: 5 GB
  ├── Swap: 1 GB
  └── Disk: 30 GB+
```

#### 7.3.2 模型磁盘缓存

```bash
# 将模型目录放在外置 SSD 上（如果使用）
ln -s /Volumes/ExternalSSD/funasr-models ~/xiaozhi-funasr/models
```

#### 7.3.3 关闭不必要功能

```bash
# 减少内存占用：关闭 N-gram LM 和 ITN
nohup bash run_server_2pass.sh \
  --model-dir damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx \
  --online-model-dir damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx \
  --vad-dir damo/speech_fsmn_vad_zh-cn-16k-common-onnx \
  --punc-dir damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx \
  --lm-dir "" \
  --itn-dir "" \
  --certfile 0 \
  > log.txt 2>&1 &
```

### 7.4 并发性能参考

| 配置 | 并发路数 | 端到端延迟 |
|------|----------|------------|
| 1 核 + 1GB | 1 路 | 800ms-1.2s |
| 2 核 + 2GB | 2 路 | 800ms-1.5s |
| **4 核 + 4GB (推荐)** | **2-4 路** | **600ms-1s** |
| 8 核 + 8GB | 8-16 路 | 400-800ms |

---

## 8. 安全与网络配置

### 8.1 网络隔离

```yaml
# docker-compose-funasr.yml
networks:
  funasr-internal:
    internal: true   # 禁止外网访问
  xiaozhi-shared:
    external: true   # 与 Java 服务共享网络
    name: xiaozhi-network
```

### 8.2 仅监听本地

容器启动时限制绑定地址：

```bash
# 修改 run_server_2pass.sh 或使用环境变量
# 默认绑定 0.0.0.0，可改为 127.0.0.1
```

### 8.3 防火墙配置

```bash
# macOS 防火墙：只允许本地访问 10096
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --add /Applications/Docker.app
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --block /Applications/Docker.app
# 如需对外提供，单独配置
```

### 8.4 热词配置（可选）

如需启用热词功能：

```bash
# 创建热词文件
cat > ~/xiaozhi-funasr/hotwords.txt << EOF
小智 30
智能音箱 25
阿里巴巴 20
ESP32 20
EOF
```

```bash
# 启动时指定热词文件
nohup bash run_server_2pass.sh \
  --hotword /workspace/models/hotwords.txt \
  ... > log.txt 2>&1 &
```

---

## 9. 运行验证与测试方案

### 9.1 验证 Docker 部署

```bash
# 1. 检查容器状态
docker ps | grep funasr

# 2. 查看启动日志（首次启动会下载模型）
docker logs -f xiaozhi-funasr
# 期望看到: "Started server on 0.0.0.0:10095"

# 3. 检查端口监听
lsof -iTCP:10096 -sTCP:LISTEN
```

### 9.2 官方客户端测试

```bash
# 下载客户端测试工具
cd ~/xiaozhi-funasr
wget https://isv-data.oss-cn-hangzhou.aliyuncs.com/ics/MaaS/ASR/sample/funasr_samples.tar.gz
tar -xzf funasr_samples.tar.gz
cd samples

# Python 客户端测试（需安装 websockets）
pip install websockets
python3 funasr_wss_client.py --host "127.0.0.1" --port 10096 --mode 2pass
# 期望: 麦克风输入测试或指定音频文件
```

### 9.3 集成测试

```bash
# 1. 启动 xiaozhi 服务
cd /path/to/xiaozhi-esp32-server-java
mvn spring-boot:run  # 分别启动 server 和 dialogue

# 2. 验证 WebSocket 连接
# 在管理后台检查 STT 服务是否正常
# 发送测试音频（mock 数据）
```

### 9.4 性能基准测试

参考 [benchmark_onnx_cpp.md](https://github.com/modelscope/FunASR/blob/main/runtime/docs/benchmark_onnx_cpp.md)：

```bash
# 准备 20-50 条测试音频（覆盖短/长/静音/噪声/中英文）
# 记录：
#   - 模型名、版本
#   - 设备、CPU/GPU 型号
#   - RTF (实时率，< 1 为达标)
#   - 延迟、吞吐、内存
#   - 失败样例
```

---

## 10. 运维与故障排查

### 10.1 常用命令

```bash
# 查看容器日志
docker logs -f xiaozhi-funasr

# 查看容器资源使用
docker stats xiaozhi-funasr

# 进入容器调试
docker exec -it xiaozhi-funasr bash

# 重启服务
docker compose -f docker-compose-funasr.yml restart

# 停止服务
docker compose -f docker-compose-funasr.yml down

# 删除容器（保留模型卷）
docker compose -f docker-compose-funasr.yml down --remove-orphans
```

### 10.2 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 容器启动后立即退出 | 模型下载失败 | 检查网络，使用代理 `BUILD_PROXY` |
| WebSocket 连接拒绝 | 端口冲突或服务未就绪 | 等待模型加载完成（首次 1-2 分钟） |
| 识别准确率低 | 音频采样率不匹配 | 确认输入 16kHz、16bit、单声道 PCM |
| 内存溢出 | 并发数过多 | 减少 `decoder-thread-num` |
| 延迟过高 | 设备资源不足 | 关闭 LM/ITN，升级到 M2/M4 |
| SSL 错误 | 证书校验失败 | `--certfile 0` 关闭 SSL |

### 10.3 监控指标

```bash
# 关键监控点
- 容器 CPU 使用率（应 < 80%）
- 容器内存使用（应 < 4GB）
- WebSocket 连接数
- RTF (实时率)
- 首字延迟
- 识别错误率
```

### 10.4 日志位置

```bash
# Docker 容器内
/workspace/log.txt      # FunASR 服务日志
/workspace/FunASR/runtime/log/   # 详细日志

# 宿主机（卷挂载）
~/xiaozhi-funasr/log.txt
```

---

## 11. 方案对比与选型建议

### 11.1 FunASR vs 现有 STT 方案对比

| 方案 | 部署方式 | 资源占用 | 中文精度 | 流式支持 | 适用场景 |
|------|----------|----------|----------|----------|----------|
| **Vosk（当前默认）** | 本地 JNI | ~200MB | 中等 | ✅ | 离线、低算力 |
| **FunASR (本方案)** | Docker / WebSocket | ~1-2GB | 高 | ✅ (2pass) | 生产环境 |
| 阿里云 NLS | 云端 API | 0 | 高 | ✅ | 大规模、低延迟要求 |
| 腾讯云 ASR | 云端 API | 0 | 高 | ✅ | 已有腾讯云服务 |
| 讯飞 ASR | 云端 API | 0 | 高 | ✅ | 中文教育、垂直领域 |
| 火山 ASR | 云端 API | 0 | 高 | ✅ | 短视频场景 |

### 11.2 选型建议

| 场景 | 推荐方案 | 原因 |
|------|----------|------|
| **开发/调试** | **FunASR Docker（方案 A）** | 本地可控、可离线、隐私好 |
| **生产环境（隐私敏感）** | **FunASR 独立服务器部署** | 数据不外流 |
| **生产环境（成本敏感）** | 阿里云 NLS / 腾讯云 ASR | 按量付费，免运维 |
| **生产环境（极致性能）** | FunASR + GPU 服务器 | RTF < 0.3 |
| **离线小设备** | Vosk | 资源占用低 |

### 11.3 Mac 用户的优势

| 优势 | 说明 |
|------|------|
| **离线可用** | Docker 启动后无需网络即可识别 |
| **数据隐私** | 所有音频不出本机 |
| **可调试** | 直接 `docker exec` 进入容器 |
| **便于演示** | 移动办公场景下可便携部署 |

---

## 12. 附录

### 12.1 一键部署脚本（参考）

```bash
#!/bin/bash
# deploy-funasr-mac.sh
# Mac 一键部署 FunASR 脚本（仅供文档参考，未实际执行）

set -e

echo "=========================================="
echo "  FunASR 本地部署脚本 (MacBook Air M1)"
echo "=========================================="

# 1. 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker Desktop for Mac"
    exit 1
fi

# 2. 创建工作目录
WORK_DIR="$HOME/xiaozhi-funasr"
mkdir -p "$WORK_DIR/models"
touch "$WORK_DIR/hotwords.txt"

# 3. 拉取镜像
echo "==> 拉取 FunASR 镜像..."
docker pull registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13

# 4. 启动容器
echo "==> 启动容器..."
docker run -d \
  --name xiaozhi-funasr \
  --restart unless-stopped \
  --platform linux/arm64 \
  -p 10096:10095 \
  -v "$WORK_DIR/models:/workspace/models" \
  -v "$WORK_DIR/hotwords.txt:/workspace/models/hotwords.txt" \
  --memory=4g \
  --cpus=4 \
  registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13

# 5. 等待服务启动
echo "==> 等待服务启动（首次启动约需 1-2 分钟下载模型）..."
sleep 60

# 6. 验证
if docker ps | grep -q xiaozhi-funasr; then
    echo "✅ FunASR 部署成功！"
    echo "   WebSocket 地址: ws://localhost:10096"
    echo "   管理命令: docker logs -f xiaozhi-funasr"
else
    echo "❌ 启动失败，请查看日志: docker logs xiaozhi-funasr"
    exit 1
fi

# 7. 输出数据库配置提示
echo ""
echo "=========================================="
echo "  下一步: 配置 xiaozhi 项目"
echo "=========================================="
echo "在 sys_config 表中插入："
echo "INSERT INTO xiaozhi.sys_config (..., provider, apiUrl, ...)"
echo "VALUES (..., 'funasr', 'ws://host.docker.internal:10096', ...);"
```

### 12.2 关键链接

| 资源 | URL |
|------|-----|
| FunASR 模型选择指南 | https://modelscope.github.io/FunASR/zh/model-selection.html |
| FunASR 实时听写教程 | https://github.com/modelscope/FunASR/blob/main/runtime/docs/SDK_tutorial_online_zh.md |
| FunASR 开发指南 | https://github.com/modelscope/FunASR/blob/main/runtime/docs/SDK_advanced_guide_online_zh.md |
| Docker 镜像 | `registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13` |
| WebSocket 协议 | https://github.com/modelscope/FunASR/blob/main/runtime/docs/websocket_protocol_zh.md |
| 性能基准 | https://github.com/modelscope/FunASR/blob/main/runtime/docs/benchmark_onnx_cpp.md |
| 在线体验 | https://www.funasr.com/static/offline/index.html |

### 12.3 现有项目文件引用

| 文件 | 作用 |
|------|------|
| [FunASRSttService.java](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/xiaozhi-ai/src/main/java/com/xiaozhi/ai/stt/providers/FunASRSttService.java) | Java 端 STT 实现 |
| [SttServiceFactory.java](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/xiaozhi-ai/src/main/java/com/xiaozhi/ai/stt/SttServiceFactory.java) | STT 工厂 |
| [ConfigBO.java](file:///Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/xiaozhi-common/src/main/java/com/xiaozhi/common/model/bo/ConfigBO.java) | 配置数据模型 |

### 12.4 关键端口清单

| 端口 | 用途 | 服务 |
|------|------|------|
| 8091 | xiaozhi-server 管理后台 | Java |
| 8092 | xiaozhi-dialogue 对话服务 | Java |
| 10096 | FunASR WebSocket（宿主机） | Docker |
| 10095 | FunASR WebSocket（容器内） | Docker |
| 3306 | MySQL | Docker |
| 6379 | Redis | Docker |

### 12.5 升级路径

| 阶段 | 升级内容 |
|------|----------|
| 短期 | FunASR Docker 本地部署（本文档） |
| 中期 | 替换为 SenseVoiceSmall 减少资源占用 |
| 长期 | FunASR-Nano + vLLM 加速（需 GPU 服务器） |
| 备选 | 切换到云端 ASR（成本与稳定性平衡） |

---

## 文档总结

本文档针对 **MacBook Air M1 (8GB RAM, arm64)** 设备，提供了将 **FunASR 实时语音听写服务**集成到 **xiaozhi-esp32-server-java** 项目的完整技术部署方案。

**核心要点**：
1. ✅ **零代码修改**：Java 端已实现 `FunASRSttService`，仅需数据库配置
2. ✅ **Docker 容器化**：使用 arm64 镜像一键部署
3. ✅ **2pass 模式**：流式 + 离线修正，平衡延迟与精度
4. ✅ **资源友好**：4GB 内存限制适配 8GB 物理内存
5. ✅ **可降级**：如部署困难，可切换至云端 ASR

**待执行任务清单**（仅文档，未真实部署）：
- [ ] 安装/确认 Docker Desktop for Mac
- [ ] 创建工作目录 `~/xiaozhi-funasr`
- [ ] 拉取 FunASR 镜像
- [ ] 配置 `docker-compose-funasr.yml`
- [ ] 启动容器并验证端口
- [ ] 在 `sys_config` 表插入配置
- [ ] 在 Web 后台绑定角色 STT
- [ ] 进行端到端测试

---

*文档版本: v1.0*
*最后更新: 2026-06-23*
*适用项目版本: xiaozhi-esp32-server-java v5.0.0*
