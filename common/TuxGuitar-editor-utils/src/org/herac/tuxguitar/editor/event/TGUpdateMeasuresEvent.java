package org.herac.tuxguitar.editor.event;

import java.util.List;

import org.herac.tuxguitar.util.TGAbstractContext;

public class TGUpdateMeasuresEvent extends TGUpdateEvent {
	
	public static final String PROPERTY_MEASURE_NUMBERS = "measureNumbers";
	
	public TGUpdateMeasuresEvent(List<Integer> numbers, TGAbstractContext context) {
		super(MEASURE_UPDATED, context);
		
		this.setAttribute(PROPERTY_MEASURE_NUMBERS, numbers);
	}
}
