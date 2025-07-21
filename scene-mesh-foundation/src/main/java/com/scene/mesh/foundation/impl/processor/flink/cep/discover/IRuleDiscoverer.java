package com.scene.mesh.foundation.impl.processor.flink.cep.discover;

import org.apache.flink.cep.event.Rule;

import java.io.Serializable;
import java.util.List;

public interface IRuleDiscoverer {

    List<Rule> getRules() throws Exception;

}
