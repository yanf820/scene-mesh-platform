package com.scene.mesh.foundation.spec.parameter.data.calculate;

public interface IParameterCalculatorManager {

    void registerParameterCalculator(IParameterCalculator parameterCalculator);

    void unregisterParameterCalculator(IParameterCalculator parameterCalculator);

    IParameterCalculator getParameterCalculator(IParameterCalculator.CalculateType calculateType);
}
