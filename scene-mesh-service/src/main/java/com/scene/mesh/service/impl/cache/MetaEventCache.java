package com.scene.mesh.service.impl.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.api.cache.IDisposed;

/**
 * 元事件缓存
 */
public class MetaEventCache implements IDisposed {

    ICache<String, IMetaEvent> metaEventCache;

    public MetaEventCache(ICache<String, IMetaEvent> metaEventCache) {
        this.metaEventCache = metaEventCache;
    }

    public IMetaEvent findById(String id) {
        return metaEventCache.get("meta_event_"+id);
    }

    @Override
    public void dispose() {
        this.metaEventCache = null;
    }
}
