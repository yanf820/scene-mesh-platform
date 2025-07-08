package com.scene.mesh.service.impl.cache.event;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.Product;
import com.scene.mesh.service.spec.cache.IDisposed;

import java.util.List;

public class MetaEventCache implements IDisposed {

    private final ICache<String, IMetaEvent> cache;

    public static String KEY_PREFIX = "metaEvent:";

    public MetaEventCache(ICache<String, IMetaEvent> cache) {
        this.cache = cache;
    }

    public IMetaEvent getMetaEvent(String metaEventId) {
        return cache.get(KEY_PREFIX + metaEventId);
    }

    @Override
    public void dispose() {
        this.cache.deleteByKeyPrefix(KEY_PREFIX + "*");
    }

    public List<IMetaEvent> getMetaEvents() {
        return this.cache.getAll(KEY_PREFIX + "*");
    }
}
