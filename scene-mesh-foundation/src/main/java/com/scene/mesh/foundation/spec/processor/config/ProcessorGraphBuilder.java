
package com.scene.mesh.foundation.spec.processor.config;

import java.util.ArrayList;
import java.util.List;


public class ProcessorGraphBuilder {

    public static ProcessorGraphBuilder createWithId(String graphId) {
        ProcessorGraphBuilder gb = new ProcessorGraphBuilder(graphId);
        return gb;
    }

    private final ProcessorGraph graph;
    private final List<ProcessorNodeBuilder> nodeBuilders;

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

}
