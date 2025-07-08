package com.scene.mesh.foundation.spec.parameter.data;

public class BinaryParameterDateType extends BaseParameterDataType{
    public BinaryParameterDateType() {
        super(DataType.BINARY);
    }

    @Override
    protected boolean doValidate(Object value) {
        return true;
    }
}
