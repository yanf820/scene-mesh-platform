
package com.scene.mesh.foundation.spec.processor.config;

import java.io.Serializable;
import java.util.Objects;


public class ProcessorNode implements Serializable {

    private static final long serialVersionUID = -6680701868251850100L;
    private String id;
    private String componentId;
    private Class outputType;
    private int parallelism;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public Class getOutputType() {
        return outputType;
    }

    public void setOutputType(Class outputType) {
        this.outputType = outputType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessorNode that = (ProcessorNode) o;

        return Objects.equals(id, that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        String sb = "ProcessorNode{" + "id='" + id + '\'' +
                ", componentId='" + componentId + '\'' +
                ", parallelism=" + parallelism +
                '}';
        return sb;
    }
}