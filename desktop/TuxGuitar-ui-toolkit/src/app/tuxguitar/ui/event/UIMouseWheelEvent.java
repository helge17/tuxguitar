package app.tuxguitar.ui.event;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.resource.UIPosition;

public class UIMouseWheelEvent extends UIMouseEvent {

	private Integer value;

	public UIMouseWheelEvent(UIComponent control, UIPosition position, Integer button, Integer value, Boolean isShiftDown) {
		super(control, position, button, isShiftDown);

		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
}
