package com.scene.mesh.facade;

import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import java.util.Base64;

public class OpusTestDataGenerator {
    
    public static void main(String[] args) throws OpusException {
        // 配置参数（与您系统一致）
        int sampleRate = 16000;
        int channels = 1;
        int frameSize = 960; // 20ms @ 16kHz
        int bitrate = 16000;
        
        // 创建编码器
        OpusEncoder encoder = new OpusEncoder(sampleRate, channels, OpusApplication.OPUS_APPLICATION_VOIP);
        encoder.setBitrate(bitrate);
        
        // 生成测试音频数据（1秒钟的正弦波，440Hz A音）
        short[] pcmData = generateSineWave(sampleRate, 1.0, 440.0);
        
        System.out.println("=== Opus测试数据生成器 ===");
        System.out.println("配置: " + sampleRate + "Hz, " + channels + "声道, " + bitrate + "bps");
        System.out.println("PCM数据长度: " + pcmData.length + " 样本");
        System.out.println();
        
        // 按帧编码并输出base64
        int frameCount = 0;
        for (int i = 0; i < pcmData.length; i += frameSize) {
            // 提取一帧数据
            short[] frame = new short[frameSize];
            int actualFrameSize = Math.min(frameSize, pcmData.length - i);
            System.arraycopy(pcmData, i, frame, 0, actualFrameSize);
            
            // 如果最后一帧不够，用0填充
            if (actualFrameSize < frameSize) {
                for (int j = actualFrameSize; j < frameSize; j++) {
                    frame[j] = 0;
                }
            }
            
            // 编码为Opus
            byte[] opusBuffer = new byte[1275]; // 最大Opus帧大小
            int opusLength = encoder.encode(frame, 0, frameSize, opusBuffer, 0, opusBuffer.length);
            
            // 创建实际大小的帧
            byte[] opusFrame = new byte[opusLength];
            System.arraycopy(opusBuffer, 0, opusFrame, 0, opusLength);
            
            // 转为base64
            String base64 = Base64.getEncoder().encodeToString(opusFrame);
            
            frameCount++;
            System.out.println("=== 帧 " + frameCount + " ===");
            System.out.println("Opus字节长度: " + opusLength);
            System.out.println("Base64数据: " + base64);
            System.out.println();
            
            // 只输出前5帧作为测试
            if (frameCount >= 5) {
                break;
            }
        }
        
        // 生成一个完整的测试用例（短音频）
        System.out.println("=== 完整测试音频 (200ms) ===");
        short[] shortAudio = generateSineWave(sampleRate, 0.2, 800.0); // 200ms, 800Hz
        
        // 将短音频编码为单个长帧或多个帧
        StringBuilder fullBase64 = new StringBuilder();
        for (int i = 0; i < shortAudio.length; i += frameSize) {
            short[] frame = new short[frameSize];
            int actualFrameSize = Math.min(frameSize, shortAudio.length - i);
            System.arraycopy(shortAudio, i, frame, 0, actualFrameSize);
            
            if (actualFrameSize < frameSize) {
                for (int j = actualFrameSize; j < frameSize; j++) {
                    frame[j] = 0;
                }
            }
            
            byte[] opusBuffer = new byte[1275];
            int opusLength = encoder.encode(frame, 0, frameSize, opusBuffer, 0, opusBuffer.length);
            
            byte[] opusFrame = new byte[opusLength];
            System.arraycopy(opusBuffer, 0, opusFrame, 0, opusLength);
            
            String frameBase64 = Base64.getEncoder().encodeToString(opusFrame);
            if (i == 0) {
                System.out.println("第一帧Base64: " + frameBase64);
                System.out.println("第一帧字节长度: " + opusLength);
            }
        }
        
        System.out.println();
        System.out.println("=== 静音测试帧 ===");
        // 生成静音帧
        short[] silenceFrame = new short[frameSize]; // 默认全0
        byte[] silenceOpusBuffer = new byte[1275];
        int silenceOpusLength = encoder.encode(silenceFrame, 0, frameSize, silenceOpusBuffer, 0, silenceOpusBuffer.length);
        
        byte[] silenceOpusFrame = new byte[silenceOpusLength];
        System.arraycopy(silenceOpusBuffer, 0, silenceOpusFrame, 0, silenceOpusLength);
        
        String silenceBase64 = Base64.getEncoder().encodeToString(silenceOpusFrame);
        System.out.println("静音帧Base64: " + silenceBase64);
        System.out.println("静音帧字节长度: " + silenceOpusLength);
    }
    
    /**
     * 生成正弦波PCM数据
     */
    private static short[] generateSineWave(int sampleRate, double durationSeconds, double frequency) {
        int numSamples = (int) (sampleRate * durationSeconds);
        short[] samples = new short[numSamples];
        
        double amplitude = 8000; // 大约1/4的最大振幅，避免削波
        
        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / sampleRate;
            double value = amplitude * Math.sin(2 * Math.PI * frequency * time);
            samples[i] = (short) Math.round(value);
        }
        
        return samples;
    }
} 