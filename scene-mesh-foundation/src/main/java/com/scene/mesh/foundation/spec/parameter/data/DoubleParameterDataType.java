package com.scene.mesh.foundation.spec.parameter.data;

public class DoubleParameterDataType extends BaseParameterDataType{
    public DoubleParameterDataType() {
        super(DataType.DOUBLE);
    }

    @Override
    protected boolean doValidate(Object value) {
        //TODO 校验
        return true;
    }
}
