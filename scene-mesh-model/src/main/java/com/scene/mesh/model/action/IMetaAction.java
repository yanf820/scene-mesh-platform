package com.scene.mesh.model.action;

import com.scene.mesh.foundation.spec.parameter.IMetaParameterized;
import com.scene.mesh.foundation.spec.parameter.MetaParameters;

import java.io.Serializable;

public interface IMetaAction extends IMetaParameterized, Serializable {

    /**
     * 校验 json 是否符合 MetaAction 规范
     * @return
     */
    boolean validate(MetaParameters jsonData);
}
