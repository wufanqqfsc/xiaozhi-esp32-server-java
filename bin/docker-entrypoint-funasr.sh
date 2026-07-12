#!/bin/bash
# FunASR 容器内启动脚本 (Python WebSocket 版)
# 由宿主机 ./bin/funasr.sh start 调用
# 注意: 此脚本在容器内执行
# 使用 Python WebSocket 服务 + Paraformer-large 模型（新版 0.4.6 镜像）
# 与之前 C++ WebSocket 服务的区别：
#   - 解决了 2pass-offline 多结果未正确累积的问题
#   - 输出格式更稳定
#   - 模型自动从 ModelScope 下载

set -e

echo "==== FunASR 容器启动 (Python WebSocket 版) ===="
echo "镜像: funasr-runtime-sdk-cpu-0.4.6"
echo "监听端口: 10095"
echo

# 检查 funasr_wss_server.py 是否存在
WSS_SCRIPT="/workspace/FunASR/runtime/python/websocket/funasr_wss_server.py"
if [ ! -f "$WSS_SCRIPT" ]; then
    echo "错误: 找不到 $WSS_SCRIPT"
    exit 1
fi

echo "==> 启动 Python WebSocket 服务 (Paraformer)..."

cd /workspace/FunASR/runtime/python/websocket

exec python funasr_wss_server.py \
    --host 0.0.0.0 \
    --port 10095 \
    --asr_model "iic/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-pytorch" \
    --asr_model_revision "v2.0.4" \
    --vad_model "iic/speech_fsmn_vad_zh-cn-16k-common-pytorch" \
    --vad_model_revision "v2.0.4" \
    --punc_model "iic/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727" \
    --punc_model_revision "v2.0.4" \
    --ngpu 0 \
    --device cpu \
    --ncpu 4 \
    --certfile "" \
    --keyfile ""