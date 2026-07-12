#!/bin/bash
echo "=========================================="
echo "FunASR WebSocket 服务启动 (Paraformer-large)"
echo "=========================================="

cd /workspace/FunASR/runtime/python/websocket

echo "启动 WebSocket 服务..."
echo "模型: Paraformer-large"
echo "端口: 10095"

exec python funasr_wss_server.py \
    --host 0.0.0.0 \
    --port 10095 \
    --asr_model "iic/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-pytorch" \
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
