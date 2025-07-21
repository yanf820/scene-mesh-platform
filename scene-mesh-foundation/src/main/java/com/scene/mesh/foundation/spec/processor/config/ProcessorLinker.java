
package com.scene.mesh.foundation.spec.processor.config;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;


public class ProcessorLinker implements Serializable {

    private static final long serialVersionUID = 8197756220966679186L;
    private String fromNodeId;
    private String toNodeId;
    private String linkType;
    private Map<String, String> linkParameters;

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public Map<String, String> getLinkParameters() {
        return linkParameters;
    }

    public void setLinkParameters(Map<String, String> linkParameters) {
        this.linkParameters = linkParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessorLinker that = (ProcessorLinker) o;

        if (!Objects.equals(fromNodeId, that.fromNodeId)) return false;
        return Objects.equals(toNodeId, that.toNodeId);

    }

    @Override
    public int hashCode() {
        int result = fromNodeId != null ? fromNodeId.hashCode() : 0;
        result = 31 * result + (toNodeId != null ? toNodeId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String sb = "ProcessorLinker{" + "fromNodeId='" + fromNodeId + '\'' +
                ", toNodeId='" + toNodeId + '\'' +
                ", linkType='" + linkType + '\'' +
                ", linkParameters=" + linkParameters +
                '}';
        return sb;
    }
}