package org.herac.tuxguitar.editor.event;

import org.herac.tuxguitar.util.TGAbstractContext;

public class TGUpdateMeasureEvent extends TGUpdateEvent {
	
	public static final String PROPERTY_MEASURE_NUMBER = "measureNumber";
	
	public TGUpdateMeasureEvent(int number, TGAbstractContext context) {
		super(MEASURE_UPDATED, context);
		
		this.setAttribute(PROPERTY_MEASURE_NUMBER, Integer.valueOf(number));
	}
}
