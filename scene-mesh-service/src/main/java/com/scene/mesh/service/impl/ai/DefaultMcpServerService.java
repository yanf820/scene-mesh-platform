package com.scene.mesh.service.impl.ai;

import com.scene.mesh.model.mcp.McpServer;
import com.scene.mesh.service.spec.ai.IMcpServerService;
import com.scene.mesh.service.spec.cache.MutableCacheService;

import java.util.List;

public class DefaultMcpServerService implements IMcpServerService {

    private final MutableCacheService mutableCacheService;

    public DefaultMcpServerService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public List<McpServer> getAllMcpServers() {
        return this.mutableCacheService.getAllMcpServers();
    }
}
