package com.scene.mesh.model.product;

import com.scene.mesh.model.protocol.ProtocolConfig;
import lombok.Data;

@Data
public class ProductSetting {
    private String ttsLLM;
    private String sttLLM;
    private String llm;
    // 密钥组
    private String[] secretKey;
    // 协议配置
    private ProtocolConfig protocolConfig;
}
