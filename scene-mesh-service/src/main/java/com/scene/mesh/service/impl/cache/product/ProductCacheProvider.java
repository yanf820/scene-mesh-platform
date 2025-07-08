package com.scene.mesh.service.impl.cache.product;

import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.product.Product;
import com.scene.mesh.service.spec.cache.ICacheProvider;
import com.scene.mesh.service.spec.product.IProductService;

import java.util.List;

public class ProductCacheProvider implements ICacheProvider<ProductCache,Product> {

    private final ICache<String, Product> cache;

    public ProductCacheProvider(ICache cache) {
        this.cache = cache;
    }

    @Override
    public ProductCache generateCacheObject() {
        return new ProductCache(cache);
    }

    @Override
    public ProductCache refreshCacheObject(List<Product> products) {
        if (products.isEmpty()) {return new ProductCache(cache);}

        for (Product product : products) {
            this.cache.set(ProductCache.KEY_PREFIX + product.getId(), product);
        }
        return new ProductCache(cache);
    }
}
