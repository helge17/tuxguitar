package app.tuxguitar.editor.event;

import java.util.List;

import app.tuxguitar.util.TGAbstractContext;

public class TGUpdateMeasuresEvent extends TGUpdateEvent {

	public static final String PROPERTY_MEASURE_NUMBERS = "measureNumbers";
	public static final String PROPERTY_UPDATE_CARET = "updateMeasuresUpdateCaret";

	public TGUpdateMeasuresEvent(List<Integer> numbers, TGAbstractContext context) {
		super(MEASURE_UPDATED, context);

		this.setAttribute(PROPERTY_MEASURE_NUMBERS, numbers);
	}
}
