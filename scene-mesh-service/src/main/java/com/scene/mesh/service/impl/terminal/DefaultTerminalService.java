package com.scene.mesh.service.impl.terminal;

import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.terminal.Terminal;
import com.scene.mesh.model.terminal.TerminalRepository;
import com.scene.mesh.model.terminal.TerminalStatus;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.Instant;
import java.util.List;

@Slf4j
public class DefaultTerminalService implements ITerminalService {

    private final TerminalRepository terminalRepository;

    public DefaultTerminalService(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @Override
    public void updateStatus(String productId, String terminalId, TerminalStatus terminalStatus) {
        Terminal terminal = this.getTerminalWithProductId(productId, terminalId);
        if (terminal == null) {
            log.error("terminal not found - productId:{}, terminalId:{}", productId, terminalId);
            throw new RuntimeException("terminal not found");
        }
        terminal.setStatus(terminalStatus);
        this.terminalRepository.save(terminal);
    }

    @Override
    public Terminal getTerminalWithProductId(String productId, String terminalId) {
        List<Terminal> terminals = this.terminalRepository
                .getTerminalByProductIdAndTerminalId(productId, terminalId);

        if (terminals == null || terminals.isEmpty()) {
            return null;
        }
        if (terminals.size() > 1) {
            throw new RuntimeException(
                    StringHelper.format("发现多个 terminal - productId:{0}, terminalId:{1} ", productId, terminalId));
        }
        return terminals.get(0);
    }

    @Override
    public boolean registerTerminal(String productId, String terminalId) {
        Terminal terminal = new Terminal();
        terminal.setProductId(productId);
        terminal.setTerminalId(terminalId);
        terminal.setStatus(TerminalStatus.ACTIVITY);

        this.terminalRepository.save(terminal);

        return true;
    }

    @Override
    public Page<Terminal> searchTerminals(String productId, String terminalId, TerminalStatus terminalStatus, Instant createTimeBegin, Instant createTimeEnd, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Terminal> terminals = this.terminalRepository.searchTerminalsByProductIdOrTerminalIdOrStatusOrCreatedAtBetween(
                productId,terminalId,terminalStatus,createTimeBegin,createTimeEnd,pageable);
        return terminals;
    }

    @Override
    public Terminal getTerminalWithTerminalId(String terminalId) {
        return this.terminalRepository.getTerminalByTerminalId(terminalId);
    }

}
