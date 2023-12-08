package org.herac.tuxguitar.ui.qt.event;

import org.qtjambi.qt.core.QEvent;

public interface QTEventHandler {
	
	boolean handle(QEvent event);
}
