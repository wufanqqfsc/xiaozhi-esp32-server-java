#!/bin/bash
# SenseVoice 服务启动脚本

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$SCRIPT_DIR/sensevoice.log"
PID_FILE="$SCRIPT_DIR/sensevoice.pid"

start() {
    echo "启动 SenseVoice 服务..."

    # 检查是否已运行
    if [ -f "$PID_FILE" ]; then
        OLD_PID=$(cat "$PID_FILE")
        if kill -0 "$OLD_PID" 2>/dev/null; then
            echo "服务已在运行 (PID: $OLD_PID)"
            return 1
        fi
        rm -f "$PID_FILE"
    fi

    # 检查端口
    if lsof -i:10096 -sTCP:LISTEN >/dev/null 2>&1; then
        echo "错误: 端口 10096 已被占用"
        return 1
    fi

    # 后台启动
    cd "$SCRIPT_DIR"
    nohup python3 sensevoice_server.py > "$LOG_FILE" 2>&1 &
    echo $! > "$PID_FILE"

    echo "服务已启动 (PID: $(cat "$PID_FILE"))"
    echo "日志: $LOG_FILE"
}

stop() {
    echo "停止 SenseVoice 服务..."

    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if kill -0 "$PID" 2>/dev/null; then
            kill "$PID"
            rm -f "$PID_FILE"
            echo "服务已停止"
        else
            rm -f "$PID_FILE"
            echo "服务未运行"
        fi
    else
        echo "服务未运行"
    fi
}

status() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if kill -0 "$PID" 2>/dev/null; then
            echo "服务运行中 (PID: $PID)"
            return 0
        fi
    fi

    if lsof -i:10096 -sTCP:LISTEN >/dev/null 2>&1; then
        echo "服务运行中 (端口 10096)"
        return 0
    fi

    echo "服务未运行"
    return 1
}

logs() {
    if [ -f "$LOG_FILE" ]; then
        tail -f "$LOG_FILE"
    else
        echo "日志文件不存在"
    fi
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        sleep 2
        start
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
