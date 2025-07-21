package com.scene.mesh.foundation.impl.processor.standalone;

import com.scene.mesh.foundation.spec.component.IComponentProvider;
import com.scene.mesh.foundation.spec.processor.config.ProcessorGraph;
import com.scene.mesh.foundation.spec.processor.execute.IProcessExecutor;

/**
 */
public class StandaloneProcessExecutor implements IProcessExecutor {

    private final IComponentProvider componentProvider;

    public StandaloneProcessExecutor(IComponentProvider componentProvider) {
        this.componentProvider = componentProvider;
    }

    @Override
    public void execute(ProcessorGraph processGraph, String... args) throws Exception {
        StandaloneProcessActuator actuator = new StandaloneProcessActuator(this.componentProvider);
        actuator.setMaxPending(100000);
        actuator.initialize(processGraph, args);
        actuator.launch();
    }
}
