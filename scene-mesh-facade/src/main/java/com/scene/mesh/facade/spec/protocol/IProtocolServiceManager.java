package com.scene.mesh.facade.spec.protocol;

import com.scene.mesh.model.protocol.ProtocolType;

/**
 * 协议服务管理器
 */
public interface IProtocolServiceManager {

    /**
     * 注册协议服务
     * @param protocolService
     */
    void register(IProtocolService protocolService);

    /**
     * 获取协议服务
     * @param protocolType
     * @return
     */
    IProtocolService getProtocolService(ProtocolType protocolType);

}
