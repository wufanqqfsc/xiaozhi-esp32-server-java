#!/bin/bash
echo "=========================================="
echo "SenseVoice Small WebSocket 服务 (CPU)"
echo "=========================================="

cd /workspace

echo "启动 WebSocket 服务..."
echo "模型: SenseVoiceSmall"
echo "端口: 10095"
echo "CPU: 4 核"

exec python /workspace/sensevoice_wss_server.py
