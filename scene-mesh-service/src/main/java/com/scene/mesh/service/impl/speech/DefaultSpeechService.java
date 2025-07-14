package com.scene.mesh.service.impl.speech;

import com.scene.mesh.model.product.Product;
import com.scene.mesh.model.terminal.Terminal;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.speech.AudioProcessorManager;
import com.scene.mesh.service.spec.speech.IOpusProcessor;
import com.scene.mesh.service.spec.speech.ISpeechService;
import com.scene.mesh.service.spec.speech.ISttProcessor;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import io.github.jaredmdobson.concentus.OpusException;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Slf4j
public class DefaultSpeechService implements ISpeechService {

    private final AudioProcessorManager audioProcessorManager;

    private final IProductService productService;

    private final ITerminalService terminalService;

    public DefaultSpeechService(AudioProcessorManager audioProcessorManager, IProductService productService, ITerminalService terminalService) {
        this.audioProcessorManager = audioProcessorManager;
        this.productService = productService;
        this.terminalService = terminalService;
    }

    @Override
    public String stt(String terminalId, String base64audioStr) {
        // Prepare the necessary tools
        Terminal terminal= this.terminalService.getTerminalWithTerminalId(terminalId);
        if (terminal == null) throw new RuntimeException("Can not find terminal with id " + terminalId);
        Product product = this.productService.getProduct(terminal.getProductId());
        if (product == null) throw new RuntimeException("Can not find product by id " + terminal.getProductId());
        String sttProcessorConfig = product.getSettings().getSttProcessor();
        ISttProcessor sttProcessor = this.audioProcessorManager.getSttProcessor(sttProcessorConfig);
        if (sttProcessor == null) throw new RuntimeException("sttProcessor not configured or cannot find matched processor.");

        // opus 编码音频
        byte[] opusBytes = Base64.getDecoder().decode(base64audioStr);
        byte[] pcmBytes = null;
        
        try {
            // 判断音频数据类型：单帧 vs 完整音频流
            if (isCompleteAudioStream(opusBytes)) {
                log.info("Processing complete audio stream ({} bytes) for terminal: {}", opusBytes.length, terminalId);
                pcmBytes = this.audioProcessorManager.getOpusProcessor().decodeCompleteOpusStreamToPcm(terminalId, opusBytes);
            } else {
                log.info("Processing single audio frame ({} bytes) for terminal: {}", opusBytes.length, terminalId);
                pcmBytes = this.audioProcessorManager.getOpusProcessor().decodeOpusFrameToPcm(terminalId, opusBytes);
            }
        } catch (OpusException e) {
            log.error("Failed to decode Opus audio for terminal: {}", terminalId, e);
            throw new RuntimeException(e);
        }
        
        if (pcmBytes == null || pcmBytes.length == 0) {
            log.warn("No PCM data decoded for terminal: {}", terminalId);
            return null;
        }

        log.info("Starting speech recognition for terminal: {}, PCM data size: {} bytes", terminalId, pcmBytes.length);
        return sttProcessor.recognition(pcmBytes);
    }
    
    /**
     * 判断是否为完整音频流（多帧合并）还是单帧
     * 基于数据大小的启发式判断
     */
    private boolean isCompleteAudioStream(byte[] opusBytes) {
        // 单个Opus帧通常小于150字节
        // 完整音频流通常大于200字节
        int threshold = 150;
        boolean isComplete = opusBytes.length > threshold;
        
        log.debug("Audio data size: {} bytes, threshold: {} bytes, isCompleteStream: {}", 
                 opusBytes.length, threshold, isComplete);
        
        return isComplete;
    }

    @Override
    public String tts(String terminalId, String audioText) {

        return "";
    }
}
