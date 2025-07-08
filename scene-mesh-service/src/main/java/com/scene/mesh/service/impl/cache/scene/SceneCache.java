package com.scene.mesh.service.impl.cache.scene;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.spec.cache.IDisposed;

import java.util.List;

public class SceneCache implements IDisposed {

    private final ICache<String, Scene> cache;

    public static String KEY_PREFIX = "scene:";

    public SceneCache(ICache<String, Scene> cache) {
        this.cache = cache;
    }

    public Scene getScene(String sceneId) {
        return cache.get(KEY_PREFIX + sceneId);
    }

    @Override
    public void dispose() {
        this.cache.deleteByKeyPrefix(KEY_PREFIX + "*");
    }

    public List<Scene> getScenes() {
        return cache.getAll(KEY_PREFIX + "*");
    }
}
