package com.scene.mesh.facade.impl.common;

import com.scene.mesh.facade.spec.common.ITerminalAuthenticator;
import com.scene.mesh.facade.spec.protocol.TerminalProtocolStateManager;
import com.scene.mesh.model.protocol.ProtocolType;
import com.scene.mesh.model.terminal.Terminal;
import com.scene.mesh.model.terminal.TerminalStatus;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTerminalAuthenticator implements ITerminalAuthenticator {

    private final ITerminalService terminalService;

    private final IProductService productService;

    private final TerminalProtocolStateManager terminalProtocolStateManager;

    public DefaultTerminalAuthenticator(ITerminalService terminalService, IProductService productService,TerminalProtocolStateManager terminalProtocolStateManager) {
        this.terminalService = terminalService;
        this.productService = productService;
        this.terminalProtocolStateManager = terminalProtocolStateManager;
    }

    @Override
    public boolean authenticate(String productId, String terminalId, String secretKey, ProtocolType protocolType) {

        // verify current protocol type
        ProtocolType currentType = this.terminalProtocolStateManager.getProtocolState(terminalId);
        if (currentType != null && protocolType != currentType) {
            log.error("Does not support multi-protocol connections. currentType: {}, connect protocolType: {},productId:{}, terminalId:{} ", currentType, protocolType,productId, terminalId);
            return false;
        }

        // verify the product key, regardless of whether the terminal exists or not.
        boolean isMatched = this.productService.verifyProductSecret(productId,secretKey);
        if (!isMatched) {
            return false;
        }

        // Handling unregistered terminals
        Terminal terminal = this.terminalService.getTerminalWithProductId(productId,terminalId);
        if (terminal == null) {
            //注册 terminal
            boolean isSuccess = this.terminalService.registerTerminal(productId,terminalId);
            if (!isSuccess) {
                log.error("Terminal registration failed.");
                return false;
            }
            return true;
        }

        // The terminal status is not activated. Proceed with activation processing.
        if (!TerminalStatus.ACTIVITY.equals(terminal.getStatus())){
            this.terminalService.updateStatus(productId, terminalId,TerminalStatus.ACTIVITY);
        }

        return true;
    }
}
