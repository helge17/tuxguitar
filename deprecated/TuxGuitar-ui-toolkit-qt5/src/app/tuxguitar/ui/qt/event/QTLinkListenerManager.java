package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UILinkEvent;
import app.tuxguitar.ui.event.UILinkListenerManager;
import app.tuxguitar.ui.qt.QTComponent;

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
