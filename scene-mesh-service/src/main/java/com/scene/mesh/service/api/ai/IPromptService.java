package com.scene.mesh.service.api.ai;

import org.springframework.core.io.Resource;

import java.util.Map;

public interface IPromptService {

    String assembleUserMessage(Resource templateResource, Map<String, Object> variables);

}
