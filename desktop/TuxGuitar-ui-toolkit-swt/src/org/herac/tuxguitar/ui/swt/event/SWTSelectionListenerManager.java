package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTSelectionListenerManager extends UISelectionListenerManager implements SelectionListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTSelectionListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}
	
	public SWTEventReceiver<?> getControl() {
		return this.control;
	}
	
	public void widgetSelected(SelectionEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onSelect(new UISelectionEvent(this.control));
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {
		// Nothing to do
	}
}
