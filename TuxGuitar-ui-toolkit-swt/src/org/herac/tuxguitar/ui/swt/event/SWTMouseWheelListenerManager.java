package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListenerManager;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMouseWheelListenerManager extends UIMouseWheelListenerManager implements MouseWheelListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTMouseWheelListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}
	
	public void mouseScrolled(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onMouseWheel(new UIMouseWheelEvent(this.control, new UIPosition(e.x, e.y), e.button, e.count));
		}
	}
}
