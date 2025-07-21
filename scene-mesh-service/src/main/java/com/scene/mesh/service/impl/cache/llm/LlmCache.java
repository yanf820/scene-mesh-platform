package com.scene.mesh.service.impl.cache.llm;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.service.spec.cache.IDisposed;

import java.util.List;

public class LlmCache implements IDisposed {

    private final ICache<String, LanguageModelProvider> cache;

    public static String KEY_PREFIX = "lmp:";

    public LlmCache(ICache<String, LanguageModelProvider> cache) {
        this.cache = cache;
    }

    @Override
    public void dispose() {
        this.cache.deleteByKeyPrefix(KEY_PREFIX + "*");
    }

    public List<LanguageModelProvider> getAllLanguageModelProviders() {
        return this.cache.getAll(KEY_PREFIX + "*");
    }

}
