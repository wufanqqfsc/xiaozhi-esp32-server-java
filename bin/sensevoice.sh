#!/bin/bash
# SenseVoice Docker 服务管理脚本

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

COMPOSE_FILE="$ROOT_DIR/docker-compose-sensevoice.yml"
CONTAINER_NAME="xiaozhi-sensevoice"
HOST_PORT=10096

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; NC='\033[0m'

info()  { echo -e "${CYAN}[sensevoice]${NC} $*"; }
ok()    { echo -e "${GREEN}[sensevoice]${NC} $*"; }
warn()  { echo -e "${YELLOW}[sensevoice]${NC} $*"; }
err()   { echo -e "${RED}[sensevoice]${NC} $*" >&2; }

check_docker() {
    if ! docker info &>/dev/null; then
        err "Docker 未运行"
        return 1
    fi
    ok "Docker 就绪"
}

start() {
    info "构建并启动 SenseVoice 服务..."
    docker compose -f "$COMPOSE_FILE" up -d --build
    ok "服务已启动"
    info "WebSocket 端口: $HOST_PORT"
    info "查看日志: ./bin/sensevoice.sh logs"
}

stop() {
    info "停止 SenseVoice 服务..."
    docker compose -f "$COMPOSE_FILE" down
    ok "服务已停止"
}

restart() {
    stop
    sleep 2
    start
}

status() {
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        ok "服务运行中"
        docker ps --filter "name=$CONTAINER_NAME" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    else
        warn "服务未运行"
    fi
}

logs() {
    docker compose -f "$COMPOSE_FILE" logs -f
}

case "$1" in
    start)
        check_docker && start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    logs)
        logs
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status|logs}"
        exit 1
        ;;
esac
