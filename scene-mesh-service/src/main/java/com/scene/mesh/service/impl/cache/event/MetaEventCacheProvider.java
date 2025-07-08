package com.scene.mesh.service.impl.cache.event;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.Product;
import com.scene.mesh.service.impl.cache.product.ProductCache;
import com.scene.mesh.service.spec.cache.ICacheProvider;
import com.scene.mesh.service.spec.event.IMetaEventService;
import com.scene.mesh.service.spec.product.IProductService;

import java.util.List;

public class MetaEventCacheProvider implements ICacheProvider<MetaEventCache,IMetaEvent> {

    private final ICache<String, IMetaEvent> cache;

    public MetaEventCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public MetaEventCache generateCacheObject() {
        return new MetaEventCache(cache);
    }

    @Override
    public MetaEventCache refreshCacheObject(List<IMetaEvent>  metaEvents) {
        if (metaEvents.isEmpty()) {return new MetaEventCache(cache);}

        for (IMetaEvent metaEvent: metaEvents) {
            this.cache.set(MetaEventCache.KEY_PREFIX + metaEvent.getUuid(), metaEvent);
        }
        return new MetaEventCache(cache);
    }
}
