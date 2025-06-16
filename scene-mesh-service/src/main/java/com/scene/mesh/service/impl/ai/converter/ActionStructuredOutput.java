package com.scene.mesh.service.impl.ai.converter;

import com.scene.mesh.model.action.Action;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ActionStructuredOutput {

    private Map<String, Map<String, String>> actionToPayloadMap;

}
