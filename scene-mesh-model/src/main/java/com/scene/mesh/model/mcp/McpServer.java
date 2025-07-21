package com.scene.mesh.model.mcp;

import lombok.Data;

import java.time.Duration;

@Data
public class McpServer {
    private String id;
    private String name;
    private String description;
    private String type;
    private String baseUrl;
    private String endpoint;
    private String header;
    private Integer timeout;
    private Boolean enabled;
}
