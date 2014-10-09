package org.herac.tuxguitar.app.editors;

public class TGUpdateMeasureEvent extends TGUpdateEvent {
	
	public static final String PROPERTY_MEASURE_NUMBER = "measureNumber";
	
	public TGUpdateMeasureEvent(int number) {
		super(MEASURE_UPDATED);
		
		this.setProperty(PROPERTY_MEASURE_NUMBER, Integer.valueOf(number));
	}
}
