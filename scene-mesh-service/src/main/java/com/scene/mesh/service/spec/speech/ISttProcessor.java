package com.scene.mesh.service.spec.speech;

public interface ISttProcessor {

    SttProcessorType processorType();

    String recognition(byte[] audioData);

    enum SttProcessorType{
        VOSK,
    }
}
