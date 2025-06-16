package com.scene.mesh.engin.model;

import com.scene.mesh.model.event.Event;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OperationResponse {

    private String productId;

    private String terminalId;

    private String sceneId;

    private List<Event> events;

    public OperationResponse() {
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }
}
