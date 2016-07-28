package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.qt.menu.QTMenuBar;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QMainWindow;

public class QTWindow extends QTAbstractWindow<QMainWindow> {
	
	public QTWindow() {
		super(new QMainWindow(), null);
	}
	
	public void setMenuBar(UIMenuBar menuBar) {
		this.getControl().setMenuBar(menuBar != null ? ((QTMenuBar) menuBar).getControl() : null);
		
		super.setMenuBar(menuBar);
	}
	
	public void join() {
		while(!this.isDisposed()) {
			if (QApplication.hasPendingEvents()) {
				QApplication.processEvents();
				if(!this.isDisposed()) {
					QApplication.sendPostedEvents();
				}
			}
			Thread.yield();
		}
	}
}