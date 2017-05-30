package org.herac.tuxguitar.midi.synth;

import java.util.Map.Entry;
import java.util.Set;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;

public final class TGProgramPropertiesUtil {
	
	public static TGProgram getProgram(TGProperties properties, String prefix) {
		TGProgram program = null;
		TGProgramElement element = TGProgramPropertiesUtil.getElement(properties, prefix);
		if( element != null ) {
			program = new TGProgram();
			program.setReceiver(element);
			
			Integer processorCount = TGPropertiesUtil.getIntegerValue(properties, prefix + ".output.count");
			if( processorCount != null ) {
				for(int i = 0 ; i < processorCount ; i ++) {
					TGProgramElement processor = TGProgramPropertiesUtil.getElement(properties, prefix + ".output." + i);
					if( processor != null ) {
						program.addOutput(processor);
					}
				}
			}
		}
		return program;
	}
	
	public static TGProgramElement getElement(TGProperties properties, String keyPrefix) {
		TGProgramElement element = null;
		String elementId = TGPropertiesUtil.getStringValue(properties, keyPrefix + ".id");
		String elementType = TGPropertiesUtil.getStringValue(properties, keyPrefix + ".type");
		if( elementId != null && elementType != null ) {
			element = new TGProgramElement();
			element.setId(elementId);
			element.setType(elementType);
			
			Integer paramCount = TGPropertiesUtil.getIntegerValue(properties, keyPrefix + ".param.count");
			if( paramCount != null ) {
				for(int i = 0 ; i < paramCount ; i ++) {
					String paramName = TGPropertiesUtil.getStringValue(properties, keyPrefix + ".param." + i + ".name");
					String paramValue = TGPropertiesUtil.getStringValue(properties, keyPrefix + ".param." + i + ".value");
					if( paramName != null ) {
						element.setParameter(paramName, paramValue);
					}
				}
			}
		}
		return element;
	}
	
	public static void setProgram(TGProperties properties, String prefix, TGProgram program) {
		if( program.getReceiver() != null ) {
			TGProgramPropertiesUtil.setElement(properties, prefix, program.getReceiver());
			
			TGPropertiesUtil.setValue(properties, prefix + ".output.count", program.countOutputs());
			for(int i = 0 ; i < program.countOutputs() ; i ++) {
				TGProgramElement processor = program.getOutput(i);
				if( processor != null ) {
					TGProgramPropertiesUtil.setElement(properties, prefix + ".output." + i, processor);
				}
			}
		}
	}
	
	public static void setElement(TGProperties properties, String prefix, TGProgramElement element) {
		if( element.getId() != null && element.getType() != null ) {
			TGPropertiesUtil.setValue(properties, prefix + ".id", element.getId());
			TGPropertiesUtil.setValue(properties, prefix + ".type", element.getType());
			
			Set<Entry<String, String>> parameters = element.getParameters().entrySet();
			TGPropertiesUtil.setValue(properties, prefix + ".param.count", parameters.size());
			
			int index = 0;
			for(Entry<String, String> parameter : parameters) {
				TGPropertiesUtil.setValue(properties, prefix + ".param." + index + ".name", parameter.getKey());
				TGPropertiesUtil.setValue(properties, prefix + ".param." + index + ".value", parameter.getValue());
				
				index ++;
			}
		}
	}
}
