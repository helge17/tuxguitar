package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.SWT;
import app.tuxguitar.ui.event.UIMouseWheelEvent;
import app.tuxguitar.ui.event.UIMouseWheelListenerManager;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMouseWheelListenerManager extends UIMouseWheelListenerManager implements MouseWheelListener {

	private SWTEventReceiver<?> control;

	public SWTMouseWheelListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void mouseScrolled(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onMouseWheel(new UIMouseWheelEvent(this.control, new UIPosition(e.x, e.y), e.button, e.count, (e.stateMask & SWT.SHIFT) != 0));
		}
	}
}
