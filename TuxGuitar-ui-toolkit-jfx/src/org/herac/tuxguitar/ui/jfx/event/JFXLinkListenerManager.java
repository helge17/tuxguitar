package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXLinkListenerManager extends UILinkListenerManager {
	
	private JFXEventReceiver<?> control;
	
	public JFXLinkListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void fireEvent(String link) {
		if(!this.control.isIgnoreEvents()) {
			this.onLinkSelect(new UILinkEvent(this.control, link));
		}
	}
}
