package com.scene.mesh.service.spec.speech;

public interface ISpeechService {

    /**
     * opus base64 audio -> text
     * @param base64audioStr
     * @return
     */
    String stt(String terminalId, String base64audioStr);

    /**
     * opus text -> opus base64
     * @param audioText
     * @return
     */
    String tts(String terminalId, String audioText);
}
