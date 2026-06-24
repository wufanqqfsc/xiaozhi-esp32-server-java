#!/usr/bin/env bash
# =============================================================================
# FunASR 一键启动脚本
# 部署 Paraformer-Large + 2pass 模式作为 STT 服务
# 详细文档: docs/FUNASR_DEPLOYMENT.md
# =============================================================================

set -e

# ---- 路径与常量 ----
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

WORKSPACE_DIR="$ROOT_DIR/funasr-runtime-resources"
MODEL_DIR="$WORKSPACE_DIR/models"
HOTWORDS_FILE="$WORKSPACE_DIR/hotwords.txt"
COMPOSE_FILE="$ROOT_DIR/docker-compose-funasr.yml"
CONTAINER_NAME="xiaozhi-funasr"
HOST_PORT=10096
CONTAINER_PORT=10095

# ---- 镜像地址 ----
IMAGE_ARM64="registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13"
IMAGE_AMD64="registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-online-cpu-0.1.13"

# ---- 颜色 ----
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

# ---- 日志函数 ----
_log()  { echo -e "${GREEN}[funasr]${NC} $*"; }
_info() { echo -e "${CYAN}[funasr]${NC} $*"; }
_warn() { echo -e "${YELLOW}[funasr]${NC} $*"; }
_err()  { echo -e "${RED}[funasr]${NC} $*" >&2; }
_ok()   { echo -e "${GREEN}[funasr]${NC} ${BOLD}$*${NC}" ; }

# ---- 检查 Docker ----
check_docker() {
  if ! command -v docker &> /dev/null; then
    _err "未检测到 docker，请先安装 Docker Desktop for Mac"
    _err "下载地址: https://www.docker.com/products/docker-desktop/"
    return 1
  fi

  if ! docker info &> /dev/null; then
    _err "Docker 未运行，请启动 Docker Desktop"
    return 1
  fi

  _log "Docker 已就绪: $(docker --version)"
}

# ---- 检测架构 ----
detect_arch() {
  local arch
  arch=$(uname -m)
  case "$arch" in
    arm64|aarch64)
      echo "arm64"
      ;;
    x86_64|amd64)
      echo "amd64"
      ;;
    *)
      _warn "未知架构: $arch，默认使用 arm64"
      echo "arm64"
      ;;
  esac
}

# ---- 准备工作目录 ----
prepare_workspace() {
  _info "准备工作目录: $WORKSPACE_DIR"
  mkdir -p "$MODEL_DIR"

  # 初始化空热词文件
  if [[ ! -f "$HOTWORDS_FILE" ]]; then
    cat > "$HOTWORDS_FILE" << 'EOF'
# FunASR 热词配置文件
# 格式: 热词 权重
# 示例:
# 小智 30
# 智能音箱 25
# 阿里巴巴 20
EOF
    _log "已创建默认热词文件: $HOTWORDS_FILE"
  fi
}

# ---- 拉取镜像 ----
pull_image() {
  local arch="$1"
  local image

  if [[ "$arch" == "arm64" ]]; then
    image="$IMAGE_ARM64"
  else
    image="$IMAGE_AMD64"
  fi

  _info "拉取 FunASR 镜像 ($arch)..."
  _info "镜像: $image"
  docker pull "$image"
  _ok "镜像拉取完成"
}

# ---- 启动服务 ----
start_service() {
  _info "启动 FunASR 服务..."

  if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    _warn "容器 $CONTAINER_NAME 已存在，先移除旧容器"
    docker rm -f "$CONTAINER_NAME" &>/dev/null || true
  fi

  # 主机架构判断
  local arch
  arch=$(detect_arch)

  if [[ "$arch" == "amd64" ]]; then
    _warn "检测到 x86_64 架构，将使用 Rosetta 模拟（性能略低）"
  fi

  # 使用 docker compose
  if command -v docker compose &> /dev/null; then
    docker compose -f "$COMPOSE_FILE" up -d
  else
    # 兼容老版本 docker-compose
    _warn "未找到 docker compose，使用 docker run 启动"
    local image
    if [[ "$arch" == "arm64" ]]; then
      image="$IMAGE_ARM64"
    else
      image="$IMAGE_AMD64"
    fi

    docker run -d \
      --name "$CONTAINER_NAME" \
      --restart unless-stopped \
      --platform "linux/$arch" \
      -p "$HOST_PORT:$CONTAINER_PORT" \
      -v "$MODEL_DIR:/workspace/models" \
      -v "$HOTWORDS_FILE:/workspace/models/hotwords.txt" \
      -e TZ=Asia/Shanghai \
      --memory=4g \
      --cpus=4 \
      "$image"
  fi

  _ok "FunASR 容器已启动"
}

# ---- 等待服务就绪 ----
wait_ready() {
  _info "等待 FunASR 服务就绪..."
  _warn "首次启动需要下载模型（约 1-2 GB），请耐心等待..."

  local max_wait=300  # 最长等待 5 分钟
  local wait_time=0
  local interval=10

  while (( wait_time < max_wait )); do
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
      # 检查容器内 10095 端口是否在监听
      if docker exec "$CONTAINER_NAME" bash -c "cat /proc/net/tcp 2>/dev/null | awk '{print \$2}' | grep -qi '275F'" 2>/dev/null; then
        _ok "FunASR 服务就绪 (port $HOST_PORT)"
        return 0
      fi
    fi

    sleep "$interval"
    wait_time=$((wait_time + interval))
    _info "等待中... (${wait_time}s / ${max_wait}s)"
  done

  _err "服务启动超时，请查看日志: docker logs $CONTAINER_NAME"
  return 1
}

# ---- 停止服务 ----
stop_service() {
  _info "停止 FunASR 服务..."

  if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    docker stop "$CONTAINER_NAME"
    docker rm "$CONTAINER_NAME"
    _ok "FunASR 已停止"
  else
    _warn "FunASR 未在运行"
  fi
}

# ---- 重启服务 ----
restart_service() {
  stop_service
  sleep 2
  start_service
  wait_ready
}

# ---- 查看状态 ----
status_service() {
  echo
  echo -e "${BOLD}========== FunASR 状态 ==========${NC}"

  if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    local status
    status=$(docker inspect -f '{{.State.Status}}' "$CONTAINER_NAME" 2>/dev/null)
    local uptime
    uptime=$(docker inspect -f '{{.State.StartedAt}}' "$CONTAINER_NAME" 2>/dev/null)

    echo -e "  ${GREEN}●${NC} 容器状态: ${BOLD}$status${NC}"
    echo -e "  ${GREEN}●${NC} 启动时间: $uptime"

    # 资源使用
    local stats
    stats=$(docker stats --no-stream --format "{{.CPUPerc}}\t{{.MemUsage}}" "$CONTAINER_NAME" 2>/dev/null)
    if [[ -n "$stats" ]]; then
      echo -e "  ${GREEN}●${NC} CPU/内存: $(echo -e "$stats" | tr '\t' ' ')"
    fi
  else
    echo -e "  ${RED}○${NC} 容器未运行"
  fi

  # 端口检查
  if lsof -iTCP:"$HOST_PORT" -sTCP:LISTEN &>/dev/null; then
    echo -e "  ${GREEN}●${NC} 端口 $HOST_PORT: 监听中"
  else
    echo -e "  ${RED}○${NC} 端口 $HOST_PORT: 未监听"
  fi

  echo
  echo -e "${BOLD}========== 集成信息 ==========${NC}"
  echo -e "  WebSocket URL: ${BOLD}ws://localhost:$HOST_PORT${NC}"
  echo -e "  容器内端口: $CONTAINER_PORT"
  echo -e "  模型目录: $MODEL_DIR"
  echo
  echo -e "  ${CYAN}数据库配置 SQL:${NC}"
  cat << EOF
  INSERT INTO xiaozhi.sys_config (
    configName, configType, provider, apiUrl, state, isDefault
  ) VALUES (
    'FunASR 本地服务', 'stt', 'funasr',
    'ws://localhost:$HOST_PORT', '1', '1'
  );
EOF
  echo
}

# ---- 查看日志 ----
logs_service() {
  if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    _info "跟踪 FunASR 日志（Ctrl+C 退出）..."
    docker logs -f "$CONTAINER_NAME"
  else
    _err "容器 $CONTAINER_NAME 不存在"
    return 1
  fi
}

# ---- 配置 xiaozhi 集成 ----
integrate_xiaozhi() {
  echo
  echo -e "${BOLD}========== xiaozhi 集成指引 ==========${NC}"
  echo
  echo -e "${CYAN}1. 通过 Web 后台配置（推荐）：${NC}"
  echo "   访问 http://localhost:8091"
  echo "   进入: STT 配置 → 新建"
  echo "   - 配置名称: FunASR 本地服务"
  echo "   - 配置类型: STT"
  echo "   - 服务商: funasr"
  echo "   - API URL: ws://localhost:$HOST_PORT"
  echo "   - 状态: 启用"
  echo
  echo -e "${CYAN}2. 通过 SQL 配置：${NC}"
  cat << EOF
   INSERT INTO xiaozhi.sys_config (
     userId, configName, configDesc, configType, provider, apiUrl, state, isDefault,
     createTime, updateTime
   ) VALUES (
     1, 'FunASR 本地服务', 'Paraformer-Large 2pass 模式',
     'stt', 'funasr', 'ws://localhost:$HOST_PORT', '1', '1',
     NOW(), NOW()
   );
EOF
  echo
  echo -e "${CYAN}3. 关联到角色：${NC}"
  echo "   - 在 Web 后台：角色配置 → 编辑角色 → STT 服务选择新建的配置"
  echo "   - 或 SQL: UPDATE xiaozhi.sys_role SET sttId=<configId> WHERE roleId=1;"
  echo
  echo -e "${CYAN}4. 重启 xiaozhi 对话服务使配置生效：${NC}"
  echo "   ./bin/dialogue.sh restart"
  echo
}

# ---- 主入口 ----
case "${1:-start}" in
  start)
    check_docker
    prepare_workspace
    start_service
    wait_ready
    status_service
    integrate_xiaozhi
    ;;
  stop)
    stop_service
    ;;
  restart)
    check_docker
    restart_service
    status_service
    ;;
  status)
    status_service
    ;;
  logs)
    logs_service
    ;;
  pull)
    check_docker
    pull_image "$(detect_arch)"
    ;;
  integrate)
    integrate_xiaozhi
    ;;
  *)
    echo -e "${BOLD}用法:${NC} $0 <start|stop|restart|status|logs|pull|integrate>"
    echo
    echo "  start      编译并启动 FunASR 服务"
    echo "  stop       停止 FunASR 服务"
    echo "  restart    重启 FunASR 服务"
    echo "  status     查看服务状态"
    echo "  logs       查看运行日志"
    echo "  pull       拉取最新镜像"
    echo "  integrate  显示 xiaozhi 集成指引"
    echo
    echo "详细文档: docs/FUNASR_DEPLOYMENT.md"
    exit 1
    ;;
esac
