package com.scene.mesh.service.impl.cache.llm;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.service.spec.cache.ICacheProvider;

import java.util.List;

public class LlmCacheProvider implements ICacheProvider<LlmCache, LanguageModelProvider> {

    private final ICache<String, LanguageModelProvider> cache;

    public LlmCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public LlmCache generateCacheObject() {
        return new LlmCache(cache);
    }

    @Override
    public LlmCache refreshCacheObject(List<LanguageModelProvider> lmps) {
        if (lmps.isEmpty()) {return new LlmCache(cache);}

        for (LanguageModelProvider lmp: lmps) {
            this.cache.set(LlmCache.KEY_PREFIX + lmp.getId(), lmp);
        }
        return new LlmCache(cache);
    }
}
