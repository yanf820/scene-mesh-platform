
package com.scene.mesh.foundation.spec.processor;

import com.scene.mesh.foundation.spec.processor.config.ProcessorGraph;

public interface IProcessActuator {

    public void initialize(ProcessorGraph graph, String... args);

    public void launch() throws Exception;

    public void shutdown() throws Exception;

    public boolean isLaunched();

}
