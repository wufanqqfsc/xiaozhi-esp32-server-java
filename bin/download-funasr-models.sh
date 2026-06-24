#!/usr/bin/env bash
# =============================================================================
# FunASR 模型预下载脚本
# 用于预先下载 FunASR 所需模型到本地目录，避免容器首次启动阻塞
# 模型来源: https://www.modelscope.cn/
# 详细文档: docs/FUNASR_DEPLOYMENT.md
# =============================================================================

set -e

# ---- 路径 ----
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODEL_DIR="$ROOT_DIR/funasr-runtime-resources/models"
mkdir -p "$MODEL_DIR"

# ---- 颜色 ----
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

_log()  { echo -e "${GREEN}[download]${NC} $*"; }
_info() { echo -e "${CYAN}[download]${NC} $*"; }
_warn() { echo -e "${YELLOW}[download]${NC} $*"; }
_err()  { echo -e "${RED}[download]${NC} $*" >&2; }
_ok()   { echo -e "${GREEN}[download]${NC} ${BOLD}$*${NC}" ; }

# ---- 模型清单（2pass 模式所需） ----
MODELS=(
  "damo/speech_fsmn_vad_zh-cn-16k-common-onnx"
  "damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx"
  "damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx"
  "damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx"
)

# ---- 检查 Python ----
check_python() {
  if ! command -v python3 &> /dev/null; then
    _err "未找到 python3，请先安装 Python 3.8+"
    return 1
  fi
  _log "Python 版本: $(python3 --version)"
}

# ---- 安装 modelscope ----
install_modelscope() {
  if python3 -c "import modelscope" 2>/dev/null; then
    _log "modelscope 已安装: $(python3 -c 'import modelscope; print(modelscope.__version__)')"
    return 0
  fi

  _info "安装 modelscope..."
  pip3 install --user modelscope || {
    _err "modelscope 安装失败，请检查 pip 配置"
    return 1
  }
  _ok "modelscope 安装完成"
}

# ---- 下载模型 ----
download_model() {
  local model_id="$1"
  local target_dir="$MODEL_DIR/$model_id"

  if [[ -d "$target_dir" ]] && [[ -n "$(ls -A "$target_dir" 2>/dev/null)" ]]; then
    _log "已存在: $model_id"
    return 0
  fi

  _info "下载模型: $model_id"
  _info "目标: $target_dir"

  python3 << EOF
from modelscope import snapshot_download
import os
try:
    path = snapshot_download(
        model_id="$model_id",
        cache_dir="$MODEL_DIR"
    )
    print(f"✅ 下载完成: {path}")
except Exception as e:
    print(f"❌ 下载失败: {e}")
    raise
EOF
}

# ---- 显示磁盘空间 ----
show_disk_usage() {
  _info "模型目录大小:"
  du -sh "$MODEL_DIR" 2>/dev/null || echo "  (空目录)"
  _info "磁盘剩余空间:"
  df -h "$MODEL_DIR" | tail -1 | awk '{print "  " $4 " 可用"}'
}

# ---- 主流程 ----
main() {
  echo
  echo -e "${BOLD}========================================${NC}"
  echo -e "${BOLD}  FunASR 模型预下载工具${NC}"
  echo -e "${BOLD}========================================${NC}"
  echo
  _info "目标目录: $MODEL_DIR"
  _info "模型数量: ${#MODELS[@]}"
  echo

  check_python
  install_modelscope

  echo
  _info "开始下载模型（这可能需要较长时间，取决于网络速度）..."
  echo

  local start_time=$(date +%s)
  local success=0
  local failed=0

  for model in "${MODELS[@]}"; do
    if download_model "$model"; then
      success=$((success + 1))
    else
      failed=$((failed + 1))
      _warn "模型 $model 下载失败，可稍后重试"
    fi
    echo
  done

  local end_time=$(date +%s)
  local duration=$((end_time - start_time))

  echo
  echo -e "${BOLD}========================================${NC}"
  echo -e "${BOLD}  下载汇总${NC}"
  echo -e "${BOLD}========================================${NC}"
  echo "  成功: $success / ${#MODELS[@]}"
  echo "  失败: $failed"
  echo "  耗时: ${duration} 秒"
  echo
  show_disk_usage
  echo

  if [[ $failed -eq 0 ]]; then
    _ok "所有模型下载完成！"
    echo
    _info "下一步: 启动 FunASR 服务"
    echo "  ./bin/funasr.sh start"
  else
    _warn "部分模型下载失败，可重新执行此脚本重试"
    return 1
  fi
}

# ---- 参数处理 ----
case "${1:-all}" in
  all)
    main
    ;;
  vad)
    check_python
    install_modelscope
    download_model "damo/speech_fsmn_vad_zh-cn-16k-common-onnx"
    ;;
  online)
    check_python
    install_modelscope
    download_model "damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-online-onnx"
    ;;
  offline)
    check_python
    install_modelscope
    download_model "damo/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-onnx"
    ;;
  punc)
    check_python
    install_modelscope
    download_model "damo/punc_ct-transformer_zh-cn-common-vad_realtime-vocab272727-onnx"
    ;;
  status)
    show_disk_usage
    echo
    _info "已下载的模型:"
    find "$MODEL_DIR" -name "config.yaml" -type f 2>/dev/null | head -20
    ;;
  *)
    echo "用法: $0 <all|vad|online|offline|punc|status>"
    echo
    echo "  all      下载所有模型（默认）"
    echo "  vad      仅下载 VAD 模型"
    echo "  online   仅下载流式 ASR 模型"
    echo "  offline  仅下载离线 ASR 模型"
    echo "  punc     仅下载标点预测模型"
    echo "  status   查看已下载模型"
    echo
    echo "详细文档: docs/FUNASR_DEPLOYMENT.md"
    exit 1
    ;;
esac
