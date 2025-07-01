package com.scene.mesh.service.impl.product;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.StringParameterDataType;
import com.scene.mesh.model.event.DefaultMetaEvent;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.Product;
import com.scene.mesh.model.product.ProductSetting;
import com.scene.mesh.model.protocol.ProtocolConfig;
import com.scene.mesh.model.protocol.ProtocolType;
import com.scene.mesh.service.spec.product.IProductService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultProductService implements IProductService {

    @Override
    public Product getProduct(String productId) {
        Product mockProduct = new Product();
        mockProduct.setId("product-1");
        mockProduct.setName("星宝");
        mockProduct.setMetaEvents(mockMetaEvents());
        ProductSetting productSetting = new ProductSetting();
        productSetting.setSecretKey(new String[]{"123456","23456"});
        productSetting.setProtocolConfig(new ProtocolConfig(){{setSupportedProtocolTypes(
                List.of(ProtocolType.MQTT,ProtocolType.WEBSOCKET));}});
        mockProduct.setSettings(productSetting);
        return mockProduct;
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

    private List<IMetaEvent> mockMetaEvents(){
        List<IMetaEvent> metaEvents = new ArrayList<>();

        DefaultMetaEvent rfidEventModel = new DefaultMetaEvent("wakeup_rfid","rfid 唤醒事件","rfid 唤醒事件");
        rfidEventModel.addParameterDescriptor(new MetaParameterDescriptor("rfid","rfid","终端上传的 RFID",new StringParameterDataType(),true));

        DefaultMetaEvent wordEventModel = new DefaultMetaEvent("wakeup_word","关键词唤醒事件","关键词唤醒事件");
        wordEventModel.addParameterDescriptor(new MetaParameterDescriptor("word","唤醒词","终端唤醒的关键词",new StringParameterDataType(),true));

        metaEvents.add(rfidEventModel);
        metaEvents.add(wordEventModel);
        return metaEvents;
    }
}
