package com.scene.mesh.service.impl.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.spec.api.ApiClient;
import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.*;
import com.scene.mesh.model.event.DefaultMetaEvent;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.OriginalProduct;
import com.scene.mesh.model.product.Product;
import com.scene.mesh.model.product.ProductSetting;
import com.scene.mesh.model.protocol.ProtocolConfig;
import com.scene.mesh.model.protocol.ProtocolType;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.scene.WhenThen;
import com.scene.mesh.service.impl.event.SttCalculateType;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.speech.ISpeechService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
public class DefaultProductService implements IProductService {

    private final MutableCacheService mutableCacheService;

    public DefaultProductService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public Product getProduct(String productId) {
        return mutableCacheService.getProductById(productId);
    }

    @Override
    public boolean verifyProductSecret(String productId, String secretKey) {
        Product product = this.getProduct(productId);
        if (product == null) {
            log.error("product not found for productId :{}", productId);
            return false;
        }
        if (product.getSettings() == null || product.getSettings().getSecretKey() == null) {
            log.error("product settings not found or secretKey is null");
            return false;
        }

        String[] secretKeys = product.getSettings().getSecretKey();
        boolean isMatched = false;
        for (String sk : secretKeys) {
            if (sk.equals(secretKey)) {
                isMatched = true;
                break;
            }
        }
        return isMatched;
    }

}
