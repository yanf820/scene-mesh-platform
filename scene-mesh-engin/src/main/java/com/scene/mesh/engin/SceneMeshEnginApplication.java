package com.scene.mesh.engin;

import com.scene.mesh.foundation.api.component.IComponentProvider;
import com.scene.mesh.foundation.api.processor.config.ProcessorGraph;
import com.scene.mesh.foundation.api.processor.config.ProcessorGraphBuilder;
import com.scene.mesh.foundation.api.processor.config.ProcessorNodeBuilder;
import com.scene.mesh.foundation.api.processor.execute.IProcessManager;
import com.scene.mesh.foundation.impl.component.SpringApplicationContextUtils;
import com.scene.mesh.foundation.impl.component.SpringComponentProvider;
import com.scene.mesh.foundation.impl.processor.execute.DefaultProcessManager;
import com.scene.mesh.foundation.impl.processor.flink.FlinkProcessExecutor;
import com.scene.mesh.foundation.impl.processor.standalone.StandaloneProcessExecutor;
import com.scene.mesh.model.event.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneMeshEnginApplication {

    public static void main(String[] args) {

        String executorType = System.getProperty("mx.processor.executorType", "");

        SpringApplicationContextUtils.setContextId("engin.xml");

        log.info("执行引擎启动... 执行类型:{}, 执行文件:{}", executorType, "engin.xml");

        IComponentProvider componentProvider = new SpringComponentProvider();

        StandaloneProcessExecutor standaloneProcessExecutor = new StandaloneProcessExecutor(componentProvider);
        FlinkProcessExecutor flinkProcessExecutor = new FlinkProcessExecutor(componentProvider);
        IProcessManager processManager = new DefaultProcessManager();

        if ("flink".equals(executorType)) {
            processManager.setExecutor(flinkProcessExecutor);
        } else {
            processManager.setExecutor(standaloneProcessExecutor);
        }


        //register graph
        ProcessorGraph graph = makeEnginGraph();
        processManager.registerProcess(graph);
        log.info("Graph: {} 已注册， graph信息: {}", graph.getGraphId(), graph.toString());


        //startup
        try {
            processManager.executeAllProcesses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ProcessorGraph makeEnginGraph() {
        return ProcessorGraphBuilder.createWithId("event-process-graph")
                .addNode(ProcessorNodeBuilder.createWithId("source")
                        .withComponentId("event-producer")
                        .withParallelism(1)
                        .withOutputType(Event.class)
                )
//                .addNode(ProcessorNodeBuilder.createWithId("cep-processor")
//                        .withComponentId("")
//                        .withParallelism(2)
//                        .from("event-processor")
//                )
                .build();
    }

}
