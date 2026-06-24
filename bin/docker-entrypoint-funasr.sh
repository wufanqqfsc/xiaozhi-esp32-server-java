#!/bin/bash
# FunASR 容器内启动脚本
# 由宿主机 ./bin/funasr.sh start 调用
# 注意: 此脚本在容器内执行
# 关键: 必须让 funasr-wss-server-2pass 作为容器 PID 1，否则容器会立即退出

set -e

echo "==== FunASR 容器启动 ===="
echo "工作目录: /workspace/FunASR/runtime"
echo "模型目录: /workspace/models"
echo "监听端口: 10095"
echo

cd /workspace/FunASR/runtime

# 预下载所有模型（让 C++ 进程启动时检测到已存在，跳过下载）
echo "==> 预下载/校验模型（可能需要几分钟）..."
for model in \
  "damo/speech_fsmn_vad_zh-cn-16k-common-onnx v2.0.4" \
  "damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx v2.0.5" \
  "damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx v2.0.5" \
  "damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx v2.0.5"; do
  NAME=$(echo $model | awk '{print $1}')
  REV=$(echo $model | awk '{print $2}')
  echo "  - $NAME ($REV)"
  python -m funasr.download.runtime_sdk_download_tool \
    --type onnx --quantize True \
    --model-name "$NAME" \
    --export-dir /workspace/models \
    --model_revision "$REV" 2>&1 | tail -2
done

echo "==> 模型准备完成"
echo

# 直接执行 funasr-wss-server-2pass 二进制（PID 1 仍是 entrypoint，bash 等待 C++ 退出）
# 关键: 不使用 run_server_2pass.sh（它会 fork 后立即退出）
echo "==> 启动 FunASR 服务..."
cd /workspace/FunASR/runtime/websocket/build/bin
exec ./funasr-wss-server-2pass \
  --download-model-dir /workspace/models \
  --model-dir damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx \
  --online-model-dir damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx \
  --vad-dir damo/speech_fsmn_vad_zh-cn-16k-common-onnx \
  --punc-dir damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx \
  --itn-dir "" \
  --lm-dir "" \
  --decoder-thread-num 4 \
  --model-thread-num 1 \
  --io-thread-num 2 \
  --port 10095 \
  --certfile "" \
  --keyfile "" \
  --hotword /workspace/FunASR/runtime/websocket/hotwords.txt
