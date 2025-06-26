package com.scene.mesh.service.api.product;

import com.scene.mesh.model.product.Product;

/**
 * 产品服务
 */
public interface IProductService {

    Product getProduct(String productId);
}
