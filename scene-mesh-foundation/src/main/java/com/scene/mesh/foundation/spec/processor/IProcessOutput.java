
package com.scene.mesh.foundation.spec.processor;

import com.scene.mesh.foundation.spec.collector.ICollector;

import java.io.Serializable;


public interface IProcessOutput extends Serializable {

    ICollector getCollector();

}

