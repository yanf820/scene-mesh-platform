package com.scene.mesh.engin;

import com.scene.mesh.engin.config.EnginConfig;
import com.scene.mesh.engin.config.FoundationConfig;
import com.scene.mesh.engin.config.McpConfig;
import com.scene.mesh.engin.config.ServiceConfig;
import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.OperationResponse;
import com.scene.mesh.engin.model.SceneMatchedResult;
import com.scene.mesh.foundation.spec.processor.config.CepModeDescriptor;
import com.scene.mesh.foundation.spec.processor.config.ProcessorGraph;
import com.scene.mesh.foundation.spec.processor.config.ProcessorGraphBuilder;
import com.scene.mesh.foundation.spec.processor.config.ProcessorNodeBuilder;
import com.scene.mesh.foundation.spec.processor.execute.IProcessManager;
import com.scene.mesh.foundation.impl.component.SpringApplicationContextUtils;
import com.scene.mesh.model.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.typeutils.GenericTypeInfo;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
public class SceneMeshEnginApplication {

    public static void main(String[] args) {

        Class[] configClasses = new Class[]{
                EnginConfig.class,
                FoundationConfig.class,
                McpConfig.class,
                ServiceConfig.class
        };

        SpringApplicationContextUtils.setContextClass(configClasses);

        log.info("执行引擎启动...  加载引擎配置:{}", Arrays.toString(configClasses));

        IProcessManager processManager = SpringApplicationContextUtils
                .getApplicationContextByAnnotation()
                .getBean(IProcessManager.class);

        //注册when graph
        ProcessorGraph whenGraph = whenGraph();
        processManager.registerProcess(whenGraph);
        log.info("Graph: {} 已注册， graph信息: {}", whenGraph.getGraphId(), whenGraph);

        //注册 then graph
        ProcessorGraph thenGraph = thenGraph();
        processManager.registerProcess(thenGraph);
        log.info("Graph: {} 已注册， graph信息: {}", thenGraph.getGraphId(), thenGraph);

        //注册 cacheScheduler graph
        ProcessorGraph cacheScheduler = cacheScheduler();
        processManager.registerProcess(cacheScheduler);
        log.info("Graph: {} 已注册， graph信息: {}", cacheScheduler.getGraphId(), cacheScheduler);

        //startup
        try {
//            processManager.executeProcesses(graphId,new String[0]);
            processManager.executeAllProcesses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ProcessorGraph whenGraph() {

        return ProcessorGraphBuilder.createWithId("when")
                .addNode(ProcessorNodeBuilder.createWithId("scene-event-source")
                        .withComponentId("scene-event-producer")
                        .withParallelism(1)
                        .withOutputType(Event.class))
                .enableCepMode(CepModeDescriptor.builder()
                        .enabled(true)
                        .databaseUrl("jdbc:postgresql://127.0.0.1:5432/postgres")
                        .driverName("org.postgresql.Driver")
                        .ruleSource("cep_rules")
                        .username("postgres")
                        .password("scene_mesh")
                        .period(Duration.ofSeconds(10))
                        .keyed(new String[]{"terminalId"})
                        .parallelism(1)
                        .cepMatchedResultType(new GenericTypeInfo<>(SceneMatchedResult.class)))
                .addNode(ProcessorNodeBuilder.createWithId("scene-match-sink")
                        .withComponentId("scene-match-sinker")
                        .withParallelism(1)
                        .withOutputType(Event.class)
                        .from("scene-event-source")
                )
                .build();
    }

    private static ProcessorGraph thenGraph(){
        return ProcessorGraphBuilder.createWithId("then")
                .addNode(ProcessorNodeBuilder.createWithId("matched-scene-source")
                        .withComponentId("matched-scene-producer")
                        .withParallelism(1)
                        .withOutputType(SceneMatchedResult.class)
                )
                .addNode(ProcessorNodeBuilder.createWithId("scene-handler")
                        .withComponentId("scene-selector")
                        .withParallelism(1)
                        .withOutputType(OperationRequest.class)
                        .from("matched-scene-source")
                )
                .addNode(ProcessorNodeBuilder.createWithId("operation-handler")
                        .withComponentId("operation-handler")
                        .withParallelism(1)
                        .withOutputType(OperationResponse.class)
                        .from("scene-handler")
                )
                .build();
    }

    private static ProcessorGraph cacheScheduler(){
        return ProcessorGraphBuilder.createWithId("cache-scheduler")
                .addNode(ProcessorNodeBuilder.createWithId("trigger-source")
                        .withComponentId("cron-trigger")
                        .withParallelism(1)
                        .withOutputType(String.class)
                )
                .addNode(ProcessorNodeBuilder.createWithId("cache-handler")
                        .withComponentId("cache-processor")
                        .withParallelism(1)
                        .withOutputType(Boolean.class)
                        .from("trigger-source")
                )
                .build();
    }

}
