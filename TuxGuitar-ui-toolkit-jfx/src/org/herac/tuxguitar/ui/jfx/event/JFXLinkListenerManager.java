package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXLinkListenerManager extends UILinkListenerManager {
	
	private JFXComponent<?> control;
	
	public JFXLinkListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void fireEvent(String link) {
		this.onLinkSelect(new UILinkEvent(this.control, link));
	}
}
