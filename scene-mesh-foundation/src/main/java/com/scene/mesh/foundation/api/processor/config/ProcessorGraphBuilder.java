
package com.scene.mesh.foundation.api.processor.config;

import java.util.ArrayList;
import java.util.List;


public class ProcessorGraphBuilder {

    public static ProcessorGraphBuilder createWithId(String graphId) {
        ProcessorGraphBuilder gb = new ProcessorGraphBuilder(graphId);
        return gb;
    }

    private ProcessorGraph graph;
    private List<ProcessorNodeBuilder> nodeBuilders;

    public ProcessorGraphBuilder(String graphId) {
        this.graph = new ProcessorGraph();
        this.graph.setGraphId(graphId);
        this.nodeBuilders = new ArrayList<>();
    }

    public ProcessorGraphBuilder addNode(ProcessorNodeBuilder nodeBuilder) {
        this.nodeBuilders.add(nodeBuilder);
        return this;
    }

    public ProcessorGraphBuilder enableCepMode(CepModeDescriptor.CepModeDescriptorBuilder cepModeBuilder) {
        this.graph.setCepModeDescriptor(cepModeBuilder.build());
        return this;
    }

    public ProcessorGraph build() {

        List<ProcessorNode> nodes = new ArrayList<>();
        List<ProcessorLinker> linkers = new ArrayList<>();
        for(ProcessorNodeBuilder nb : this.nodeBuilders) {
            ProcessorNode node = nb.getProcessorNode();
            List<ProcessorLinker> ls = nb.getProcessorLinkers();
            if(!nodes.contains(node)) nodes.add(node);
            for(ProcessorLinker pl : ls){
                if(!linkers.contains(pl)) linkers.add(pl);
            }
        }

        this.graph.setLinkers(linkers);
        this.graph.setNodes(nodes);
        return this.graph;
    }

    public static void main(String[] args) {
        ProcessorGraph graph = ProcessorGraphBuilder.createWithId("transformer")
                .addNode(ProcessorNodeBuilder.createWithId("input")
                        .withComponentId("comp-input")
                        .withParallelism(1)
                )
                .addNode(ProcessorNodeBuilder.createWithId("ouput")
                        .withComponentId("comp-output")
                        .withParallelism(1)
                        .from("input")
                ).build();

        System.out.println(">> " + graph);

    }

}
