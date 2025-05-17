package com.scene.mesh.service.impl.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.scene.MockSceneRepository;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.api.cache.ICacheProvider;

/**
 * 场景缓存提供器
 */
public class SceneCacheProvider implements ICacheProvider<SceneCache> {

    private final ICache<String, Scene> cache;

    private final MockSceneRepository mockSceneRepository;

    public SceneCacheProvider(ICache<String, Scene> cache, MockSceneRepository mockSceneRepository) {
        this.cache = cache;
        this.mockSceneRepository = mockSceneRepository;
    }

    @Override
    public SceneCache generateCacheObject() {
        this.mockSceneRepository.getAllScenes().forEach(scene -> {
            this.cache.set("scene_"+scene.getId(), scene);
        });
        return new SceneCache(cache);
    }
}
