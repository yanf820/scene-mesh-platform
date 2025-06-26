package com.scene.mesh.facade.impl.outbound;

import com.scene.mesh.facade.api.outbound.IOutboundMessageHandler;
import com.scene.mesh.facade.api.outbound.OutboundMessage;
import com.scene.mesh.facade.api.protocol.IProtocolService;
import com.scene.mesh.facade.api.protocol.IProtocolServiceManager;
import com.scene.mesh.facade.api.protocol.TerminalProtocolStateManager;
import com.scene.mesh.model.protocol.ProtocolType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultOutboundMessageHandler implements IOutboundMessageHandler {

    @Autowired
    private TerminalProtocolStateManager terminalProtocolStateManager;

    @Autowired
    private IProtocolServiceManager protocolServiceManager;

    @Override
    public void handle(OutboundMessage outboundMessage) {
        String terminalId = outboundMessage.getTerminalId();
        // 从协议管理器 中获取当前 terminal 所连通的协议
        ProtocolType currentProtocolType = this.terminalProtocolStateManager.getProtocolState(terminalId);
        // 获取协议服务
        IProtocolService protocolService = this.protocolServiceManager.getProtocolService(currentProtocolType);
        protocolService.send(outboundMessage);
    }

}
