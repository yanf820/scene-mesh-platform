package com.scene.mesh.foundation.spec.parameter.data.calculate;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;

import java.util.Map;

public interface IParameterCalculator {

    CalculateType getCalculateType();

    void calculate(String terminalId, Map<String,Object> payload, MetaParameterDescriptor calculatedField);

    enum CalculateType{
        TTS,
        STT
    }
}
