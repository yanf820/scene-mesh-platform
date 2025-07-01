package com.scene.mesh.foundation.impl.processor.flink;

import com.scene.mesh.foundation.spec.processor.config.ProcessorNode;

/**
 */
public interface IFlinkProcessorAgent<T> {

    boolean isAsProducer();

    ProcessorNode getProcessorNode();

    Class<T> getOutputType();

    void open() throws Exception;
}
