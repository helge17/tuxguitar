package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import app.tuxguitar.ui.event.UIZoomEvent;
import app.tuxguitar.ui.event.UIZoomListenerManager;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTZoomListenerManager extends UIZoomListenerManager implements Listener {

	public static final int EVENT_TYPE = SWT.MouseWheel;

	private SWTEventReceiver<?> control;

	public SWTZoomListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void handleEvent(Event event) {
		if(!this.control.isIgnoreEvents()) {
			if(((event.stateMask & SWT.CONTROL) == SWT.CONTROL) && (event.count!=0)) {
				event.doit = false;

				this.onZoom(new UIZoomEvent(this.control, (event.count > 0 ? 1 : -1)));
			}
		}
	}
}
