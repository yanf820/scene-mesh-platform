package com.scene.mesh.model.terminal;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, String> {

    List<Terminal> findByTerminalId(String terminalId);

    List<Terminal> getTerminalByProductIdAndTerminalId(String productId, String terminalId);

    Page<Terminal> searchTerminalsByProductIdOrTerminalIdOrStatusOrCreatedAtBetween(String productId, String terminalId, TerminalStatus status, Instant createdAtAfter, Instant createdAtBefore, Pageable pageable);

    Terminal getTerminalByTerminalId(String terminalId);
}
