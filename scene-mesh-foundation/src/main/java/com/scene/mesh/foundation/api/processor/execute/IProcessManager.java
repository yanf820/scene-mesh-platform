
package com.scene.mesh.foundation.api.processor.execute;

import com.scene.mesh.foundation.api.processor.config.ProcessorGraph;

import java.util.List;

public interface IProcessManager {
	
    void setExecutor(IProcessExecutor executor);

    IProcessExecutor getExecutor();

    void executeAllProcesses() throws Exception;

    void executeProcesses(String processId, String... args) throws Exception;

    void registerProcess(ProcessorGraph processorGraph);

    List<ProcessorGraph> getProcesses();
    
}