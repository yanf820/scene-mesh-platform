package com.scene.mesh.facade.impl.protocol.mqtt;

import com.scene.mesh.facade.spec.common.ITerminalAuthenticator;
import com.scene.mesh.model.protocol.ProtocolType;
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
public class MqttAuthenticator implements IAuthenticator {

    private final ITerminalAuthenticator terminalAuthenticator;

    public MqttAuthenticator(ITerminalAuthenticator terminalAuthenticator) {
        this.terminalAuthenticator = terminalAuthenticator;
    }

    @Override
    public boolean checkValid(String clientId, String username, byte[] password) {
        String passwordString = new String(password, StandardCharsets.UTF_8);
        return terminalAuthenticator.authenticate(username, clientId, passwordString, ProtocolType.MQTT);
    }
}
