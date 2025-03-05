package org.herac.tuxguitar.ui.qt;

import io.qt.core.QMetaObject;
import io.qt.core.Qt;
import io.qt.core.Qt.ConnectionType;
import io.qt.widgets.QApplication;

public class QTApplicationHandle {
	
	public QApplication getHandle() {
		return QApplication.instance();
	}
	
	public void initialize() {
		QApplication.initialize(new String[0]);
	}
	
	public void setStyle(String style) {
		QApplication.setStyle(style);
	}
	
	public void setApplicationName(String name) {
		QApplication.setApplicationName(name);
	}
	
	public void exec() {
		this.getHandle().exec();
	}
	
	public void quit() {
		QApplication.quit();
	}
	
	public void invokeLater(Runnable runnable) {
		QMetaObject.invokeMethod(runnable::run, Qt.ConnectionType.QueuedConnection);
	}
}
