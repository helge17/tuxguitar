package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;

public class UIKeyEvent extends UIEvent {
	
	private UIKeyCombination keyCombination;
	
	public UIKeyEvent(UIComponent control, UIKeyCombination keyCombination) {
		super(control);
		
		this.keyCombination = keyCombination;
	}

	public UIKeyCombination getKeyCombination() {
		return keyCombination;
	}
}
