package com.scene.mesh.foundation.spec.parameter.data;

public class StringParameterDataType extends BaseParameterDataType<String> {

    public StringParameterDataType() {
        super(DataType.STRING);
    }

    @Override
    protected boolean doValidate(String value) {
        //TODO 校验
        return true;
    }
}
