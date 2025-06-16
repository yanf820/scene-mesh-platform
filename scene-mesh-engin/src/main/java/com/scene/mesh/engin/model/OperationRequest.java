package com.scene.mesh.engin.model;

import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.operation.Operation;
import lombok.Data;

import java.util.List;

@Data
public class OperationRequest {

    private String productId;

    private String terminalId;

    private String sceneId;

    private Operation operation;

    private List<Event> eventsInScene;

}
