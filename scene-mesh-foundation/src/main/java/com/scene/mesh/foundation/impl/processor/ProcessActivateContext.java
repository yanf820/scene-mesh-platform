package com.scene.mesh.foundation.impl.processor;

import com.scene.mesh.foundation.spec.processor.IProcessActivateContext;
/**
 */
public class ProcessActivateContext implements IProcessActivateContext {

    private Class outputType;

    public Class getOutputType() {
        return outputType;
    }

    public void setOutputType(Class outputType) {
        this.outputType = outputType;
    }
}
