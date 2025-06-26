package com.scene.mesh.facade.api.inboud;

import com.scene.mesh.model.protocol.ProtocolType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 入站消息拦截器
 */
public interface InboundMessageInterceptor {

    /**
     * 拦截入站消息
     */
    void intercept(InboundMessageRequest request, InboundMessageResponse response);

    /**
     * 初始化
     */
    void init();

    /**
     * 拦截器名称
     * @return
     */
    String getName();

    class InboundMessageRequest {

        @Getter
        private InboundMessage message;

        private final Map<String, Object> properties;

        public InboundMessageRequest(InboundMessage message) {
            this.properties = new HashMap<>();
            this.message = message;
        }

        public Object getPropVal(String key) {
            return properties.get(key);
        }

        public void setPropVal(String key, Object val) {
            properties.put(key, val);
        }
    }

    class InboundMessageResponse {
        //成功状态
        @Setter
        @Getter
        private boolean success = true;
        //异常信息
        @Setter
        @Getter
        private String opinion;
        @Setter
        @Getter
        private String terminalId;
        //源消息
        @Setter
        @Getter
        private String sourceMessage;

        private final Map<String, Object> properties;

        public InboundMessageResponse() {
            this.properties = new HashMap<>();
        }

        public Object getPropVal(String key) {
            return properties.get(key);
        }

        public void addPropEntry(String key, Object val) {
            properties.put(key, val);
        }
    }
}
