package com.scene.mesh.foundation.spec.parameter.data.calculate;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;

import java.util.Map;

public interface IParameterCalculator {

    void calculate(Map<String,Object> payload, MetaParameterDescriptor calculatedField);

}
