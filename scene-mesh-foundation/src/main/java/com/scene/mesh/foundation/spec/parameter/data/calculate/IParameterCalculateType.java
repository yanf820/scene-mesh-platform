package com.scene.mesh.foundation.spec.parameter.data.calculate;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IParameterCalculateType extends Serializable {

    List<MetaParameterDescriptor> getSourceParameterDescriptors();

    void addSourceParameterDescriptor(MetaParameterDescriptor metaParameterDescriptor);

    void setAssociatedParam(MetaParameterDescriptor tMetaParameterDescriptor);

    MetaParameterDescriptor getAssociatedParam();

    enum CalculateType{
        TTS,
        STT
    }
}
