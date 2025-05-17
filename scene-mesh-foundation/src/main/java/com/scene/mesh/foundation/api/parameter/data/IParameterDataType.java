package com.scene.mesh.foundation.api.parameter.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * 参数数据类型
 */
public interface IParameterDataType<T> extends Serializable {

    DataType getDataType();

    @JsonIgnore
    boolean validate(T value);

    enum DataType {
        //字符串
        STRING,
        //整数
        INTEGER,
        //小数
        DOUBLE,
        //布尔
        BOOLEAN,
        //时间，毫秒值
        TIME
    }
}
