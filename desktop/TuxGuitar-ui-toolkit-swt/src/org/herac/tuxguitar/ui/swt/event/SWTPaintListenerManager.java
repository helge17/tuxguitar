package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListenerManager;
import org.herac.tuxguitar.ui.swt.resource.SWTPainter;
import org.herac.tuxguitar.ui.swt.widget.SWTControl;

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
