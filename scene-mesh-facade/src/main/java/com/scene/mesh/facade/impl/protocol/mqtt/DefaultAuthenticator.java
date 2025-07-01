package com.scene.mesh.facade.impl.protocol.mqtt;

import com.scene.mesh.model.terminal.Terminal;
import com.scene.mesh.model.terminal.TerminalStatus;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import io.moquette.broker.security.IAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class DefaultAuthenticator implements IAuthenticator {

    private final ITerminalService terminalService;

    private final IProductService productService;

    public DefaultAuthenticator(ITerminalService terminalService, IProductService productService) {
        this.terminalService = terminalService;
        this.productService = productService;
    }

    @Override
    public boolean checkValid(String clientId, String username, byte[] password) {

        // First, verify the product key, regardless of whether the terminal exists or not.
        String passwordString = new String(password, StandardCharsets.UTF_8);
        boolean isMatched = this.productService.verifyProductSecret(username,passwordString);
        if (!isMatched) {
            return false;
        }

        // Handling unregistered terminals
        Terminal terminal = this.terminalService.getTerminalWithProductId(username,clientId);
        if (terminal == null) {
            //注册 terminal
           boolean isSuccess = this.terminalService.registerTerminal(username,clientId);
           if (!isSuccess) {
               log.error("Terminal registration failed.");
               return false;
           }
           return true;
        }

        // The terminal status is not activated. Proceed with activation processing.
        if (!TerminalStatus.ACTIVITY.equals(terminal.getStatus())){
            this.terminalService.updateStatus(username, clientId,TerminalStatus.ACTIVITY);
        }

        return true;
    }
}
