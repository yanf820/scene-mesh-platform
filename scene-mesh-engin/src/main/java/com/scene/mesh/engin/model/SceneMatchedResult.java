package com.scene.mesh.engin.model;

import com.scene.mesh.model.event.Event;
import lombok.Data;

import java.util.List;

@Data
public class SceneMatchedResult {
    private String sceneId;
    private String thenId;
    private String terminalId;
    private List<Event> matchedEvents;
    private long matchedTime;
}
