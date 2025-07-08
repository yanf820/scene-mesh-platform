package com.scene.mesh.service.impl.event;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.calculate.BaseParameterCalculateType;
import com.scene.mesh.service.spec.speech.ISpeechService;

import java.util.List;
import java.util.Map;

/**
 * speech to text calculate
 */
public class SttCalculateType extends BaseParameterCalculateType {

//    @Override
//    public void calculate(Map<String,Object> payload) {
////        List<MetaParameterDescriptor> descriptors = this.getSourceParameterDescriptors();
////        if (descriptors == null || descriptors.size() != 1){
////            throw new RuntimeException("stt calculate - It is not allowed for the source field list to be empty " +
////                    "or to have a number of source fields that is not equal to 1.");
////        }
////        MetaParameterDescriptor audioParam = descriptors.get(0);
//        Object base64Audio = payload.get("audio");
//        if (!(base64Audio instanceof String)){
//            throw new RuntimeException("stt calculate - The source fields involved in the calculation " +
//                    "must be of the string type.");
//        }
//        String base64audioStr = (String) base64Audio;
//        String audioText = this.speechService.stt(base64audioStr);
//
//        payload.put(this.getAssociatedParam().getName(), audioText);
//    }
}
