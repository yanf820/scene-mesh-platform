package com.scene.mesh.service.impl.cache.action;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.impl.cache.event.MetaEventCache;
import com.scene.mesh.service.spec.cache.ICacheProvider;
import com.scene.mesh.service.spec.event.IMetaEventService;

import java.util.List;

public class MetaActionCacheProvider implements ICacheProvider<MetaActionCache, IMetaAction> {

    private final ICache<String, IMetaAction> cache;

    public MetaActionCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public MetaActionCache generateCacheObject() {
        return new MetaActionCache(cache);
    }

    @Override
    public MetaActionCache refreshCacheObject(List<IMetaAction>  metaActions) {
        if (metaActions.isEmpty()) {return new MetaActionCache(cache);}

        for (IMetaAction metaAction: metaActions) {
            this.cache.set(MetaActionCache.KEY_PREFIX + metaAction.getUuid(), metaAction);
        }
        return new MetaActionCache(cache);
    }
}
