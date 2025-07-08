package com.scene.mesh.service.spec.cache;

import java.util.List;

/**
 * 缓存提供
 */
public interface ICacheProvider<T,C> {

    T generateCacheObject();

    T refreshCacheObject(List<C> objects);
}
