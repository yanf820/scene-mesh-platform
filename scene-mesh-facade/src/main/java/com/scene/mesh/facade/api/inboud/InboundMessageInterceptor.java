package com.scene.mesh.facade.api.inboud;

import com.scene.mesh.facade.api.ProtocolType;
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
        private String clientId;
        //协议层 sessionId
        @Getter
        private String protocolSessionId;
        //消息
        @Getter
        private String message;
        @Getter
        private String metaEventId;

        @Getter
        private ProtocolType protocolType;

        private final Map<String, Object> payload;

        public InboundMessageRequest(String metaEventId,String clientId, String message, ProtocolType protocolType, String protocolSessionId) {
            this.payload = new HashMap<>();
            this.clientId = clientId;
            this.metaEventId = metaEventId;
            this.message = message;
            this.protocolType = protocolType;
            this.protocolSessionId = protocolSessionId;
        }

        public Object getPayloadVal(String key) {
            return payload.get(key);
        }

        public void setPayloadVal(String key, Object val) {
            payload.put(key, val);
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
        private String clientId;
        //源消息
        @Setter
        @Getter
        private String sourceMessage;
        @Setter
        @Getter
        private ProtocolType protocolType;

        private final Map<String, Object> payload;

        public InboundMessageResponse() {
            this.payload = new HashMap<>();
        }

        public Object getPayloadVal(String key) {
            return payload.get(key);
        }

        public void addPayloadEntry(String key, Object val) {
            payload.put(key, val);
        }
    }
}
