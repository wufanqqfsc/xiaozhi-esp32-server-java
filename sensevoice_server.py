#!/usr/bin/env python3
"""
SenseVoice Small Int8 WebSocket 服务
纯 CPU 推理，适用于本地智能助手
"""
import asyncio
import json
import base64
import io
import wave
import struct
import re
import os
import signal
import sys

os.environ["FUNASR_DISABLE_UPDATE"] = "1"
os.environ["MODELSCOPE_SDK_DEBUG"] = "0"
os.environ["PYTHONUNBUFFERED"] = "1"

# 原始 print 替换为自动 flush 版本
_original_print = print
def print(*args, **kwargs):
    kwargs['flush'] = True
    _original_print(*args, **kwargs)

from funasr import AutoModel

print("=" * 50)
print("SenseVoice Small WebSocket 服务 (CPU)")
print("=" * 50)

MODEL_NAME = "/workspace/models/SenseVoiceSmall"
SERVER_PORT = 10095

class SenseVoiceServer:
    def __init__(self):
        print(f"[初始化] 加载模型: {MODEL_NAME}")
        print("[初始化] 设备: CPU")
        print("[初始化] 量化: int8")

        self.model = AutoModel(
            model=MODEL_NAME,
            device="cpu",
            disable_pbar=True,
            disable_log=True,
            disable_update=True,
            ncpu=4,
        )
        print("[初始化] 模型加载完成!")

    def clean_text(self, text):
        """清理 SenseVoice 特殊标签"""
        if not text:
            return text
        text = re.sub(r'<\|[^|]*\|>', '', text)
        return text.strip()

    def process_audio_bytes(self, audio_bytes, sample_rate=16000):
        """处理音频字节流"""
        import tempfile
        import os as _os
        try:
            tmp_path = _os.path.join(_os.environ.get('TMPDIR', '/tmp'), 'sensevoice_input.wav')
            with wave.open(tmp_path, 'wb') as wf:
                wf.setnchannels(1)
                wf.setsampwidth(2)
                wf.setframerate(sample_rate)
                wf.writeframes(audio_bytes)

            res = self.model.generate(
                input=tmp_path,
                batch_size_s=300,
                merge_vad=True,
                merge_length_s=15,
                use_itn=True,
            )

            if res and len(res) > 0:
                result = res[0]
                text = result.get("text", "")
                text = self.clean_text(text)

                return {
                    "text": text,
                    "text_with_punc": text,
                    "emotion": result.get("emotion", "neutral"),
                    "event": result.get("event", ""),
                }
            return None
        except Exception as e:
            import traceback
            print(f"[错误] 处理音频失败: {e}")
            traceback.print_exc()
            return None

print("[启动] 正在启动 WebSocket 服务...")

server_instance = None

async def handle_client(websocket):
    """处理客户端连接"""
    global server_instance
    client_ip = websocket.remote_address
    print(f"[连接] 客户端: {client_ip}")

    audio_chunks = []
    speaking = False
    wav_buffer = io.BytesIO()

    try:
        async for message in websocket:
            if isinstance(message, str):
                try:
                    data = json.loads(message)
                except json.JSONDecodeError:
                    continue

                mode = data.get("mode", "")
                is_speaking = data.get("is_speaking")

                if mode == "offline" or is_speaking == True:
                    speaking = True
                    audio_chunks = []
                    wav_buffer = io.BytesIO()
                    wav_buffer.write(b'RIFF')
                    wav_buffer.write(struct.pack('<I', 0))
                    wav_buffer.write(b'WAVE')
                    wav_buffer.write(b'fmt ')
                    wav_buffer.write(struct.pack('<I', 16))
                    wav_buffer.write(struct.pack('<H', 1))
                    wav_buffer.write(struct.pack('<H', 1))
                    wav_buffer.write(struct.pack('<I', 16000))
                    wav_buffer.write(struct.pack('<I', 32000))
                    wav_buffer.write(struct.pack('<H', 2))
                    wav_buffer.write(struct.pack('<H', 16))
                    wav_buffer.write(b'data')
                    wav_buffer.write(struct.pack('<I', 0))
                    print(f"[开始] 开始录音...")

                elif is_speaking == False and speaking:
                    speaking = False
                    wav_buffer.seek(0)
                    wav_bytes = wav_buffer.getvalue()

                    if len(audio_chunks) > 0:
                        print(f"[处理] 音频数据: {len(audio_chunks)} 块")
                        result = server_instance.process_audio_bytes(b''.join(audio_chunks))

                        if result and result["text"]:
                            response = {
                                "mode": "offline",
                                "is_final": True,
                                "text": result["text"],
                                "emotion": result.get("emotion", "neutral"),
                            }
                            await websocket.send(json.dumps(response, ensure_ascii=False))
                            print(f"[识别] {result['text']}")
                        else:
                            print("[识别] 无识别结果")
                    else:
                        print("[识别] 无音频数据")

            elif speaking:
                audio_chunks.append(message)
                wav_buffer.write(message)

    except websockets.exceptions.ConnectionClosed:
        print(f"[断开] 客户端断开: {client_ip}")
    except Exception as e:
        print(f"[错误] {e}")

async def main():
    global server_instance

    print("[启动] 初始化模型...")
    server_instance = SenseVoiceServer()

    print(f"[启动] 启动 WebSocket 服务: ws://0.0.0.0:{SERVER_PORT}")
    print("-" * 50)

    stop_event = asyncio.Event()

    def signal_handler(sig, frame):
        print("\n[退出] 收到停止信号...")
        stop_event.set()

    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    try:
        async with websockets.serve(
            handle_client,
            "0.0.0.0",
            SERVER_PORT,
            ping_interval=None,
            ping_timeout=None,
        ):
            print("[就绪] 服务已启动，按 Ctrl+C 停止")
            await stop_event.wait()
    except Exception as e:
        print(f"[错误] 服务异常: {e}")

if __name__ == "__main__":
    import websockets
    asyncio.run(main())
