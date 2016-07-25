package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.herac.tuxguitar.ui.event.UIMouseEnterListener;
import org.herac.tuxguitar.ui.event.UIMouseEnterListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseExitListener;
import org.herac.tuxguitar.ui.event.UIMouseExitListenerManager;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMouseTrackListenerManager implements MouseTrackListener {
	
	private SWTEventReceiver<?> control;
	private UIMouseEnterListenerManager mouseEnterListener;
	private UIMouseExitListenerManager mouseExitListener;
	
	public SWTMouseTrackListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
		this.mouseEnterListener = new UIMouseEnterListenerManager();
		this.mouseExitListener = new UIMouseExitListenerManager();
	}
	
	public boolean isEmpty() {
		return (this.mouseEnterListener.isEmpty() && this.mouseExitListener.isEmpty());
	}
	
	public void addListener(UIMouseEnterListener listener) {
		this.mouseEnterListener.addListener(listener);
	}
	
	public void addListener(UIMouseExitListener listener) {
		this.mouseExitListener.addListener(listener);
	}
	
	public void removeListener(UIMouseEnterListener listener) {
		this.mouseEnterListener.removeListener(listener);
	}
	
	public void removeListener(UIMouseExitListener listener) {
		this.mouseExitListener.removeListener(listener);
	}
	
	public void mouseEnter(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.mouseEnterListener.onMouseEnter(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
		}
	}

	public void mouseExit(MouseEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.mouseExitListener.onMouseExit(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
		}
	}

	public void mouseHover(MouseEvent e) {
		// not implemented
	}
}
