package com.scene.mesh.service.spec.speech;

public interface ISpeechService {

    String stt(String base64audioStr);

    String tts(String audioText);
}
