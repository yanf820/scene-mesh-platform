package com.scene.mesh.service.spec.speech;

import io.github.jaredmdobson.concentus.OpusException;

import java.util.List;

public interface IOpusProcessor {

    byte[] decodeOpusFrameToPcm(String terminalId, byte[] opusData) throws OpusException;

    /**
     * 解码完整的音频流（多帧合并的Opus数据）为PCM
     * @param terminalId 终端ID
     * @param completeOpusData 完整的Opus音频数据（多帧合并）
     * @return 完整的PCM音频数据
     */
    byte[] decodeCompleteOpusStreamToPcm(String terminalId, byte[] completeOpusData) throws OpusException;

    List<byte[]> encodePcmToOpus(String terminalId, byte[] pcmData, int sampleRate, int channels, int frameDurationMs)
            throws OpusException;

    void cleanupOpusCoderForTerminal(String terminalId);

    void cleanupAllOpusCoders();

    String getCacheStatus();
}
