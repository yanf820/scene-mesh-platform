package com.scene.mesh.foundation.impl.processor.execute;

import com.scene.mesh.foundation.api.processor.config.ProcessorGraph;
import com.scene.mesh.foundation.api.processor.execute.IProcessExecutor;
import com.scene.mesh.foundation.api.processor.execute.IProcessManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 默认管理器
 */
@Slf4j
public class DefaultProcessManager implements IProcessManager {

    private IProcessExecutor processExecutor;
    private List<ProcessorGraph> processGraphs;
    private final ExecutorService executorService;

    public DefaultProcessManager() {
        this.processGraphs = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void setExecutor(IProcessExecutor executor) {
        this.processExecutor = executor;
    }

    @Override
    public IProcessExecutor getExecutor() {
        return this.processExecutor;
    }

//    @Override
//    public void executeAllProcesses(String... args) throws Exception {
//        for (ProcessorGraph graph : this.processGraphs) {
//            this.processExecutor.execute(graph, args);
//            log.info("Graph: {} 已执行", graph.getGraphId());
//        }
//    }

    @Override
    public void executeAllProcesses(String... args) throws Exception {
        for (ProcessorGraph graph : this.processGraphs) {
            Runnable graphProcessTask = () -> {
                try {
                    processExecutor.execute(graph, args);
                    log.info("Graph: {} 已成功异步执行", graph.getGraphId());
                } catch (Exception e) {
                    log.error("Graph: {} 异步执行失败", graph.getGraphId(), e);
                    // 在异步任务中，通常需要将检查型异常包装成运行时异常抛出
                    throw new RuntimeException("异步执行 " + graph.getGraphId() + " 时出错", e);
                }
            };
            CompletableFuture.runAsync(graphProcessTask, this.executorService);
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
