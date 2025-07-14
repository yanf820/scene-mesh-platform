package com.scene.mesh.service.impl.event;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.calculate.BaseParameterCalculator;
import com.scene.mesh.service.spec.speech.ISpeechService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SttParameterCalculator extends BaseParameterCalculator {

    private final ISpeechService speechService;

    public SttParameterCalculator(ISpeechService speechService) {
        this.speechService = speechService;
    }

    @Override
    public CalculateType getCalculateType() {
        return CalculateType.STT;
    }

    @Override
    public void calculate(String terminalId, Map<String,Object> payload, MetaParameterDescriptor calculatedField) {
        if (payload.get("audio") == null || !(payload.get("audio") instanceof String)) {
            throw new RuntimeException("Can not find calculate audio parameter in payload.");
        }
        String base64Audio = (String) payload.get("audio");

        String text = this.speechService.stt(terminalId, base64Audio);

        payload.put(calculatedField.getName(), text);
    }
}
