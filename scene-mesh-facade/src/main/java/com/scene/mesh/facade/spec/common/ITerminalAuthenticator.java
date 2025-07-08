package com.scene.mesh.facade.spec.common;

import com.scene.mesh.model.protocol.ProtocolType;

public interface ITerminalAuthenticator {

    boolean authenticate(String productId, String terminalId, String secretKey, ProtocolType protocolType);

}
