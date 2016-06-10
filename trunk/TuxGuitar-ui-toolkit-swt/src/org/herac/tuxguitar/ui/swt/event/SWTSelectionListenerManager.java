package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTSelectionListenerManager extends UISelectionListenerManager implements SelectionListener {
	
	private SWTComponent<?> control;
	
	public SWTSelectionListenerManager(SWTComponent<?> control) {
		this.control = control;
	}
	
	public void widgetSelected(SelectionEvent arg0) {
		this.onSelect(new UISelectionEvent(this.control));
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {
		// Nothing to do
	}
}
