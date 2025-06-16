package com.scene.mesh.service.impl.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.api.cache.ICacheProvider;

public class TerminalSessionCacheProvider implements ICacheProvider<TerminalSessionCache> {

    private final ICache<String, TerminalSession> cache;

    public TerminalSessionCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public TerminalSessionCache generateCacheObject() {
        return new TerminalSessionCache(cache);
    }
}
