package com.scene.mesh.service.impl.product;

import com.scene.mesh.foundation.api.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.api.parameter.data.StringParameterDataType;
import com.scene.mesh.model.event.DefaultMetaEvent;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.Product;
import com.scene.mesh.model.protocol.ProtocolConfig;
import com.scene.mesh.model.protocol.ProtocolType;
import com.scene.mesh.service.api.product.IProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class DefaultProductService implements IProductService {

    @Override
    public Product getProduct(String productId) {
        Product mockProduct = new Product();
        mockProduct.setId("product-1");
        mockProduct.setName("星宝");
        mockProduct.setMetaEvents(mockMetaEvents());
        mockProduct.setProtocolConfig(new ProtocolConfig(){{setSupportedProtocolTypes(
                List.of(ProtocolType.MQTT,ProtocolType.WEBSOCKET));}});
        return mockProduct;
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
