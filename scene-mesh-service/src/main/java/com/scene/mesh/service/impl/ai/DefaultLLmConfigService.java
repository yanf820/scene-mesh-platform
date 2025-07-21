package com.scene.mesh.service.impl.ai;

import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.service.spec.ai.ILLmConfigService;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DefaultLLmConfigService implements ILLmConfigService {

    private final MutableCacheService mutableCacheService;

    public DefaultLLmConfigService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public LanguageModelProvider getLmpConfig(String providerName) {
        List<LanguageModelProvider> lmps = this.mutableCacheService.getAllLmp();
        if (lmps == null || lmps.isEmpty()) {
            log.error("Can't find any llm providers.");
            return null;
        }
        for (LanguageModelProvider lmp : lmps) {
            if (lmp.getName().equals(providerName))
                return lmp;
        }

        log.error("Can't find any llm providers for provider name: {}", providerName);
        return null;
    }
}
