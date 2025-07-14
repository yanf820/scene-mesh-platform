package com.scene.mesh.service.spec.speech;

import com.scene.mesh.service.impl.speech.DefaultOpusProcessor;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioProcessorManager {

    @Getter
    private final IOpusProcessor opusProcessor;

    private final Map<ISttProcessor.SttProcessorType,ISttProcessor> sttProcessorMap;

    public AudioProcessorManager() {
        this.opusProcessor = new DefaultOpusProcessor();
        this.sttProcessorMap = new ConcurrentHashMap<>();
    }

    public void registerSttProcessor(ISttProcessor sttProcessor){
        this.sttProcessorMap.put(sttProcessor.processorType(),sttProcessor);
    }

    public void unregisterSttProcessor(ISttProcessor sttProcessor){
        this.sttProcessorMap.remove(sttProcessor.processorType());
    }

    public ISttProcessor getSttProcessor(String sttProcessorType){
        ISttProcessor.SttProcessorType processorType = ISttProcessor.SttProcessorType.valueOf(sttProcessorType);
        return this.sttProcessorMap.get(processorType);
    }

}
