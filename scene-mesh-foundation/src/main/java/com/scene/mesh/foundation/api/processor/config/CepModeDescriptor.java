package com.scene.mesh.foundation.api.processor.config;

import lombok.Builder;
import lombok.Data;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.Serializable;
import java.time.Duration;

/**
 * cep 模式描述符
 */
@Builder
@Data
public class CepModeDescriptor implements Serializable {
    //是否开启 cep 模式
    private boolean enabled;
    //计算分区键
    private String[] keyed = new String[2];
    //规则表源
    private String ruleSource;
    //驱动名称
    private String driverName;
    //db 连接
    private String databaseUrl;
    //用户名
    private String username;
    //密码
    private String password;
    //更新周期
    private Duration period;
    //cep 输出类型
    private TypeInformation cepMatchedResultType;
    //cep 处理并行度
    private int parallelism;

}
