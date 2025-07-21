package com.scene.mesh.engin.model;

import com.scene.mesh.model.event.Event;
import lombok.Data;

import java.util.List;

@Data
public class ThenRequest {

    private String terminalId;

    private String sceneId;

    private String thenId;

    private List<Event> eventsInScene;

}
