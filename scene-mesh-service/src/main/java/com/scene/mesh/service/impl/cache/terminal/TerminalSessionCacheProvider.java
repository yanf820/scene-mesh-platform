package com.scene.mesh.service.impl.cache.terminal;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.spec.cache.ICacheProvider;

import java.util.List;

public class TerminalSessionCacheProvider implements ICacheProvider<TerminalSessionCache,TerminalSession> {

    private final ICache<String, TerminalSession> cache;

    public TerminalSessionCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public TerminalSessionCache generateCacheObject() {
        return new TerminalSessionCache(cache);
    }

    @Override
    public TerminalSessionCache refreshCacheObject(List<TerminalSession> objects) {
        return null;
    }
}
