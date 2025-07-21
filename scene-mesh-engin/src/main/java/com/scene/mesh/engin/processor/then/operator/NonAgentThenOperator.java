package com.scene.mesh.engin.processor.then.operator;

import com.scene.mesh.engin.model.ThenRequest;
import com.scene.mesh.engin.model.ThenResponse;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;
import com.scene.mesh.model.action.Action;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.scene.WhenThen;
import com.scene.mesh.model.session.TerminalSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NonAgentThenOperator implements IThenOperator {
    @Override
    public String getOperatorType() {
        return "FORMAT_OUTPUT";
    }

    @Override
    public boolean process(String terminalId, Scene scene, WhenThen.Then then, List<Event> eventsInScene, IProcessOutput output) {
        List<WhenThen.OutputAction> outputActions = then.getOutputActions();
        if (outputActions == null || outputActions.isEmpty()) {
            return true;
        }

        for (WhenThen.OutputAction outputAction : outputActions) {
            String actionId = outputAction.getActionId();
            List<WhenThen.OutPutActionValue> outPutActionValues =outputAction.getValues();
            Map<String,Object> payload = new HashMap<>();
            for (WhenThen.OutPutActionValue fieldVal : outPutActionValues) {
                String key = fieldVal.getFieldName();
                String value = fieldVal.getValue();
                payload.put(key,value);
            }
            Action action = new Action(actionId);
            action.setTerminalId(terminalId);
            action.setPayload(payload);

            output.getCollector().collect(action);
        }

        return true;
    }
}
