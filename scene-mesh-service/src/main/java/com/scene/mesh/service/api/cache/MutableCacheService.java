package com.scene.mesh.service.api.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.event.MockMetaEventRepository;
import com.scene.mesh.model.scene.MockSceneRepository;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.impl.cache.MetaEventCache;
import com.scene.mesh.service.impl.cache.MetaEventCacheProvider;
import com.scene.mesh.service.impl.cache.SceneCache;
import com.scene.mesh.service.impl.cache.SceneCacheProvider;

/**
 * 缓存服务
 */
public class MutableCacheService {

    private static CacheObjectContainer<SceneCache> sceneCacheContainer;

    private static CacheObjectContainer<MetaEventCache> eventCacheContainer;

    public MutableCacheService(ICache cache, MockSceneRepository mockSceneRepository, MockMetaEventRepository mockMetaEventRepository) {
        sceneCacheContainer = new TimeExpireCacheObjectContainer<>(
                new SceneCacheProvider(cache,mockSceneRepository), 3600L);

        eventCacheContainer = new TimeExpireCacheObjectContainer<>(
                new MetaEventCacheProvider(cache,mockMetaEventRepository),3600L);

    }

    public Scene getSceneById(String sceneId){
        return sceneCacheContainer.read().findById(sceneId);
    }

    public IMetaEvent getMetaEventById(String metaEventId){
        return eventCacheContainer.read().findById(metaEventId);
    }


}
