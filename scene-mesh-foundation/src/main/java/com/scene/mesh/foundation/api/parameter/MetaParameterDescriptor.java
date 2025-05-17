
package com.scene.mesh.foundation.api.parameter;

import com.scene.mesh.foundation.api.parameter.data.IParameterDataType;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 参数描述符
 */
public class MetaParameterDescriptor implements Serializable {

    @Getter
    private String name;
    @Getter
    private String title;
    @Getter
    private String description;
    /**
     * 参数数据类型
     */
    @Getter
    private IParameterDataType dataType;

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
