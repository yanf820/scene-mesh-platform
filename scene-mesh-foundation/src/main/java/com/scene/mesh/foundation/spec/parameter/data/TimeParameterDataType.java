package com.scene.mesh.foundation.spec.parameter.data;

public class TimeParameterDataType extends BaseParameterDataType{
    public TimeParameterDataType() {
        super(DataType.TIME);
    }

    @Override
    protected boolean doValidate(Object value) {
        //TODO 校验
        return true;
    }
}
