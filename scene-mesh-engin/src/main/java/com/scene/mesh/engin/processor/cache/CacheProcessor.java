package com.scene.mesh.engin.processor.cache;

import com.scene.mesh.foundation.impl.processor.BaseProcessor;
import com.scene.mesh.foundation.spec.processor.IProcessInput;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheProcessor extends BaseProcessor {

    private final MutableCacheService mutableCacheService;

    public CacheProcessor(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    protected boolean process(Object inputObject, IProcessInput input, IProcessOutput output) throws Exception {
        log.info(">>>>>>>> {}",inputObject.toString());
        this.mutableCacheService.refreshAll();
        return true;
    }
}
