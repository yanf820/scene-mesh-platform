package com.scene.mesh.service.spec.terminal;

import com.scene.mesh.model.terminal.Terminal;
import com.scene.mesh.model.terminal.TerminalStatus;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.LocalDateTime;

public interface ITerminalService {

    void updateStatus(String productId, String terminalId, TerminalStatus terminalStatus);

    Terminal getTerminalWithProductId(String productId, String terminalId);

    boolean registerTerminal(String productId, String terminalId);

    Page<Terminal> searchTerminals(String productId, String terminalId, TerminalStatus terminalStatus, Instant createTimeBegin, Instant createTimeEnd, int page, int size);
}
