package com.scene.mesh.service.impl.action;

import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.service.spec.action.IMetaActionService;
import com.scene.mesh.service.spec.cache.MutableCacheService;

import java.util.List;

public class DefaultMetaActionService implements IMetaActionService {

    private final MutableCacheService mutableCacheService;

    public DefaultMetaActionService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public IMetaAction getIMetaAction(String metaActionId) {
        return mutableCacheService.getMetaActionById(metaActionId);
    }

    @Override
    public List<IMetaAction> getAllMetaActions() {
        return mutableCacheService.getAllMetaAction();
    }
}
