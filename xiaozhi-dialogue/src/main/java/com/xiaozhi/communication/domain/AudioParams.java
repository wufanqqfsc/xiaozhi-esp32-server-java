package com.xiaozhi.communication.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AudioParams {
    private int channels;
    private String format;
    private int sampleRate;
    private int frameDuration;

    public static final AudioParams Opus = new AudioParams()
            .setChannels(1)
            .setFormat("opus")
            .setSampleRate(48000)  // Concentus 编码器实际输出 48000Hz，ESP32 会自动重采样到 I2S 输出采样率
            .setFrameDuration(60);
}
