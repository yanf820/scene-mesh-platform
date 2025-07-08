package com.scene.mesh.service.spec.cache;

import java.util.List;

/**
 * 不会过期的缓存容器，把过期管理交给 ICache
 * @param <T>
 */
public class NonExpiringCacheObjectContainer<T,C> extends CacheObjectContainer<T,C>{

    public NonExpiringCacheObjectContainer(ICacheProvider<T,C> provider,boolean waitRefresh) {
        super(provider,waitRefresh);
    }

    @Override
    public boolean isExpire() {
        return false;
    }
}
