#!/usr/bin/env python3
"""测试 FunASR STT 速度 - 直接连接 WebSocket 发送音频"""
import asyncio
import websockets
import json
import time
import sys

AUDIO_FILE = "/Users/sfan/Desktop/cv/github/OpenMAIC/xiaozhi-esp32-server-java/audio/2026-07-12/a0-f2-62-e4-3a-40/1/2026-07-12T163911-user.wav"
WS_URL = "ws://localhost:10096"

CHUNK_SIZE = 960  # 60ms @ 16kHz 16bit mono

def read_wav_pcm_16k(wav_path):
    import wave
    import struct
    with wave.open(wav_path, 'rb') as wf:
        n_channels = wf.getnchannels()
        sampwidth = wf.getsampwidth()
        framerate = wf.getframerate()
        n_frames = wf.getnframes()
        raw = wf.readframes(n_frames)
    
    samples = list(struct.unpack(f'<{n_frames * n_channels}h', raw))
    if n_channels > 1:
        samples = samples[::n_channels]
    
    if framerate != 16000:
        ratio = framerate / 16000
        new_len = int(len(samples) / ratio)
        new_samples = []
        for i in range(new_len):
            src_idx = int(i * ratio)
            new_samples.append(samples[min(src_idx, len(samples) - 1)])
        samples = new_samples
    
    return struct.pack(f'<{len(samples)}h', *samples)

async def test_stt():
    print(f"连接到 {WS_URL} ...")
    start_time = time.time()
    
    async with websockets.connect(WS_URL) as ws:
        connect_time = time.time()
        print(f"连接建立: {connect_time - start_time:.3f}s")
        
        # 发送开始信号
        start_msg = {
            "mode": "2pass",
            "wav_name": "test",
            "is_speaking": True,
            "wav_format": "pcm",
            "itn": True,
            "chunk_size": [5, 10, 5]
        }
        await ws.send(json.dumps(start_msg))
        
        # 读取并发送音频
        audio_data = read_wav_pcm_16k(AUDIO_FILE)
        print(f"音频长度: {len(audio_data)} bytes, {len(audio_data)/32000:.2f}s (16kHz mono)")
        
        # 分片发送
        sent_start = time.time()
        for i in range(0, len(audio_data), CHUNK_SIZE):
            chunk = audio_data[i:i+CHUNK_SIZE]
            await ws.send(chunk)
            # 模拟实时发送速度
            await asyncio.sleep(len(chunk) / 32000)
        
        sent_time = time.time()
        print(f"音频发送完成: {sent_time - sent_start:.3f}s")
        
        # 发送结束信号
        end_msg = {"is_speaking": False}
        await ws.send(json.dumps(end_msg))
        
        # 接收结果
        last_msg_time = time.time()
        results = []
        try:
            while True:
                msg = await asyncio.wait_for(ws.recv(), timeout=40.0)
                last_msg_time = time.time()
                msg_data = json.loads(msg)
                is_final = msg_data.get("is_final")
                mode = msg_data.get("mode")
                text = msg_data.get("text", "")
                results.append((mode, is_final, text, last_msg_time - sent_time))
                print(f"  [{mode}] is_final={is_final} text='{text}' @{last_msg_time - sent_time:.3f}s")
        except asyncio.TimeoutError:
            print(f"10秒无新消息，测试结束")
        
        total_time = time.time() - sent_time
        print(f"\n--- 统计 ---")
        print(f"收到消息数: {len(results)}")
        print(f"首条结果: {results[0][3]:.3f}s" if results else "无结果")
        print(f"末条结果: {results[-1][3]:.3f}s" if results else "无结果")
        print(f"总耗时: {total_time:.3f}s")

if __name__ == "__main__":
    asyncio.run(test_stt())
