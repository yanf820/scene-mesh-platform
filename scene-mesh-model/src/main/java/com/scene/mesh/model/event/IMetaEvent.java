package com.scene.mesh.model.event;

import com.scene.mesh.foundation.spec.parameter.IMetaParameterized;

import java.io.Serializable;
import java.util.Map;

/**
 * 事件元模型
 */
public interface IMetaEvent extends IMetaParameterized, Serializable {

    /**
     * 校验 json 是否符合 MetaEvent 规范
     * @return
     */
    boolean validate(Map<String, Object> jsonData);
}
