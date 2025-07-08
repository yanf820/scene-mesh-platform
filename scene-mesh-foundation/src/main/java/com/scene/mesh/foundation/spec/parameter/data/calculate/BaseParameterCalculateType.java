package com.scene.mesh.foundation.spec.parameter.data.calculate;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseParameterCalculateType implements IParameterCalculateType{

    private List<MetaParameterDescriptor> sourceParameterDescriptors;
    private MetaParameterDescriptor associatedParam;

    public BaseParameterCalculateType() {
        this.sourceParameterDescriptors = new ArrayList<>();
    }

    @Override
    public void addSourceParameterDescriptor(MetaParameterDescriptor metaParameterDescriptor) {
        this.sourceParameterDescriptors.add(metaParameterDescriptor);
    }

    @Override
    public List<MetaParameterDescriptor> getSourceParameterDescriptors() {
        return this.sourceParameterDescriptors;
    }

    @Override
    public void setAssociatedParam(MetaParameterDescriptor metaParameterDescriptor) {
        this.associatedParam = metaParameterDescriptor;
    }

    @Override
    public MetaParameterDescriptor getAssociatedParam() {
        return this.associatedParam;
    }
}
