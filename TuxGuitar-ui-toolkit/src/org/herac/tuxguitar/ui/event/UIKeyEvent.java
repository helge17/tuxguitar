package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;

public class UIKeyEvent extends UIEvent {
	
	private UIKeyConvination keyConvination;
	
	public UIKeyEvent(UIComponent control, UIKeyConvination keyConvination) {
		super(control);
		
		this.keyConvination = keyConvination;
	}

	public UIKeyConvination getKeyConvination() {
		return keyConvination;
	}
}
