package com.scene.mesh.service.impl.speech;

import com.scene.mesh.service.spec.speech.ISttProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.ByteArrayInputStream;

@Slf4j
public class VoskSttProcessor extends BaseSttProcessor {

    private final Model model;

    public VoskSttProcessor(Model model) {
        this.model = model;
    }

    @Override
    protected String doRecognition(byte[] audioData) {

        try (Recognizer recognizer = new Recognizer(model, 16000)) { // 16000 是采样率
            ByteArrayInputStream audioStream = new ByteArrayInputStream(audioData);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = audioStream.read(buffer)) != -1) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    // 如果识别到完整的结果
                    return recognizer.getResult();
                }
            }

            // 返回最终的识别结果
            return recognizer.getFinalResult();

        } catch (Exception e) {
            log.error("处理音频时发生错误！", e);
            return null;
        }
    }

    @Override
    public SttProcessorType processorType() {
        return SttProcessorType.VOSK;
    }
}
