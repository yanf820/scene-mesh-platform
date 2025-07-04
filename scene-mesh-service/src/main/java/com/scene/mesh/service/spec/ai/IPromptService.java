package com.scene.mesh.service.spec.ai;

import org.springframework.core.io.Resource;

import java.util.Map;

public interface IPromptService {

    String assembleUserMessage(Resource templateResource, Map<String, Object> variables);

}
