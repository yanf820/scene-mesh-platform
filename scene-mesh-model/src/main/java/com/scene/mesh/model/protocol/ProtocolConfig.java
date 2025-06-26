package com.scene.mesh.model.protocol;

import lombok.Data;

import java.util.List;

@Data
public class ProtocolConfig {
    private List<ProtocolType> supportedProtocolTypes;
}
