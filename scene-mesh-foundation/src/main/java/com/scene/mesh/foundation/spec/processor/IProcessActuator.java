
package com.scene.mesh.foundation.spec.processor;

import com.scene.mesh.foundation.spec.processor.config.ProcessorGraph;

public interface IProcessActuator {

    void initialize(ProcessorGraph graph, String... args);

    void launch() throws Exception;

    void shutdown() throws Exception;

    boolean isLaunched();

}
