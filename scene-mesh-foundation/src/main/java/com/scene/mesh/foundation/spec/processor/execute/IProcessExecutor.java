
package com.scene.mesh.foundation.spec.processor.execute;


import com.scene.mesh.foundation.spec.processor.config.ProcessorGraph;

public interface IProcessExecutor {

	void execute(ProcessorGraph processorGraph, String... args) throws Exception;
}
