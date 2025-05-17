package com.scene.mesh.foundation.api.component;

import java.io.Serializable;

/**
 * 组件提供者，用于不同环境的对象加载
 */
public interface IComponentProvider extends Serializable {

    public Object getComponent(String componentId);

}
