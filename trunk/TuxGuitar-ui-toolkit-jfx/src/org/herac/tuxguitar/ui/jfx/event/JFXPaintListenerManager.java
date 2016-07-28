package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXCanvas;

public class JFXPaintListenerManager extends UIPaintListenerManager {
	
	private JFXCanvas control;
	
	public JFXPaintListenerManager(JFXCanvas control) {
		this.control = control;
	}
	
	public void fireEvent() {
		if(!this.control.isIgnoreEvents()) {
			this.onPaint(new UIPaintEvent(this.control, this.control.createPainter()));
		}
	}

	public JFXCanvas getControl() {
		return this.control;
	}
}
