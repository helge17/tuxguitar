package org.herac.tuxguitar.ui.qt;

import com.trolltech.qt.gui.QApplication;

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
		QApplication.invokeLater(runnable);
	}
}
