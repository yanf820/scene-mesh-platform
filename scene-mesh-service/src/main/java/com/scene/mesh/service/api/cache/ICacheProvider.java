package com.scene.mesh.service.api.cache;

/**
 * 缓存提供
 */
public interface ICacheProvider<T> {

    T generateCacheObject();
}
