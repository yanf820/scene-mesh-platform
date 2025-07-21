package com.scene.mesh.model.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OriginalMcpServer {

    @JsonProperty("id")
    private String id;

    @JsonProperty("modelName")
    private String modelName; // 例如: "mcpService"

    @JsonProperty("values")
    private McpServerDetails values;

    @JsonProperty("isDeleted")
    private boolean isDeleted;

    /**
     * 对应于内嵌的 "values" JSON 对象，
     * 包含了 MCP 服务的具体配置详情。
     */
    @Data
    public static class McpServerDetails {

        @JsonProperty("baseUrl")
        private String baseUrl;

        @JsonProperty("endpoint")
        private String endpoint;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type; // 例如: "sse"

        @JsonProperty("enable")
        private boolean enable;

        @JsonProperty("header")
        private String header; // 注意：如果header可能是复杂的JSON对象，这里可能需要改成 Map<String, String>

        @JsonProperty("timeout")
        private int timeout;

        @JsonProperty("description")
        private String description;
    }

}
