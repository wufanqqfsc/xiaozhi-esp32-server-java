#!/usr/bin/env bash
# =============================================================================
# FunASR 麦克风测试页面启动脚本
#
# 用法:
#   ./bin/funasr-mic-test.sh           默认端口 8765
#   ./bin/funasr-mic-test.sh 9000      指定端口
#   ./bin/funasr-mic-test.sh open      直接用浏览器打开（无需 HTTP 服务）
#
# 注意:
#   - 浏览器对 getUserMedia / WebSocket 有安全限制
#   - 必须通过 http://localhost:端口 访问（不要直接打开 file://）
#   - FunASR 服务必须已启动（默认 ws://localhost:10096）
# =============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HTML_FILE="$ROOT_DIR/bin/funasr-mic-test.html"
DEFAULT_PORT=8765
PORT="${1:-$DEFAULT_PORT}"

# 直接打开 file:// 模式
if [[ "$PORT" == "open" ]]; then
  if command -v open >/dev/null 2>&1; then
    open "file://$HTML_FILE"
  elif command -v xdg-open >/dev/null 2>&1; then
    xdg-open "file://$HTML_FILE"
  else
    echo "请手动在浏览器打开: file://$HTML_FILE"
  fi
  echo
  warn "⚠️  file:// 模式下 getUserMedia 可能被浏览器拒绝"
  echo "   建议使用 ./bin/funasr-mic-test.sh 启动本地 HTTP 服务"
  exit 0
fi

# 检查端口占用
if lsof -nP -iTCP:"$PORT" -sTCP:LISTEN >/dev/null 2>&1; then
  err "端口 $PORT 已被占用"
  lsof -nP -iTCP:"$PORT" -sTCP:LISTEN | head -3
  exit 1
fi

# 启动 Python HTTP 服务
echo "============================================================"
echo "  FunASR 麦克风测试页面"
echo "============================================================"
echo
echo "  访问地址: ${GREEN}http://localhost:$PORT/bin/funasr-mic-test.html${NC}"
echo "  或:       ${GREEN}http://localhost:$PORT/${NC}  （会自动跳转）"
echo
echo "  FunASR:   ws://localhost:10096  （需先 ./bin/funasr.sh start）"
echo
echo "  按 Ctrl+C 停止服务"
echo "============================================================"
echo

cd "$ROOT_DIR/bin"

# 尝试 python3 -> python -> node
if command -v python3 >/dev/null 2>&1; then
  echo "使用: $(python3 --version)"
  PYTHONUNBUFFERED=1 exec python3 -m http.server "$PORT"
elif command -v python >/dev/null 2>&1; then
  echo "使用: $(python --version 2>&1)"
  PYTHONUNBUFFERED=1 exec python -m http.server "$PORT"
elif command -v npx >/dev/null 2>&1; then
  echo "使用: npx serve"
  exec npx --yes serve -l "$PORT" -s . --no-clipboard
else
  err "未找到 python3 / python / npx，请先安装其一"
  exit 1
fi