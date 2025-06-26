package com.scene.mesh.facade.impl.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scene.mesh.facade.api.inboud.InboundMessage;
import com.scene.mesh.foundation.api.parameter.MetaParameters;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.api.event.IMetaEventService;

import java.util.Map;

public class MessageLegalityChecker extends BaseInboundMessageInterceptor {

    private IMetaEventService metaEventService;

    public MessageLegalityChecker(IMetaEventService metaEventService) {
        this.metaEventService = metaEventService;
    }

    @Override
    protected void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        try {

            InboundMessage inboundMessage = request.getMessage();
            //验证 terminalId
            String terminalId = inboundMessage.getTerminalId();
            if (terminalId == null || terminalId.isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 terminalId - 为空");
                return;//TODO 真实的 client 校验
            }

            // 校验json格式
            String message = inboundMessage.getMessage();
            if (message == null || message.isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 message - 为空");
                return;
            }
            MetaParameters metaParameters = new MetaParameters(message);
            if (metaParameters.getParameterMap()==null || metaParameters.getParameterMap().isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 json 数据 - json 结构不合规");
                return;
            }

            //校验 metaEventId 字段存在
            String metaEventId = metaParameters.get("metaEventId",null);
            if (metaEventId == null || metaEventId.isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion("非法的 json 数据 - 缺少 metaEventId 信息");
                return;
            }

            //校验 metaEventId 模型存在
            IMetaEvent metaEvent = this.metaEventService.getIMetaEvent(metaEventId);
            if (metaEvent == null) {
                response.setSuccess(Boolean.FALSE);
                response.setOpinion(StringHelper.format("非法的 metaEventId 信息 - 无法在缓存中找到该事件模型 : {0}",metaEventId));
                return;
            }

            //校验 payload 是否符合 metaEvent 模型规范
            Map<String,Object> payload = metaParameters.getMap("payload");
            if(!metaEvent.validate(payload)){
                response.setSuccess(Boolean.FALSE);
                response.setOpinion(StringHelper.format(
                        "非法的 payload 数据 - 与 metaEvent 不匹配 - json: {0}, metaEvent: {1}",
                        metaEventId, SimpleObjectHelper.objectData2json(metaEvent)));
                return;
            }

            //交给下游拦截器
            response.addPropEntry("metaParameters", metaParameters);
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
