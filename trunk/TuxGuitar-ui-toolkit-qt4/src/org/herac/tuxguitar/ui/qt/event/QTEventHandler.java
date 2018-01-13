package org.herac.tuxguitar.ui.qt.event;

import com.trolltech.qt.core.QEvent;

public interface QTEventHandler {
	
	boolean handle(QEvent event);
}
