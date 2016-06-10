package swtimpl.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseMoveListenerManager;
import org.herac.tuxguitar.ui.resource.UIPosition;

import swtimpl.SWTComponent;

public class SWTMouseMoveListenerManager extends UIMouseMoveListenerManager implements MouseMoveListener {
	
	private SWTComponent<?> control;
	
	public SWTMouseMoveListenerManager(SWTComponent<?> control) {
		this.control = control;
	}
	
	public void mouseMove(MouseEvent e) {
		this.onMouseMove(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
	}
}
