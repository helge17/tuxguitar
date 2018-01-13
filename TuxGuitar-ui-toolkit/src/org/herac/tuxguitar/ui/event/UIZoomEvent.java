package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;

public class UIZoomEvent extends UIEvent {
	
	private Integer value;
	
	public UIZoomEvent(UIComponent control, Integer value) {
		super(control);
		
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
}
