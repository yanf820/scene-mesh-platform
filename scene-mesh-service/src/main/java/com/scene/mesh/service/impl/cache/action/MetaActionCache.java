package com.scene.mesh.service.impl.cache.action;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.spec.cache.IDisposed;

import java.util.List;

public class MetaActionCache implements IDisposed {

    private final ICache<String, IMetaAction> cache;

    public static String KEY_PREFIX = "metaAction:";

    public MetaActionCache(ICache<String, IMetaAction> cache) {
        this.cache = cache;
    }

    public IMetaAction getMetaAction(String metaActionId) {
        return cache.get(KEY_PREFIX + metaActionId);
    }

    @Override
    public void dispose() {
        this.cache.deleteByKeyPrefix(KEY_PREFIX + "*");
    }

    public List<IMetaAction> getMetaActions() {
        return cache.getAll(KEY_PREFIX + "*");
    }
}
