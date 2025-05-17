package com.scene.mesh.service.api.cache;

import lombok.Getter;

/**
 * 缓存容器，根据时间过期
 * @param <T>
 */
public class TimeExpireCacheObjectContainer<T> extends CacheObjectContainer<T> {

    private final Long timeExpire;

    /**
     * 创建时间（毫秒时间戳）
     */
    @Getter
    private long createTime;

    public TimeExpireCacheObjectContainer(ICacheProvider<T> provider, Long timeExpire) {
        super(provider);
        this.timeExpire = timeExpire;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public boolean isExpire() {
        long elapsedSeconds = (System.currentTimeMillis() - createTime) / 1000;
        return elapsedSeconds >= timeExpire;
    }
}
