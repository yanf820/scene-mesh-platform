package com.scene.mesh.facade.impl.protocol;

import com.scene.mesh.facade.spec.protocol.IProtocolService;
import com.scene.mesh.facade.spec.protocol.IProtocolServiceManager;
import com.scene.mesh.model.protocol.ProtocolType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 终端通信管理器，管理终端当前所在协议信道和协议服务
 */
@Component
public class DefaultProtocolServiceManager implements IProtocolServiceManager {

    private final Map<ProtocolType, IProtocolService> protocolServiceMap;

    public DefaultProtocolServiceManager(List<IProtocolService> protocolServiceList) {
        this.protocolServiceMap = new ConcurrentHashMap<>();
        for (IProtocolService protocolService : protocolServiceList) {
            this.register(protocolService);
        }
    }

    public void register(IProtocolService protocolService){
        this.protocolServiceMap.put(protocolService.getProtocolType(), protocolService);
    }

    public IProtocolService getProtocolService(ProtocolType protocolType){
        return this.protocolServiceMap.get(protocolType);
    }
}
