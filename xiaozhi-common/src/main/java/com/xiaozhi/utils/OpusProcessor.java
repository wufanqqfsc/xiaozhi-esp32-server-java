package com.xiaozhi.utils;

import io.github.jaredmdobson.concentus.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
/**
 * Opus音频处理器
 * 编码、解码，通常是两个过程，只是可能会共享基本设置，例如采样率，频道数，帧大小。
 *
 * <p>重要：解码器与编码器使用不同的采样率：
 * <ul>
 *   <li>解码器 16kHz —— ESP32 麦克风采样率，设备上行 Opus 始终以 16kHz 编码</li>
 *   <li>编码器 24kHz —— ESP32 I2S 输出采样率，TTS 必须以 24kHz Opus 下发才能避免设备端重采样</li>
 * </ul>
 * 二者分离的原因：Silero VAD 只支持 16kHz 输入，但 ESP32 I2S 固定 24kHz 输出。
 */
@Slf4j
public class OpusProcessor {
    private OpusDecoder decoders;
    private OpusEncoder encoders;

    // 残留数据状态缓存
    private final LeftoverState leftoverStates = new LeftoverState();

    // 常量
    private static final int CHANNELS = AudioUtils.CHANNELS;
    public static final int OPUS_FRAME_DURATION_MS = AudioUtils.OPUS_FRAME_DURATION_MS;
    private static final int MAX_SIZE = 1275;

    /**
     * 解码器采样率：ESP32 上行 Opus 始终以 16kHz 编码（设备麦克风 16kHz）。
     * 必须等于 AudioUtils.SAMPLE_RATE，否则 VAD/STT 会处理错误采样率的音频。
     */
    private static final int DECODE_SAMPLE_RATE = AudioUtils.SAMPLE_RATE; // 16000
    /** 解码器帧大小（samples per 60ms frame） */
    private static final int DECODE_FRAME_SIZE = DECODE_SAMPLE_RATE * OPUS_FRAME_DURATION_MS / 1000; // 960

    /**
     * 编码器采样率：TTS 输出到 ESP32 必须 24kHz，以匹配设备 I2S 输出（避免设备日志
     * "Server sample rate X does not match device output sample rate 24000"）。
     * 必须等于 AudioUtils.TTS_OUTPUT_SAMPLE_RATE。
     */
    private static final int ENCODE_SAMPLE_RATE = AudioUtils.TTS_OUTPUT_SAMPLE_RATE; // 24000
    /** 编码器帧大小（samples per 60ms frame）：24kHz * 60ms = 1440 */
    private static final int ENCODE_FRAME_SIZE = ENCODE_SAMPLE_RATE * OPUS_FRAME_DURATION_MS / 1000; // 1440

    public OpusProcessor() {
        this(0, 0);
    }

    /**
     * 带采样率参数的构造器（用于需要不同采样率的应用，例如 AecService 的 AEC 参考解码）。
     *
     * <p>说明：当前实现中，解码器始终使用 AudioUtils.SAMPLE_RATE (16kHz)，编码器始终使用
     * AudioUtils.TTS_OUTPUT_SAMPLE_RATE (24kHz)。这两个采样率通过私有静态常量绑定，因此本构造器的
     * 参数当前仅用于语义标识和未来扩展（如支持独立编码器实例）。
     *
     * @param decodeSampleRate 解码器采样率（解码设备上行 Opus）；当前必须传 0 或 SAMPLE_RATE
     * @param encodeSampleRate 编码器采样率（编码 TTS 输出 Opus）；当前必须传 0 或 TTS_OUTPUT_SAMPLE_RATE
     */
    public OpusProcessor(int decodeSampleRate, int encodeSampleRate) {
        this.decoders = initDecoder();
    }

    /**
     * 残留数据状态类
     */
    public static class LeftoverState {
        public short[] leftoverBuffer;
        public int leftoverCount;
        public boolean isFirst = true;

        public LeftoverState() {
            leftoverBuffer = new short[ENCODE_FRAME_SIZE]; // 1440（24kHz 60ms）
            leftoverCount = 0;
        }

        public void clear() {
            leftoverCount = 0;
            Arrays.fill(leftoverBuffer, (short) 0);
        }
    }

    /**
     * 刷新残留数据，生成最后一帧
     */
    public List<byte[]> flushLeftover() {
        LeftoverState state = leftoverStates;
        List<byte[]> frames = new ArrayList<>();

        if (state.leftoverCount <= 0) {
            return frames;
        }

        OpusEncoder encoder = getOrInitEncoder();

        short[] shortBuf = new short[ENCODE_FRAME_SIZE];
        byte[] opusBuf = new byte[MAX_SIZE];

        System.arraycopy(state.leftoverBuffer, 0, shortBuf, 0, state.leftoverCount);
        Arrays.fill(shortBuf, state.leftoverCount, ENCODE_FRAME_SIZE, (short) 0);

        try {
            int opusLen = encoder.encode(shortBuf, 0, ENCODE_FRAME_SIZE, opusBuf, 0, opusBuf.length);
            if (opusLen > 0) {
                byte[] frame = new byte[opusLen];
                System.arraycopy(opusBuf, 0, frame, 0, opusLen);
                frames.add(frame);
            }
        } catch (OpusException e) {
            log.warn("残留数据编码失败: {}", e.getMessage());
        }

        state.clear();
        return frames;
    }

    /**
     * Opus转PCM字节数组（解码设备上行 Opus 16kHz）
     */
    public byte[] opusToPcm(byte[] data) throws OpusException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }

        try {
            OpusDecoder decoder = decoders;
            short[] buf = new short[DECODE_FRAME_SIZE * 12];
            int samples = decoder.decode(data, 0, data.length, buf, 0, buf.length, false);

            byte[] pcm = new byte[samples * 2];
            for (int i = 0; i < samples; i++) {
                pcm[i * 2] = (byte) (buf[i] & 0xFF);
                pcm[i * 2 + 1] = (byte) ((buf[i] >> 8) & 0xFF);
            }

            return pcm;
        } catch (OpusException e) {
            log.warn("解码失败: {}", e.getMessage());
            decoders = initDecoder();
            throw e;
        }
    }

    /**
     * PCM转Opus（编码 TTS 输出 PCM 24kHz → Opus 24kHz）
     */
    public List<byte[]> pcmToOpus(byte[] pcm, boolean isStream) {
        if (pcm == null || pcm.length == 0) {
            return new ArrayList<>();
        }

        int pcmLen = pcm.length;
        if (pcmLen % 2 != 0) {
            pcmLen--;
        }

        int frameSize = ENCODE_FRAME_SIZE;

        OpusEncoder encoder = getOrInitEncoder();

        List<byte[]> frames = new ArrayList<>();

        LeftoverState state = leftoverStates;

        ByteBuffer pcmBuf = ByteBuffer.wrap(pcm, 0, pcmLen).order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer inputShorts = pcmBuf.asShortBuffer();
        int totalInputSamples = inputShorts.remaining();

        short[] combined;
        short[] shortBuf = new short[frameSize];
        byte[] opusBuf = new byte[MAX_SIZE];

        if (isStream) {
            if (state.leftoverCount > 0 || !state.isFirst) {
                combined = new short[state.leftoverCount + totalInputSamples];
                System.arraycopy(state.leftoverBuffer, 0, combined, 0, state.leftoverCount);
                inputShorts.get(combined, state.leftoverCount, totalInputSamples);
            } else {
                combined = new short[totalInputSamples];
                inputShorts.get(combined);
                state.isFirst = false;
            }
        } else {
            combined = new short[totalInputSamples];
            inputShorts.get(combined);
        }

        int availableSamples = combined.length;
        int frameCount = availableSamples / frameSize;
        int remainingSamples = availableSamples % frameSize;

        if (frameCount > 0 && state.isFirst) {
            System.arraycopy(combined, 0, shortBuf, 0, frameSize);

            int fadeInSamples = Math.min(480, frameSize);
            for (int i = 0; i < fadeInSamples; i++) {
                float gain = (float) i / fadeInSamples;
                shortBuf[i] = (short) (shortBuf[i] * gain);
            }

            try {
                int opusLen = encoder.encode(shortBuf, 0, frameSize, opusBuf, 0, opusBuf.length);
                if (opusLen > 0) {
                    frames.add(Arrays.copyOf(opusBuf, opusLen));
                }
            } catch (Exception | AssertionError e) {
                log.warn("淡入帧编码失败: {}", e.getMessage());
            }

            for (int i = 1; i < frameCount; i++) {
                int start = i * frameSize;
                System.arraycopy(combined, start, shortBuf, 0, frameSize);
                try {
                    int opusLen = encoder.encode(shortBuf, 0, frameSize, opusBuf, 0, opusBuf.length);
                    if (opusLen > 0) {
                        frames.add(Arrays.copyOf(opusBuf, opusLen));
                    }
                } catch (Exception | AssertionError e) {
                    log.warn("帧 #{} 编码失败: {}", i, e.getMessage());
                }
            }
        } else {
            for (int i = 0; i < frameCount; i++) {
                int start = i * frameSize;
                System.arraycopy(combined, start, shortBuf, 0, frameSize);
                try {
                    int opusLen = encoder.encode(shortBuf, 0, frameSize, opusBuf, 0, opusBuf.length);
                    if (opusLen > 0) {
                        frames.add(Arrays.copyOf(opusBuf, opusLen));
                    }
                } catch (Exception | AssertionError e) {
                    log.warn("帧 #{} 编码失败: {}", i, e.getMessage());
                }
            }
        }

        if (isStream) {
            state.leftoverCount = remainingSamples;
            if (remainingSamples > 0) {
                if (state.leftoverBuffer.length < remainingSamples) {
                    state.leftoverBuffer = new short[frameSize];
                }
                System.arraycopy(combined, frameCount * frameSize, state.leftoverBuffer, 0, remainingSamples);
            } else {
                Arrays.fill(state.leftoverBuffer, (short) 0);
            }
        }
        return frames;
    }

    /**
     * 获取解码器（16kHz，用于解码设备上行 Opus）
     */
    private OpusDecoder initDecoder() {
        try {
            OpusDecoder decoder = new OpusDecoder(DECODE_SAMPLE_RATE, CHANNELS);
            decoder.setGain(0);
            return decoder;
        } catch (OpusException e) {
            log.error("创建解码器失败: 采样率={}, 通道={}", DECODE_SAMPLE_RATE, CHANNELS, e);
            throw new RuntimeException("创建解码器失败", e);
        }
    }

    /**
     * 懒初始化编码器（24kHz，用于编码 TTS 输出 Opus）
     */
    private OpusEncoder getOrInitEncoder() {
        if (encoders == null) {
            encoders = initEncoder();
        }
        return encoders;
    }

    /**
     * 创建编码器（24kHz，用于 TTS 输出）
     */
    private OpusEncoder initEncoder() {
        try {
            OpusEncoder encoder = new OpusEncoder(ENCODE_SAMPLE_RATE, CHANNELS, OpusApplication.OPUS_APPLICATION_AUDIO);

            encoder.setBitrate(AudioUtils.BITRATE);
            encoder.setSignalType(OpusSignal.OPUS_SIGNAL_VOICE);
            encoder.setComplexity(10);
            encoder.setUseVBR(true);
            encoder.setPacketLossPercent(0);
            encoder.setForceChannels(CHANNELS);
            encoder.setUseDTX(false);

            return encoder;
        } catch (OpusException e) {
            log.error("创建编码器失败: 采样率={}, 通道={}", ENCODE_SAMPLE_RATE, CHANNELS, e);
            throw new RuntimeException("创建编码器失败", e);
        }
    }

}
