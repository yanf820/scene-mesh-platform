package com.scene.mesh.service.api.cache;

/**
 * 缓存容器
 * @param <T>
 */
public abstract class CacheObjectContainer<T> {

    private final ICacheProvider<T> provider;
    private T cacheObject;

    public CacheObjectContainer(ICacheProvider<T> provider) {
        this.provider = provider;
        this.cacheObject = this.provider.generateCacheObject();
    }

    public T read(){
        if (this.isExpire()){
            if (this.cacheObject instanceof IDisposed){
                ((IDisposed) this.cacheObject).dispose();
            }
            this.cacheObject = this.provider.generateCacheObject();
            this.updateCacheObject(this.cacheObject);
        }
        return cacheObject;
    }

    public abstract boolean isExpire();

    protected void updateCacheObject(T obj){
        this.cacheObject = obj;
    }

}
