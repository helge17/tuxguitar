package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.SWT;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseMoveListenerManager;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMouseMoveListenerManager extends UIMouseMoveListenerManager implements MouseMoveListener {

	private SWTEventReceiver<?> control;

	public SWTMouseMoveListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void mouseMove(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onMouseMove(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button, (e.stateMask & SWT.SHIFT) != 0));
		}
	}
}
