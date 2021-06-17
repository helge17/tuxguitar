package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.qt.menu.QTMenuBar;
import org.qtjambi.qt.widgets.QMainWindow;

public class QTWindow extends QTAbstractWindow<QMainWindow> {
	
	public QTWindow() {
		super(new QMainWindow(), null);
	}
	
	public void setMenuBar(UIMenuBar menuBar) {
		this.getControl().setMenuBar(menuBar != null ? ((QTMenuBar) menuBar).getControl() : null);
		
		super.setMenuBar(menuBar);
	}
}