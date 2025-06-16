package com.scene.mesh.model.llm;

public enum LanguageModelType {

    vision("视觉"),
    tts("语音合成"),
    stt("语音转录");

    private final String name;
    LanguageModelType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
