package com.scene.mesh.manager.terminal;

import com.scene.mesh.model.terminal.Terminal;
import com.scene.mesh.model.terminal.TerminalStatus;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rest/v1")
public class TerminalController {

    private final ITerminalService terminalService;

    public TerminalController(ITerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @GetMapping("/terminals")
    public ResponseEntity<Page<Terminal>> findTerminals(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String terminalId,
            @RequestParam(required = false) TerminalStatus terminalStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Instant createTimeBegin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Instant createTimeEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Terminal> terminals = this.terminalService
                .searchTerminals(productId,terminalId,terminalStatus,createTimeBegin,createTimeEnd,page,size);

        return ResponseEntity.ok(terminals);
    }
}
