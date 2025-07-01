package com.scene.mesh.facade.spec.protocol;

import com.scene.mesh.model.protocol.ProtocolType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 终端当前协议状态管理
 */
@Component
public class TerminalProtocolStateManager {

    private Map<String, ProtocolType> activatedProtocolTypes;

    public TerminalProtocolStateManager() {
        activatedProtocolTypes = new ConcurrentHashMap<>();
    }

    public void setProtocolState(String terminalId, ProtocolType protocolType) {
        this.activatedProtocolTypes.put(terminalId, protocolType);
    }

    public void removeProtocolState(String terminalId) {
        this.activatedProtocolTypes.remove(terminalId);
    }

    public ProtocolType getProtocolState(String terminalId) {
        return this.activatedProtocolTypes.get(terminalId);
    }
}
