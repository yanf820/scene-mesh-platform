package com.scene.mesh.foundation.spec.component;

import java.io.Serializable;

/**
 * 组件提供者，用于不同环境的对象加载
 */
public interface IComponentProvider extends Serializable {

    Object getComponent(String componentId);

}
