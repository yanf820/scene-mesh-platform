package com.scene.mesh.foundation.impl.processor.execute;

import com.scene.mesh.foundation.api.processor.config.ProcessorGraph;
import com.scene.mesh.foundation.api.processor.execute.IProcessExecutor;
import com.scene.mesh.foundation.api.processor.execute.IProcessManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * 默认管理器
 */
@Slf4j
public class DefaultProcessManager implements IProcessManager {

    private IProcessExecutor processExecutor;
    private List<ProcessorGraph> processGraphs;

    public DefaultProcessManager() {
        this.processGraphs = new ArrayList<ProcessorGraph>();
    }

    @Override
    public void setExecutor(IProcessExecutor executor) {
        this.processExecutor = executor;
    }

    @Override
    public IProcessExecutor getExecutor() {
        return this.processExecutor;
    }

    @Override
    public void executeAllProcesses() throws Exception {
        for (ProcessorGraph graph : this.processGraphs) {
            this.processExecutor.execute(graph, null);
            log.info("Graph: {} 已执行", graph.getGraphId());
        }
    }

    @Override
    public void executeProcesses(String processId, String... args) throws Exception {
        if (processId == null) {
            throw new Exception("There is no process with Id[" + processId + "]");
        }
        ProcessorGraph graph = null;
        for (ProcessorGraph g : this.processGraphs) {
            if (processId.equals(g.getGraphId())) {
                graph = g;
                break;
            }
        }
        if (graph == null) {
            throw new Exception("There is no process with Id[" + processId + "]");
        }
        this.processExecutor.execute(graph, args);
    }

    @Override
    public void registerProcess(ProcessorGraph processorGraph) {
        if (processorGraph != null) {
            this.processGraphs.add(processorGraph);
        }
    }

    @Override
    public List<ProcessorGraph> getProcesses() {
        return this.processGraphs;
    }
}
