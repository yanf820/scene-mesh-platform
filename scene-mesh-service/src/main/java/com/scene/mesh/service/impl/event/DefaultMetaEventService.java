package com.scene.mesh.service.impl.event;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.*;
import com.scene.mesh.foundation.spec.parameter.data.calculate.IParameterCalculateType;
import com.scene.mesh.model.event.DefaultMetaEvent;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.OriginalProduct;
import com.scene.mesh.service.impl.speech.DefaultSpeechService;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.event.IMetaEventService;
import com.scene.mesh.service.spec.speech.ISpeechService;

import java.util.ArrayList;
import java.util.List;

public class DefaultMetaEventService implements IMetaEventService {

    private final MutableCacheService mutableCacheService;

    public DefaultMetaEventService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public IMetaEvent getIMetaEvent(String metaEventId) {
        return this.mutableCacheService.getIMetaEvent(metaEventId);
    }

    @Override
    public List<IMetaEvent> getAllMetaEvents(){
        return this.mutableCacheService.getAllMetaEvent();
    }
}
