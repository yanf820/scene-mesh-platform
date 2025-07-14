package com.scene.mesh.model.product;

import com.scene.mesh.model.protocol.ProtocolConfig;
import lombok.Data;

@Data
public class ProductSetting {
    // 密钥组
    private String[] secretKey;
    // 协议配置
    private ProtocolConfig protocolConfig;
    // stt model
    private String sttProcessor;
    // tts model
    private String ttsProcessor;
}
