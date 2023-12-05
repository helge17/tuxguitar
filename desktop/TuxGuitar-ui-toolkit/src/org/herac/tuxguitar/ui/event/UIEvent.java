package org.herac.tuxguitar.ui.event;
import org.herac.tuxguitar.ui.UIComponent;

public class UIEvent {
	
	private UIComponent control;
	
	public UIEvent(UIComponent control) {
		this.control = control;
	}
	
	public UIComponent getComponent() {
		return control;
	}
}
