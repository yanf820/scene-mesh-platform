package com.scene.mesh.foundation.impl.processor.flink;

import com.scene.mesh.foundation.api.processor.config.ProcessorNode;
import org.apache.flink.cep.CEP;
import org.apache.flink.configuration.Configuration;

/**
 */
public interface IFlinkProcessorAgent<T> {

    boolean isAsProducer();

    ProcessorNode getProcessorNode();

    Class<T> getOutputType();

    void open() throws Exception;
}
