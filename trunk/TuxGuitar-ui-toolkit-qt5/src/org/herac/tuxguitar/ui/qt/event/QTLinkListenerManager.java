package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

public class QTLinkListenerManager extends UILinkListenerManager {
	
	public static final String SIGNAL_METHOD = "handle(String)";
	
	private QTComponent<?> control;
	
	public QTLinkListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(String link) {
		if( link != null ) {
			this.onLinkSelect(new UILinkEvent(this.control, link));
		}
	}
}
