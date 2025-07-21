package com.scene.mesh.foundation.spec.processor;

import java.io.Serializable;

public interface IProcessInput extends Serializable {

    Object getInputObject();

    boolean hasInputObject();

}
