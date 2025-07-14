package com.scene.mesh.service.impl.event;

import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.event.IMetaEventService;

import java.util.List;

public class DefaultMetaEventService implements IMetaEventService {

    private final MutableCacheService mutableCacheService;

    public DefaultMetaEventService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public IMetaEvent getIMetaEvent(String metaEventId) {
        return this.mutableCacheService.getIMetaEvent(metaEventId);
    }

    @Override
    public List<IMetaEvent> getAllMetaEvents(){
        return this.mutableCacheService.getAllMetaEvent();
    }
}
