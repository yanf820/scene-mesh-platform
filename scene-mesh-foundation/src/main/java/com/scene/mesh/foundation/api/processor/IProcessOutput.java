
package com.scene.mesh.foundation.api.processor;

import com.scene.mesh.foundation.api.collector.ICollector;

import java.io.Serializable;


public interface IProcessOutput extends Serializable {

    ICollector getCollector();

}

