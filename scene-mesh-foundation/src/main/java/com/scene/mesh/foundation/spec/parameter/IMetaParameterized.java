
package com.scene.mesh.foundation.spec.parameter;

/**
 * 元模型
 */
public interface IMetaParameterized {

    String getUuid();

    String getName();

    String getDescription();

    MetaParameterDescriptorCollection getParameterCollection();

    void addParameterDescriptor(MetaParameterDescriptor parameterDescriptor);
}
