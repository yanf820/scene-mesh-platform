package com.scene.mesh.service.impl.cache.mcp;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.model.mcp.McpServer;
import com.scene.mesh.service.impl.cache.llm.LlmCache;
import com.scene.mesh.service.spec.cache.ICacheProvider;

import java.util.List;

public class McpServerCacheProvider implements ICacheProvider<McpServerCache, McpServer> {

    private final ICache<String, McpServer> cache;

    public McpServerCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public McpServerCache generateCacheObject() {
        return new McpServerCache(cache);
    }

    @Override
    public McpServerCache refreshCacheObject(List<McpServer> mcpServers) {
        if (mcpServers.isEmpty()) {return new McpServerCache(cache);}

        for (McpServer mcpServer: mcpServers) {
            this.cache.set(McpServerCache.KEY_PREFIX + mcpServer.getId(), mcpServer);
        }
        return new McpServerCache(cache);
    }
}
