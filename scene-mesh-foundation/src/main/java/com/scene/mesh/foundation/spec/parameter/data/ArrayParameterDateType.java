package com.scene.mesh.foundation.spec.parameter.data;

public class ArrayParameterDateType extends BaseParameterDataType{

    public ArrayParameterDateType() {
        super(DataType.ARRAY);
    }

    @Override
    protected boolean doValidate(Object value) {
        return true;
    }
}
