
package com.scene.mesh.foundation.api.processor.execute;


import com.scene.mesh.foundation.api.processor.config.ProcessorGraph;

public interface IProcessExecutor {

	public void execute(ProcessorGraph processorGraph, String... args) throws Exception;
	
}
