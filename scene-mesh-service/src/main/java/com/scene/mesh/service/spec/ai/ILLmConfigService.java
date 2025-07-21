package com.scene.mesh.service.spec.ai;

import com.scene.mesh.model.llm.LanguageModelProvider;

public interface ILLmConfigService {

    LanguageModelProvider getLmpConfig(String providerName);
}
