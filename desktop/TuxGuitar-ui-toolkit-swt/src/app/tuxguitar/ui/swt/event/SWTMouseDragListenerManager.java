package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.SWT;
import app.tuxguitar.ui.event.UIMouseDragListenerManager;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMouseDragListenerManager extends UIMouseDragListenerManager implements MouseListener, MouseMoveListener {

	private SWTEventReceiver<?> control;
	private UIPosition startPosition;

	public SWTMouseDragListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void mouseMove(MouseEvent e) {
		if(!this.control.isIgnoreEvents() && this.startPosition != null ) {
			this.onMouseDrag(new UIMouseEvent(this.control, new UIPosition(e.x - this.startPosition.getX(), e.y - this.startPosition.getY()), e.button, (e.stateMask & SWT.SHIFT) != 0));
		}
	}

	public void mouseDown(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.startPosition = new UIPosition(e.x, e.y);
		}
	}

	public void mouseUp(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.startPosition = null;
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
		// nothing to do
	}
}
