package com.scene.mesh.facade.impl.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scene.mesh.foundation.api.parameter.MetaParameters;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.api.cache.MutableCacheService;

public class MessageLegalityChecker extends BaseInboundMessageInterceptor {

    private MutableCacheService mutableCacheService;

    public MessageLegalityChecker(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    protected void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        try {

            //验证 clientID
            String clientId = request.getClientId();
            if (clientId == null || clientId.isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 clientId - 为空");
                return;//TODO 真实的 client 校验
            }

            // 校验json格式
            String message = request.getMessage();
            if (message == null || message.isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 message - 为空");
                return;
            }
            MetaParameters metaParameters = new MetaParameters(message);
            if (metaParameters.getParameterMap()==null || metaParameters.getParameterMap().isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 json 数据 - 无键值对");
                return;
            }

            //校验 metaEventId 字段存在
            String metaEventId = request.getMetaEventId();
            if (metaEventId == null || metaEventId.isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 json 数据 - 缺少 metaEventId 信息");
                return;
            }

            //校验 metaEventId 模型存在
            IMetaEvent metaEvent = this.mutableCacheService.getMetaEventById(metaEventId);
            if (metaEvent == null) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion(StringHelper.format("非法的 metaEventId 信息 - 无法在缓存中找到该事件模型 : {0}",metaEventId));
                return;
            }

            //校验 json 是否符合 metaEvent 模型规范
            if(!metaEvent.validate(metaParameters)){
                response.setSuccess(Boolean.FALSE);
                response.setOpinion(StringHelper.format(
                        "非法的 json 数据 - 与 metaEvent 不匹配 - json: {0}, metaEvent: {1}",
                        metaEventId, SimpleObjectHelper.objectData2json(metaEvent)));
                return;
            }

            //交给下游拦截器
            response.addPayloadEntry("metaParameters", metaParameters);
            response.setSuccess(Boolean.TRUE);
            response.setOpinion("json 校验完成");

        } catch (JsonProcessingException e) {
            response.setSuccess(Boolean.FALSE);
            response.setOpinion(StringHelper.format("json 校验失败 - {0}", e.getMessage()));
        }
    }

    @Override
    public String getName() {
        return "MessageLegalityChecker";
    }
}
