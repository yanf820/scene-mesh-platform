package com.scene.mesh.service.api.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.event.MockMetaEventRepository;
import com.scene.mesh.model.scene.MockSceneRepository;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.impl.cache.*;

import java.util.LinkedList;

/**
 * 缓存服务
 */
public class MutableCacheService {

    private static CacheObjectContainer<SceneCache> sceneCacheContainer;

    private static CacheObjectContainer<MetaEventCache> eventCacheContainer;

    private static CacheObjectContainer<TerminalSessionCache> terminalSessionCacheContainer;


    public MutableCacheService(ICache cache, MockSceneRepository mockSceneRepository, MockMetaEventRepository mockMetaEventRepository) {
        sceneCacheContainer = new TimeExpireCacheObjectContainer<>(
                new SceneCacheProvider(cache, mockSceneRepository), 3600L);

        eventCacheContainer = new TimeExpireCacheObjectContainer<>(
                new MetaEventCacheProvider(cache, mockMetaEventRepository), 3600L);

        terminalSessionCacheContainer = new NonExpiringCacheObjectContainer<>(
                new TerminalSessionCacheProvider(cache));
    }

    public Scene getSceneById(String sceneId) {
        return sceneCacheContainer.read().findById(sceneId);
    }

    public TerminalSession getTerminalSessionByTerminalId(String terminalId) {
        return terminalSessionCacheContainer.read().findByTerminalId(terminalId);
    }

    public void setTerminalSession(TerminalSession terminalSession) {
        terminalSessionCacheContainer.read().setTerminalSession(terminalSession);
    }

    public boolean updateTerminalSession(TerminalSession terminalSession) {
        terminalSessionCacheContainer.read().deleteTerminalSession(terminalSession.getTerminalId());
        terminalSessionCacheContainer.read().setTerminalSession(terminalSession);
        return true;
    }

    public LinkedList<Scene> getParentScenesById(String sceneId) {
        return sceneCacheContainer.read().findParentScenesById(sceneId);
    }

    public IMetaEvent getMetaEventById(String metaEventId) {
        return eventCacheContainer.read().findById(metaEventId);
    }


}
