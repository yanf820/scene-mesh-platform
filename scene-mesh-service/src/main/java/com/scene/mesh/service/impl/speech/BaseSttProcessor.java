package com.scene.mesh.service.impl.speech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scene.mesh.foundation.impl.helper.DateHelper;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.service.spec.speech.ISttProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSttProcessor implements ISttProcessor {

    @Override
    public String recognition(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            log.warn("audioData is empty");
            return null;
        }

        long startTime = System.currentTimeMillis(); // 记录开始时间
        String result = this.doRecognition(audioData);
        long endTime = System.currentTimeMillis();
        double duration = DateHelper.calculateDuration(startTime, endTime);
        log.info("stt recognition completed，It took {} seconds", duration);
        try {
            return (String) SimpleObjectHelper.json2map(result).get("text");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String doRecognition(byte[] audioData);
}
