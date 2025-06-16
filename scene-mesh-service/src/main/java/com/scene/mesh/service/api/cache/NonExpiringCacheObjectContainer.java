package com.scene.mesh.service.api.cache;

/**
 * 不会过期的缓存容器，把过期管理交给 ICache
 * @param <T>
 */
public class NonExpiringCacheObjectContainer<T> extends CacheObjectContainer<T>{

    public NonExpiringCacheObjectContainer(ICacheProvider<T> provider) {
        super(provider);
    }

    @Override
    public boolean isExpire() {
        return false;
    }
}
