package com.scene.mesh.facade.spec.inboud;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * inbound message Interceptor
 */
public interface InboundMessageInterceptor {

    void intercept(InboundMessageRequest request, InboundMessageResponse response);

    /**
     * init method
     */
    void init();

    /**
     * Interceptor name
     * @return
     */
    String getName();

    class InboundMessageRequest {

        @Getter
        private final InboundMessage message;

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

    @Data
    class InboundMessageResponse {

        private boolean success = true;

        private String opinion;

        private String terminalId;

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
