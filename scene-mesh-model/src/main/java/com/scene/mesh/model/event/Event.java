package com.scene.mesh.model.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Event implements Serializable {
    //event ID
    @Setter(AccessLevel.NONE)
    private String id;
    //meta event ID
    @Setter(AccessLevel.NONE)
    private String type;
    //terminal ID
    private String terminalId;
    //payload
    private Map<String, Object> payload;

    public Event() {
    }

    public Event(String type) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.payload = new HashMap<>();
    }

    public void addPayloadEntry(String key, Object val) {
        payload.put(key, val);
    }

    public Object getPayloadVal(String key) {
        return payload.get(key);
    }

}
