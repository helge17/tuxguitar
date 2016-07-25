package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseMoveListenerManager;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMouseMoveListenerManager extends UIMouseMoveListenerManager implements MouseMoveListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTMouseMoveListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}
	
	public void mouseMove(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onMouseMove(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
		}
	}
}
