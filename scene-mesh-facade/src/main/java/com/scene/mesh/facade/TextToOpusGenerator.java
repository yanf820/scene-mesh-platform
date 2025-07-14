package com.scene.mesh.facade;

import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import java.util.Base64;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class TextToOpusGenerator {
    
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNELS = 1;
    private static final int FRAME_SIZE = 320; // 20ms @ 16kHz
    private static final int BITRATE = 16000;
    
    public static void main(String[] args) throws Exception {
        
        String text = "最近怎么样？";
        System.out.println("=== 文本转Opus Base64生成器 ===");
        System.out.println("输入文本: " + text);
        System.out.println();
        
        try {
            String base64Result = convertTextToOpusBase64(text);
            System.out.println("=== 生成结果 ===");
            System.out.println("Opus Base64数据:");
            System.out.println(base64Result);
            System.out.println();
            System.out.println("数据长度: " + base64Result.length() + " 字符");
            System.out.println("预估字节大小: " + (base64Result.length() * 3 / 4) + " 字节");
            
            // 生成测试JSON格式
            System.out.println();
            System.out.println("=== 测试用JSON格式 ===");
            String jsonExample = String.format(
                "{\n  \"type\": \"speech_event\",\n  \"payload\": {\n    \"audio\": \"%s\"\n  }\n}",
                base64Result
            );
            System.out.println(jsonExample);
            
        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 将文本转换为Opus Base64格式
     */
    public static String convertTextToOpusBase64(String text) throws Exception {
        // 1. 使用macOS的say命令生成音频文件
        String tempAudioFile = "temp_speech_" + System.currentTimeMillis() + ".aiff";
        String tempPcmFile = "temp_pcm_" + System.currentTimeMillis() + ".wav";
        
        try {
            // 生成AIFF格式音频
            ProcessBuilder sayBuilder = new ProcessBuilder(
                "say", "-o", tempAudioFile, "--file-format=AIFF", text
            );
            Process sayProcess = sayBuilder.start();
            int sayResult = sayProcess.waitFor();
            
            if (sayResult != 0) {
                throw new RuntimeException("say命令执行失败");
            }
            
            // 转换为16kHz PCM WAV格式
            ProcessBuilder ffmpegBuilder = new ProcessBuilder(
                "ffmpeg", "-i", tempAudioFile, 
                "-ar", String.valueOf(SAMPLE_RATE),
                "-ac", String.valueOf(CHANNELS),
                "-f", "wav",
                "-y", tempPcmFile
            );
            Process ffmpegProcess = ffmpegBuilder.start();
            int ffmpegResult = ffmpegProcess.waitFor();
            
            if (ffmpegResult != 0) {
                throw new RuntimeException("FFmpeg转换失败");
            }
            
            // 2. 读取PCM数据
            short[] pcmData = readWavFile(tempPcmFile);
            
            // 3. 编码为Opus并转Base64
            return encodeToOpusBase64(pcmData);
            
        } finally {
            // 清理临时文件
            deleteFileIfExists(tempAudioFile);
            deleteFileIfExists(tempPcmFile);
        }
    }
    
    /**
     * 读取WAV文件的PCM数据
     */
    private static short[] readWavFile(String filename) throws Exception {
        File file = new File(filename);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        
        AudioFormat format = audioInputStream.getFormat();
        System.out.println("音频格式: " + format);
        
        // 读取所有字节
        byte[] audioBytes = audioInputStream.readAllBytes();
        audioInputStream.close();
        
        // 转换为short数组（假设16位PCM）
        short[] pcmData = new short[audioBytes.length / 2];
        for (int i = 0; i < pcmData.length; i++) {
            // 小端序转换
            pcmData[i] = (short) ((audioBytes[i * 2] & 0xFF) | (audioBytes[i * 2 + 1] << 8));
        }
        
        System.out.println("PCM数据长度: " + pcmData.length + " 样本");
        return pcmData;
    }
    
    /**
     * 将PCM数据编码为Opus Base64
     */
    private static String encodeToOpusBase64(short[] pcmData) throws OpusException {
        OpusEncoder encoder = new OpusEncoder(SAMPLE_RATE, CHANNELS, OpusApplication.OPUS_APPLICATION_VOIP);
        encoder.setBitrate(BITRATE);
        
        List<byte[]> opusFrames = new ArrayList<>();
        
        // 按帧编码
        for (int i = 0; i < pcmData.length; i += FRAME_SIZE) {
            // 提取一帧数据
            short[] frame = new short[FRAME_SIZE];
            int actualFrameSize = Math.min(FRAME_SIZE, pcmData.length - i);
            System.arraycopy(pcmData, i, frame, 0, actualFrameSize);
            
            // 不够的用0填充
            if (actualFrameSize < FRAME_SIZE) {
                for (int j = actualFrameSize; j < FRAME_SIZE; j++) {
                    frame[j] = 0;
                }
            }
            
            // 编码为Opus
            byte[] opusBuffer = new byte[1275];
            int opusLength = encoder.encode(frame, 0, FRAME_SIZE, opusBuffer, 0, opusBuffer.length);
            
            // 添加到列表
            byte[] opusFrame = new byte[opusLength];
            System.arraycopy(opusBuffer, 0, opusFrame, 0, opusLength);
            opusFrames.add(opusFrame);
        }
        
        System.out.println("生成了 " + opusFrames.size() + " 个Opus帧");
        
        // 合并所有帧（如果需要单个base64）或返回第一帧
        if (opusFrames.isEmpty()) {
            throw new RuntimeException("没有生成任何Opus帧");
        }
        
        // 生成标准Opus流格式 - 每帧前加2字节长度前缀
        int totalLength = 0;
        for (byte[] frame : opusFrames) {
            totalLength += 2 + frame.length; // 2字节长度前缀 + 帧数据
        }
        
        byte[] completeAudio = new byte[totalLength];
        int offset = 0;
        
        for (byte[] frame : opusFrames) {
            // 添加2字节长度前缀（小端序）
            int frameLength = frame.length;
            completeAudio[offset] = (byte)(frameLength & 0xFF);        // 低字节
            completeAudio[offset + 1] = (byte)((frameLength >> 8) & 0xFF); // 高字节
            offset += 2;
            
            // 添加帧数据
            System.arraycopy(frame, 0, completeAudio, offset, frame.length);
            offset += frame.length;
        }
        
        System.out.println("标准Opus流格式数据:");
        System.out.println("- 帧数: " + opusFrames.size());
        System.out.println("- 总字节数: " + totalLength + " (包含长度前缀)");
        System.out.println("- 平均帧大小: " + (opusFrames.stream().mapToInt(f -> f.length).sum() / opusFrames.size()) + " 字节");
        System.out.println("- 预估音频时长: " + (opusFrames.size() * 20) + " ms");
        System.out.println("- 流格式: 每帧前缀2字节长度信息");
        
        return Base64.getEncoder().encodeToString(completeAudio);
    }
    
    /**
     * 安全删除文件
     */
    private static void deleteFileIfExists(String filename) {
        try {
            Path path = Paths.get(filename);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (Exception e) {
            System.err.println("删除临时文件失败: " + filename + " - " + e.getMessage());
        }
    }
    
    /**
     * 生成多个测试用例
     */
    public static void generateTestCases() throws Exception {
        String[] testTexts = {
            "你好",
            "测试语音识别",
            "朝气蓬勃万物生，扬长铁马任我行",
            "Hello world",
            "This is a test"
        };
        
        System.out.println("=== 批量生成测试用例 ===");
        
        for (int i = 0; i < testTexts.length; i++) {
            System.out.println("\n--- 测试用例 " + (i + 1) + " ---");
            System.out.println("文本: " + testTexts[i]);
            
            try {
                String base64 = convertTextToOpusBase64(testTexts[i]);
                System.out.println("Base64: " + base64.substring(0, Math.min(50, base64.length())) + "...");
                System.out.println("长度: " + base64.length());
            } catch (Exception e) {
                System.out.println("生成失败: " + e.getMessage());
            }
        }
    }
} 