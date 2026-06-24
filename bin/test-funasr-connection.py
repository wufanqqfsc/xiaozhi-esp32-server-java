#!/usr/bin/env python3
"""
FunASR WebSocket 客户端测试脚本
测试 ws://localhost:10096 服务可用性
"""
import asyncio
import json
import sys
import websockets


async def test_connection():
    """测试 WebSocket 连接"""
    uri = "ws://localhost:10096"
    print(f"==> 连接到 {uri}")

    try:
        async with websockets.connect(uri, ping_interval=None) as ws:
            # 发送 2pass 模式配置
            config = {
                "mode": "2pass",
                "chunk_size": [5, 10, 5],
                "audio_fs": 16000,
                "wav_name": "test",
                "is_speaking": True,
                "wav_format": "pcm"
            }
            await ws.send(json.dumps(config))
            print("==> 发送配置成功")

            # 等待服务器响应
            print("==> 等待服务器响应（3秒）...")
            try:
                response = await asyncio.wait_for(ws.recv(), timeout=3.0)
                print(f"==> 收到响应: {response[:200]}")
                return True
            except asyncio.TimeoutError:
                print("⚠️  等待响应超时（3秒）")
                # 超时也可能是正常的，因为没发送音频数据
                return True

    except Exception as e:
        print(f"❌ 连接失败: {e}")
        return False


async def main():
    print("=" * 50)
    print("FunASR WebSocket 连接测试")
    print("=" * 50)

    success = await test_connection()

    print()
    if success:
        print("✅ FunASR 服务可用！")
        print()
        print("下一步：")
        print("  1. 在 xiaozhi 后台配置 STT: provider=funasr, apiUrl=ws://localhost:10096")
        print("  2. 重启 xiaozhi 对话服务")
        print("  3. 通过 ESP32 设备进行实时语音对话测试")
    else:
        print("❌ FunASR 服务不可用")
        print()
        print("排查步骤：")
        print("  1. 检查容器状态: docker ps -a | grep funasr")
        print("  2. 查看日志: docker logs xiaozhi-funasr")
        print("  3. 检查端口: lsof -iTCP:10096")

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    asyncio.run(main())
