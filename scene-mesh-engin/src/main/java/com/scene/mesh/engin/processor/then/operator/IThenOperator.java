package com.scene.mesh.engin.processor.then.operator;

import com.scene.mesh.engin.model.ThenRequest;
import com.scene.mesh.engin.model.ThenResponse;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.scene.WhenThen;

import java.util.List;

public interface IThenOperator {

    String getOperatorType();

    boolean process(String terminalId, Scene scene, WhenThen.Then then, List<Event> eventsInScene, IProcessOutput output);
}