package org.herac.tuxguitar.ui.event;
import org.herac.tuxguitar.ui.UIComponent;

public class UIDisposeEvent extends UIEvent {
	
	public UIDisposeEvent(UIComponent control) {
		super(control);
	}
}
