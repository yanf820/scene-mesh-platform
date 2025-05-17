package com.scene.mesh.service.impl.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.api.cache.IDisposed;

import java.util.List;

/**
 * 场景缓存
 */
public class SceneCache implements IDisposed {

    private ICache<String,Scene> cache;

    public SceneCache(ICache<String,Scene> cache) {
        this.cache = cache;
    }

    public Scene findById(String id) {
        return cache.get("scene_"+id);
    }

    @Override
    public void dispose() {
        this.cache = null;
    }
}
