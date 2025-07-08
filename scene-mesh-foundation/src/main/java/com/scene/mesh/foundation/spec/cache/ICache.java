package com.scene.mesh.foundation.spec.cache;

import java.util.List;

/**
 * 缓存接口
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 */
public interface ICache<K, V> {

    /**
     * 设置缓存
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @return 是否设置成功
     */
    boolean set(K key, V value);
    
    /**
     * 设置缓存并指定过期时间
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param expireSeconds 过期时间(秒)
     * @return 是否设置成功
     */
    boolean set(K key, V value, long expireSeconds);
    
    /**
     * 获取缓存
     * 
     * @param key 缓存键
     * @return 缓存值
     */
    V get(K key);

    /**
     * 根据前缀获取所有 V
     * @param keyPrefix
     * @return
     */
    List<V> getAll(K keyPrefix);
    
    /**
     * 删除缓存
     * 
     * @param key 缓存键
     * @return 是否删除成功
     */
    boolean delete(K key);

    boolean deleteByKeyPrefix(K keyPrefix);
    /**
     * 判断缓存是否存在
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    boolean exists(K key);
    
    /**
     * 设置过期时间
     * 
     * @param key 缓存键
     * @param expireSeconds 过期时间(秒)
     * @return 是否设置成功
     */
    boolean expire(K key, long expireSeconds);
    
    /**
     * 清空所有缓存
     * 
     * @return 是否清空成功
     */
    boolean clear();
}
