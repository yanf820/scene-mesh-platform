package com.scene.mesh.engin.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class McpConfig {

    @Value("${scene-mesh.ai.mcp.server.url}")
    private String mcpServerUrl;

    @Bean
    public McpSyncClient sseMcpClient() {
        try {
            // 创建 SSE 传输层
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(mcpServerUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);

            McpClientTransport transport = WebFluxSseClientTransport
                    .builder(webClientBuilder)
                    .sseEndpoint("/sse")
                    .build();

            McpSyncClient mcpSyncClient = McpClient.sync(transport).build();
            return mcpSyncClient;

        } catch (Exception e) {
            log.error("Failed to create Scene Mesh MCP client", e);
            throw new RuntimeException("MCP client initialization failed", e);
        }
    }

    @Bean
    public ToolCallbackProvider sceneMeshToolCallbackProvider(McpSyncClient mcpSyncClient) {
        mcpSyncClient.initialize();

        // 🔍 打印服务器信息
        log.info("🔍 连接的MCP服务器信息: {}", mcpSyncClient.getServerInfo());
        log.info("🔍 服务器能力: {}", mcpSyncClient.getServerCapabilities());

        SyncMcpToolCallbackProvider provider = new SyncMcpToolCallbackProvider(mcpSyncClient);

        // 🔍 打印工具详情
        ToolCallback[] callbacks = provider.getToolCallbacks();
        log.info("🔍 从MCP服务器获取到 {} 个工具:", callbacks.length);
        for (int i = 0; i < callbacks.length; i++) {
            log.info("  {}. {} - {}", i + 1,
                    callbacks[i].getToolDefinition().name(),
                    callbacks[i].getToolDefinition().description());
        }

        return provider;
    }
}
