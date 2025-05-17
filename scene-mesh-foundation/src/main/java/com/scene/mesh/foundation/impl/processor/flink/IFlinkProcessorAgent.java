package com.scene.mesh.foundation.impl.processor.flink;

import com.scene.mesh.foundation.api.processor.config.ProcessorNode;
import org.apache.flink.cep.CEP;
import org.apache.flink.configuration.Configuration;

/**
 */
public interface IFlinkProcessorAgent {

    boolean isAsProducer();

    ProcessorNode getProcessorNode();

    Class getOutputType();

    void open(Configuration parameters) throws Exception;
}
