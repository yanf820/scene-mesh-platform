package com.scene.mesh.foundation.impl.component;

import com.scene.mesh.foundation.spec.component.IComponentProvider;

/**
 * 基于类加载的组件提供者
 */
public class ClassInstantiationComponentProvider implements IComponentProvider {

    @Override
    public Object getComponent(String componentId) {

        try {
            Class<?> clz = Class.forName(componentId, true, this.getClass().getClassLoader());
            return clz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
