package com.scene.mesh.service.impl.cache.scene;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.impl.cache.event.MetaEventCache;
import com.scene.mesh.service.spec.cache.ICacheProvider;
import com.scene.mesh.service.spec.scene.ISceneService;

import java.util.List;

public class SceneCacheProvider implements ICacheProvider<SceneCache, Scene> {

    private final ICache<String, Scene> cache;

    public SceneCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public SceneCache generateCacheObject() {
        return new SceneCache(cache);
    }

    @Override
    public SceneCache refreshCacheObject(List<Scene> scenes) {
        if (scenes.isEmpty()) {return new SceneCache(cache);}

        for (Scene scene: scenes) {
            this.cache.set(SceneCache.KEY_PREFIX + scene.getId(), scene);
        }
        return new SceneCache(cache);
    }
}
