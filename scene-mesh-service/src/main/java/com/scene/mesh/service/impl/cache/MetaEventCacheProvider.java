package com.scene.mesh.service.impl.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.event.MockMetaEventRepository;
import com.scene.mesh.service.api.cache.ICacheProvider;

/**
 * 元事件缓存提供器
 */
public class MetaEventCacheProvider implements ICacheProvider<MetaEventCache> {

    private ICache<String, IMetaEvent> cache;

    private MockMetaEventRepository mockMetaEventRepository;

    public MetaEventCacheProvider(ICache<String, IMetaEvent> cache, MockMetaEventRepository mockMetaEventRepository) {
        this.cache = cache;
        this.mockMetaEventRepository = mockMetaEventRepository;
    }

    @Override
    public MetaEventCache generateCacheObject() {
        this.mockMetaEventRepository.getAllMetaEvents().forEach(metaEvent -> {
            this.cache.set("meta_event_"+metaEvent.getUuid(), metaEvent);
        });
        return new MetaEventCache(cache);
    }
}
