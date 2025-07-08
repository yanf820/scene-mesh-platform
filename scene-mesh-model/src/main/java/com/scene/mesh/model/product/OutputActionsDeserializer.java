package com.scene.mesh.model.product;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.scene.mesh.model.scene.WhenThen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputActionsDeserializer extends JsonDeserializer<List<WhenThen.OutputAction>> {

    @Override
    public List<WhenThen.OutputAction> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<WhenThen.OutputAction> result = new ArrayList<>();

        if (p.currentToken() == JsonToken.START_ARRAY) {
            JsonNode arrayNode = p.readValueAsTree();

            for (JsonNode node : arrayNode) {
                if (node.isTextual()) {
                    // 情况1：字符串数组 ["id1", "id2"]
                    WhenThen.OutputAction action = new WhenThen.OutputAction();
                    action.setActionId(node.asText());
                    action.setValues(new ArrayList<>()); // 空values
                    result.add(action);
                } else if (node.isObject()) {
                    // 情况2：对象数组 [{actionId:"id1", values:[...]}]
                    WhenThen.OutputAction action = ctxt.readTreeAsValue(node, WhenThen.OutputAction.class);
                    result.add(action);
                }
            }
        }

        return result;
    }

}
