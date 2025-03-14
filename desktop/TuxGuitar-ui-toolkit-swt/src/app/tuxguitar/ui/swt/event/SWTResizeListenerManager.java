package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import app.tuxguitar.ui.event.UIResizeEvent;
import app.tuxguitar.ui.event.UIResizeListenerManager;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTResizeListenerManager extends UIResizeListenerManager implements Listener {

	private SWTEventReceiver<?> control;

	public SWTResizeListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void handleEvent(Event e) {
		if(!this.control.isIgnoreEvents()) {
			this.onResize(new UIResizeEvent(this.control));
		}
	}
}
