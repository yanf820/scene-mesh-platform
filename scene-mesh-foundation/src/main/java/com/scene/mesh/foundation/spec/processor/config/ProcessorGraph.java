
package com.scene.mesh.foundation.spec.processor.config;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ProcessorGraph implements Serializable {

    private static final long serialVersionUID = -7070767373645169755L;
    private String graphId;
    private List<ProcessorNode> nodes;
    private List<ProcessorLinker> linkers;

    @Getter
    @Setter
    // cep mode 描述
    private CepModeDescriptor cepModeDescriptor;

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public List<ProcessorNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ProcessorNode> nodes) {
        this.nodes = nodes;
    }

    public List<ProcessorLinker> getLinkers() {
        return linkers;
    }

    public void setLinkers(List<ProcessorLinker> linkers) {
        this.linkers = linkers;
    }

    public boolean isProducerNode(String nodeId) {
        for(ProcessorLinker linker : this.getLinkers()){
            if(nodeId.equals(linker.getToNodeId())){
                return false;
            }
        }
        return true;
    }

    public boolean isEnableCepMode(){
        return this.cepModeDescriptor!=null && this.cepModeDescriptor.isEnabled();
    }

    public List<ProcessorNode> getProducerNodes() {
        List<ProcessorNode> ns = new ArrayList<ProcessorNode>();
        for(ProcessorNode pn : this.nodes){
            if(isProducerNode(pn.getId())) ns.add(pn);
        }
        return ns;
    }

    public ProcessorNode findNodeById(String nodeId) {
        if(nodeId == null) return null;
        for(ProcessorNode pn : this.nodes) {
            if(nodeId.equals(pn.getId())){
                return pn;
            }
        }
        return null;
    }

    public List<ProcessorLinker> findLinkersByNodeId(String nodeId, boolean asFromNode) {
        List<ProcessorLinker> ls = new ArrayList<ProcessorLinker>();
        if(nodeId != null) {
            for(ProcessorLinker pl : this.linkers) {
                if(asFromNode?nodeId.equals(pl.getFromNodeId()):nodeId.equals(pl.getToNodeId())) {
                    ls.add(pl);
                }
            }
        }
        return ls;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProcessorGraph{");
        sb.append("graphId='").append(graphId).append('\'');
        sb.append(", nodes=").append(nodes);
        sb.append(", linkers=").append(linkers);
        sb.append('}');
        return sb.toString();
    }
}