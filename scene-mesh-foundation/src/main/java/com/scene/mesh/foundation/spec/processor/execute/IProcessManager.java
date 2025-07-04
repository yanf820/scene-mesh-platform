
package com.scene.mesh.foundation.spec.processor.execute;

import com.scene.mesh.foundation.spec.processor.config.ProcessorGraph;

import java.util.List;

public interface IProcessManager {
	
    void setExecutor(IProcessExecutor executor);

    IProcessExecutor getExecutor();

    void executeAllProcesses(String... args) throws Exception;

    void executeProcesses(String processId, String... args) throws Exception;

    void registerProcess(ProcessorGraph processorGraph);

    List<ProcessorGraph> getProcesses();
    
}