package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListenerManager;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTLinkListenerManager extends UILinkListenerManager implements SelectionListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTLinkListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}
	
	public void widgetSelected(SelectionEvent event) {
		if(!this.control.isIgnoreEvents()) {
			if( event.text != null ){
				this.onLinkSelect(new UILinkEvent(this.control, event.text));
			}
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {
		// Nothing to do
	}
}
