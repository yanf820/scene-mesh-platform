package com.scene.mesh.service.spec.cache;

import java.util.List;

/**
 * 缓存容器
 *
 * @param <T>
 */
public abstract class CacheObjectContainer<T,C> {

    private final ICacheProvider<T,C> provider;
    private T cacheObject;

    public CacheObjectContainer(ICacheProvider<T,C> provider, boolean waitRefresh) {
        this.provider = provider;
        if (!waitRefresh) {
            this.cacheObject = this.provider.generateCacheObject();
        }
    }

    public T read() {
        if (this.isExpire()) {
            if (this.cacheObject instanceof IDisposed) {
                ((IDisposed) this.cacheObject).dispose();
            }
            this.cacheObject = this.provider.generateCacheObject();
            this.updateCacheObject(this.cacheObject);
        }
        return cacheObject;
    }

    public void refresh(List<C> objects) {
        if (this.cacheObject instanceof IDisposed) {
            ((IDisposed) this.cacheObject).dispose();
        }
        this.cacheObject = this.provider.refreshCacheObject(objects);
        this.updateCacheObject(this.cacheObject);
    }

    public abstract boolean isExpire();

    protected void updateCacheObject(T obj) {
        this.cacheObject = obj;
    }

}
