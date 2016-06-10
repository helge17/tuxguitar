package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListenerManager;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTLinkListenerManager extends UILinkListenerManager implements SelectionListener {
	
	private SWTComponent<?> control;
	
	public SWTLinkListenerManager(SWTComponent<?> control) {
		this.control = control;
	}
	
	public void widgetSelected(SelectionEvent event) {
		final String link = event.text;
		if( link != null ){
			this.onLinkSelect(new UILinkEvent(this.control, link));
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {
		// Nothing to do
	}
}
