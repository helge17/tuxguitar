package org.herac.tuxguitar.ui.qt.event;

import io.qt.core.QEvent;

public interface QTEventHandler {

	boolean handle(QEvent event);
}
