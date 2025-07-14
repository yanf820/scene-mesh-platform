package com.scene.mesh.service.impl.speech;

import com.scene.mesh.service.spec.speech.IOpusProcessor;
import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusDecoder;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DefaultOpusProcessor implements IOpusProcessor {

    // 默认的帧大小
    private static final int FRAME_SIZE = 320; // Opus典型帧大小
    
    // 缓存解码器实例 (按终端ID)
    private final ConcurrentHashMap<String, OpusDecoder> decoderCache = new ConcurrentHashMap<>();
    
    // 缓存编码器实例 (按终端ID)
    private final ConcurrentHashMap<String, OpusEncoder> encoderCache = new ConcurrentHashMap<>();
    
    // 默认配置
    private static final int DEFAULT_SAMPLE_RATE = 16000;
    private static final int DEFAULT_CHANNELS = 1;
    private static final int DEFAULT_BITRATE = 16000;

    private OpusDecoder getOrCreateDecoder(String terminalId) throws OpusException {
        return decoderCache.computeIfAbsent(terminalId, key -> {
            try {
                log.debug("Creating new OpusDecoder for terminal: {}", terminalId);
                return new OpusDecoder(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNELS);
            } catch (OpusException e) {
                log.error("Failed to create OpusDecoder for terminal: {}", terminalId, e);
                throw new RuntimeException(e);
            }
        });
    }

    private OpusEncoder getOrCreateEncoder(String terminalId, int sampleRate, int channels) throws OpusException {
        return encoderCache.computeIfAbsent(terminalId, key -> {
            try {
                log.debug("Creating new OpusEncoder for terminal: {} ({}Hz, {} channels)", 
                         terminalId, sampleRate, channels);
                OpusEncoder encoder = new OpusEncoder(sampleRate, channels, OpusApplication.OPUS_APPLICATION_VOIP);
                encoder.setBitrate(DEFAULT_BITRATE);
                return encoder;
            } catch (OpusException e) {
                log.error("Failed to create OpusEncoder for terminal: {} ({}Hz, {} channels)", 
                         terminalId, sampleRate, channels, e);
                throw new RuntimeException(e);
            }
        });
    }

    public byte[] decodeOpusFrameToPcm(String terminalId, byte[] opusData) throws OpusException {
        OpusDecoder decoder = getOrCreateDecoder(terminalId);
        short[] pcmBuffer = new short[FRAME_SIZE];
        int samplesDecoded = decoder.decode(opusData, 0, opusData.length, pcmBuffer, 0, FRAME_SIZE, false);

        // 转换为字节数组
        byte[] pcmBytes = new byte[samplesDecoded * 2];
        for (int i = 0; i < samplesDecoded; i++) {
            pcmBytes[i * 2] = (byte) (pcmBuffer[i] & 0xFF);
            pcmBytes[i * 2 + 1] = (byte) ((pcmBuffer[i] >> 8) & 0xFF);
        }

        return pcmBytes;
    }

    @Override
    public byte[] decodeCompleteOpusStreamToPcm(String terminalId, byte[] opusStreamData) throws OpusException {
        if (opusStreamData == null || opusStreamData.length == 0) {
            log.warn("Opus stream data is empty");
            return new byte[0];
        }

        log.info("开始解码完整Opus流，数据大小: {} 字节", opusStreamData.length);

        OpusDecoder decoder = getOrCreateDecoder(terminalId);
        List<short[]> pcmFrames = new ArrayList<>();
        
        try {
            // 解析带长度前缀的标准Opus流
            int offset = 0;
            int frameCount = 0;
            
            while (offset < opusStreamData.length - 1) {
                // 读取2字节长度前缀（小端序）
                if (offset + 2 > opusStreamData.length) {
                    log.warn("数据不足，无法读取帧长度前缀，剩余字节: {}", opusStreamData.length - offset);
                    break;
                }
                
                int frameLength = (opusStreamData[offset] & 0xFF) | 
                                ((opusStreamData[offset + 1] & 0xFF) << 8);
                offset += 2;
                
                // 验证帧长度合理性
                if (frameLength <= 0 || frameLength > 1275) {
                    log.warn("检测到异常帧长度: {} 字节，跳过", frameLength);
                    break;
                }
                
                // 检查是否有足够的数据
                if (offset + frameLength > opusStreamData.length) {
                    log.warn("数据不足，期望 {} 字节，实际剩余 {} 字节", 
                            frameLength, opusStreamData.length - offset);
                    break;
                }
                
                // 提取帧数据
                byte[] frameData = new byte[frameLength];
                System.arraycopy(opusStreamData, offset, frameData, 0, frameLength);
                offset += frameLength;
                
                // 解码帧
                try {
                                         short[] pcmFrame = new short[FRAME_SIZE];
                     int decodedSamples = decoder.decode(frameData, 0, frameLength, pcmFrame, 0, FRAME_SIZE, false);
                    
                    if (decodedSamples > 0) {
                        // 只保留有效样本
                        short[] validPcm = new short[decodedSamples];
                        System.arraycopy(pcmFrame, 0, validPcm, 0, decodedSamples);
                        pcmFrames.add(validPcm);
                        frameCount++;
                    } else {
                        log.warn("第 {} 帧解码失败，返回样本数: {}", frameCount + 1, decodedSamples);
                    }
                    
                } catch (Exception e) {
                    log.warn("第 {} 帧解码异常: {}", frameCount + 1, e.getMessage());
                    // 继续处理下一帧
                }
            }
            
            log.info("标准Opus流解码完成:");
            log.info("- 成功解码帧数: {}", frameCount);
            log.info("- 总样本数: {}", pcmFrames.stream().mapToInt(f -> f.length).sum());
            
            if (pcmFrames.isEmpty()) {
                log.warn("没有成功解码任何帧");
                return new byte[0];
            }
            
            // 合并所有PCM帧
            int totalSamples = pcmFrames.stream().mapToInt(f -> f.length).sum();
            byte[] pcmData = new byte[totalSamples * 2]; // 16位PCM，每样本2字节
            int pcmOffset = 0;
            
            for (short[] frame : pcmFrames) {
                for (short sample : frame) {
                    // 转换为小端序字节
                    pcmData[pcmOffset++] = (byte)(sample & 0xFF);
                    pcmData[pcmOffset++] = (byte)((sample >> 8) & 0xFF);
                }
            }
            
            log.info("PCM数据生成完成，总大小: {} 字节", pcmData.length);
            return pcmData;
            
        } catch (Exception e) {
            log.error("解码完整Opus流时发生异常", e);
            return new byte[0];
        }
    }
    
    /**
     * 智能分割合并的Opus帧数据
     * 改进的分割算法，基于Opus帧结构和统计分析
     */


    public List<byte[]> encodePcmToOpus(String terminalId, byte[] pcmData, int sampleRate, int channels, int frameDurationMs)
            throws OpusException {
        // 使用按会话缓存的编码器
        OpusEncoder encoder = getOrCreateEncoder(terminalId, sampleRate, channels);

        // 每帧样本数
        int frameSize = sampleRate * frameDurationMs / 1000;

        // 处理PCM数据
        List<byte[]> opusFrames = new ArrayList<>();
        short[] shortBuffer = new short[frameSize * channels];

        for (int i = 0; i < pcmData.length / 2; i += frameSize * channels) {
            // 将字节数据转换为short
            for (int j = 0; j < frameSize * channels && (i + j) < pcmData.length / 2; j++) {
                int byteIndex = (i + j) * 2;
                if (byteIndex + 1 < pcmData.length) {
                    shortBuffer[j] = (short) ((pcmData[byteIndex] & 0xFF) | (pcmData[byteIndex + 1] << 8));
                }
            }

            // 编码
            byte[] opusBuffer = new byte[1275]; // 最大Opus帧大小
            int opusLength = encoder.encode(shortBuffer, 0, frameSize, opusBuffer, 0, opusBuffer.length);

            // 创建正确大小的帧并添加到列表
            byte[] opusFrame = new byte[opusLength];
            System.arraycopy(opusBuffer, 0, opusFrame, 0, opusLength);
            opusFrames.add(opusFrame);
        }

        return opusFrames;
    }

    public void cleanupOpusCoderForTerminal(String terminalId) {
        OpusDecoder decoder = decoderCache.remove(terminalId);
        OpusEncoder encoder = encoderCache.remove(terminalId);
        
        if (decoder != null || encoder != null) {
            log.debug("Cleaned up OpusDecoder and OpusEncoder for terminal: {}", terminalId);
        }
    }

    public void cleanupAllOpusCoders() {
        int decoderCount = decoderCache.size();
        int encoderCount = encoderCache.size();
        
        decoderCache.clear();
        encoderCache.clear();
        
        log.info("Cleaned up {} decoders and {} encoders", decoderCount, encoderCount);
    }

    public String getCacheStatus() {
        return String.format("Decoders: %d, Encoders: %d", 
                decoderCache.size(), encoderCache.size());
    }

}