package com.scene.mesh.model.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Event {
    //事件 ID
    @Setter(AccessLevel.NONE)
    private String id;
    //事件元模型 ID
    @Setter(AccessLevel.NONE)
    private String metaEventId;
    //租户 ID
    private String tenantId;
    //产品 ID
    private String productId;
    //终端 ID
    private String terminalId;
    //协议 session ID
    private String protocolSessionId;
    //payload
    private Map<String, Object> payload;

    public Event() {
    }

    public Event(String metaEventId) {
        this.id = UUID.randomUUID().toString();
        this.metaEventId = metaEventId;
        this.payload = new HashMap<>();
    }

    public void addPayloadEntry(String key, Object val) {
        payload.put(key, val);
    }

    public Object getPayloadVal(String key) {
        return payload.get(key);
    }

}
