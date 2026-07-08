#!/usr/bin/env bash
# =============================================================================
# Xiaozhi ESP32 Server Java — 一键启动脚本
#
# 用法:
#   ./start.sh                    一键启动（下载模型 + 编译 + 启动后端）
#   ./start.sh start             同上
#   ./start.sh stop               停止后端 + FunASR
#   ./start.sh restart            重启后端 + FunASR
#   ./start.sh status            查看后端服务状态
#   ./start.sh logs [name]        查看日志（all | server | dialogue | web）
#   ./start.sh db-only           仅启动 MySQL + Redis（Docker）
#   ./start.sh db-down            停止并销毁 DB 容器
#   ./start.sh web                启动前端管理平台（Vue dev server）
#   ./start.sh funasr <action>    控制 FunASR 语音服务（包装 bin/funasr.sh）
#   ./start.sh all                启动全部：DB + 后端 + 前端 + FunASR
#   ./start.sh clean              清理编译产物
#
# 环境变量:
#   SKIP_DOWNLOAD=1   跳过模型/原生库下载
#   SKIP_BUILD=1      跳过 Maven 编译
#   SKIP_FUNASR=1     跳过 FunASR 启动（用于 all）
#   JAVA_HOME=<path> 指定 Java（默认 /opt/homebrew/Cellar/openjdk/25.0.2）
#   DB_MODE=docker    DB 模式：docker（默认）| local（需本机安装 MySQL+Redis）
# =============================================================================

# set -euo pipefail
# 注意：macOS 默认 bash 3.2 在 set -u 下对 local var=$() 模式存在已知 bug，
# 会误报 unbound variable。改为仅 set -eo pipefail，依靠 set -e 捕获错误。
set -eo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

RUN_DIR="$ROOT_DIR/run"
LOGS_DIR="$ROOT_DIR/logs"
DB_COMPOSE="$ROOT_DIR/docker-compose-db.yml"
WEB_DIR="$ROOT_DIR/web"

# ---- 服务端口 ----
SERVER_NAME="xiaozhi-server";   SERVER_MODULE="xiaozhi-server";   SERVER_PORT=8091
DIALOGUE_NAME="xiaozhi-dialogue"; DIALOGUE_MODULE="xiaozhi-dialogue"; DIALOGUE_PORT=8092
WEB_PORT=8084

# ---- FunASR 语音服务 ----
FUNASR_NAME="xiaozhi-funasr"
FUNASR_SCRIPT="$ROOT_DIR/bin/funasr.sh"
FUNASR_PORT=10096

# ---- Java ----
JAVA_HOME="${JAVA_HOME:-/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home}"

# ---- 颜色 ----
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

log()  { echo -e "${GREEN}[$(date '+%H:%M:%S')]${NC} $*"; }
info() { echo -e "${BLUE}[$(date '+%H:%M:%S')]${NC} $*"; }
warn() { echo -e "${YELLOW}[$(date '+%H:%M:%S')]${NC} $*"; }
err()  { echo -e "${RED}[$(date '+%H:%M:%S')]${NC} $*" >&2; }
ok()   { echo -e "${GREEN}[$(date '+%H:%M:%S')] ✓${NC} ${BOLD}$*${NC}"; }
fail() { err "$*"; exit 1; }

mkdir -p "$RUN_DIR" "$LOGS_DIR"

# =============================================================================
# 日志清理
# =============================================================================

clean_logs() {
  info "清理日志文件夹..."
  if [[ -d "$LOGS_DIR" ]]; then
    # 仅清理 *.log 文件，保留 start_*.log 和目录结构
    find "$LOGS_DIR" -maxdepth 1 -name "*.log" -type f -delete
    ok "日志已清理"
  fi
}

# =============================================================================
# 端口检查与进程清理
# =============================================================================

cleanup_port() {
  local port="${1:-}" name="${2:-}"
  if (echo >/dev/tcp/127.0.0.1/"$port") 2>/dev/null; then
    info "端口 $port 被占用，查找并终止进程..."
    local pids
    pids="$(lsof -nP -iTCP:"$port" -sTCP:LISTEN -t 2>/dev/null || true)"
    if [[ -n "$pids" ]]; then
      for pid in $pids; do
        local proc_info
        proc_info="$(ps -p "$pid" -o comm= 2>/dev/null || echo "unknown")"
        warn "终止 pid=$pid ($proc_info) 占用端口 $port..."
        kill "$pid" 2>/dev/null || true
        sleep 1
        if kill -0 "$pid" 2>/dev/null; then
          kill -9 "$pid" 2>/dev/null || true
        fi
      done
      ok "端口 $port 已释放"
    else
      warn "端口 $port 仍被占用，强制清理..."
      # fallback: 尝试从 PID 文件清理
      for pid_file in "$RUN_DIR"/*.pid; do
        [[ -f "$pid_file" ]] || continue
        local p="$(cat "$pid_file")"
        if kill -0 "$p" 2>/dev/null; then
          kill -9 "$p" 2>/dev/null || true
        fi
        rm -f "$pid_file"
      done
    fi
  fi
}

cleanup_all_ports() {
  info "检查端口占用..."
  cleanup_port 8091  "xiaozhi-server"
  cleanup_port 8092  "xiaozhi-dialogue"
  cleanup_port 8084  "xiaozhi-web"
}

# =============================================================================
# 前置检查
# =============================================================================

need() { command -v "$1" >/dev/null 2>&1 || fail "缺少：$1，请先安装"; }

check_java() {
  export JAVA_HOME
  export PATH="$JAVA_HOME/bin:$PATH"
  local ver
  ver="$(java -version 2>&1 | head -1 | awk -F '"' '{print $2}')"
  local major="${ver%%.*}"
  (( major >= 21 )) || fail "需要 Java 21+，当前为 $ver"
  ok "Java $ver"
}

check_maven() {
  export JAVA_HOME; export PATH="$JAVA_HOME/bin:$PATH"
  need mvn
  ok "Maven"
}

check_node() {
  need node
  local ver
  ver="$(node -v | tr -d 'v')"
  local major="${ver%%.*}"
  (( major >= 20 )) || fail "需要 Node.js 20+，当前 v$ver"
  ok "Node.js v$ver"
}

check_docker() {
  need docker
  docker info >/dev/null 2>&1 || fail "Docker daemon 未运行，请先启动 Docker Desktop"
  ok "Docker"
}

port_in_use() {
  local port="${1:-}"
  if command -v lsof >/dev/null 2>&1; then
    lsof -nP -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
  else
    (echo >/dev/tcp/127.0.0.1/"$port") >/dev/null 2>&1
  fi
}

wait_port() {
  local host="${1-127.0.0.1}" port="${2-0}" name="${3-port}" timeout="${4-90}"
  : "${host:=127.0.0.1}" "${port:=0}" "${name:=port}" "${timeout:=90}"
  local i=0
  while (( i < timeout )); do
    if [[ "$port" -gt 0 ]] && (echo >/dev/tcp/"$host"/"$port") >/dev/null 2>&1; then
      return 0
    fi
    sleep 1; (( i++ ))
  done
  return 1
}

write_pid() { [[ -n "${1:-}" && -n "${2:-}" ]] && echo "$2" > "$RUN_DIR/$1.pid"; }
pid_alive() { [[ -n "${1:-}" && -f "$RUN_DIR/$1.pid" ]] && kill -0 "$(cat "$RUN_DIR/$1.pid")" 2>/dev/null; }

find_jar() {
  local module="${1:-}"
  if [[ -z "$module" ]]; then
    echo ""
  elif [[ "$module" == "xiaozhi-dialogue" ]]; then
    ls "$ROOT_DIR/$module/target/$module"-*-exec.jar 2>/dev/null | head -1 || true
  else
    ls "$ROOT_DIR/$module/target/$module"-*.jar 2>/dev/null \
      | grep -v 'original' | grep -v '\-exec\.jar' | head -1 || true
  fi
}

# =============================================================================
# DB（Docker）
# =============================================================================

db_up() {
  # 仅清理应用端口（8091/8092/8084），不清理 DB 端口（3306/6379）
  cleanup_port 8091 "xiaozhi-server"
  cleanup_port 8092 "xiaozhi-dialogue"
  cleanup_port 8084 "xiaozhi-web"
  info "检查 DB 容器..."
  if docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^xiaozhi-mysql$'; then
    ok "MySQL 已在运行"
  else
    check_docker
    # 预拉镜像（超时容忍）
    info "预拉 MySQL + Redis 镜像..."
    timeout 120 docker pull mysql:8.0-debian   2>&1 | tail -3 || warn "MySQL 镜像拉取超时，尝试启动..."
    timeout 60  docker pull redis:7-alpine     2>&1 | tail -3 || warn "Redis 镜像拉取超时，尝试启动..."

    info "启动 MySQL + Redis..."
    docker compose -f "$DB_COMPOSE" up -d

    # 等健康
    info "等待 MySQL 健康检查..."
    local i=0
    while (( i < 60 )); do
      if docker inspect --format='{{.State.Health.Status}}' xiaozhi-mysql 2>/dev/null | grep -q '^healthy$'; then
        ok "MySQL 健康"
        break
      fi
      sleep 2; (( i++ ))
    done
    (( i < 60 )) || warn "MySQL 健康检查未在 60s 内完成，继续..."

    info "等待 Redis 健康检查..."
    local j=0
    while (( j < 30 )); do
      if docker inspect --format='{{.State.Health.Status}}' xiaozhi-redis 2>/dev/null | grep -q '^healthy$'; then
        ok "Redis 健康"
        break
      fi
      sleep 1; (( j++ ))
    done
    (( j < 30 )) || warn "Redis 健康检查未在 30s 内完成，继续..."
  fi
}

db_down() {
  info "停止并销毁 DB 容器..."
  docker compose -f "$DB_COMPOSE" down 2>/dev/null || true
  ok "DB 已停止"
}

db_status() {
  if docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^xiaozhi-mysql$'; then
    echo -e "  ${GREEN}●${NC} ${BOLD}MySQL${NC}  xiaozhi-mysql  port=3306"
  else
    echo -e "  ${RED}○${NC} ${BOLD}MySQL${NC}  未运行"
  fi
  if docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^xiaozhi-redis$'; then
    echo -e "  ${GREEN}●${NC} ${BOLD}Redis${NC}  xiaozhi-redis  port=6379"
  else
    echo -e "  ${RED}○${NC} ${BOLD}Redis${NC}  未运行"
  fi
}

# =============================================================================
# 模型下载
# =============================================================================

download_models() {
  if [[ "${SKIP_DOWNLOAD:-0}" == "1" ]]; then
    warn "SKIP_DOWNLOAD=1 跳过模型下载"
    return 0
  fi
  if [[ -d "$ROOT_DIR/lib" ]] && [[ -d "$ROOT_DIR/models" ]]; then
    local native_count
    native_count="$(find "$ROOT_DIR/lib" -maxdepth 1 -type f 2>/dev/null | wc -l | tr -d ' ')"
    if (( native_count > 0 )); then
      ok "lib/models 已存在（$native_count 个原生库），跳过下载"
      return 0
    fi
  fi
  info "下载原生库与基础模型（首次运行需要较长时间）..."
  bash "$ROOT_DIR/scripts/download_base.sh"
  ok "基础依赖下载完成"
}

# =============================================================================
# Maven 编译
# =============================================================================

build_backend() {
  if [[ "${SKIP_BUILD:-0}" == "1" ]]; then
    warn "SKIP_BUILD=1 跳过编译"
    return 0
  fi
  check_java
  check_maven
  info "Maven 编译（首次需要下载依赖，请耐心等待）..."
  export JAVA_HOME; export PATH="$JAVA_HOME/bin:$PATH"
  mvn clean install -DskipTests -q -f "$ROOT_DIR/pom.xml"
  ok "编译完成"
}

# =============================================================================
# 后端启停
# =============================================================================

# 自动检测本机 LAN IP（用于 ServerAddressProvider 正确返回给 ESP32 设备的 OTA URL）。
# 优先取用户已 export 的 HOST_IP / DOCKER_HOST_IP / LOCAL_IP / XIAOZHI_HOST_IP，
# 没有任何已设置时探测本机网络接口。
# 修复背景：ServerAddressProvider 默认通过公网查询获取 IP，结果会因 ISP / VPN
# 出现错误的公网地址，导致设备端 OTA 响应中 websocket.url 不可达。
detect_lan_ip() {
  for env_name in HOST_IP DOCKER_HOST_IP LOCAL_IP XIAOZHI_HOST_IP; do
    local v="${!env_name:-}"
    if [[ -n "$v" ]]; then
      echo "$v"
      return 0
    fi
  done
  # macOS 取 en0；Linux 取 1.2.3.4 之外的第一个非 loopback 地址
  local ip
  if command -v ifconfig >/dev/null 2>&1; then
    ip="$(ifconfig en0 2>/dev/null | awk '/inet / {print $2; exit}')"
    if [[ -z "$ip" ]]; then
      ip="$(ifconfig 2>/dev/null | awk '/inet / && $2 !~ /^127[.]/ {print $2; exit}')"
    fi
  elif command -v hostname >/dev/null 2>&1; then
    ip="$(hostname -I 2>/dev/null | awk '{print $1}')"
  fi
  echo "${ip:-}"
}

# 确保 LAN IP 已 export 到当前 shell（供后续 nohup java 进程继承）
ensure_lan_ip_exported() {
  local detected
  detected="$(detect_lan_ip)"
  if [[ -z "$detected" ]]; then
    warn "未能自动检测 LAN IP，将由 ServerAddressProvider 走公网查询（可能不可达）"
    return 0
  fi
  : "${HOST_IP:=$detected}"
  : "${DOCKER_HOST_IP:=$detected}"
  : "${LOCAL_IP:=$detected}"
  : "${XIAOZHI_HOST_IP:=$detected}"
  export HOST_IP DOCKER_HOST_IP LOCAL_IP XIAOZHI_HOST_IP
  ok "已注入 LAN IP 环境变量: HOST_IP=$HOST_IP"
}

start_service() {
  local name="${1:-}" module="${2:-}" port="${3:-}"

  if pid_alive "$name"; then
    warn "$name 已在运行（pid=$(cat "$RUN_DIR/$name.pid")）"
    return 0
  fi
  if port_in_use "$port"; then
    warn "端口 $port 已被占用，假设 $name 在外部运行"
    return 0
  fi

  # 启动前确保 LAN IP 环境变量已就绪
  ensure_lan_ip_exported
  # 强制使用脚本里配置的 JAVA_HOME 启动 java，避免 nohup 子进程用系统 java
  export JAVA_HOME
  export PATH="$JAVA_HOME/bin:$PATH"

  local jar
  jar="$(find_jar "$module")"
  if [[ -z "$jar" ]]; then
    err "$module jar 不存在，请先编译"
    return 1
  fi

  nohup "$JAVA_HOME/bin/java" \
    -Djava.library.path="$ROOT_DIR/lib" \
    -jar "$jar" \
    >> "$LOGS_DIR/$name.log" 2>&1 &

  local pid=$!
  write_pid "$name" "$pid"
  ok "$name 已启动  pid=$pid  日志: logs/$name.log"
}

start_backend() {
  build_backend
  start_service "$SERVER_NAME"   "$SERVER_MODULE"   "$SERVER_PORT"
  start_service "$DIALOGUE_NAME" "$DIALOGUE_MODULE" "$DIALOGUE_PORT"
  wait_port 127.0.0.1 "$SERVER_PORT"   "$SERVER_NAME"   60 || true
  wait_port 127.0.0.1 "$DIALOGUE_PORT" "$DIALOGUE_NAME" 60 || true

  # 启动后在控制台实时输出 dialogue 日志
  info "正在监听 dialogue 日志输出..."
  tail -f "$LOGS_DIR/$DIALOGUE_NAME.log" &
  local tail_pid=$!
  write_pid "dialogue-log-tail" "$tail_pid"
}

stop_service() {
  local name="${1:-}"
  local pid_file="$RUN_DIR/$name.pid"

  if ! pid_alive "$name"; then
    warn "$name 未在运行"
    return 0
  fi

  local pid
  pid="$(cat "$pid_file")"
  info "停止 $name（pid=$pid）..."
  kill "$pid" 2>/dev/null || true
  local i=0
  while kill -0 "$pid" 2>/dev/null && (( i < 15 )); do
    sleep 1; (( i++ ))
  done
  if kill -0 "$pid" 2>/dev/null; then
    warn "未能正常关闭，强制结束..."
    kill -9 "$pid" 2>/dev/null || true
  fi
  rm -f "$pid_file"
  ok "$name 已停止"
}

stop_backend() {
  # 先停止日志 tail 进程
  if pid_alive "dialogue-log-tail"; then
    local tail_pid
    tail_pid="$(cat "$RUN_DIR/dialogue-log-tail.pid")"
    kill "$tail_pid" 2>/dev/null || true
    rm -f "$RUN_DIR/dialogue-log-tail.pid"
  fi
  stop_service "$SERVER_NAME"
  stop_service "$DIALOGUE_NAME"
}

backend_status() {
  if pid_alive "$SERVER_NAME"; then
    echo -e "  ${GREEN}●${NC} ${BOLD}$SERVER_NAME${NC}   pid=$(cat "$RUN_DIR/$SERVER_NAME.pid")  port=$SERVER_PORT  日志: logs/$SERVER_NAME.log"
  else
    echo -e "  ${RED}○${NC} ${BOLD}$SERVER_NAME${NC}   未运行"
  fi
  if pid_alive "$DIALOGUE_NAME"; then
    echo -e "  ${GREEN}●${NC} ${BOLD}$DIALOGUE_NAME${NC} pid=$(cat "$RUN_DIR/$DIALOGUE_NAME.pid")  port=$DIALOGUE_PORT  日志: logs/$DIALOGUE_NAME.log"
  else
    echo -e "  ${RED}○${NC} ${BOLD}$DIALOGUE_NAME${NC} 未运行"
  fi
}

# =============================================================================
# 前端
# =============================================================================

detect_pkg() {
  if [[ -f "$WEB_DIR/bun.lock" ]] && command -v bun >/dev/null 2>&1; then echo "bun"
  elif [[ -f "$WEB_DIR/pnpm-lock.yaml" ]] && command -v pnpm >/dev/null 2>&1; then echo "pnpm"
  else echo "npm"; fi
}

install_web_deps() {
  check_node
  local pkg
  pkg="$(detect_pkg)"
  info "前端包管理器：$pkg"
  case "$pkg" in
    npm)  need npm;  [[ -d "$WEB_DIR/node_modules" ]] || npm install  --no-audit --no-fund --prefix "$WEB_DIR";;
    pnpm) need pnpm; [[ -d "$WEB_DIR/node_modules" ]] || pnpm install --silent --dir "$WEB_DIR";;
    bun)  need bun;  [[ -d "$WEB_DIR/node_modules" ]] || bun install --silent "$WEB_DIR";;
  esac
  ok "前端依赖就绪"
}

start_web() {
  cleanup_port 8084 "xiaozhi-web"
  if pid_alive "xiaozhi-web"; then
    warn "xiaozhi-web 已在运行（pid=$(cat "$RUN_DIR/xiaozhi-web.pid")）"
    return 0
  fi
  if port_in_use "$WEB_PORT"; then
    warn "端口 $WEB_PORT 已被占用"
    return 0
  fi
  install_web_deps

  local pkg
  pkg="$(detect_pkg)"
  info "启动前端 dev server（port ${WEB_PORT:-8084}）..."
  cd "$WEB_DIR"
  nohup $pkg run dev > "$LOGS_DIR/xiaozhi-web.log" 2>&1 &
  local web_pid=$!
  cd "$ROOT_DIR"
  write_pid "xiaozhi-web" "$web_pid"
  ok "xiaozhi-web 已启动  pid=$web_pid  日志: logs/xiaozhi-web.log"
}

stop_web() {
  local pid_file="$RUN_DIR/xiaozhi-web.pid"
  if ! pid_alive "xiaozhi-web"; then
    warn "xiaozhi-web 未在运行"
    return 0
  fi
  local pid
  pid="$(cat "$pid_file")"
  info "停止 xiaozhi-web（pid=$pid）..."
  pkill -P "$pid" 2>/dev/null || true
  kill "$pid" 2>/dev/null || true
  local i=0
  while kill -0 "$pid" 2>/dev/null && (( i < 10 )); do
    sleep 1; (( i++ ))
  done
  kill -9 "$pid" 2>/dev/null || true
  rm -f "$pid_file"
  ok "xiaozhi-web 已停止"
}

web_status() {
  if pid_alive "xiaozhi-web"; then
    echo -e "  ${GREEN}●${NC} ${BOLD}xiaozhi-web${NC} pid=$(cat "$RUN_DIR/xiaozhi-web.pid")  port=$WEB_PORT  日志: logs/xiaozhi-web.log"
  else
    echo -e "  ${RED}○${NC} ${BOLD}xiaozhi-web${NC} 未运行"
  fi
}

# =============================================================================
# FunASR 语音服务（Docker 容器，包装 bin/funasr.sh）
# =============================================================================

start_funasr() {
  local name="${FUNASR_NAME:-xiaozhi-funasr}"
  local script="${FUNASR_SCRIPT:-$ROOT_DIR/bin/funasr.sh}"
  local port="${FUNASR_PORT:-10096}"

  if [[ "${SKIP_FUNASR:-0}" == "1" ]]; then
    warn "SKIP_FUNASR=1 跳过 FunASR"
    return 0
  fi
  if [[ ! -x "$script" ]]; then
    warn "FunASR 脚本不存在或不可执行: $script"
    return 0
  fi
  # 已运行则跳过
  if docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${name}$"; then
    ok "FunASR 已在运行"
    return 0
  fi
  info "启动 FunASR 语音服务（port ${port}）..."
  bash "$script" start >> "$LOGS_DIR/funasr.log" 2>&1 || {
    err "FunASR 启动失败，查看日志: logs/funasr.log"
    return 1
  }
  ok "FunASR 已启动  端口: ${port}  日志: logs/funasr.log"
}

stop_funasr() {
  local script="${FUNASR_SCRIPT:-$ROOT_DIR/bin/funasr.sh}"
  local name="${FUNASR_NAME:-xiaozhi-funasr}"
  if [[ ! -x "$script" ]]; then
    return 0
  fi
  if ! docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${name}$"; then
    return 0
  fi
  info "停止 FunASR..."
  bash "$script" stop >> "$LOGS_DIR/funasr.log" 2>&1 || true
  ok "FunASR 已停止"
}

funasr_status() {
  local name="${FUNASR_NAME:-xiaozhi-funasr}"
  local port="${FUNASR_PORT:-10096}"
  local script="${FUNASR_SCRIPT:-$ROOT_DIR/bin/funasr.sh}"
  echo -e "  ${BOLD}FunASR${NC}  容器: $name  端口: $port"
  if docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${name}$"; then
    echo -e "    ${GREEN}●${NC} 运行中"
  else
    echo -e "    ${RED}○${NC} 未运行"
  fi
  if lsof -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1; then
    echo -e "    ${GREEN}●${NC} 端口 $port 监听中"
  else
    echo -e "    ${RED}○${NC} 端口 $port 未监听"
  fi
}

# =============================================================================
# 复合命令
# =============================================================================

start_default() {
  banner "启动 Xiaozhi 后端服务"
  clean_logs
  cleanup_all_ports
  db_up
  download_models
  start_backend
  ok "后端已全部启动"
  show_status
}

start_all() {
  banner "启动 Xiaozhi 全部服务"
  clean_logs
  cleanup_all_ports
  db_up
  download_models
  start_funasr
  start_backend
  start_web
  banner "启动完成"
  show_status
  cat <<EOF

${BOLD}访问地址：${NC}
  ${GREEN}▸${NC} 管理平台前端  http://localhost:$WEB_PORT
  ${GREEN}▸${NC} Server API    http://localhost:$SERVER_PORT
  ${GREEN}▸${NC} Dialogue WS    ws://localhost:$DIALOGUE_PORT
  ${GREEN}▸${NC} FunASR WS      ws://localhost:${FUNASR_PORT:-10096}

${BOLD}常用命令：${NC}
  ./start.sh status    # 查看状态
  ./start.sh logs      # 查看日志
  ./start.sh stop      # 停止后端
  ./start.sh db-down   # 停止并销毁 DB 容器
EOF
}

stop_all() {
  stop_web
  stop_backend
  stop_funasr
  ok "全部已停止"
}

restart_all() {
  stop_web
  stop_backend
  stop_funasr
  sleep 2
  start_funasr
  start_backend
  start_web
  ok "重启完成"
}

show_status() {
  banner "服务状态"
  echo -e "${BOLD}数据库：${NC}"
  db_status
  echo ""
  echo -e "${BOLD}后端：${NC}"
  backend_status
  echo ""
  echo -e "${BOLD}前端：${NC}"
  web_status
  echo ""
  echo -e "${BOLD}AI 服务：${NC}"
  funasr_status
  echo ""
}

show_logs() {
  local target="${1:-all}"
  case "$target" in
    server)   tail -f "$LOGS_DIR/$SERVER_NAME.log" ;;
    dialogue) tail -f "$LOGS_DIR/$DIALOGUE_NAME.log" ;;
    web)      tail -f "$LOGS_DIR/xiaozhi-web.log" ;;
    all)
      echo -e "${BOLD}=== $SERVER_NAME ===${NC}"; tail -n 20 "$LOGS_DIR/$SERVER_NAME.log" 2>/dev/null || echo "(no log)"
      echo ""; echo -e "${BOLD}=== $DIALOGUE_NAME ===${NC}"; tail -n 20 "$LOGS_DIR/$DIALOGUE_NAME.log" 2>/dev/null || echo "(no log)"
      echo ""; echo -e "${BOLD}=== xiaozhi-web ===${NC}"; tail -n 20 "$LOGS_DIR/xiaozhi-web.log" 2>/dev/null || echo "(no log)"
      ;;
    *) fail "未知：$target（all | server | dialogue | web）" ;;
  esac
}

banner() {
  echo ""
  echo -e "${BOLD}${BLUE}============================================================${NC}"
  echo -e "${BOLD}${BLUE}  $*${NC}"
  echo -e "${BOLD}${BLUE}============================================================${NC}"
  echo ""
}

usage() {
  cat <<EOF
用法: ${BOLD}$0${NC} <command>

命令:
  start              下载模型 + 编译 + 启动后端        [默认]
  stop               停止后端 + FunASR
  restart            重启后端 + FunASR
  status             查看全部服务状态
  logs [name]        查看日志（all|server|dialogue|web），默认 all
  db-only            仅启动 DB（MySQL + Redis）
  db-down            停止并销毁 DB 容器
  web                仅启动前端（Vue dev server）
  funasr <action>    控制 FunASR 语音服务（start|stop|restart|status|logs）
  all                启动全部：DB + 后端 + 前端 + FunASR
  clean              清理编译产物

环境变量:
  SKIP_DOWNLOAD=1   跳过模型/原生库下载
  SKIP_BUILD=1       跳过 Maven 编译
  SKIP_FUNASR=1      跳过 FunASR 启动（用于 all）
  JAVA_HOME=<path>   指定 Java（默认 /opt/homebrew/Cellar/openjdk/25.0.2）

示例:
  $0                       # 后端一键启动
  $0 all                   # 全部启动（DB + 后端 + 前端 + FunASR）
  $0 logs server           # 查看 server 日志
  $0 db-only && $0 start   # 先 DB 后端端分离启动
  $0 funasr status         # 查看 FunASR 状态
  SKIP_DOWNLOAD=1 $0 start # 跳过下载快速启动
  SKIP_FUNASR=1 $0 all     # 启动全部但跳过 FunASR
EOF
}

# =============================================================================
main() {
  local cmd="${1:-start}"
  shift 2>/dev/null || true

  case "$cmd" in
    start)         start_default ;;
    stop)          stop_all ;;
    restart)       restart_all ;;
    status)        show_status ;;
    logs)          show_logs "$1" ;;
    db-only)       db_up; db_status ;;
    db-down)       db_down ;;
    web)           start_web; web_status ;;
    funasr)
      local fs="${FUNASR_SCRIPT:-$ROOT_DIR/bin/funasr.sh}"
      [[ -x "$fs" ]] || fail "FunASR 脚本不存在或不可执行: $fs"
      local action="${1:-status}"
      bash "$fs" "$action"
      ;;
    all)           start_all ;;
    clean)
      banner "清理"
      rm -rf "$RUN_DIR"/*.pid
      mvn -f "$ROOT_DIR/pom.xml" clean -q 2>/dev/null || true
      ok "已清理"
      ;;
    -h|--help|help) usage ;;
    *) usage; exit 1 ;;
  esac
}

main "$@"
