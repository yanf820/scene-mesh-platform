package com.scene.mesh.foundation.spec.parameter.data;

public class JsonParameterDateType extends BaseParameterDataType{

    public JsonParameterDateType() {
        super(DataType.JSON);
    }

    @Override
    protected boolean doValidate(Object value) {
        return true;
    }
}
