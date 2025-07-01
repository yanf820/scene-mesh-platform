package com.scene.mesh.model.terminal;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

/**
 * 终端模型
 */
@Data
@Entity
@Table(name = "terminal")
public class Terminal {

    @Id
    @Column
    @UuidGenerator
    private String id;

    @Column
    private String terminalId;

    @Column
    private String name;

    @Column
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private TerminalStatus status;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column
    @UpdateTimestamp
    private Instant updatedAt;
}
