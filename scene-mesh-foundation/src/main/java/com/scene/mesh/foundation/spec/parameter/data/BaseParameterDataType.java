package com.scene.mesh.foundation.spec.parameter.data;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseParameterDataType<T> implements IParameterDataType<T>{

    private final DataType dataType;

    public BaseParameterDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public DataType getDataType() {
        return this.dataType;
    }

    @Override
    public boolean validate(T value) {
        log.debug("校验参数值: 数据类型 - {}, 值 - {}", dataType, value);
        boolean result = doValidate(value);
        log.debug("校验结果: {}", result);
        return result;
    }

    /**
     * 子类实际校验
     * @param value
     * @return
     */
    protected abstract boolean doValidate(T value);
}
