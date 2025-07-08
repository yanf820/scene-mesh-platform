package com.scene.mesh.model.protocol;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProtocolConfig {
    private List<ProtocolType> supportedProtocolTypes;

    public ProtocolConfig() {
        this.supportedProtocolTypes = new ArrayList<>();
    }

    public void add(ProtocolType supportedProtocolType) {
        this.supportedProtocolTypes.add(supportedProtocolType);
    }
}
