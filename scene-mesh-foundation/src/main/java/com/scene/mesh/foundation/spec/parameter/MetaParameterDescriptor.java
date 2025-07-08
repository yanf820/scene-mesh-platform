
package com.scene.mesh.foundation.spec.parameter;

import com.scene.mesh.foundation.spec.parameter.data.IParameterDataType;
import com.scene.mesh.foundation.spec.parameter.data.calculate.IParameterCalculateType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 参数描述符
 */
public class MetaParameterDescriptor<T> implements Serializable {

    @Getter
    private String name;
    @Getter
    private String title;
    @Getter
    private String description;
    @Getter
    private IParameterDataType<T> dataType;
    @Getter
    @Setter
    private IParameterCalculateType calculateType;
    @Getter
    @Setter
    private boolean asInput;
    @Getter
    private boolean required;

    public MetaParameterDescriptor() {
    }

    public MetaParameterDescriptor(String name, String title, String description, IParameterDataType dataType, boolean
            required) {
        super();
        this.name = name;
        this.title = title;
        this.description = description;
        this.required = required;
        this.dataType = dataType;
    }

    public MetaParameterDescriptor(String name, String title, String description, IParameterDataType dataType,IParameterCalculateType calculateType, boolean
            required) {
        super();
        this.name = name;
        this.title = title;
        this.description = description;
        this.required = required;
        this.dataType = dataType;
        this.calculateType = calculateType;
        this.calculateType.setAssociatedParam(this);
    }

    public MetaParameterDescriptor setName(String name) {
        this.name = name;
        return this;
    }

    public MetaParameterDescriptor setTitle(String title) {
        this.title = title;
        return this;
    }

    public MetaParameterDescriptor setDescription(String description) {
        this.description = description;
        return this;
    }

    public MetaParameterDescriptor setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public MetaParameterDescriptor setParameterDataType(IParameterDataType dataType) {
        this.dataType = dataType;
        return this;
    }

}
