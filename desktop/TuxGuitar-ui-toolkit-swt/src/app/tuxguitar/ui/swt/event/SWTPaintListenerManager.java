package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListenerManager;
import app.tuxguitar.ui.swt.resource.SWTPainter;
import app.tuxguitar.ui.swt.widget.SWTControl;

public class SWTPaintListenerManager extends UIPaintListenerManager implements PaintListener {

	private SWTControl<?> control;

	public SWTPaintListenerManager(SWTControl<?> control) {
		this.control = control;
	}

	public void paintControl(PaintEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onPaint(new UIPaintEvent(this.control, new SWTPainter(e.gc)));
		}
	}
}
