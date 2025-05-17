package com.scene.mesh.foundation.impl.processor.flink;

import com.scene.mesh.foundation.api.component.IComponentProvider;
import com.scene.mesh.foundation.api.processor.config.ProcessorGraph;
import com.scene.mesh.foundation.api.processor.execute.IProcessExecutor;

/**
 * Created by shixin on 2016/11/2.
 */
public class FlinkProcessExecutor implements IProcessExecutor {

    private IComponentProvider componentProvider;

    public FlinkProcessExecutor(IComponentProvider componentProvider) {
        this.componentProvider = componentProvider;
    }

    @Override
    public void execute(ProcessorGraph processorGraph, String... args) throws Exception {
        FlinkProcessActuator actuator = new FlinkProcessActuator(this.componentProvider);
        actuator.initialize(processorGraph, args);
        actuator.launch();
    }
}
