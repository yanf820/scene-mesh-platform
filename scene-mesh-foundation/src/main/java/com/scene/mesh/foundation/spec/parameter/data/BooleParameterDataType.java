package com.scene.mesh.foundation.spec.parameter.data;

public class BooleParameterDataType extends BaseParameterDataType{
    public BooleParameterDataType() {
        super(DataType.BOOLEAN);
    }

    @Override
    protected boolean doValidate(Object value) {
        //TODO 校验
        return true;
    }
}
