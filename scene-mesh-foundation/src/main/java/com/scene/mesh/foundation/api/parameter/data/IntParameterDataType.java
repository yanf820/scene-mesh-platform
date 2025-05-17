package com.scene.mesh.foundation.api.parameter.data;

public class IntParameterDataType extends BaseParameterDataType<Integer>{

    public IntParameterDataType() {
        super(DataType.INTEGER);
    }

    @Override
    protected boolean doValidate(Integer value) {
        //TODO 校验
        return true;
    }
}
