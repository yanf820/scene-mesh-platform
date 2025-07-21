package com.scene.mesh.foundation.spec.processor;

import java.io.Serializable;

public interface IProcessor extends Serializable {

    void activate(IProcessActivateContext activateContext) throws Exception;

    void deactivate() throws Exception;

    void process(IProcessInput input, IProcessOutput output) throws Exception;

}